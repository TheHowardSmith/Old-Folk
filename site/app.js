/* Old Folk Air: the web radio.
 *
 * Loads the published track list (copied into data/ when the site is
 * deployed), picks random tracks from the selected genres without repeating
 * any until all have been heard, and plays each one straight from the archive
 * that holds it. Only the next track is fetched ahead (downloaded once, then
 * played from that download), to keep the load on the archives light. Tracks
 * are levelled, one-sided transfers play on both speakers, pops are limited and
 * very noisy discs are skipped (sound.js). Preferences and history live in this
 * browser's localStorage.
 */
(() => {
  "use strict";
  const DATA = "data/";
  const REPO = "https://github.com/TheHowardSmith/Old-Folk";
  const UP_NEXT = 5;
  const $ = (id) => document.getElementById(id);

  // ------------------------------------------------------------------ storage
  const store = {
    get(k, d) {
      try { const v = localStorage.getItem("ofa." + k); return v == null ? d : JSON.parse(v); }
      catch { return d; }
    },
    set(k, v) { try { localStorage.setItem("ofa." + k, JSON.stringify(v)); } catch { /* private mode */ } },
  };

  // ------------------------------------------------------------------ tracks
  const encoder = new TextEncoder();
  function keyOf(t) {               // FNV-1a of "id/file", short and stable (cached on the track)
    if (t._k) return t._k;
    let h = 0x811c9dc5;
    for (const b of encoder.encode(t.i + "/" + t.f)) { h ^= b; h = Math.imul(h, 0x01000193) >>> 0; }
    return (t._k = h.toString(36));
  }
  const encPath = (p) => String(p).split("/").map(encodeURIComponent).join("/");
  const streamUrl = (t) => t.u || `https://archive.org/download/${encodeURIComponent(t.i)}/${encPath(t.f)}`;
  const pageUrl = (t) => t.w || `https://archive.org/details/${encodeURIComponent(t.i)}`;
  const sourceName = (t) => t.u ? (t.s || (t.u.includes("loc.gov") ? "Library of Congress" : new URL(t.u).host)) : "Internet Archive";
  const yearLabel = (t) => t.y ? String(t.y) : "78 rpm era";
  function artUrl(t) {
    const p = t.p == null ? null : String(t.p);
    if (t.u) return p && p.startsWith("https://") ? p : null;
    const id = encodeURIComponent(t.i);
    if (!p) return `https://archive.org/services/img/${id}`;
    if (p === "1") return `https://archive.org/download/${id}/${encodeURIComponent(t.i + "_itemimage.jpg")}`;
    if (p === "2") return `https://archive.org/download/${id}/${encodeURIComponent(t.i + "_itemimage.png")}`;
    return `https://archive.org/download/${id}/${encPath(p)}`;
  }
  function reportUrl(t) {
    const q = new URLSearchParams({
      template: "track-report.yml", title: `Track report: ${t.t}`.slice(0, 120),
      item: t.i, file: t.f, track: `${t.t} / ${t.a || "unknown"} / ${yearLabel(t)}`,
    });
    return `${REPO}/issues/new?${q}`;
  }

  // ------------------------------------------------------------------ state
  let index = null;
  const genreNames = new Map();
  const tracks = [];
  let shardsLeft = 0;
  let selected = new Set(store.get("genres", []));        // empty = all genres
  const played = new Set(store.get("played", []));
  let history = store.get("history", []);                  // [{t, at}], oldest first
  let queue = [];
  let current = null;
  const back = [];                                         // this session's previous tracks
  let errors = 0;

  // Two players: `audio` goes through the sound graph (levelling, one-sided fix,
  // limiter) and plays archives that allow cross-site audio (archive.org,
  // loc.gov); `plain` plays the others untouched.
  const audio = $("audio");
  const plain = $("plain");
  let player = audio;
  let graph = null;                                        // created on the first play (needs a click)
  let skipNoisy = store.get("skipNoisy", true);
  const CORS_HOSTS = ["archive.org", "loc.gov"];
  const canProcess = (t) => { try { const h = new URL(streamUrl(t)).host; return CORS_HOSTS.some((d) => h === d || h.endsWith("." + d)); } catch { return false; } };
  const ready = new Map();                                 // key -> {url, gainDb, side, noisy}
  const pending = new Map();                               // key -> Promise
  const MAX_HOLD = 40e6, HEAD_BYTES = 1600000;             // hold files up to 40 MB; measure the first 1.6 MB

  const inSelection = (t) => selected.size === 0 || selected.has(t.g);
  const status = (msg) => { $("status").textContent = msg || ""; };

  // ------------------------------------------------------------------ loading
  async function getJson(path) {
    const r = await fetch(DATA + path);
    if (!r.ok) throw new Error(`${path}: HTTP ${r.status}`);
    return r.json();
  }

  async function load() {
    try {
      index = await getJson("index.json");
    } catch (e) {
      status("Couldn't load the track list. Check your connection and reload the page.");
      return;
    }
    for (const g of index.genres || []) genreNames.set(g.k, g.name);
    renderGenres();
    renderDial(null);
    const shards = [...(index.shards || []), ...(index.ext || [])];
    shardsLeft = shards.length;
    // One shard of the chosen genres first so the radio can start, then the rest.
    const wanted = shards.filter((s) => selected.size === 0 || selected.has(s.g));
    const first = pickWeighted(wanted.length ? wanted : shards);
    const rest = shards.filter((s) => s !== first);
    await loadShard(first);
    fillQueue();
    status(current ? "" : `${tracks.length.toLocaleString()} tracks ready. Press play to tune in.`);
    renderHistory();
    for (const s of rest) {
      await loadShard(s);
      if (queue.length < UP_NEXT) fillQueue();
    }
    if (!current) status(`${tracks.length.toLocaleString()} recordings. Press play to tune in.`);
    renderHistory();
  }

  function pickWeighted(list) {
    const total = list.reduce((n, s) => n + Math.max(1, s.n || 1), 0);
    let r = Math.random() * total;
    for (const s of list) { r -= Math.max(1, s.n || 1); if (r <= 0) return s; }
    return list[list.length - 1];
  }

  async function loadShard(s) {
    try {
      const d = await getJson(s.f);
      for (const t of d.t || []) { keyOf(t); tracks.push(t); }
    } catch (e) {
      console.warn(e);
    } finally {
      shardsLeft--;
    }
  }

  // ------------------------------------------------------------------ picking
  function pick() {
    const taken = new Set(queue.map(keyOf));
    if (current) taken.add(keyOf(current));
    const candidates = tracks.filter((t) => inSelection(t) && !played.has(keyOf(t)) && !taken.has(keyOf(t)));
    if (candidates.length) return candidates[Math.floor(Math.random() * candidates.length)];
    if (shardsLeft > 0) return null;
    // Heard everything in the selection: start again (except what's queued).
    let cleared = 0;
    for (const t of tracks) if (inSelection(t) && played.delete(keyOf(t))) cleared++;
    if (cleared) {
      store.set("played", [...played]);
      status("You've heard every track in your selection, so the radio is starting over.");
      return pick();
    }
    return null;
  }

  function fillQueue() {
    queue = queue.filter(inSelection);
    while (queue.length < UP_NEXT) {
      const t = pick();
      if (!t) break;
      queue.push(t);
    }
    // Download only the very next track ahead (once: it then plays from that
    // download), measure it, and drop it if it's very noisy.
    if (queue[0]) prepare(queue[0]).then((r) => {
      if (r && r.noisy && skipNoisy && queue[0] && keyOf(queue[0]) === keyOf(r.track)) {
        noteNoisy(queue.shift());
        fillQueue();
      }
    });
    renderUpNext();
  }

  // ------------------------------------------------------------------ sound
  function prepare(t, headOnly = false) {
    const k = keyOf(t);
    if (ready.has(k)) return Promise.resolve(ready.get(k));
    if (!canProcess(t)) return Promise.resolve(null);
    if (pending.has(k)) return pending.get(k);
    const p = (async () => {
      try {
        const r = await fetch(streamUrl(t));
        if (!r.ok) return null;
        let head, url = null;
        if (headOnly || +r.headers.get("content-length") > MAX_HOLD) {
          // Already streaming, or too big to hold: read just the start to measure.
          head = await readStart(r, HEAD_BYTES);
        } else {
          const blob = await r.blob();
          head = await blob.slice(0, HEAD_BYTES).arrayBuffer();
          url = URL.createObjectURL(blob);
        }
        let info = { gainDb: 0, side: 0, noisy: false };
        try { info = await window.OFASound.analyse(head); }
        catch (e) { console.warn("analyse", e); }
        const res = { track: t, url, ...info };
        ready.set(k, res);
        trimReady();
        return res;
      } catch (e) { console.warn("prefetch", e); return null; }
      finally { pending.delete(k); }
    })();
    pending.set(k, p);
    return p;
  }

  async function readStart(r, n) {      // the first n bytes of a response, then cancel the rest
    const reader = r.body.getReader(); const parts = []; let got = 0;
    while (got < n) { const { done, value } = await reader.read(); if (done) break; parts.push(value); got += value.length; }
    reader.cancel().catch(() => {});
    const out = new Uint8Array(Math.min(got, n)); let o = 0;
    for (const p of parts) { const c = p.subarray(0, out.length - o); out.set(c, o); o += c.length; if (o >= out.length) break; }
    return out.buffer;
  }

  function trimReady() {               // keep downloads for the current, next and last few tracks
    const keep = new Set([current, queue[0], ...back.slice(-3)].filter(Boolean).map(keyOf));
    for (const [k, r] of ready) if (!keep.has(k)) { if (r.url) URL.revokeObjectURL(r.url); ready.delete(k); }
  }

  function noteNoisy(t) {
    if (!t) return;
    played.add(keyOf(t));               // don't pick it again
    store.set("played", [...played]);
    const n = store.get("noisySkipped", 0) + 1;
    store.set("noisySkipped", n);
    renderSound();
  }

  function stopAll() { for (const el of [audio, plain]) { el.pause(); el.removeAttribute("src"); el.load(); } }

  // ------------------------------------------------------------------ playback
  async function playTrack(t, { record = true, fromRadio = true } = {}) {
    current = t;
    const k = keyOf(t);
    stopAll();
    if (canProcess(t)) {
      player = audio;
      if (!graph) { try { graph = new window.OFASound.Graph(audio); } catch (e) { console.warn(e); } }
      graph && graph.resume();
      let r = ready.get(k);
      if (!r && pending.has(k)) r = await Promise.race([pending.get(k), new Promise((ok) => setTimeout(ok, 6000))]);
      if (current !== t) return;
      if (r) {
        graph && graph.setGain(r.gainDb); graph && graph.setSide(r.side);
        audio.src = r.url || streamUrl(t);
        if (r.noisy && skipNoisy && fromRadio) { noteNoisy(t); status(`Skipped a very noisy recording (“${t.t}”).`); setTimeout(next, 50); return; }
      } else {
        // Not downloaded yet (skipping fast): stream it and measure in the background.
        graph && graph.setGain(0); graph && graph.setSide(0);
        audio.src = streamUrl(t);
        prepare(t, true).then((res) => {
          if (!res || current !== t) return;
          graph && graph.setGain(res.gainDb); graph && graph.setSide(res.side);
          if (res.noisy && skipNoisy && fromRadio) { noteNoisy(t); status(`Skipped a very noisy recording (“${t.t}”).`); next(); }
        });
      }
    } else {
      player = plain;
      plain.src = streamUrl(t);
    }
    player.play().catch(() => {});
    if (record) {
      played.add(k);
      store.set("played", [...played]);
      const { _k, ...plainTrack } = t;
      history.push({ t: plainTrack, at: Date.now() });
      if (history.length > 2000) history = history.slice(-2000);
      store.set("history", history);
    }
    queue = queue.filter((q) => keyOf(q) !== k);
    fillQueue();
    trimReady();
    renderNow();
    renderHistory();
  }

  function next() {
    if (!queue.length) fillQueue();
    const t = queue.shift();
    if (!t) { status(tracks.length ? "No tracks in your selection." : "Still loading the track list…"); return; }
    if (current) back.push(current);
    playTrack(t);
  }

  function prev() {
    if (player.currentTime > 5 || !back.length) { player.currentTime = 0; return; }
    if (current) queue.unshift(current);
    playTrack(back.pop(), { record: false, fromRadio: false });
  }

  function togglePlay() {
    if (!current) { next(); return; }
    graph && graph.resume();
    if (player.paused) player.play().catch(() => {}); else player.pause();
  }

  for (const el of [audio, plain]) {
    el.addEventListener("playing", () => { if (el !== player) return; errors = 0; setPlaying(true); status(""); });
    el.addEventListener("pause", () => { if (el === player) setPlaying(false); });
    el.addEventListener("waiting", () => { if (el === player) status("Buffering…"); });
    el.addEventListener("ended", () => { if (el === player) next(); });
    el.addEventListener("error", () => {
      if (el !== player || !current || !el.getAttribute("src")) return;
      errors++;
      if (errors >= 6) { status("Several tracks in a row wouldn't play. Check your connection and press play."); setPlaying(false); return; }
      status(`Couldn't play “${current.t}”, skipping…`);
      setTimeout(next, 900);
    });
  }

  function setPlaying(on) {
    $("eye").classList.toggle("on", on);
    $("art").classList.toggle("spin", on && $("art-img").hidden);
    $("play").setAttribute("aria-label", on ? "Pause" : "Play");
    $("play-icon").innerHTML = on
      ? '<path fill="currentColor" d="M6 5h4v14H6zm8 0h4v14h-4z"/>'
      : '<path fill="currentColor" d="M8 5v14l11-7z"/>';
    if ("mediaSession" in navigator) navigator.mediaSession.playbackState = on ? "playing" : "paused";
  }

  // ------------------------------------------------------------------ rendering
  function renderDial(t) {
    const scale = $("scale");
    if (!scale.dataset.ready) {
      for (let y = 1900; y <= 1970; y += 5) {
        const i = document.createElement("i");
        i.style.left = pos(y) + "%";
        i.style.height = y % 10 === 0 ? "14px" : "7px";
        scale.appendChild(i);
        if (y % 20 === 0) {
          const s = document.createElement("span");
          s.style.left = pos(y) + "%"; s.textContent = y;
          scale.appendChild(s);
        }
      }
      scale.dataset.ready = "1";
    }
    const needle = $("needle");
    needle.style.left = pos(t && t.y ? t.y : 1935) + "%";
    needle.innerHTML = t ? `<span class="yr">${t.y || "78"}</span>` : "";
    const g = selected.size === 0 ? "ALL GENRES"
      : selected.size === 1 ? (genreNames.get([...selected][0]) || "").toUpperCase() : `${selected.size} GENRES`;
    $("dial-text").textContent = `RADIO · ${g}`;
  }
  const pos = (y) => 4 + ((Math.min(1975, Math.max(1895, y)) - 1895) / 80) * 92;

  function renderNow() {
    const t = current;
    $("title").textContent = t ? t.t : "Press play to tune in";
    $("artist").textContent = t ? (t.a || "Unknown performer") : "";
    $("meta").textContent = t ? `${yearLabel(t)} · ${t.r || "Unknown"}` : "";
    $("genre").textContent = t ? (genreNames.get(t.g) || "") : "";
    $("source").textContent = t && t.u ? `from the ${sourceName(t)}` : "";
    const img = $("art-img");
    const art = t && artUrl(t);
    if (art) {
      img.hidden = true;
      img.onload = () => { img.hidden = false; $("art").classList.remove("spin"); };
      img.onerror = () => { img.hidden = true; };
      img.src = art;
      img.alt = `Label or cover of “${t.t}”`;
    } else { img.hidden = true; img.removeAttribute("src"); }
    const a = $("link-archive"), r = $("link-report");
    a.hidden = r.hidden = !t;
    if (t) { a.href = pageUrl(t); a.querySelector("span").textContent = `${sourceName(t)} page`; r.href = reportUrl(t); }
    renderDial(t);
    document.title = t ? `${t.t} · Old Folk Air` : "Old Folk Air: endless old folk radio";
    if ("mediaSession" in navigator && t) {
      navigator.mediaSession.metadata = new MediaMetadata({
        title: t.t, artist: t.a || "Unknown performer", album: `${yearLabel(t)} · ${t.r || ""}`,
        artwork: art ? [{ src: art, sizes: "512x512" }] : [{ src: "favicon.svg", sizes: "64x64", type: "image/svg+xml" }],
      });
    }
  }

  function trackRow(t, sub, onClick, isNow) {
    const li = document.createElement("li");
    if (isNow) li.className = "now-playing";
    const b = document.createElement("button");
    const tt = document.createElement("span"); tt.className = "t"; tt.textContent = t.t;
    const ss = document.createElement("span"); ss.className = "s"; ss.textContent = sub;
    b.append(tt, ss);
    b.addEventListener("click", onClick);
    li.appendChild(b);
    return li;
  }

  function renderUpNext() {
    const ol = $("upnext");
    ol.replaceChildren(...queue.map((t, n) => trackRow(t, `${t.a || "Unknown"} · ${yearLabel(t)}`, () => {
      if (current) back.push(current);
      queue.splice(n, 1);
      playTrack(t, { fromRadio: false });
      showTab("radio");
    })));
    if (!queue.length) ol.innerHTML = '<li class="empty">Nothing queued yet.</li>';
  }

  function renderHistory() {
    renderUpNext();
    const ol = $("played");
    const fmt = new Intl.DateTimeFormat(undefined, { day: "numeric", month: "short", hour: "2-digit", minute: "2-digit" });
    const rows = history.slice(-300).reverse().map(({ t, at }, n) =>
      trackRow(t, `${t.a || "Unknown"} · ${yearLabel(t)} · ${fmt.format(at)}`, () => {
        if (current) back.push(current);
        playTrack(t, { record: false, fromRadio: false });
        showTab("radio");
      }, n === 0 && current && keyOf(t) === keyOf(current)));
    ol.replaceChildren(...rows);
    if (!rows.length) ol.innerHTML = '<li class="empty">Nothing played yet.</li>';
  }

  function renderGenres() {
    const list = $("genre-list");
    const genres = [...(index?.genres || [])].sort((a, b) => a.name.localeCompare(b.name));
    list.replaceChildren(...genres.map((g) => {
      const li = document.createElement("li");
      const label = document.createElement("label");
      const box = document.createElement("input");
      box.type = "checkbox";
      box.checked = selected.size === 0 || selected.has(g.k);
      box.addEventListener("change", () => toggleGenre(g.k, box.checked));
      const name = document.createElement("span"); name.textContent = g.name;
      const n = document.createElement("span"); n.className = "n"; n.textContent = (g.n || 0).toLocaleString();
      label.append(box, name, n);
      li.appendChild(label);
      return li;
    }));
    const total = genres.reduce((s, g) => s + (selected.size === 0 || selected.has(g.k) ? g.n || 0 : 0), 0);
    $("genres-count").textContent = `${total.toLocaleString()} tracks selected`;
  }

  function toggleGenre(k, on) {
    const all = (index?.genres || []).map((g) => g.k);
    const sel = selected.size === 0 ? new Set(all) : new Set(selected);
    if (on) sel.add(k); else sel.delete(k);
    if (sel.size === 0) { renderGenres(); return; }      // keep at least one
    selected = sel.size === all.length ? new Set() : sel;
    store.set("genres", [...selected]);
    renderGenres();
    renderDial(current);
    fillQueue();
  }

  function renderSound() {
    const box = $("skip-noisy");
    if (!box) return;
    box.checked = skipNoisy;
    const n = store.get("noisySkipped", 0);
    $("noisy-count").textContent = n ? `${n.toLocaleString()} skipped so far in this browser.` : "";
  }

  // ------------------------------------------------------------------ tabs
  const TABS = ["radio", "genres", "history", "about"];
  // On a wide screen the radio, genres and history share one page.
  const wide = window.matchMedia("(min-width: 1000px)");
  let tab = "radio";
  function showTab(name) {
    if (!TABS.includes(name)) name = "radio";
    if (wide.matches && name !== "about") name = "radio";
    tab = name;
    const shown = wide.matches && name === "radio" ? ["radio", "genres", "history"] : [name];
    for (const t of TABS) $("tab-" + t).hidden = !shown.includes(t);
    document.querySelectorAll(".tabs a").forEach((a) => {
      if (a.dataset.tab === name) a.setAttribute("aria-current", "page"); else a.removeAttribute("aria-current");
    });
    if (shown.includes("history")) renderHistory();
    if (location.hash.slice(1) !== name) history_replace(name);
  }
  wide.addEventListener("change", () => showTab(tab));
  const history_replace = (name) => window.history.replaceState(null, "", name === "radio" ? location.pathname : "#" + name);
  window.addEventListener("hashchange", () => showTab(location.hash.slice(1)));
  document.querySelectorAll("[data-tab]").forEach((a) => a.addEventListener("click", (e) => {
    e.preventDefault(); showTab(a.dataset.tab);
  }));

  // ------------------------------------------------------------------ controls
  $("play").addEventListener("click", togglePlay);
  $("next").addEventListener("click", next);
  $("prev").addEventListener("click", prev);
  $("genres-all").addEventListener("click", () => {
    selected = new Set(); store.set("genres", []); renderGenres(); renderDial(current); fillQueue();
  });
  $("history-clear").addEventListener("click", () => {
    if (!confirm("Clear your listening history? Tracks you've heard can then come up again.")) return;
    history = []; played.clear(); store.set("history", []); store.set("played", []);
    renderHistory();
  });
  if ("mediaSession" in navigator) {
    navigator.mediaSession.setActionHandler("play", () => { graph && graph.resume(); player.play().catch(() => {}); });
    navigator.mediaSession.setActionHandler("pause", () => player.pause());
    navigator.mediaSession.setActionHandler("nexttrack", next);
    navigator.mediaSession.setActionHandler("previoustrack", prev);
  }
  document.addEventListener("keydown", (e) => {
    if (e.target.closest("input, textarea")) return;
    if (e.code === "Space" && !e.target.closest("button, a")) { e.preventDefault(); togglePlay(); }
  });

  $("skip-noisy").addEventListener("change", (e) => { skipNoisy = e.target.checked; store.set("skipNoisy", skipNoisy); });
  renderSound();
  showTab(location.hash.slice(1));
  load();
})();

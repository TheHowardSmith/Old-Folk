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
    if (p === "0") return null;               // no cover or label scan: the drawn 78
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
  let selected = new Set(store.get("genres", []));        // empty = all styles
  // Genres that were split (Nov 2026): a saved choice of the old one keeps the new ones.
  for (const [flag, from, to] of [["euroSplit", "european", ["nordic", "eastern_europe", "mediterranean"]],
                                  ["asiaSplit", "asia", ["mideast"]],
                                  ["countrySplit", "oldtime", ["country"]], ["countrySplit2", "american", ["country"]],
                                  ["classicalSplit", "southasia", ["classical"]], ["classicalSplit2", "asia", ["classical"]],
                                  ["classicalSplit3", "mideast", ["classical"]]]) {
    if (selected.has(from) && !store.get(flag, false)) {
      to.forEach((k) => selected.add(k));
      store.set("genres", [...selected]);
    }
    store.set(flag, true);
  }
  // Regions chosen on the map (keys of index.regions); empty = everywhere.
  let regionSel = new Set(store.get("regions", []));
  // Genres became styles and places became the map (Nov 2026): a saved choice of a regional genre
  // becomes that region on the map. A mix of styles and regions can't be kept exactly, so it starts
  // again from everything.
  if (!store.get("stylesSplit", false)) {
    const OLD = { american: ["north_america"], british: ["british_isles"], european: ["western_europe"],
      nordic: ["nordic"], eastern_europe: ["eastern_europe"], mediterranean: ["southern_europe"],
      latin: ["latin_america"], african: ["africa"], mideast: ["middle_east"],
      asia: ["east_asia", "southeast_asia", "central_asia"], southasia: ["south_asia"], pacific: ["pacific"], world: [] };
    const regional = [...selected].filter((k) => k in OLD), styles = [...selected].filter((k) => !(k in OLD));
    if (regional.length && styles.length) { selected = new Set(); regionSel = new Set(); }
    else if (regional.length) { selected = new Set(); regionSel = new Set(regional.flatMap((k) => OLD[k])); }
    store.set("genres", [...selected]); store.set("regions", [...regionSel]); store.set("stylesSplit", true);
  }
  const regionKeyOf = new Map();                           // track region ("r") -> map region key
  const regionNames = new Map();
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

  const styleOk = (t) => selected.size === 0 || selected.has(t.g);
  const regionOk = (t) => regionSel.size === 0 || !regionKeyOf.size || regionSel.has(regionKeyOf.get(t.r));
  const inSelection = (t) => styleOk(t) && regionOk(t);
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
    for (const r of index.regions || []) { regionNames.set(r.k, r.name); for (const n of r.r || []) regionKeyOf.set(n, r.k); }
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
    renderGenres();
    status(current ? "" : `${tracks.length.toLocaleString()} tracks ready. Press play to tune in.`);
    renderHistory();
    for (const s of rest) {
      await loadShard(s);
      if (queue.length < UP_NEXT) fillQueue();
    }
    if (!current) status(`${tracks.length.toLocaleString()} recordings. Press play to tune in.`);
    renderHistory();
    renderGenres();                                        // counts now cover the whole list
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
  // An album (archive item) is chosen first, then a song on it, so a 300-track compilation doesn't
  // crowd out single 78s: an album's chance grows only with the square root of its unplayed tracks.
  // Albums heard in the last 30 plays, or already queued, are skipped while there are others.
  const RECENT_ALBUMS = 30;
  function pick() {
    const taken = new Set(queue.map(keyOf));
    if (current) taken.add(keyOf(current));
    const avoid = new Set(history.slice(-RECENT_ALBUMS).map((h) => h.t && h.t.i));
    for (const q of queue) avoid.add(q.i);
    if (current) avoid.add(current.i);
    const byItem = new Map();
    for (const t of tracks) {
      if (!inSelection(t) || played.has(keyOf(t)) || taken.has(keyOf(t))) continue;
      let list = byItem.get(t.i);
      if (!list) byItem.set(t.i, (list = []));
      list.push(t);
    }
    if (byItem.size) {
      let albums = [...byItem.entries()].filter(([i]) => !avoid.has(i)).map(([, l]) => l);
      if (!albums.length) albums = [...byItem.values()];
      const weights = albums.map((l) => Math.sqrt(l.length));
      let r = Math.random() * weights.reduce((a, b) => a + b, 0);
      let album = albums[albums.length - 1];
      for (let k = 0; k < albums.length; k++) { r -= weights[k]; if (r <= 0) { album = albums[k]; break; } }
      return album[Math.floor(Math.random() * album.length)];
    }
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
    $("dial").classList.toggle("lit", on);
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
      for (let y = 1898; y <= 1972; y += 1) {
        const i = document.createElement("i");
        i.style.left = pos(y) + "%";
        i.style.height = y % 10 === 0 ? "15px" : y % 5 === 0 ? "9px" : "4px";
        if (y % 10 === 0) i.className = "m";
        scale.appendChild(i);
        if (y % 10 === 0 && y >= 1900 && y <= 1970) {
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
    const style = selected.size === 0 ? "" : selected.size === 1 ? genreNames.get([...selected][0]) || "" : `${selected.size} styles`;
    const where = regionSel.size === 0 || !regionKeyOf.size ? ""
      : regionSel.size === 1 ? regionNames.get([...regionSel][0]) || "" : `${regionSel.size} regions`;
    const parts = [where, style].filter(Boolean);
    $("dial-text").textContent = `RADIO · ${(parts.length ? parts.join(" · ") : "all styles").toUpperCase()}`;
    // The year rides above the needle; near the left end it would run into the station name,
    // so there it sits beside the needle instead. (Measured at the needle's destination, not
    // mid-slide.)
    const yr = needle.querySelector(".yr");
    if (yr) {
      const sc = $("scale").getBoundingClientRect(), tx = $("dial-text").getBoundingClientRect(), r = yr.getBoundingClientRect();
      const x = sc.left + (parseFloat(needle.style.left) / 100) * sc.width;
      const clash = r.top < tx.bottom + 2 && r.bottom > tx.top - 2 && x - r.width / 2 - 4 < tx.right && x + r.width / 2 + 4 > tx.left;
      yr.classList.toggle("side", clash);
    }
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
    const box = $("art");
    box.classList.add("bare");                 // the drawn 78 until a picture loads
    if (art) {
      img.hidden = true;
      img.dataset.want = art;
      img.onload = () => { if (img.dataset.want !== art) return; img.hidden = false; box.classList.remove("spin", "bare"); };
      img.onerror = () => { img.hidden = true; };
      img.src = art;
      img.alt = `Label or cover of “${t.t}”`;
    } else { img.hidden = true; img.removeAttribute("src"); }
    const a = $("link-archive"), r = $("link-report");
    a.hidden = r.hidden = !t;
    if (t) {
      a.href = pageUrl(t); r.href = reportUrl(t);
      a.title = `${sourceName(t)} page`;
      // On phones the links share one row with the app link, so the label is short.
      a.querySelector("span").textContent = matchMedia("(max-width: 640px)").matches ? "Archive page" : `${sourceName(t)} page`;
    }
    renderDial(t);
    renderPlayhead();
    queueFit();
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

  // "3:05" from seconds (or "1:02:05").
  function clock(sec) {
    sec = Math.max(0, Math.floor(sec || 0));
    const h = Math.floor(sec / 3600), m = Math.floor((sec % 3600) / 60), x = String(sec % 60).padStart(2, "0");
    return h ? `${h}:${String(m).padStart(2, "0")}:${x}` : `${m}:${x}`;
  }
  // Track lengths for history entries saved before the list carried them.
  var lengthByKey = null;
  function lengthOf(t) {
    if (t.d) return t.d;
    if (!lengthByKey || lengthByKey.size < tracks.length) {
      lengthByKey = new Map();
      for (const x of tracks) if (x.d) lengthByKey.set(keyOf(x), x.d);
    }
    return lengthByKey.get(keyOf(t)) || 0;
  }

  function renderHistory() {
    const ol = $("played");
    const fmt = new Intl.DateTimeFormat(undefined, { day: "numeric", month: "short", hour: "2-digit", minute: "2-digit" });
    const rows = history.slice(-300).reverse().map(({ t, at }, n) =>
      trackRow(t, [t.a || "Unknown", yearLabel(t), lengthOf(t) ? clock(lengthOf(t)) : null, fmt.format(at)].filter(Boolean).join(" · "), () => {
        if (current) back.push(current);
        playTrack(t, { record: false, fromRadio: false });
        showTab("radio");
      }, n === 0 && current && keyOf(t) === keyOf(current)));
    ol.replaceChildren(...rows);
    if (!rows.length) ol.innerHTML = '<li class="empty">Nothing played yet.</li>';
  }

  function genreRow(name, count, checked, onChange, cls) {
    const li = document.createElement("li");
    if (cls) li.className = cls;
    const label = document.createElement("label");
    const box = document.createElement("input");
    box.type = "checkbox";
    box.checked = checked;
    box.addEventListener("change", () => onChange(box.checked));
    const nm = document.createElement("span"); nm.textContent = name;
    const n = document.createElement("span"); n.className = "n"; n.textContent = (count || 0).toLocaleString();
    label.append(box, nm, n);
    li.appendChild(label);
    return li;
  }

  // "All genres" is ticked when nothing in particular is chosen; ticking a genre
  // unticks it, and unticking the last genre ticks it again.
  function renderGenres() {
    const list = $("genre-list");
    const genres = index?.genres || [];                    // styles, in the builder's order
    const regions = (index?.regions || []).filter((r) => r.n);
    // Counts follow the other filter: styles within the chosen regions, regions within the chosen styles.
    const byStyle = new Map(), byRegion = new Map();
    let total = 0;
    for (const t of tracks) {
      const ro = regionOk(t), so = styleOk(t);
      if (ro) byStyle.set(t.g, (byStyle.get(t.g) || 0) + 1);
      if (so) { const k = regionKeyOf.get(t.r); if (k) byRegion.set(k, (byRegion.get(k) || 0) + 1); }
      if (ro && so) total++;
    }
    const loaded = tracks.length > 0;
    const styleN = (g) => (loaded ? byStyle.get(g.k) || 0 : g.n);
    const all = genreRow("All styles", loaded ? [...byStyle.values()].reduce((a, b) => a + b, 0) : index?.n || 0,
      selected.size === 0, (on) => { if (on) setSelection(new Set()); else renderGenres(); }, "all");
    list.replaceChildren(all, ...genres.map((g) => genreRow(g.name, styleN(g), selected.has(g.k), (on) => toggleGenre(g.k, on))));

    const chips = $("region-chips");
    const chip = (label, n, on, onClick, cls) => {
      const li = document.createElement("li");
      const b = document.createElement("button");
      b.type = "button"; b.setAttribute("aria-pressed", String(on)); if (cls) b.className = cls;
      b.append(label);
      if (n != null) { const s = document.createElement("span"); s.className = "n"; s.textContent = n.toLocaleString(); b.append(s); }
      b.addEventListener("click", onClick);
      li.append(b);
      return li;
    };
    chips.replaceChildren(
      chip("Everywhere", null, regionSel.size === 0, () => setRegions(new Set()), "all"),
      ...regions.map((r) => chip(r.name, loaded ? byRegion.get(r.k) || 0 : r.n, regionSel.has(r.k), () => toggleRegion(r.k))));
    $("atlas").hidden = !regions.length;
    if (regions.length) loadAtlas().then(updateAtlas);
    $("genres-count").textContent = `${(loaded ? total : index?.n || 0).toLocaleString()} tracks selected`;
  }

  function toggleGenre(k, on) {
    const sel = new Set(selected);
    if (on) sel.add(k); else sel.delete(k);
    setSelection(sel);                                      // none left = all styles
  }

  function setSelection(sel) {
    selected = sel;
    store.set("genres", [...selected]);
    renderGenres();
    renderDial(current);
    fillQueue();
  }

  function toggleRegion(k) {
    const sel = new Set(regionSel);
    if (sel.has(k)) sel.delete(k); else sel.add(k);
    setRegions(sel);                                        // none left = everywhere
  }

  function setRegions(sel) {
    regionSel = sel;
    store.set("regions", [...regionSel]);
    renderGenres();
    renderDial(current);
    fillQueue();
  }

  // The map: drawn once from map.json (made by builder/scripts/make_map.py), then only its classes change.
  var atlasReady = null;
  function loadAtlas() {
    if (atlasReady) return atlasReady;
    atlasReady = fetch("map.json").then((r) => r.json()).then((m) => {
      const NS = "http://www.w3.org/2000/svg";
      const svg = document.createElementNS(NS, "svg");
      svg.setAttribute("viewBox", `0 0 ${m.w} ${m.h}`);
      const add = (parent, tag, attrs) => {
        const el = document.createElementNS(NS, tag);
        for (const [k, v] of Object.entries(attrs)) el.setAttribute(k, v);
        parent.appendChild(el); return el;
      };
      add(svg, "rect", { class: "sea", width: m.w, height: m.h });
      const grat = add(svg, "g", { class: "grat" });
      const y = (lat) => ((80 - lat) / 136) * m.h, x = (lon) => ((lon + 180) / 360) * m.w;
      for (let lat = 60; lat > -60; lat -= 30) add(grat, "line", { x1: 0, x2: m.w, y1: y(lat), y2: y(lat) });
      for (let lon = -150; lon < 180; lon += 30) add(grat, "line", { y1: 0, y2: m.h, x1: x(lon), x2: x(lon) });
      if (m.land) add(svg, "path", { class: "land", d: m.land });
      for (const r of m.regions) {
        const path = add(svg, "path", { class: "rg", d: r.d, "data-k": r.k });
        const title = document.createElementNS(NS, "title"); title.textContent = regionNames.get(r.k) || r.k; path.appendChild(title);
      }
      for (const r of m.regions) {
        if (r.ax != null) add(svg, "line", { class: "lead", x1: r.x, y1: r.y + 6, x2: r.ax, y2: r.ay });
        const t = add(svg, "text", { x: r.x, y: r.y, "data-k": r.k });
        t.textContent = (regionNames.get(r.k) || r.k).toUpperCase();
      }
      svg.addEventListener("click", (e) => {
        const k = e.target.closest("[data-k]")?.dataset.k;
        if (k && regionNames.has(k)) toggleRegion(k);
      });
      $("atlas").replaceChildren(svg);
    }).catch((e) => { console.warn("map", e); $("atlas").hidden = true; });
    return atlasReady;
  }

  function updateAtlas() {
    const any = regionSel.size > 0;
    document.querySelectorAll("#atlas [data-k]").forEach((el) => {
      const on = regionSel.has(el.dataset.k);
      el.classList.toggle("on", on);
      el.classList.toggle("off", any && !on && el.tagName === "path");
    });
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
  // Fit the radio to the screen: the dial, cover, track details, knobs and links must all be
  // visible without scrolling. The cover gives way first (down to three quarters of its size), then the
  // dial and knobs get smaller (fit-compact, fit-tight), and as a last resort the cover is hidden.
  function fitRadio() {
    const tabEl = $("tab-radio");
    if (!tabEl || tabEl.hidden) return;
    const art = $("art"), links = tabEl.querySelector(".actions"), knobs = tabEl.querySelector(".grille-cab");
    const bar = document.querySelector(".tabs");
    const barTop = bar && getComputedStyle(bar).position === "fixed" ? bar.getBoundingClientRect().top : innerHeight;
    const limit = barTop - 8;                                     // viewport y everything must end above
    const bottom = (el) => el.getBoundingClientRect().bottom + scrollY;
    const body = document.body;
    const levels = [[], ["fit-compact"], ["fit-compact", "fit-tight"]];
    for (let i = 0; i < levels.length; i++) {
      body.classList.remove("fit-compact", "fit-tight");
      body.classList.add(...levels[i]);
      art.classList.remove("gone");
      art.style.width = "";
      const natural = art.getBoundingClientRect().width;
      const stacked = art.getBoundingClientRect().bottom <= knobs.getBoundingClientRect().top + 1;
      const ends = Math.max(bottom(links), bottom(art));
      const over = ends - limit;
      if (over <= 0) return;
      const width = stacked ? natural - over : natural - (bottom(art) - limit);
      const controlsFit = stacked || bottom(links) <= limit;
      const last = i === levels.length - 1;
      if (controlsFit && (width >= natural * 0.75 || (last && width >= 72))) {
        art.style.width = Math.floor(width) + "px";
        return;
      }
      if (last) {
        if (width >= 72) art.style.width = Math.floor(width) + "px";
        else art.classList.add("gone");
        return;
      }
    }
  }
  var fitQueued = false;   // var/function: renderNow may call queueFit before this line runs
  function queueFit() { if (!fitQueued) { fitQueued = true; requestAnimationFrame(() => { fitQueued = false; fitRadio(); }); } }
  window.addEventListener("resize", queueFit);
  window.addEventListener("orientationchange", queueFit);
  if (document.fonts && document.fonts.ready) document.fonts.ready.then(queueFit);
  // Track details, status lines and links change height as tracks load: refit when they do.
  if ("ResizeObserver" in window) {
    const ro = new ResizeObserver(queueFit);
    document.querySelectorAll("#tab-radio .now, #tab-radio .actions").forEach((el) => ro.observe(el));
  }

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
    if (shown.includes("radio")) queueFit();
    if (location.hash.slice(1) !== name) history_replace(name);
  }
  wide.addEventListener("change", () => showTab(tab));
  const history_replace = (name) => window.history.replaceState(null, "", name === "radio" ? location.pathname : "#" + name);
  window.addEventListener("hashchange", () => showTab(location.hash.slice(1)));
  document.querySelectorAll("[data-tab]").forEach((a) => a.addEventListener("click", (e) => {
    e.preventDefault(); showTab(a.dataset.tab);
  }));

  // ------------------------------------------------------------------ play head
  // Shows how far into the track the radio is; drag to jump. Uses whichever player is playing.
  var seeking = false;   // var / function: renderNow can call renderPlayhead before this line runs
  function trackLength() {
    const d = player.duration;
    return Number.isFinite(d) && d > 0 ? d : (current && current.d) || 0;
  }
  function renderPlayhead() {
    const bar = $("ph-bar");
    const dur = trackLength();
    const pos = seeking ? (bar.value / 1000) * dur : player.currentTime || 0;
    bar.disabled = !current || !dur;
    if (!seeking) bar.value = dur ? Math.round((pos / dur) * 1000) : 0;
    bar.style.setProperty("--pct", `${bar.value / 10}%`);
    $("ph-pos").textContent = current ? clock(pos) : "";
    $("ph-dur").textContent = current && dur ? clock(dur) : "";
  }
  $("ph-bar").addEventListener("input", () => { seeking = true; renderPlayhead(); });
  $("ph-bar").addEventListener("change", () => {
    const bar = $("ph-bar"), dur = trackLength();
    if (dur) player.currentTime = (bar.value / 1000) * dur;
    seeking = false; renderPlayhead();
  });
  for (const el of [audio, plain]) for (const ev of ["timeupdate", "durationchange", "loadedmetadata", "emptied"]) el.addEventListener(ev, renderPlayhead);
  setInterval(renderPlayhead, 1000);

  // ------------------------------------------------------------------ appearance
  // Light, Dark, or Auto (follows the device); applied before paint by a line in index.html.
  function applyTheme(mode) {
    if (mode === "light" || mode === "dark") document.documentElement.dataset.theme = mode;
    else delete document.documentElement.dataset.theme;
  }
  const themeNow = store.get("theme", "system");
  document.querySelectorAll('input[name="theme"]').forEach((r) => {
    r.checked = r.value === themeNow;
    r.addEventListener("change", () => { if (r.checked) { store.set("theme", r.value); applyTheme(r.value); } });
  });
  applyTheme(themeNow);

  // ------------------------------------------------------------------ controls
  $("play").addEventListener("click", togglePlay);
  $("next").addEventListener("click", next);
  $("prev").addEventListener("click", prev);
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

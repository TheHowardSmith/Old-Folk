/* Old Folk Air: sound processing for the web radio, ported from the Android app.
 *
 * analyse(arrayBuffer) decodes the first ~90 s of a downloaded track and returns
 *   gainDb    playback gain that brings its gated loudness to -20 dBFS (clamped
 *             -15..+10 dB), as in the app's Levels.kt;
 *   side      0 = normal, 1 = only the left channel has sound, 2 = only the right
 *             (copied to both speakers, like ChannelFixer.kt);
 *   noisy     heavy surface noise by the app's NoiseCheck.kt rule (hiss band
 *             4-10 kHz vs music band 150-3000 Hz >= -16 dB, flatness >= 0.45).
 * Graph(audioEl) routes an <audio> element through gain -> channel fix ->
 * limiter (for thumps and pops on unmastered transfers).
 */
(() => {
  "use strict";
  const TARGET_DB = -20, MIN_GAIN = -15, MAX_GAIN = 10;
  const RATIO_DB = -16.0, FLATNESS = 0.45, NOISE_SECONDS = 30;
  const N = 2048, HOP = 1024;

  let decoder = null;
  function decoderCtx() {
    if (!decoder) {
      const C = window.OfflineAudioContext || window.webkitOfflineAudioContext;
      decoder = new C(2, 44100, 44100);
    }
    return decoder;
  }

  function gatedLoudness(ms) {
    const db = (v) => 10 * Math.log10(Math.max(v, 1e-12));
    const abs = ms.filter((v) => db(v) > -60);
    if (!abs.length) return null;
    const mean = abs.reduce((a, b) => a + b, 0) / abs.length;
    const gated = abs.filter((v) => db(v) > db(mean) - 15);
    const use = gated.length ? gated : abs;
    return db(use.reduce((a, b) => a + b, 0) / use.length);
  }

  function windowsOf(chans, len, size) {
    const out = [];
    for (let s = 0; s + size <= len; s += size) {
      let sum = 0;
      for (const c of chans) for (let i = s; i < s + size; i++) sum += c[i] * c[i];
      out.push(sum / (size * chans.length));
    }
    return out;
  }

  function fft(re, im) {
    const n = re.length;
    for (let i = 1, j = 0; i < n; i++) {
      let bit = n >> 1;
      for (; j & bit; bit >>= 1) j ^= bit;
      j ^= bit;
      if (i < j) { [re[i], re[j]] = [re[j], re[i]]; [im[i], im[j]] = [im[j], im[i]]; }
    }
    for (let len = 2; len <= n; len <<= 1) {
      const ang = -2 * Math.PI / len, wr = Math.cos(ang), wi = Math.sin(ang);
      for (let i = 0; i < n; i += len) {
        let cr = 1, ci = 0;
        for (let k = 0; k < len / 2; k++) {
          const a = i + k, b = a + len / 2;
          const tr = re[b] * cr - im[b] * ci, ti = re[b] * ci + im[b] * cr;
          re[b] = re[a] - tr; im[b] = im[a] - ti; re[a] += tr; im[a] += ti;
          const ncr = cr * wr - ci * wi; ci = cr * wi + ci * wr; cr = ncr;
        }
      }
    }
  }

  const median = (v) => {
    const s = [...v].sort((a, b) => a - b), m = s.length >> 1;
    return s.length % 2 ? s[m] : (s[m - 1] + s[m]) / 2;
  };

  function noiseCheck(x, rate) {
    if (x.length < N * 4) return null;
    const win = new Float64Array(N).map((_, i) => 0.5 - 0.5 * Math.cos(2 * Math.PI * i / (N - 1)));
    const binHz = rate / N;
    const bin = (hz) => Math.max(0, Math.min(N / 2, Math.floor(hz / binHz)));
    const m0 = bin(150), m1 = bin(3000), n0 = bin(4000), n1 = bin(Math.min(10000, rate / 2 - binHz));
    if (n1 - n0 < 8) return null;
    const music = [], ratios = [], flats = [];
    const re = new Float64Array(N), im = new Float64Array(N);
    for (let start = 0; start + N <= x.length; start += HOP) {
      for (let i = 0; i < N; i++) { re[i] = x[start + i] * win[i]; im[i] = 0; }
      fft(re, im);
      let mu = 0, no = 0, logSum = 0;
      for (let k = m0; k < m1; k++) mu += re[k] * re[k] + im[k] * im[k];
      for (let k = n0; k < n1; k++) { const p = re[k] * re[k] + im[k] * im[k] + 1e-12; no += p; logSum += Math.log(p); }
      const count = n1 - n0;
      music.push(mu); ratios.push((no + 1e-12) / (mu + 1e-12)); flats.push(Math.exp(logSum / count) / (no / count));
    }
    // Ignore the quietest 30% of frames (gaps, run-in and run-out grooves).
    const gate = [...music].sort((a, b) => a - b)[Math.floor(music.length * 0.3)];
    let keep = music.map((m, i) => (m > gate ? i : -1)).filter((i) => i >= 0);
    if (!keep.length) keep = music.map((_, i) => i);
    const ratioDb = 10 * Math.log10(median(keep.map((i) => ratios[i])));
    const flatness = median(flats);
    return { ratioDb, flatness, noisy: ratioDb >= RATIO_DB && flatness >= FLATNESS };
  }

  async function analyse(buf) {
    const audio = await decoderCtx().decodeAudioData(buf);
    const rate = audio.sampleRate;
    const len = Math.min(audio.length, rate * 90);
    const chans = [];
    for (let c = 0; c < Math.min(2, audio.numberOfChannels); c++) chans.push(audio.getChannelData(c).subarray(0, len));
    // One-sided transfers: measure the loud channel only.
    let side = chans.length === 1 ? 1 : 0;
    if (chans.length === 2) {
      let eL = 0, eR = 0;
      for (let i = 0; i < len; i++) { eL += chans[0][i] ** 2; eR += chans[1][i] ** 2; }
      if (eR < eL * 1e-3) side = 1; else if (eL < eR * 1e-3) side = 2;
    }
    const measured = side === 1 ? [chans[0]] : side === 2 ? [chans[1]] : chans;
    const loud = gatedLoudness(windowsOf(measured, len, Math.round(rate * 0.4)));
    const gainDb = loud == null ? 0 : Math.max(MIN_GAIN, Math.min(MAX_GAIN, TARGET_DB - loud));
    const nlen = Math.min(len, rate * NOISE_SECONDS);
    const mono = new Float32Array(nlen);
    for (const c of chans) for (let i = 0; i < nlen; i++) mono[i] += c[i] / chans.length;
    const noise = nlen >= rate * 8 ? noiseCheck(mono, rate) : null;
    return { gainDb, side, noisy: !!(noise && noise.noisy), ratioDb: noise?.ratioDb, flatness: noise?.flatness };
  }

  class Graph {
    constructor(el) {
      const C = window.AudioContext || window.webkitAudioContext;
      this.ctx = new C();
      this.src = this.ctx.createMediaElementSource(el);
      // Up-mix first so mono files reach both speakers before they're measured.
      this.up = this.ctx.createGain();
      Object.assign(this.up, { channelCount: 2, channelCountMode: "explicit", channelInterpretation: "speakers" });
      this.split = this.ctx.createChannelSplitter(2);
      this.merge = this.ctx.createChannelMerger(2);
      this.gain = this.ctx.createGain();
      this.limit = this.ctx.createDynamicsCompressor();
      this.limit.threshold.value = -4; this.limit.knee.value = 0; this.limit.ratio.value = 20;
      this.limit.attack.value = 0.002; this.limit.release.value = 0.08;
      this.src.connect(this.up).connect(this.split);
      this.merge.connect(this.gain).connect(this.limit).connect(this.ctx.destination);
      this.setSide(0);
    }
    setSide(side) {
      this.split.disconnect();
      const a = side === 2 ? 1 : 0, b = side === 1 ? 0 : 1;
      this.split.connect(this.merge, a, 0);
      this.split.connect(this.merge, b, 1);
    }
    setGain(db) {
      const t = this.ctx.currentTime;
      this.gain.gain.cancelScheduledValues(t);
      this.gain.gain.setTargetAtTime(Math.pow(10, db / 20), t, 0.05);
    }
    resume() { if (this.ctx.state !== "running") return this.ctx.resume().catch(() => {}); }
  }

  window.OFASound = { analyse, Graph, noiseCheck, gatedLoudness };
})();

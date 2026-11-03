# Website (oldfolkair.com)

Live at https://oldfolkair.com (GitHub Pages, DNS at Cloudflare with the proxy off, HTTPS by GitHub).

The site is the web version of the radio: a single static page (`index.html`,
`style.css`, `app.js`, no build step and no external libraries) with the player,
Genres, History and About. It loads the track list from `data/`, which the
*Website* workflow (`.github/workflows/pages.yml`) copies in from the `tracklist`
release at deploy time (browsers can't read release downloads directly); the
track-list build starts that workflow after each publish. Each recording plays
straight from the archive that holds it, and only the next track is fetched
ahead. Genre choice, the no-repeat list and history are kept in the browser's
localStorage.

To try it locally: copy a published `index.json` and its `s-*.json`/`x-*.json`
shards into `site/data/` and run `python3 -m http.server --directory site`.

## One-time setup

1. **Turn on Pages:** repository **Settings → Pages → Build and deployment →
   Source: GitHub Actions**. The site then appears at
   https://thehowardsmith.github.io/Old-Folk/ after the next run of the
   *Website* workflow (Actions tab → Website → Run workflow).
2. **Buy the domain** `oldfolkair.com` (e.g. at Cloudflare Registrar, about
   US$10–15 a year at cost, or any registrar).
3. **DNS records** at the registrar (or Cloudflare DNS, with the proxy *off* /
   "DNS only" for these records):
   - `A` records for `oldfolkair.com` → `185.199.108.153`, `185.199.109.153`,
     `185.199.110.153`, `185.199.111.153`
   - `CNAME` record for `www` → `thehowardsmith.github.io`
4. **Custom domain:** Settings → Pages → Custom domain: `oldfolkair.com` →
   Save, wait for the DNS check, then tick **Enforce HTTPS**.
   (Optionally verify the domain under your GitHub profile settings → Pages, so
   nobody else can claim it.)
5. **Optional email forwarding:** Cloudflare Email Routing can forward e.g.
   `archives@oldfolkair.com` to your Gmail for free.

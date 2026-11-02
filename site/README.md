# Website (oldfolkair.com)

A single static page, published to GitHub Pages by `.github/workflows/pages.yml`
whenever `site/` (or the app screenshots) change on `main`. The screenshots in
`img/` are copied from the app's Paparazzi snapshots at build time.

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

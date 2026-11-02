# Brief: country-by-country sweep for online folk/traditional recording archives

Context: "Old Folk Air" is a free, ad-free, non-commercial, open-source Android app (NZ-based) that streams an endless random stream of folk, traditional and field recordings recorded or released in 1970 or earlier. It streams audio straight from the archives that host them (no re-hosting), credits the archive on every track with a link back, and plays through an encrypted cache so nothing can be downloaded or redistributed. Rights rule: recordings must be public domain or openly licensed to the best of our knowledge (archive's own rights statement counts), and anything restricted, sacred or culturally sensitive is excluded or flagged.

Already known (do not re-research, just note if you find a NEW sub-collection of one): Internet Archive (incl. Great 78, 78rpm_nsfa), Library of Congress, Europeana, Wikimedia Commons, UCSB Cylinder Archive, DAHR, Gallica/BnF, British Library Sounds and Endangered Archives Programme, Smithsonian Folkways, Lomax/ACE, Ngā Taonga, Discografia Brasileira (IMS), Finna (Finland), CREM-CNRS, NDL Rekion (Japan), Max Hunter Collection, Memoria Chilena, FAU Judaica, National Library of Israel, Library and Archives Canada Virtual Gramophone, FIU Díaz-Ayala, Radford Burmese 78s, Golha, Strachwitz Frontera, AMAR, ILAM/Hugh Tracey, Austrian Mediathek, Svenskt visarkiv, Norway NL, dLib.si, Irama Nusantara, Archive of Indian Music, ARCE, Phonogrammarchiv Vienna, PARADISEC, Kounadis rebetiko archive, Biblioteca Digital Hispánica, BN Digital Brazil.

Method: go country by country through your region (every country), and for large or diverse countries also by state/province and by notable peoples/cultures/languages. For each, look for: national library and national archives sound collections, national broadcaster archives (radio sound archives), university ethnomusicology archives and digitisation projects, museum collections, community/heritage projects for specific peoples, 78 rpm digitisation projects and collector sites, and relevant Internet Archive collections from that country (give collection ids). Search in the local language(s) too (e.g. 民謡 / 民歌 / 아리랑 / लोक संगीत / musique traditionnelle / música tradicional / الموسيقى التراثية).

For every archive found, record concretely (check real pages; WebSearch/WebFetch, and curl from Bash to test whether pages/audio answer):
1. Content: peoples/genres, rough number of recordings, date range.
2. Audio access: direct streamable files (MP3/Ogg/M4A) without login? Or player/streaming only, SoundCloud/YouTube only, clips only, on-site only, sign-in?
3. Rights: per-item rights statements (public domain, CC0, CC BY, CC BY-NC, "no known restrictions") or blanket terms; is non-commercial streaming by a free app allowed? Any community/sacred restrictions?
4. Machine access: API, OAI-PMH, IIIF, sitemap, HTML only; key needed?
5. Verdict: "good fit" (stream directly), "ask permission" (worth an outreach email; give the institution's public contact address or form), or "not usable" (one-line reason).

Rules: the sandbox network policy blocks some hosts. If a host is blocked or behind a bot check/CAPTCHA/login, say so and move on: never try to get around bot checks, logins or blocks, and don't change user agents to evade blocks. Be polite (a few requests per site). Be honest about uncertainty: mark items as verified, search-only, or blocked. Don't modify anything in /home/user/Old-Folk.

Output: write a Markdown report to the file path given in your task. Put first a ranked table of the archives found (good fits first, then ask-permission, then not usable), then a country checklist showing every country covered with a one-line result (including "nothing found online"), then per-archive notes with source URLs. Return a concise summary: the good fits and the top ask-permission candidates with key facts, plus countries where nothing was found.

## Second pass (follow-up sessions)

The first pass (sweep-*.md in this folder) ran out of web searches partway
through. Before searching, read docs/archive-research/README.md and the sweep-*.md
report for your region so you don't repeat archives already assessed; treat every
archive named there as known. Then cover only the gaps in your task.

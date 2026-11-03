# Old Folk Air: regional and national sound archives (beyond IA / LoC)

Research date: 2 November 2026. Scope: archives holding traditional, folk or popular-traditional music recorded or released in 1970 or earlier, outside the sources already known (IA, LoC, Europeana, Wikimedia Commons, UCSB Cylinders, Gallica, BL Sounds, Smithsonian Folkways, Lomax/ACE, Ngā Taonga).

## How this was checked, and its limits

- **Verified** means I fetched the page, API response or audio file myself with `curl` from the sandbox: a real HTTP 200/206 with `audio/mpeg`, or a JSON/OAI response.
- **Search-only** means the claim comes from web search snippets (news, library guides, papers). I did not confirm it on the archive's own site.
- **Blocked** means the sandbox could not reach the host. Causes were egress proxy refusal, DNS failure, a Cloudflare or AWS WAF bot challenge, or an incomplete TLS chain. The `WebFetch` tool was restricted to an allow-list, so most checks were done with plain `curl`. I did not try to get past any bot challenge. A blocked host is **not** evidence that the archive is unusable. It only means I could not confirm details.
- **Rights caveat:** where an archive says an item is "public domain", that judgement is made under the archive's own national law. Before shipping, a New Zealand-based app should run its own check (sound-recording and composition terms differ by country). Nothing here is legal advice.

---

## Ranked summary (best fits first)

| # | Archive | Region / content | Size and dates | Audio access | Rights | Machine access | Verdict |
|---|---|---|---|---|---|---|---|
| 1 | **Discografia Brasileira** (Instituto Moreira Salles) | Brazil: samba, choro, modinha, maxixe, marchas, regional 78s | 63,292 recording pages in sitemap; about 47k with audio; 1902–1964 | **Direct MP3 on Amazon S3**, no login, Range supported (verified) | **Per item: `publicDomainYear` in JSON-LD** (verified). IMS released 6,000+ public-domain recordings for free download | Sitemap (5 files, about 200k URLs), schema.org JSON-LD on each page, robots `Crawl-delay: 5`; no formal API | **Good fit.** Filter on `publicDomainYear <= current year` |
| 2 | **Finna.fi API** (National Library of Finland/Doria, Uniarts/Sibelius Academy, Finnish Literature Society SKS, SLS) | Finland, Karelia, Finland-Swedish: 78s, runo singing, laments, folk fiddling | 20,144 online sound records; **927 flagged Public Domain** (899 dated 1901–1956); **1,746 SKS field recordings under CC BY-NC-ND** | **Direct MP3** (Doria, Taju, SKS "Yksa"), no login (verified) | **Per item, machine-readable** (`usage_rights_ext_str_mv`: PD, BY, NC-ND, InC…) | **Open REST API, no key** (`api.finna.fi/v1/search`), facets by rights, date and institution (verified) | **Good fit.** PD plus NC-ND both allow a free, non-commercial, unaltered stream |
| 3 | **Internet Archive sub-collections not yet noted** | NFSA Australia excess 78s (incl. Topic, Folkways, Arhoolie pressings); ARChive of Contemporary Music; Russian émigré recordings; Great 78 by language | `78rpm_nsfa` **35,237**; `archiveofcontemporarymusic` **7,197**; `mrcsf-music` 204; Great 78 language tags: Spanish 5,003, Greek 1,010, Yiddish 553, Hebrew 451, Japanese 429, Arabic 311, Portuguese 267, Turkish 94, Chinese 94, Armenian 86, Hindi 59 | Same as the existing IA integration | Per IA item; mostly no explicit licence | IA advancedsearch (verified counts) | **Good fit** (no new integration needed); same rights diligence as the rest of Great 78 |
| 4 | **CREM-CNRS / Musée de l'Homme (Telemeta)** | **Africa, Asia**, Latin America, Oceania: ethnographic field recordings and published discs | 84,399 OAI item records; in a 5,000-record sample, 45% were "public" access and **about 32% were public and dated 1970 or earlier** (Asia 780, Africa 725, Americas about 200 in the sample) | **Direct MP3 for "public" items** at `/archives/items/download/<code>.mp3`, no login, Range supported (verified) | Per item access flag plus legal note ("Copie interdite", "Restreint"…). Site notice: "ALL RIGHTS RESERVED… AUDIO FILE COPYING FORBIDDEN IF NOT EXPLICITLY ALLOWED… COMMERCIAL USING FORBIDDEN" | **OAI-PMH, no key** (verified) | **Usable with conditions:** needs written permission from CREM; the most valuable Africa and Asia source found |
| 5 | **NDL Rekion (れきおん, Historical Recordings Collection)**, Japan | Japanese SP records: min'yō, Ryukyu songs, naniwa-bushi, gidayū, popular songs | About 50k digitised; about **5,500 internet-public** (2021 figure); 1900–1950 | **HLS (.m3u8) only; returned 401 without the viewer's session token** (verified). No direct file | **Per item `rights.code = "pdm"`** and `permission.type = "internet"` in JSON (verified) | **JSON item API, no key** (`rekion.dl.ndl.go.jp/api/item/search/info:ndljp/pid/<id>`) (verified) | **Usable with conditions:** metadata and PD flags are open, but audio needs NDL's agreement or a supported token flow |
| 6 | **Max Hunter Folk Song Collection** (Missouri State / Springfield-Greene County Library), USA | Ozark ballads and songs, field tapes | About 1,600 songs, 1956–1976 | **Direct MP3** (`/_Files/0001.mp3`), Range supported (verified) | Blanket: "not aware of any U.S. copyright… believe most, if not all… public domain"; access is "for educational and research purposes" | HTML only; predictable song IDs | **Usable with conditions:** ask permission; drop items recorded after 1970 |
| 7 | **Memoria Chilena** (Biblioteca Nacional de Chile) | Chile: cueca, tonada, folk and popular | Count of audio items not established | **Direct MP3** (`/archivos2/audio/MC00xxxxx.mp3`), Range supported (verified) | Per item field "Propiedad intelectual" ("Obra protegida" vs public domain). The sampled item was protected | HTML only | **Usable with conditions:** only items marked public domain; the PD share is unknown and probably small |
| 8 | **FAU Recorded Sound Archives** (Florida Atlantic University) | Judaica (Yiddish, Ladino, cantorial, Middle Eastern Jewish) plus vintage 78s | Large (tens of thousands of sides, search-only) | Flash/JS player only; "recordings on this site cannot be downloaded" (verified) | PD plays in full; copyrighted items limited to 45-second clips | HTML only | **Usable with conditions:** only with explicit permission |
| 9 | **National Library of Israel – National Sound Archives** | Jewish and Israeli music, plus Arab and Middle Eastern recordings | "World's largest" Jewish music audio collection (search-only) | API offers audio streaming (search-only) | Per item terms; many are "personal study, teaching and research" only | **API needs a registered key**; IIIF (search-only) | **Usable with conditions:** blocked (403) from the sandbox, unverified |
| 10 | **Library and Archives Canada – Virtual Gramophone** | Canadian 78s and cylinders (incl. French-Canadian folk and fiddle) | About 7,000 recordings, 1890–1960 (search-only) | Historically MP3 download (search-only) | Historically PD (search-only) | Unknown | **Probably good, unverified:** all LAC hosts were blocked from the sandbox |
| 11 | **Kounadis Archive Virtual Museum** (Univ. of the Aegean) | Greek rebetiko and smyrneiko 78s | About 3,000 78s (search-only) | Embedded player | Item pages show a "cc" licence field (exact licence unverified) | Unknown | **Usable with conditions, unverified.** Note: **server sends an incomplete TLS chain** (curl error 60). Android does not fetch missing intermediate certificates, so app playback would probably fail until they fix it |
| 12 | **Czech: MZK Kramerius** (Moravian Library) | Czech gramophone records | 4,972 tracks, 510 public | Kramerius viewer | "public" = free access (licence details unverified) | **Kramerius 7 API, no key** (verified) | Minor: small set, mostly classical/popular |
| 13 | **Radford University – Music of Burma on locally produced 78s** | Myanmar 78s, 1919–1962 | Unknown | Unknown | Stated "open access" aim; commercial use governed by IP law | Omeka-style site | **Unverified** (host unreachable); worth a follow-up |
| 14 | **Díaz-Ayala Cuban & Latin American Popular Music Collection** (FIU, dPanther) | Cuba, Puerto Rico, Latin America 78s | About 14,500–15,000 78s; 1900–1929 digitised with a Latin GRAMMY grant (search-only) | Unknown | Unknown ("for academic and research purposes") | Unknown | **Unverified** (host unreachable); contact the archive |
| 15 | **Endangered Archives Programme (BL EAP)** audio projects | Sounds of Siam (2,429 Thai 78 tracks 1903–60), Odeon India (1,404), Young India label, Burma, Azerbaijan, Nepal, Peru, Golha, Micronesia | About 25k tracks overall | eap.bl.uk sits behind an **AWS WAF challenge** (HTTP 202, `x-amzn-waf-action: challenge`); audio mostly delivered through **BL Sounds (excluded)** | Per project; Golha marked "research purposes only" | None found | **Not usable directly** (bot wall, research-only terms, overlaps BL Sounds) |
| 16 | **Golha radio programmes** (golha.co.uk) | Persian classical/poetic radio | 1,578 programmes, about 847 h, 1956–1979 | MP3 under `/vod/` **returns 403 when fetched directly** (hotlink-protected) (verified) | "© All rights reserved"; FAQ: "does not offer a download service" | HTML | **Not usable** |
| 17 | **Strachwitz Frontera Collection** (UCLA / Arhoolie) | Mexican and Mexican-American 78s, 45s, LPs | 100k+ recordings | Wowza HLS; **off-campus users get only a short clip** (verified on the copyright page) | Copyright-restricted | HTML | **Not usable** |
| 18 | **AMAR Foundation** (Lebanon) | Nahda-era Arab art music 78s, cylinders, reels | About 7,000–12,000 discs, 400+ cylinders (search-only) | No online library; old podcast *Rawdat al-Balabel* (2013–17) on Buzzsprout | Unknown | None | **Not usable now** (site returned 503 to the sandbox) |
| 19 | **ILAM / Hugh Tracey** (Rhodes University) | Southern, Central and East Africa field recordings | About 12,000–14,000 items (search-only) | Historically 30-second clips online (search-only) | Unknown | Unknown | **Not usable / unverified** (host unreachable) |
| 20 | Others checked and **not usable**: Österreichische Mediathek, Svenskt visarkiv, Nasjonalbiblioteket (NB.no), dLib.si, Irama Nusantara, Archive of Indian Music, ARCE, Phonogrammarchiv Vienna, PARADISEC, CMAM Tunisia, SAMAP, Tobar an Dualchais / ITMA | see notes | | | | | |

---

## Per-archive notes

### 1. Discografia Brasileira (IMS), good fit
- **Content:** the national 78-rpm discography of Brazil. The sitemap lists 63,292 `fonograma` (recording), 55,636 composition, 35,059 disc and 25,225 artist pages. Coverage is 1902–1964. Press reports give about 47k digitised audio files and 6,000+ public-domain tracks released for free download, marked with a "DP" seal showing the year each entered the public domain. Genres include samba, choro, marcha, valsa, modinha, toada, embolada, caboclo/regional and more. Largely **not** mirrored on IA.
- **Audio (verified):** every recording page embeds schema.org JSON-LD with `audio.contentUrl` pointing at `https://discografia-brasileira.s3.sa-east-1.amazonaws.com/<hash>.mp3`. A test file returned HTTP 206 `audio/mpeg` (22 kHz mono MP3) with no login and no referrer check.
- **Rights (verified):** the JSON-LD carries `publicDomainYear`. Examples: Odeon 10020 (1927) → 1999; Odeon 10329 (1929) → 2027; Odeon 10569 (1930) → 2045. Filter to `publicDomainYear <= 2026`. **Caveat:** the S3 files are publicly reachable for *all* digitised items, including protected ones. Only the PD subset should be streamed. The year is calculated under Brazilian law (author's death plus 70 years, phonogram plus 70 years).
- **Machine access:** `sitemap.xml` (index) → `sitemap1-5.xml`. Pages are server-rendered with JSON-LD, but the site is behind Cloudflare, so harvest slowly (`Crawl-delay: 5`). Store the S3 URL plus metadata in your own catalogue rather than scraping at play time.
- Sources: https://discografiabrasileira.com.br/sitemap.xml · https://discografiabrasileira.com.br/fonograma/19508/marcha-triunfal-brasileira-odeon-10020 · https://billboard.com.br/ims-libera-mais-de-6-mil-musicas-em-dominio-publico-para-download-gratuito/

### 2. Finna.fi (Finland), good fit
- **Content (verified via API):** 20,144 online sound records across Finnish memory institutions.
  - **National Library of Finland / Doria:** 482 records, all flagged "Public Domain". These are Finnish 78s from 1901–1956 (e.g. "Eteläpohjalainen kansanlaulu", 1906 Gramophone).
  - **Uniarts / Taju (Sibelius Academy):** 445 PD records (mostly LP-era art music, less relevant).
  - **Finnish Literature Society (SKS):** **1,746 free-online field recordings under CC BY-NC-ND**, all dated 1970 or earlier (e.g. Paraske Mitrune laments, Suojärvi 1917, phonograph copies).
  - Also SLS (Finland-Swedish folklore, 326) and Musiikkiarkisto JAPA (349).
- **Audio (verified):** `onlineUrls` and `urls` give direct MP3s, e.g. `https://www.doria.fi/bitstream/10024/72262/1/RAI-Gramophone72618-01-1-001.mp3`. Doria answers with a 301 to a jsessionid URL, then 206 with Range support. SKS files at `yksa.disec.fi/Yksa4/download/...` return 200 `audio/mpeg` but **ignored the Range header** in my test, so seeking may need care.
- **Rights:** per-record facet `usage_rights_ext_str_mv`, with values `A Free / B PD`, `B BY`, `E NC-ND`, `G InC`, `H Unknown`. CC BY-NC-ND permits verbatim non-commercial streaming with attribution.
- **Machine access:** `https://api.finna.fi/v1/search?filter[]=format:"0/Sound/"&filter[]=usage_rights_ext_str_mv:"1/A Free/B PD/"`. No key needed. JSON responses with facets and paging.
- **Indigenous material:** Finna includes some Sámi material. Check `building` and subject fields, and consider excluding Sámi yoik recordings unless the holding institution and community guidance support playback.
- Source: https://api.finna.fi/v1/search (tested live).

### 3. Internet Archive collections worth adding (already your platform)
- `78rpm_nsfa`: **35,237 items**, the National Film and Sound Archive of Australia's "Excess Audio" 78s (Edison, Topic, Folkways, Arhoolie, Tangent… per the collection description).
- `archiveofcontemporarymusic`: 7,197 items (ARChive of Contemporary Music, NYC).
- `mrcsf-music`: 204 items (Museum-Archive of Russian Culture, San Francisco).
- **Great 78 by `language:`** (in `collection:78rpm`): Spanish 5,003 · Greek 1,010 · Yiddish 553 · Hebrew 451 · Japanese 429 · Arabic 311 · Portuguese 267 · Turkish 94 · Chinese 94 · Armenian 86 · Hindi 59 · Persian 2. Language tagging is sparse, so real counts are higher. Querying by label names (Odeon, Baidaphon, Columbia "E" ethnic series, HMV India) will find more.
- **AMAR, Hugh Tracey/ILAM and CREM are *not* mirrored on IA** (searches found 0–4 stray items).
- Caveat: Great 78 was the subject of the record labels' lawsuit, which IA settled. Apply the same rights filter you already use.

### 4. CREM-CNRS (Archives sonores du CNRS – Musée de l'Homme), usable with conditions
- **Content:** France's main ethnomusicology archive, with field recordings and published discs from Africa, Asia, Oceania, the Americas and Europe. It includes collections such as "Hindustani [Inde du Nord]" and Southeast Asian 78 reissues. The OAI-PMH `completeListSize` is **84,399**. In a sample of 5,000 records spread across the set: 2,239 were `public`, and **1,618 were public and dated 1970 or earlier** (Asia 780, Africa 725, South America 92, Central America 9). Extrapolating, that suggests roughly 25–30k public pre-1971 items. This is an **estimate from sampling, not a count**.
- **Audio (verified):** for public items the player loads `https://archives.crem-cnrs.fr/archives/items/download/<CODE>.mp3`. Example: `CNRSMH_I_2003_004_001_01` (Dan music, Côte d'Ivoire, Hugo Zemp, 1962) returned 206 `audio/mpeg`, 48 kbps MP3. No login.
- **Rights:** each item carries an access level (`public`, `restricted`, `private`) plus a legal status (`Copie interdite`, `Restreint`, `Restreint (enregistrement édité)`). The site-wide legal notice says: "ALL RIGHTS RESERVED, PLEASE CONTACT CREM FOR ANY REQUEST · AUDIO FILE COPYING FORBIDDEN IF NOT EXPLICITLY ALLOWED · COMMERCIAL USING OF THE ARCHIVES FORBIDDEN IF NOT EXPLICITLY ALLOWED · Usage of the archives in the respect of cultural heritage of the original communities." Streaming in a free app is **not** clearly permitted. Ask CREM (crem.lesc (at) cnrs.fr) for written permission to stream public items from their server with attribution.
- **Sacred and ritual content:** many items are ritual recordings, e.g. initiation songs ("Chant de filles excisées"). If permission is granted, add a curation filter.
- **Machine access:** `https://archives.crem-cnrs.fr/oai/?verb=ListRecords&metadataPrefix=oai_dc` (500 records per page, resumable tokens, slow server: about 30–60 s per page). Dublin Core per item is also at `/archives/items/<code>/dc/`.
- Sources: https://archives.crem-cnrs.fr/oai/?verb=Identify · https://archives.crem-cnrs.fr/pages/legal_notices

### 5. NDL Rekion (Japan), usable with conditions
- **Content:** about 50k SP-record sides made in Japan c.1900–1950. Only items whose copyright and neighbouring rights NDL has confirmed as expired are internet-public: about 5,500 as of Jan 2021 (search-only; the current number is likely higher). The material includes many min'yō (folk songs) and Ryukyu songs.
- **Machine access (verified):** `https://rekion.dl.ndl.go.jp/api/item/search/info:ndljp/pid/1318142` ("Aizu Bandaisan", Victor 1934) returns JSON with `rights.code: "pdm"`, `permission.type: "internet"`, the date, label and genre (民謡) plus a `contentsBundles[].contents[].publicPath` ending in `.m3u8`. The site is a JS SPA; NDL Search OpenSearch did not find Rekion items with my parameters.
- **Audio:** the HLS URL `https://rekion.dl.ndl.go.jp/contents/<pid>/<bundle>/<file>.m3u8` returned **HTTP 401** without the viewer's session. Streaming probably needs a token issued to the web player. There are no plain file URLs.
- **Rights:** items are explicitly Public Domain Mark. NDL generally allows reuse of PD material (search-only).
- **Next step:** ask NDL whether a third-party player may fetch tokens, or whether they offer files for PD items.

### 6. Max Hunter Folk Song Collection (USA), usable with conditions
- About 1,600 Ozark songs from reel-to-reel field tapes, 1956–1976. Direct MP3 at `https://maxhunter.missouristate.edu/_Files/NNNN.mp3` (206, verified). Each song page gives the singer, place and date, which you need for the 1970 cutoff.
- Rights page: "not aware of any U.S. copyright protection or any other restrictions… We believe that most, if not all, of the works in this collection are in the public domain", but access is "strictly for educational and research purposes". It asks for respect for the communities recorded and has an "Offensive Material" page. Ask the Springfield-Greene County Library and MSU for permission.
- Source: https://maxhunter.missouristate.edu/restrictions.htm

### 7. Memoria Chilena (BN Chile), usable with conditions
- Direct MP3s (`https://www.memoriachilena.gob.cl/archivos2/audio/MC0061089.mp3`, 206, verified). Each record shows "Propiedad intelectual: Obra protegida" or a public-domain status. The item I checked (1991 CD track) was protected, and most folk-music audio appears to be from modern editions. The size of the PD subset is unknown. HTML only.
- Source: https://www.memoriachilena.gob.cl/602/w3-article-63285.html

### 8. FAU Recorded Sound Archives, usable with conditions
- Judaica and vintage 78s. The copyright page (verified) says: PD plays in full; copyrighted items play 45-second clips; "recordings on this site cannot be downloaded". The audio sits behind a JS player. Permission is needed.
- Source: https://rsa.fau.edu/copyright

### 9. National Library of Israel, usable with conditions (blocked)
- National Sound Archives (Jewish, Israeli, and Middle Eastern including Arab music). Its Open Library APIs (Search, IIIF, media streaming) need a **registered API key**. Item terms vary: some allow free use with credit, many are for study and research only. `nli.org.il` and `api.nli.org.il` returned 403 to the sandbox.
- Source: https://www.nli.org.il/en/research-and-teach/open-library

### 10. LAC Virtual Gramophone (blocked)
- About 7,000 Canadian 78s and cylinders, 1890–1960. Historically offered MP3 download and was described as public domain. LAC says the site is "no longer updated". Every LAC host was unreachable (proxy refusal or empty reply), so the current state is unverified.
- Source: https://www.canada.ca/en/library-archives/collection/research-help/arts-culture/gramophone-phonograph.html

### 11. Kounadis Archive Virtual Museum (Greece)
- About 3,000 Greek 78s (rebetiko, smyrneiko, demotic). Search snippets show an embedded player and a "cc" licence field per item. I could not confirm the exact licence.
- **Technical red flag (verified):** `curl` fails with "unable to get local issuer certificate", meaning the server omits its intermediate certificate. Android's TLS stack will reject this, so it is unusable from an app until they fix it.
- Source: https://vmrebetiko.aegean.gr/?lang=en

### 12. Czech Republic
- **MZK Kramerius** (verified API): 686 sound recordings, 4,972 tracks, 510 tracks `public`. Mostly mid-century classical and popular music. Open API at `https://api.kramerius.mzk.cz/search/api/client/v7.0/search`. Low yield.
- **Virtuální národní fonotéka** (now run by NTK): a union catalogue with selected listening "in accordance with copyright". The host was unreachable.

### 13–14. Radford Burma 78s and FIU Díaz-Ayala (both unverified)
- Both look promising on paper: an open-access aim, and early-20th-century 78s that are probably PD. Both hosts were unreachable from the sandbox, so contact them directly.
- Sources: https://monk.radford.edu/records/item/731-byau · https://dpanther.fiu.edu/collection/Diaz%20Ayala%20Music%20Collection

### 15. British Library Endangered Archives Programme
- Music projects include EAP1213 *Sounds of Siam* (2,429 Thai 78 tracks, 1903–1960), EAP468 Odeon India (1,404), EAP190 Young India label, EAP592 Burma, EAP124 Azerbaijan, EAP736 Nepal, EAP298 Peru, EAP088 Golha and EAP115 Micronesia (7,069).
- eap.bl.uk answered `202` with `x-amzn-waf-action: challenge` (a bot wall). EAP's own list says the audio is available "on BL Sounds", which is excluded. Golha is marked "research purposes only".
- Not usable as a stream source. Useful as a pointer to depositor archives (e.g. Gitameit Yangon, Music Museum of Nepal) you could approach directly.
- Sources: https://eap.bl.uk/node/4566 · https://eap.bl.uk/node/4316

### 16. Golha (Iran), not usable
- MP3s at `/vod/<prog>.mp3` returned **403** to a direct request. The FAQ says there is no download service; the site says "© All rights reserved". Much of the material is from 1971–1979.
- Source: https://www.golha.co.uk/en/faqs

### 17. Strachwitz Frontera Collection, not usable
- "Due to copyright restrictions the digital collection is only fully accessible from computers on the UCLA campus. Users from off campus have… a short clip" (verified).
- Source: https://frontera.library.ucla.edu/content/copyright-and-access

### 18. AMAR Foundation, not usable now
- Holds about 7,000–12,000 Nahda-era discs and 400+ cylinders. A researchers' guide says an online platform is still being planned. The podcast *Rawdat al-Balabel* (2013–2017) is on Buzzsprout. The site returned 503 to the sandbox. Not on IA.
- Sources: https://www.buzzsprout.com/7567/episodes · https://www.amar-foundation.org/about/

### 19. ILAM / Hugh Tracey, not usable or unverified
- About 12,000–14,000 items. The online sound archive historically offered 30-second excerpts, and a 2023 chapter says it is "still in its implementation stage". The host returned 502 from the proxy. Full tracks are only on Smithsonian Folkways, which is excluded.
- Source: https://www.ru.ac.za/latestnews/archives/2013/african_musical_treasure_trove_goes_online.html

### 20. Checked and not usable (one line each)
- **Österreichische Mediathek (Austria):** about 4,000 shellac sides online, but "downloading or similar is not offered or permitted for any media streamed via www.mediathek.at". Streaming in its own player only (verified). https://www.mediathek.at/en/download-/-rechte
- **Phonogrammarchiv Vienna:** catalogue only; recordings onsite or on request "due to legal and ethical restrictions" (search-only; host reset the connection).
- **Berlin Phonogramm-Archiv:** reaches the web via SMB and Europeana (excluded). Nothing separate found.
- **Svenskt visarkiv (Sweden):** "freely available… for personal use"; publishing needs permission. https://katalog.visarkiv.se/lib/views/fmken/Default.aspx?item=195
- **Nasjonalbiblioteket (Norway):** open API (verified). 10,543 music items are viewable "EVERYWHERE" but licensed `copyrighted` and play only in NB's player. Not usable.
- **dLib.si (Slovenia):** EDM JSON per record (verified), but the sampled audio was `InC`, the "stream" URLs return a viewer page, and the content already reaches Europeana.
- **Irama Nusantara (Indonesia):** now requires sign-in to play; "All rights reserved" (verified).
- **Archive of Indian Music (Vikram Sampath):** about 6,000+ pre-1950s tracks, SoundCloud only. Can't be used outside SoundCloud's own player and API.
- **ARCE / AIIS (India):** 25,000 h of field recordings; online access limited to samples; listening by appointment.
- **PARADISEC (Pacific):** catalogue metadata is open via OLAC, but audio needs a registered account and a click-through "PDSC access conditions" agreement, with conditions set per depositor. **Indigenous and community-controlled.** Not suitable for anonymous streaming, and this matters culturally for a NZ app. Catalog host returned 403.
- **CMAM Tunisia – National Sound Archive:** listening requires a request form (search-only).
- **SAMAP (South Africa):** about 13,000 audio *samples* (search-only).
- **GBC Ghana gramophone library:** 60,000+ discs digitised but not online (search-only).
- **Tobar an Dualchais (Scotland), ITMA (Ireland):** both returned 403 to the sandbox. Both are known (unverified here) as streaming-only with restrictive terms.
- **Biblioteca Digital Hispánica (Spain):** about 21,000 shellac discs, some streaming; BNE's PD policy is CC BY, but audio downloads are unconfirmed (403 to the sandbox). Worth a follow-up.
- **BN Digital Brasil (bndigital.bn.gov.br):** 403 to the sandbox; not assessed.
- **Uysal–Walker Archive of Turkish Oral Narrative:** mostly narrative plus about 100 folk-music MP3s, now in a TDL DSpace (403 to the sandbox). Images are for "study purposes" only; audio terms unconfirmed.
- **National Gugak Center (Korea), Taiwan Sound 100 Years, Minpaku metal-master DB (Japan), Lauro Ayestarán CDM (Uruguay), Instituto Carlos Vega (Argentina), BN Colombia CDM, Fonoteca Nacional México:** search-only results point to onsite access, selections in embedded players, or databases not yet public. None had an open file or rights model I could confirm. Fonoteca Nacional was unreachable.
- **dLOC (Caribbean), Manioc (French Antilles):** almost no historical music audio found.
- **Singing Wells (East Africa):** open archive, but recordings date from 2011 onwards, so outside the date range.
- **DPLA:** a useful aggregator of US audio (free API key), but `api.dp.la` returned 403 to the sandbox; not assessed.

---

## Practical notes for the app

1. **The two best new sources need no permission, have machine-readable PD flags and serve plain MP3:** Discografia Brasileira (`publicDomainYear`) and Finna (`usage_rights_ext_str_mv`). Both suit a pre-built catalogue: harvest once and store URL, title, artist, date and licence.
2. **The richest Africa and Asia source, CREM, is technically trivial** (OAI-PMH plus direct MP3) **but legally gated.** A short email asking permission for non-commercial, attributed streaming from their server is the highest-value action.
3. **Rekion** has open PD metadata but token-gated HLS. Ask NDL.
4. Hosting gotchas observed: Cloudflare (Discografia: be gentle), AWS WAF (EAP), hotlink 403 (Golha), incomplete TLS chain (Kounadis), no Range support (SKS Yksa), and a jsessionid redirect (Doria).
5. Indigenous and sacred content: CREM (ritual and initiation recordings), Finna (Sámi), PARADISEC and Max Hunter each raise community-sensitivity issues. A curation flag or exclusion list is advisable.

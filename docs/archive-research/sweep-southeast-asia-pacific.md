# Archive sweep: Southeast Asia and the Pacific

Swept 2026-11-02 for Old Folk Air (folk, traditional and field recordings from 1970 or earlier, public domain or openly licensed, streamed from the host archive).

**Limits on this sweep (please read):**
- The shared WebSearch budget ran out partway through. Countries searched after that point (Laos, Timor-Leste, Brunei, Solomon Islands, Cook Islands, Tonga/Samoa in depth, Torres Strait in depth) were covered only through direct checks on sites (curl), the Pratinada/CREM metadata API and Internet Archive queries. Nothing was searched in local languages for those countries. A follow-up pass with fresh search budget would help there.
- Many hosts in this region sit behind bot checks or are unreachable from the sandbox. I did not try to get around any of them. Blocked hosts: AIATSIS (Cloudflare challenge), NFSA (Cloudflare challenge), ELAR (Cloudflare challenge), PARADISEC catalogue (Cloudflare 403), NLA catalogue (Anubis proof-of-work), UH Mānoa Digital Library (Cloudflare Turnstile), Leiden Digital Collections (F5 CAPTCHA), State Library NSW (403), State Library SA (403), Perpusnas Khastara (Cloudflare 403), ANRI (403), Filipinas Heritage Library (403). Unreachable (DNS or connection failure): lokananta.id, vnmusicology-inst.vn, ica.pf, archives.pf, arkib.gov.my, nationalarchives.gov.fj, kaniaina.org, avakonohiki.org. The British Library EAP pages (eap.bl.uk) returned HTTP 202 challenge pages. WebFetch could not resolve library.gov.au or scholarspace.manoa.hawaii.edu, although curl could reach ScholarSpace.
- Status tags: **[verified]** means I checked live pages or the API, **[search-only]** means the facts come from search results only, **[blocked]** means the host refused automated access.

---

## 1. Ranked table

| # | Archive / collection | Countries / peoples | Pre-1970 items (approx.) | Audio access | Rights | Machine access | Verdict | Status |
|---|---|---|---|---|---|---|---|---|
| 1 | **Pratinada / DeCoSEAS index** (omekas.seasia-hearing.org): public-domain CREM and BnF items | Vietnam, Indonesia, Cambodia, PNG, Thailand, Laos, Philippines, Myanmar | **1,301 items marked "Public Domain" dated 1970 or earlier** (CREM 1,242, BnF 59) | WAV files served directly from omekas.seasia-hearing.org/files/original/… (HTTP 206 range requests OK, no login) | Item-level `schema:license` = Public Domain. The site asks for non-commercial, respectful reuse (NoC-CR) | **Open Omeka S REST API**, no key, JSON-LD, 7,637 items | **Good fit**, used as a discovery and licence index for CREM and Gallica, which are already known. Stream from the CREM or Gallica originals where possible | verified |
| 2 | **Internet Archive `papuaculturalinheritance`** (PACE Foundation) | West Papua (Sentani, Biak and others), Dutch New Guinea | 8 items, 1955–1962 (193 items in total; most are from the 1980s) | MP3, FLAC and WAV, direct | **CC BY-NC-ND 4.0** on every item | IA advancedsearch and metadata API | **Good fit** (small). Review the content before adding: mission choir items, and possibly sensitive West Papua material | verified |
| 3 | **Jaap Kunst Collection** (University of Amsterdam), via Pratinada | Indonesia: Java (Yogyakarta kraton), Sunda, Flores (Manggarai, Lio, Sikka, Lamaholot), Nias, Toba Batak, Bugis/Toraja, Minahasa, Kei, Iban, Rejang, Moi, Atoni and others | 596 items, 1920s–1934 (469 with media) | WAV files direct on omekas.seasia-hearing.org | **NoC-OKLR** (no copyright, other known legal restrictions): use is limited by Indonesian adat and Indigenous-rights law and cultural-heritage law | Omeka S API | **Ask permission, and flag as culturally sensitive.** Treat as excluded unless UvA and the source communities agree | verified |
| 4 | **Hawaiian Music Archives @ Hawaiʻi State Archives** | Hawaiʻi | Malcolm Rockwell's 6,000 78 rpm discs (1890–1960), Gene Becker papers (1911–52), the Soria and Scott collections | Only part is online, through the Digital Archives of Hawaiʻi (a JS app; the listing did not load for me) | Unknown. Note: US sound recordings published before 1926 are public domain in the US | HTML/JS only | **Ask permission:** musicarchives@hawaii.gov (archives@hawaii.gov for the digital archives) | verified (site), partial |
| 5 | **National Archives of Singapore: "Sounds of Yesteryear (1903–1941)"** and related audiovisual records | Singapore and Malaya: Malay bangsawan, lagu Melayu, dondang sayang, Chinese opera | About 50 digitised 78 rpm sides in the highlight set, plus radio from 1959 onward | In-page streaming player only. Some records are on-site only | "Use and reproduction require written permission from copyright owner(s)" | HTML only | **Ask permission** (NAS through the NLB contact channels; I did not verify an address) | verified |
| 6 | **UP Center for Ethnomusicology: José Maceda Collection** | Philippines (Cordillera, Mindanao, Visayas), plus Malaysia and Indonesia | About 2,500 hours of reel and cassette, 1950s onward; digitised 2007–2014 | On-site, or online access by emailed request only | "Private study, scholarship or research"; may be protected under the Copyright Act | None public | **Ask permission:** upethno_archive.upd@up.edu.ph (general: upethno.upd@up.edu.ph) | verified (contacts), search-only (holdings) |
| 7 | **Kaipuleohone** (University of Hawaiʻi ethnographic archive, ScholarSpace) | PNG (Enga), Fiji, Marshall Islands, Micronesia, French Polynesia, Niue, Kiribati | About 96 items dated 1970 or earlier, mostly linguistic. Songs: about 10 items (Paul Brennan, Enga *singsing*, clan songs, 1970) | **Direct MP3 + WAV** through the DSpace REST bitstream URLs, range requests OK, no login | Creative Commons (the community text and the 2009 article say CC BY-NC-SA). Users must read the "Conditions of Access" PDF (I could not fetch it) | DSpace 7 REST API (+ OAI-PMH likely) | **Ask permission** (small song yield, and conditions unread). Contact via the LAE Labs Kaipuleohone page | verified |
| 8 | **Micronesian Seminar (MicSem) music collection / EAP115** (now a known BL EAP sub-collection) | FSM (Chuuk, Pohnpei, Yap, Kosrae), Guam, Marshall Islands | 22,300 items in total; about 7,069 listenable on the BL EAP site; dates from 1960s cassettes onward | BL EAP player | BL lists "research purposes only" | BL EAP (blocked to me) | **Ask permission** (MicSem via micsem.org; BL EAP team) | search-only / blocked |
| 9 | **NLA Oral History and Folklore** (John Meredith and others) | Australian bush songs, recitations, dance music | Meredith alone: 500 to 1,200+ recordings, 1953–1994 | Trove "Listen" player after accepting an end-user licence | Per-item access conditions; personal-research style EULA | Trove API (key required); catalogue behind the Anubis bot check | **Ask permission** (NLA Ask a Librarian). Mostly Anglo-Australian folk; Aboriginal items need cultural review | search-only / blocked |
| 10 | **Bishop Museum Library & Archives** (Helen Roberts mele, the 1,255-recording chant set, Welo Hou project) | Hawaiʻi, Pacific | Large, unquantified | By appointment; online catalogues offline during a system migration | Unknown. Chant (mele, oli) can be culturally sensitive | None currently | **Ask permission later:** archives@bishopmuseum.org | verified (page) |
| 11 | **UH Mānoa Hawaiian Music Collection** (Dirk Vogel: 6,000 78 rpm discs) | Hawaiʻi | Thousands of 78 rpm sides, c.1900–1950s | Audio "Restricted to UH Mānoa" or reading-room only | Restricted | Omeka (behind a Turnstile challenge) | **Ask permission** (low odds) | blocked / search-only |
| 12 | **PARADISEC VKS2** (Vanuatu Kaljoral Senta, 2018 digitisation; a new sub-collection of known PARADISEC) | Vanuatu | Unknown | PARADISEC (Cloudflare to me) | "Open, subject to PDSC access conditions"; the main VKS collection is **closed** | PARADISEC API (known) | **Ask permission** (VKS through PARADISEC); exclude the closed VKS collection | search-only |
| 13 | **BL EAP1213 "Sounds of Siam"** (a new sub-collection of known BL EAP) | Thailand: *dontri Thai doem*, early *phleng Thai sakon* | 2,429 tracks, 1903–1960s | BL EAP player | Per the BL EAP terms (known) | BL EAP | Handle with the existing BL EAP decision | search-only / blocked |
| 14 | **BL EAP Burmese recordings project** (eap.bl.uk/node/3176; project number not verified) | Myanmar: court, classical, dramatic, folk | Unknown | BL EAP | BL EAP terms | BL EAP | Handle with the existing BL EAP decision | search-only |
| 15 | **CREM Timor-Leste 1960s field recordings** (a new sub-collection of known CREM, found through Pratinada) | Portuguese Timor | 142 items CC BY-NC-ND + 191 items InC-NC (1960s) | CREM / Pratinada WAV | CC BY-NC-ND (good) / InC non-commercial | Omeka S API, CREM OAI | Treat like CREM. The CC BY-NC-ND items look usable | verified |
| 16 | NLA-held copies at **NFSA** (Meredith tapes etc.) | Australia | — | Clips/streaming | NFSA terms | Cloudflare | Not usable (blocked; same content as NLA) | blocked |
| 17 | **AIATSIS** audiovisual collection | Aboriginal and Torres Strait Islander peoples | Very large | On request through the Audiovisual Access Unit | Per-item community and depositor conditions; many need CEO or community permission | Mura catalogue (Cloudflare) | **Not usable:** community-controlled access; exclude | blocked / search-only |
| 18 | **State libraries (SLQ, SLNSW, SLSA, SLV)** | Australia | Some field recordings and oral history | SLQ: personal research and study only; SLNSW: "in copyright, reproduction restricted" (1968–72 South Pacific Festival reels) | Restrictive | SLQ One Search requires login; SLNSW/SLSA 403 | Not usable (restrictive or blocked). Only SLQ's sheet music is CC BY | search-only / blocked |
| 19 | **Bali 1928 Archives** (Herbst / Arbiter / ITB STIKOM Bali) | Bali: Odeon and Beka 78s, 1928–29 | 98 sides | **Spotify only**; no hosted files | All rights reserved; written permission needed for any redistribution | None | Not usable for direct streaming (could ask through the bali1928.net form if Spotify embedding were ever acceptable) | verified |
| 20 | **Lokananta** (Surakarta state label) | Indonesian lagu daerah, keroncong, gamelan, from 1956 | Thousands | Spotify, Joox, YouTube; digital library on-site | Commercial label | lokananta.id unreachable; lokanantamusik.com redirects to rekaman.com | Not usable (commercial streaming platforms only) | search-only / blocked |
| 21 | **Filipinas Heritage Library: Himig** | Philippines: kundiman, harana, 20th-century OPM | 1,000+ LPs; 5,000 songs | Spotify playlists; researchers on request | Not open | Site 403 | Not usable (could ask asklibrarian@filipinaslibrary.org.ph) | search-only |
| 22 | **Bophana Center** (Phnom Penh) | Cambodia: 1960s pop LPs, 1969–70 minority recordings | Small | On-site; online database returned HTTP 500 | Copies only with written agreement from rights holders | — | Not usable | search-only |
| 23 | **Leiden UB / KITLV "Southeast Asian Pop Music"** | Indonesia, Malaysia etc. (pop vinyl) | Unknown | Behind a CAPTCHA | Per item, often in copyright | IIIF/OAI exist at Leiden generally | Not usable (blocked; mostly in-copyright pop) | blocked |
| 24 | **BBC Empire Service SE Asia** recordings (BL), via Pratinada | Malaya, Burma, Singapore | 78 items, 1930s–60s | Mostly no media | BBC copyright | Omeka S API | Not usable | verified |
| 25 | **IA `indonesian-cassette-archive`** | Java, Sunda: gamelan, jaranan, dangdut cassettes | 167 items, mostly 1980s | MP3 | No licence; commercial releases | IA API | Not usable (post-1970, in copyright) | verified |
| 26 | **IA `uclaethnomusicologyarchive`** (California Revealed) | Includes Philippine kulintang (Kalanduyan) and others | 7,411 items, mostly modern | `stream_only` | "Copyrighted. Rights owned by …" | IA API | Not usable | verified |
| 27 | **UW Ethnomusicology Archives** (Thai 78s, Burmese, Keyes collection) | Thailand, Myanmar | Small | ResearchWorks holds PDF indexes only; no audio online | — | DSpace API | Not usable (no audio online) | verified |
| 28 | **MAA Cambridge: G.K. Roth Fiji tapes** (1953–56, Fiji Broadcasting Commission) | Fiji, Tonga (lakalaka) | Dozens of tracks | Catalogue records only; account features | Unknown | HTML | Ask permission, low priority (I could not confirm any audio online) | search-only / partial |

---

## 2. Country checklist

### Southeast Asia
| Country / region | Result |
|---|---|
| **Indonesia: Java** | Jaap Kunst (Yogyakarta, Sunda): ask permission + cultural flag. CREM PD Java items through Pratinada (known). Lokananta: not usable. IA indonesian-cassette-archive: not usable. Irama Nusantara is already known. |
| **Indonesia: Bali** | Bali 1928 Archives: Spotify only, all rights reserved. CREM/BnF 1931 Exposition coloniale items through Pratinada (known). |
| **Indonesia: Sumatra** | Jaap Kunst: Toba Batak, Nias, Rejang, Palembang Melayu (ask + flag). Nothing else open found. |
| **Indonesia: Sulawesi** | Jaap Kunst: Bugis/Toraja (Masamba), Minahasa (ask + flag). |
| **Indonesia: Nusa Tenggara / Maluku** | Jaap Kunst: Flores (Manggarai, Lio, Sikka, Lamaholot, Ngada/Boawae), Timor (Atoni), Kei (ask + flag; digital copies repatriated to NTT in 2024). Kaipuleohone holds Palu'e (post-1970). |
| **Indonesia: Papua** | IA papuaculturalinheritance: 8 items 1955–62, CC BY-NC-ND (good fit, small). Jaap Kunst: Moi, Waropen (ask + flag). |
| **Indonesia: national bodies** | ANRI and Perpusnas Khastara are blocked (403). RRI: nothing online found. |
| **Malaysia: Peninsular** | Arkib Negara: no online audio found (site unreachable). NAS Singapore holds pre-war Malay 78s (ask). |
| **Malaysia: Sabah / Sarawak** | Jaap Kunst holds a few Iban (Batang Lupar) items. Sarawak and Sabah museums: nothing online found (searched only lightly). |
| **Singapore** | NAS "Sounds of Yesteryear 1903–1941" plus 1959–68 radio: player only, written permission needed (ask). NLB MusicSG URL is now 404. |
| **Brunei** | Nothing found online (no search budget left; RTB archives not checked). |
| **Philippines: general** | UP Center for Ethnomusicology / Maceda: ask. FHL Himig: Spotify only. Tagalog/kundiman 78s are in the Great 78 Project (known). CREM PD Philippines 1950s: 22 items through Pratinada (known). |
| **Philippines: Cordillera** | Covered by the Maceda collection (ask). No separate open archive found. |
| **Philippines: Mindanao** | Maceda collection (ask). The UCLA kulintang material on IA is copyrighted and stream-only. |
| **Thailand** | BL EAP1213 Sounds of Siam (2,429 tracks; known BL EAP). CREM PD Thailand 58 items (known). National Library (Princess Sirindhorn Music Library) and Mahidol hold 78s but nothing is online. UW Thai 78s: no audio online. |
| **Laos** | CREM/BnF PD 1900s–1950s: 26 items through Pratinada (known). Nothing else found (no search budget left). |
| **Cambodia** | Bophana: not usable. CREM/BnF/Phonobase PD Cambodia 118 items through Pratinada (known). |
| **Vietnam** | CREM PD Vietnam 612 items dated 1970 or earlier through Pratinada (known). Viện Âm nhạc site unreachable. No Vietnamese 78 rpm archive found online. |
| **Myanmar** | Radford is already known. BL EAP Burmese project (known EAP). CREM PD 7 items. BBC items not usable. |
| **Timor-Leste** | CREM 1960s field recordings: 142 CC BY-NC-ND + 191 InC-NC (new CREM sub-collection, through Pratinada). Jaap Kunst holds Atoni items from West Timor. No national archive online found. |

### Pacific and Australia
| Country / region | Result |
|---|---|
| **Papua New Guinea** | Kaipuleohone: Enga 1970 singsing (ask, small). CREM PD PNG 1950s–60s: 79 items (known). IPNGS Music Archive: 10,000+ hours, nothing online. PARADISEC is already known. 78rpm_nsfa has PNG items (known). |
| **Solomon Islands** | Nothing new found online beyond PARADISEC and CREM (known); no search budget left. |
| **Vanuatu** | PARADISEC VKS is closed; VKS2 is "open subject to conditions" (ask). The main VKS restrictions must be respected. |
| **Fiji** | Kaipuleohone Fiji items (Schütz, 1964) are index cards and linguistic material, no music. MAA Cambridge Roth tapes 1953–56 (ask, no audio confirmed online). Fijian 78s are in Great 78 / 78rpm_nsfa (known). National Archives of Fiji unreachable. |
| **Samoa** | Samoan 78s are in Great 78 (known). Radio/TV Samoa digitisation reported (USyd paper) but not online. Nothing else found. |
| **Tonga** | University of Auckland Archive of Māori and Pacific Sound holds Moyle's 1973–77 Radio Tonga material (post-1970). Roth 1953 lakalaka at MAA. Nothing open found. |
| **Cook Islands** | Nothing found online (no search budget left). |
| **French Polynesia** | The ICA was merged into the territorial archives in 2011; ica.pf and archives.pf unreachable. Himene recordings at CREM are partly restricted (known). |
| **Micronesia (FSM, Marshall Is., Guam, Palau, Kiribati)** | MicSem / EAP115: research only (ask). Kaipuleohone: Marshall Islands (Bender) and Micronesia items, mostly linguistic. |
| **Hawaiʻi** | Hawaiian Music Archives @ State Archives (ask: musicarchives@hawaii.gov). UH Mānoa Vogel collection: restricted to UH. Bishop Museum: catalogues offline (ask later). Hawaiian 78s are in Great 78 (2,599 hits in 78rpm; known). Kaniʻāina site unreachable (and mostly post-1970). |
| **Australia: Aboriginal and Torres Strait Islander** | AIATSIS: community-controlled access, blocked to me; **exclude**. Strehlow and similar sacred collections: exclude. NFSA: blocked. No openly licensed pre-1970 Indigenous recordings found. |
| **Australia: general folk** | NLA Meredith and other folklore collections (ask; Trove EULA). State libraries: restrictive or blocked. 78rpm_nsfa (known). |

---

## 3. Per-archive notes

### 3.1 Pratinada / DeCoSEAS (good fit as an index) [verified]
- Site: https://pratinada.net (a Vue app by LiSa Digit). Backend: **Omeka S public API** at `https://omekas.seasia-hearing.org/api/items` (no key; `per_page` up to 500; total in the `Omeka-S-Total-Results` header). Project: https://www.decoseas.org (UvA, CNRS, SOAS; funded by JPI Cultural Heritage).
- I downloaded all 16 pages (8,000 resources, 7,051 CreativeWorks). Holding institutions: CREM-LESC 6,234; UvA (Jaap Kunst) 596; British Library (BBC Empire Service) 78; BnF 59; Phonobase 6.
- Licence field `schema:license`: CREM "Public Domain" 1,393, "In Copyright, Non-Commercial Use Permitted" 4,820, CC BY-NC-ND 4. BnF Public Domain 59. Jaap Kunst NoC-OKLR 596. BBC 78.
- **Public-domain items dated 1970 or earlier: 1,301.** By country: Vietnam 612, Indonesia 355+5, Cambodia 95+20+3, PNG 79, Thailand 58, Laos 10+16, Philippines 22, Myanmar 7, Malaysia 1.
- Media: e.g. `https://omekas.seasia-hearing.org/files/original/8f8d25e5ae01008bec0c0a3d16c29f8774641776.wav` returned 206 audio/x-wav. Files are large WAVs.
- The site's own rights text: "Some Public domain archives are reusable in a non-commercial and respectful way … (NoC-CR)". Non-PD items are InC.
- Recommendation: use the API as a licence-annotated index of SE Asian CREM and Gallica items. Prefer streaming from CREM or Gallica (already known) so the crediting goes to the holding archive. The CREM copyright notice reads "For any use, please contact the CREM-LESC: crem.lesc[at]cnrs.fr". Feedback form: https://docs.google.com/forms/d/1k4tuA5PGToxPu9plV7HETdgWb81kvnpDAbAfgGDA9MA/viewform

### 3.2 Internet Archive `papuaculturalinheritance` (good fit, small) [verified]
- https://archive.org/details/papuaculturalinheritance : "The foundation PACE Papua Cultural Inheritance audio collection." 193 items, **all CC BY-NC-ND 4.0**, with MP3, FLAC and WAV files.
- Items dated 1970 or earlier: `band-147-middag-adatdansen-groen` / `-rood` (1962, Sentani flute and drum orchestra, tifa songs, adat dances); `band-140-cassette-kamma-e-12-a`/`-b` (1962, South Pacific music conference, Samoa/West Papua songs); `band-152-tape-1-programmas-a/b` and `band-153-tape-ii-programmas-a/b` (1955, 100 years of Protestant mission: speeches and choir songs).
- Caution: some tracks are mainly speech. West Papua is politically sensitive. Check each item before inclusion.

### 3.3 Jaap Kunst Collection, University of Amsterdam (ask permission + cultural flag) [verified]
- 596 items through Pratinada (469 with WAV media), 1920s–1934. Peoples: Jawa (Yogyakarta, kraton), Sunda (Priangan-Sumedang), Sikka, Manggarai, Lio, Nias, Toba Batak, Luwu/Bugis/Toraja, Ngada (Boawae), Atoni, Lamaholot, Minahasa, Moi, Iban, Rejang, Kei, Waropen and others.
- Rights: `NoC-OKLR` with this notice: "Use of the recorded material of the Jaap Kunst Collection is restricted by 1.) the third amendment to the Indonesian Constitution that recognizes the rights of Indigenous Peoples (Art. 18b-2), 2.) the recognition of rights of … Masyarakat adat …, and 3.) Indonesian National Cultural Heritage Laws …"
- Many of the Berlin originals are in the Berliner Phonogramm-Archiv. In 2024 UvA repatriated digital copies to East Nusa Tenggara (UvA news: https://aihr.uva.nl/shared/faculteiten/en/faculteit-der-geesteswetenschappen/news/2024/10/uva-returns-sound-recordings-and-film-footage-from-colonial-collection-to-indonesia.html ; project: https://decolonialfutures.uva.nl/projects/2024/digital-restitution-of-the-jaap-kunst-collection/digital-restitution-of-the-jaap-kunst-collection.html).
- Verdict: do not stream without UvA and community agreement. Contact through the DeCoSEAS team (Barbara Titus, meLê yamomo, UvA) via https://www.decoseas.org/people/.

### 3.4 Hawaiian Music Archives @ Hawaiʻi State Archives (ask permission) [verified site; holdings partial]
- https://musicarchives.hawaii.gov : a public-private partnership launched in 2022. Acquisitions include **Malcolm Rockwell's 6,000 78 rpm records** (compiler of *Hawaiian & Hawaiian Guitar Records, 1890–1960*), Harry B. Soria Jr.'s 12,000 records, and Michael Scott's 10,000 records. "Parts of which already are available to the public online" through https://digitalarchives.hawaii.gov/browse/parent/ark:70111/4jNC (Gene Becker sub-series M501, 1911–1952; the entries table failed to load in my test).
- Contact: **musicarchives@hawaii.gov** (decoded from the site); the digital archives use archives@hawaii.gov.
- Worth pursuing: US sound recordings published before 1926 are now public domain under the Music Modernization Act, and many acoustic-era Hawaiian 78s qualify. Some overlap with Great 78 is likely.

### 3.5 National Archives of Singapore (ask permission) [verified]
- Highlight set: https://www.nas.gov.sg/archivesonline/audiovisual_records/highlights/f3f86c2d-6764-11e5-ac2a-0050568939ad ("Sounds of Yesteryear (1903–1941)"). Example record 002bae00-6765-11e5-ac2a-0050568939ad: Odeon matrices OMH-704/705, Che Rahmah and Syed Ali Al'Atass, "Chatra Radin Mas", lagu Melayu / sya'ir.
- Access: "Access permitted" (online player), but "**Use and reproduction require written permission from copyright owner(s).**" No direct file URLs appear in the HTML; a `record-access` endpoint is logged.
- Other holdings: Radio Singapore 1959 (asli, dondang sayang, bangsawan), RTS 1959–68 ("come to the National Archives for full recording").

### 3.6 UP Center for Ethnomusicology (ask permission) [contacts verified]
- https://upethno.upd.edu.ph : the José Maceda Collection (UNESCO Memory of the World), about 2,500 hours of open reel and cassette from the Philippines, Malaysia and Indonesia, 1950s onward. Digitisation finished in 2014. Searchable at the UPCE Library; online access on request; use limited to "private study, scholarship, or research".
- Contacts: **upethno_archive.upd@up.edu.ph**, upethno.upd@up.edu.ph.
- Part of Maceda's material is at CREM (already known; check Pratinada for the PD subset).

### 3.7 Kaipuleohone, University of Hawaiʻi (ask permission) [verified]
- Community: https://scholarspace.manoa.hawaii.edu (handle 10125/4250, uuid 533eb99e-2e59-47a5-9c4e-652352bfc5a6). DSpace 7 REST: `/server/api/discover/search/objects?scope=…` and `/server/api/core/items/{uuid}/bundles?embed=bitstreams`.
- Example: PB1-011 (hdl 10125/32368), Enga, Kaane/Par, PNG, 1 March 1970, Paul Brennan: "Songs of the clan … singsing music … men (seated around fire) tidi pii". Files PB1-011-A.mp3 (28 MB) and WAV at `/server/api/core/bitstreams/052d4674-ddad-4fa4-82bf-2aa74df7a913/content`; the range request returned **206 with no login**.
- About 96 items carry issue dates of 1970 or earlier (Fiji/Schütz index cards, Marshall Islands/Bender, PNG/Brennan, Micronesia, French Polynesia, Niue, Kiribati). Only about 10 are songs.
- Rights: no per-item `dc.rights`. The community description says "Please read our Conditions of Access before downloading" (https://manoa.hawaii.edu/linguistics/wp-content/uploads/2024/02/Kaipuleohone-ConditionsOfAccess.pdf; my fetch failed). Search sources say materials are under Creative Commons licences.
- Contact: https://sites.google.com/hawaii.edu/laelabs/laboratories/kaipuleohone-archive (ScholarSpace help: sspace@hawaii.edu).

### 3.8 Micronesian Seminar / BL EAP115 (ask permission) [search-only; BL blocked]
- MicSem: https://micsem.org/special-collections/ (22,300 musical items: chants, laments, hymns, stringed-band music). EAP project: https://eap.bl.uk/node/2451 (sub-collections CEAP115-1 Chuuk, -2 Pohnpei, -7 Guam with 106 recordings, and others; "listen online to 7,069 recordings"; "research purposes only"). Library reopened on Yap in May 2025.

### 3.9 NLA Oral History and Folklore (ask permission) [search-only; catalogue blocked]
- Guide: https://www.library.gov.au/research/guides-and-resources/guides-selected-collections/meredith-collection ; catalogue record https://catalogue.nla.gov.au/catalog/2687559 (behind the Anubis proof-of-work check). Trove audio player requires accepting an EULA (https://trove.nla.gov.au/help/navigating/audio-player). The Trove API v3 returned 401 "No API key found".
- Per-item access conditions. Contact through NLA "Ask a Librarian".

### 3.10 Bishop Museum (ask later) [verified page]
- https://www.bishopmuseum.org/explore/library-and-archives/ : by appointment only; the library and archives catalogues are "temporarily unavailable while we transition to a new database system". Contact **archives@bishopmuseum.org**. Helen Roberts mele collection and Welo Hou digitisation (IMLS NG-04-17-0218-17). Chant can be culturally sensitive; consult Hawaiian cultural advisers.

### 3.11 UH Mānoa Hawaiian Music Collection [blocked / search-only]
- https://digital.library.manoa.hawaii.edu/collections/show/59 (Cloudflare Turnstile). Dirk Vogel collection: 6,000 78s, 2,650 LPs. Audio is "Restricted to UH Mānoa" or available in the Hamilton Library reading room.

### 3.12 AIATSIS, NFSA and state libraries (exclude / not usable) [blocked / search-only]
- AIATSIS (https://aiatsis.gov.au , Cloudflare challenge): access is case by case; "some materials may be subject to access conditions required by Indigenous communities and/or depositors". Example finding aids require CEO permission (Karajarri, MS 5151) or depositor permission (Moyle). **Exclude** under Old Folk Air's sacred and sensitive rule.
- NFSA (Cloudflare challenge): holds Meredith copies (https://www.nfsa.gov.au/collection/curated/sam-griffiths-jack-luscombe-john-meredith-folklore-collection).
- SLQ: audio "free to use for personal research and study" only; sheet-music dataset is CC BY 4.0 (https://www.data.qld.gov.au/dataset/music-queensland). SLNSW: 1968–72 South Pacific Festival reels "in copyright, reproduction restricted".

### 3.13 Bali 1928 Archives (not usable) [verified]
- https://bali1928.net/listen/ : "available … through Spotify, complete, live and free". https://bali1928.net/copyright/ : all material is protected. Copying, re-uploading, broadcasting or redistributing needs written permission from the archive and Dr. Edward Herbst (licences from RF-CUNY, UCLA Ethnomusicology Archive, the McPhee and Covarrubias estates, Dansmuseet). Contact form on the copyright page (manager Marlowe Bandem).

### 3.14 Other "not usable" notes
- **Lokananta**: digital library with funding from the Djarum Foundation (60 albums at launch, samples only). Tracks are on Spotify, Joox and YouTube. lokanantamusik.com redirects to rekaman.com. https://www.cnnindonesia.com/hiburan/20160201191051-227-108149/lokananta-buka-perpustakaan-musik-digital [search-only]
- **Filipinas Heritage Library Himig**: https://www.filipinaslibrary.org.ph/himig/the-himig-collection/ (403). Spotify playlists; researcher requests to asklibrarian@filipinaslibrary.org.ph [search-only]
- **Bophana**: https://bophana.org/archives/ (HTTP 500). Copies require written agreement from rights holders [search-only]
- **Leiden KITLV SE Asian Pop Music**: https://digitalcollections.universiteitleiden.nl/southeastasianpopmusic (CAPTCHA) [blocked]
- **IA `indonesian-cassette-archive`**: 167 items, 1980s commercial cassettes, no licence [verified]
- **IA `uclaethnomusicologyarchive`**: 7,411 items, `stream_only`, "Copyrighted. Rights are owned by …" [verified]
- **UW ResearchWorks**: e.g. item 452fe9b8-bb6d-49ca-b240-1f329a7f9089 (1967 Burmese music at Wat Ommarāwāt) has a PDF index only [verified]
- **IPNGS Music Archive** (PNG): 10,000+ hours including repatriated recordings back to 1898; nothing online [search-only]
- **University of Auckland Archive of Māori and Pacific Sound**: Tonga material is 1973–77 [search-only]
- **MAA Cambridge Roth Fiji tapes**: https://collections.maa.cam.ac.uk/documents/11103 (catalogue page reachable; no audio links seen) [partial]

### 3.15 New sub-collections of already-known archives
- **BL EAP**: EAP1213 Sounds of Siam (2,429 tracks, 1903–1960s; https://eap.bl.uk/node/4316); EAP115 Micronesian music; Burmese recordings project (https://eap.bl.uk/node/3176).
- **CREM-CNRS**: Timor-Leste 1960s (142 CC BY-NC-ND items + 191 InC-NC); the SE Asian PD subset of 1,242 items is indexed by licence through the Pratinada API.
- **PARADISEC**: VKS2 (Vanuatu Kaljoral Senta, "open subject to PDSC conditions"); VKS is closed.
- **Great 78 / 78rpm**: regional counts as of today: "Hawaiian" 2,599; "Siamese" 38; "Tagalog" 29; "Maori" 34; "Burmese" 8; "Malay" 2; Fijian 14 in 78rpm_nsfa. No new collection ids beyond the known ones.

### Internet Archive collection ids from this region
- `papuaculturalinheritance`: good fit (8 items dated 1970 or earlier; CC BY-NC-ND)
- `indonesian-cassette-archive`: not usable
- `uclaethnomusicologyarchive`: not usable (copyright, stream-only)
- Keroncong, gamelan and lagu daerah uploads are scattered across `opensource_audio` / `community` with no rights information, so they are not usable.

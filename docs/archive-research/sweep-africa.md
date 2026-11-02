# Archive sweep: sub-Saharan Africa

Date: 2026-11-02. Status labels: **verified** (I loaded the page or file with curl or an API from the sandbox), **search-only** (seen only in web-search results), **blocked** (the host refused the sandbox, timed out, failed TLS, or showed a bot check).

**Coverage caveat.** The web-search budget for this turn ran out partway through the sweep. It is a limit of 200 searches shared by all agents. After that I could only use curl on sites I already knew. These countries got no country-specific search: Chad, CAR, Equatorial Guinea, São Tomé and Príncipe, Guinea-Bissau, Togo, Mauritania, Djibouti, Eritrea, South Sudan, Burundi, Botswana, Lesotho, Eswatini, Comoros and Seychelles. For them the checklist reports what the cross-cutting archives hold (CREM, RMCA Tervuren, ILAM, BL). A follow-up pass could fill those gaps.

**Headline finding.** I found **no new source that is a clean "good fit"**: open per-item licence or public-domain statement, plus directly streamable files. Africa's historical recordings are mainly held in three kinds of places:
- national broadcasters and universities in Africa, which almost never put audio online;
- European colonial-era institutions (RMCA Tervuren, CREM, BL, Berlin, Basel), which offer clips only, sign-in, or rights reserved;
- small project sites whose rights are unclear.

The best new leads all need permission.

---

## 1. Ranked table

| # | Archive | Content | Audio access | Rights | Machine access | Verdict | Status |
|---|---|---|---|---|---|---|---|
| 1 | **RMCA / AfricaMuseum Tervuren: DEKKMMA ethnomusicology sound archive** (Belgium) | About 3,000 h of Central African field recordings from 1910 (Hutereau Edison cylinders) onward, mostly DR Congo and Rwanda. The catalogue returns, for example, 14,653 hits for "Congo", 4,203 Rwanda, 1,666 Uganda, 1,392 Burundi, 647 Angola, 537 Cameroon and 467 Gabon. Free-text counts, so approximate | **30-second MP3 clips only** (96 kbps, `music.africamuseum.be/recordings/<inv>.mp3`, no login, Range supported). Full recordings by order form (max 50 per month) | Copies "strictly reserved for… scientific research or private use". Any "uploading online" or "public broadcast" needs written consent. **Where RMCA holds the copyright it grants permission under CC BY 4.0.** Explicit note on culturally sensitive content and community agreements | HTML only (2004-era PHP search: `get_recordings2.php?freeText=`, `detailrec.php?id=`) | **Ask permission.** The highest-value African lead. Contact: **repro@africamuseum.be** (Repro Secretariat, Leuvensesteenweg 13, 3080 Tervuren) | Verified |
| 2 | **SierraLeoneHeritage.org: Cootje van Oven collection** (+ BL wax cylinders) | **236 ethnomusicological recordings, Sierra Leone 1964–1977** (Fula, Mende, Temne, Limba, Soso…), each with performer, place and date. The site also streams **110 "Wax Cylinder Recordings"** from the British Library (a BL sub-collection) | **Full-length direct MP3** (`/storage/cootje_van_oven_collection/audio/cvo_001.mp3`, about 10 MB, `audio/mpeg`, Range supported), no login | Only a site footer "Copyright ©". No per-item licence. Built by an AHRC project (Paul Basu, SOAS/UCL, 2009–12) with the Sierra Leone National Museum | HTML; stable item URLs `/item/CVO:001/...`; facet search | **Ask permission.** Technically ideal. Contact: **info@sierraleoneheritage.org** (van Oven's originals are reportedly held in Leiden, search-only) | Verified |
| 3 | **Music Time in Africa Archive** (Univ. of Michigan, VOA Leo Sarkisian collection) | 691+ VOA radio programmes from 1966–96 with scripts, plus a digitised selection of Sarkisian's own **field recordings from sub-Saharan Africa (from the early 1960s)**. About 10,000 carriers are being digitised | Streaming interface (mtia.mivideo.it.umich.edu). **Behind a Cloudflare JS challenge to the sandbox** | Rights page reportedly says users must make "your own legal assessment and secur[e] any necessary permission". VOA-made material is a US federal work (no US copyright), but the commercial records played inside programmes are not | Unknown | **Ask permission.** Ask for the Sarkisian field-recording selection only. Contact: **musictimeinafrica@umich.edu** | Blocked (bot check); search-only |
| 4 | **UCLA MEAP: African audio projects** | Ibadan Institute of African Studies audiovisual archive (Nigeria: Ijaw Ozidi epic, Urhobo udje songs, Yoruba Gelede, Duro Ladipo theatre). Also Nsukka Igbo ethnography tapes (2,000+ tapes, but **1972–83**), Radio Rurale de Kayes, Mali (**1988+**), and CEA Maputo | Hosted in UCLA Library Digital Collections | Unknown (not reachable) | UCLA DL has a Blacklight JSON/IIIF stack (unverified) | **Ask permission** for Ibadan only (others fall outside the date range). Use the contact route on the MEAP site | **Blocked:** `digital.library.ucla.edu` fails TLS in the sandbox; MEAP project page verified |
| 5 | **NCAC National Digital Archive of The Gambia** (NCAC + Univ. Hamburg FDR) | Oral history and griot (jali) recordings from 1956 onward, started by Bakari Sidibe. **1,091 records** in the Hamburg FDR repository ("Griot Songs", "Hunters' Songs"…) | **All records `restricted`**; files not public | "Permit granted by NCAC… Access upon request" | **InvenioRDM REST API, no key** (`fdr.uni-hamburg.de/api/records/?q=NCAC`) | **Ask permission (low odds)**: research-permit model. Contact: **sfadera@ncac.gm / info@ncac.gm** | Verified |
| 6 | **J.H. Kwabena Nketia Archives** (Univ. of Ghana IAS) | Nketia's 1950s–60s Ghanaian field recordings plus BAPMAF popular music; "thousands of hours" digitised since 2014 | **Not online.** The UGSpace community holds only 4 modern items (2015–2021) | UGSpace items are "educational, research, and non-commercial use" | DSpace 7 REST (verified) | **Ask permission**: worth an inquiry via the IAS (ias.ug.edu.gh) | Verified (UGSpace); search-only (archive) |
| 7 | **Basler Afrika Bibliographien** (Basel) | Ernst and Ruth Dammann collection: about 750 recordings, Namibia **1953–54** (songs, poetry, narratives, about 200 named people). Also about 1,000 sound documents for Namibia and southern Africa from 1952 on, and about 900 Nigerian shellac discs from the 1930s–50s (about 200 digitised) | On-site listening only; namibia1953.com shows profiles plus a few SoundCloud embeds | Restricted; family and community oriented | Online catalogue only | **Ask permission (low odds)**: personal, family-linked recordings. Contact: **archive@baslerafrika.ch** | Search-only; namibia1953.com verified; BAB site returned 522 |
| 8 | **Tanzania Heritage Project / Radio Tanzania Dar es Salaam archive** | RTD tapes from the 1960s onward: taarab, ngoma, dansi, Bi Kidude… 15,000+ reels claimed | **SoundCloud only** (soundcloud.com/tanzaniaheritageproject) | "Legal rights… are murky" (TBC is state-owned) | None | **Not usable** as it stands (SoundCloud only). Possible outreach target | Verified (site); search-only (rights) |
| 9 | **Lautarchiv, Humboldt-Universität Berlin** | WWI prisoner-of-war recordings, 1915–18, including West and Central Africans (e.g. Igbo song PK 864/1) | Audio icons present but behind **"Sign in for research access"** | Sensitive coerced-recording context | HTML (Kabinette des Wissens DB) | **Not usable** (login; ethically sensitive). Contact if ever needed: lautarchiv@hu-berlin.de | Verified |
| 10 | **Gramophone Records Museum and Research Centre of Ghana** (Cape Coast) | 18,000+ 78s, mostly highlife from the 1920s–60s | Digitisation project (Langlois Foundation, 2003); **no online audio found** | Unknown | — | **Not usable** (not online) | Search-only |
| 11 | **African Music Archives (AMA), Mainz** | About 20,000 recordings of African popular music from the 1940s onward (shellac, vinyl, tape) | Catalogue in the Mainz UB OPAC only | Label-owned | OPAC | **Not usable** | Search-only |
| 12 | **AMIRA / Iwalewahaus Bayreuth** | Omeka-S portal; "Beyond the Digital Return" (ILAM and GBC-library Yoruba records) | **Only 40 audio media in the whole portal**, recent uploads | Unknown | Omeka-S API, no key (verified) | **Not usable** (tiny and recent) | Verified |
| 13 | **Indiana Univ. Archives of Traditional Music** / Media Collections Online | Largest US holding of African field recordings | MCO's public "Africa" hits (296) are lectures and radio talks. **No public African field audio found** | — | MCO Blacklight JSON (verified) | **Not usable** (IU-only access) | Verified |
| 14 | National broadcasters: **SABC, KBC, NBC/FRCN, GBC, RTS (Senegal), ORTM (Mali), CRTV (Cameroon), Rádio Moçambique, Radio Hargeisa, Sudan Radio, Radio Omdurman, Radio Madagasikara, MBC (Mauritius)** | Large 1950s–70s holdings | **None has a public online audio portal.** SABC catalogue is "not open to public (Internet)"; material is sold or licensed through sales | Broadcaster-owned | — | **Not usable** (not online) | Search-only |
| 15 | Other checked, not usable | Sudan Tapes Archive (diaspora cassettes, mostly post-1970, social media); Red Sea Foundation / Ostinato Somali tapes (commercial reissue); Tamasha Records Kenya (commercial streaming); ntama.de SLBS gramophone survey (text only); Excavated Shellac (collector blog with MP3s, no rights); Diamang Digital (HTTP 503); Radio Rurale de Kayes (1988+); EAP602 Harar (Derg era, 1974+); African Cassettes Mainz (cassette era) | — | — | — | **Not usable** | Mixed (see notes) |
| — | **Blocked hosts** | ILAM `www.ilam.ru.ac.za` and SAMAP `www.samap.ukzn.ac.za` (proxy 502); Stellenbosch `digital.lib.sun.ac.za`, UCT `digitalcollections.lib.uct.ac.za` and RMCA `archives.africamuseum.be` (connection reset); `digital.library.ucla.edu` (TLS); `mtia.*.umich.edu` (Cloudflare); `digital.soas.ac.uk` (403); Cambridge Apollo (429) | | | | Not assessed | Blocked |

### New sub-collections of already-known archives (noted, not re-researched)
- **British Library / EAP:**
  - *Syliphone / Radio Télévision Guinée* (EAP187, EAP327 and EAP608): about 8,000 tracks released 1958–1984 on BL Sounds (search-only).
  - 110 BL **wax cylinders of Sierra Leone**, mirrored with direct MP3 on SierraLeoneHeritage.org (verified).
  - **Klaus Wachsmann Uganda** recordings: BL online gives only the opening seconds. Copies are at MAKWAA, Makerere (search-only).
  - EAP602 Harar City Museum (post-1974).
- **CREM-CNRS** (already known, legally gated) holds much African material:
  - *Folclore musical de Angola* (Museu do Dundo / Diamang) corpus;
  - Ferry, Senegal 1964;
  - Rouch–Rosfelder, Mali 1950–51;
  - IRSM Madagascar 1959–60;
  - the "Archives sonores du Burkina Faso" preparatory corpus;
  - Pierre Sallée, Gabon;
  - Simha Arom, CAR;
  - Jenkins, Ethiopia 1969, and Azmari 1967;
  - OCORA LPs ("Restreint (enregistrement édité)").
- **Internet Archive** (search verified): African content is thin and scattered.
  - Afrikaans 78s (Jannie Viljoen, Hendrik Susan; items such as `by-die-vaal-rivier-final`).
  - A few South African 78s in `78rpm_ucsb` (e.g. `78_kwela-rich_richard-nombali-r-nombali_gbia0133969b`, `78_baby-are-yeng_softown-bombers-strike-vilakazi_gbia0133967b`).
  - Folkways 78s in Great 78 (e.g. `78_a-ethiopian-lyre-b-ethiopian-lyre_harold-courlander_gbia8004218`).
  - The single upload `07Bam109DongboSoChantDeSocieteSecrete` ("Recordings from Mission Ogooué-Congo (1948)"). **Flag:** it contains a secret-society chant.
  - Collection `voa-music-time-in-africa` holds only 2006–08 broadcasts (out of range).
  - There is no collection dedicated to African 78s or field recordings.
- **Library of Congress** AFC: Halim El-Dabh's Ethiopia tapes (1964); Ghana collections (search-only).
- **Phonogrammarchiv Vienna:** Kubik's Angola and Mozambique recordings; repatriation to Madagascar (2012).

---

## 2. Country checklist

| Region | Country | Result |
|---|---|---|
| West | Benin | Nothing online locally. CREM (Fulani of N. Benin, others). Vodun material is sacred: flag |
| West | Burkina Faso | Copy of the "Archives sonores du Burkina Faso" repatriated in 2007 to the Musée de la musique de Ouagadougou; metadata on CREM. No open audio |
| West | Cape Verde | Nothing found online (no national or RCV sound portal); CREM/IA morna items are modern |
| West | Côte d'Ivoire | Nothing online locally. CREM (Zemp, Lannoy Senufo 1976–89); MEG Geneva Bouaké CD (commercial) |
| West | The Gambia | **NCAC National Digital Archive**: 1,091 restricted records, permit on request (row 5) |
| West | Ghana | Nketia Archives (not online, row 6); Gramophone Records Museum (not online); GBC library (not online); IA scattered |
| West | Guinea | Syliphone/RTG: about 8,000 tracks on BL Sounds (known BL) |
| West | Guinea-Bissau | No dedicated search (budget). Appears only incidentally in NCAC/CREM |
| West | Liberia | Nothing online found. Excavated Shellac blog (not usable); IU Liberian Collections (not public) |
| West | Mali | Kayes rural radio archive via MEAP (post-1988); ORTM not online; CREM (Rouch 1950–51) |
| West | Mauritania | No dedicated search (budget). CREM only |
| West | Niger | Nothing online locally. CREM (Fulani, Hausa) |
| West | Nigeria | MEAP Ibadan IAS (ask, row 4); MEAP Nsukka Igbo (1972–83); NBC/FRCN and NFVSA Jos not online; BAB Nigerian shellac (on-site); Lautarchiv Igbo POW recordings (login) |
| West | Senegal | RTS not online; IFAN Kesteloot archive (1,400 items digitised, online release pending, search-only); CREM (Ferry 1964) |
| West | Sierra Leone | **SierraLeoneHeritage.org**: van Oven 236 recordings 1964–77 + BL cylinders, direct MP3 (ask, row 2); SLBS gramophone library destroyed (German copies, not online) |
| West | Togo | No dedicated search (budget). Nothing found |
| Central | Angola | Museu do Dundo/Diamang *Folclore musical de Angola* (in CREM; Diamang Digital 503); 640 Lunda songs digitised 2024 (not online); RMCA 647 hits (clips) |
| Central | Burundi | No dedicated search (budget). RMCA 1,392 hits (clips) |
| Central | Cameroon | CRTV archive being digitised with Iwalewahaus (not online); RMCA 537 hits; CREM |
| Central | CAR | No dedicated search (budget). CREM (Arom; BaAka/Aka recordings, sensitive) |
| Central | Chad | No dedicated search (budget). CREM only |
| Central | Congo (Brazzaville) | Former national broadcaster tapes being rescued by volunteers (not online); IA single 1948 Ogooué-Congo item |
| Central | DR Congo | **RMCA Tervuren**: the bulk of the 3,000 h, 1910 onward, 30-second clips (ask, row 1) |
| Central | Equatorial Guinea | No dedicated search (budget). Nothing found |
| Central | Gabon | 2005 copy of Pierre Sallée's recordings given to Gabon (national sound archive). CREM; RMCA 467 hits |
| Central | Rwanda | RMCA: 4,203 hits; 4,000+ recordings repatriated in 2021 to the Rwanda Cultural Heritage Academy (not online) |
| Central | São Tomé and Príncipe | No dedicated search (budget). Nothing found |
| East/Horn | Djibouti | No dedicated search (budget). Some Radio Hargeisa tapes were hidden there (not online) |
| East/Horn | Eritrea | No dedicated search (budget). Italian ICBSA colonial holdings not found online |
| East/Horn | Ethiopia | No national online sound archive. CREM (azmari 1967; Jenkins 1969); LoC El-Dabh 1964; EAP602 Harar (post-1974); Ethiopiques (commercial) |
| East/Horn | Kenya | KBC archive not online or digitised; Tamasha (commercial); RMCA 454 hits |
| East/Horn | Somalia / Somaliland | Radio Hargeisa / Red Sea Foundation tapes (10,000+, commercial Ostinato reissue only); IU ATM Andrzejewski (not public) |
| East/Horn | South Sudan | No dedicated search (budget). Nothing found |
| East/Horn | Sudan | Univ. of Khartoum folklore archive (3,000+ recordings since 1963; digitisation project, not online); SRTC Sudan Memory (video only); Sudan Tapes Archive (cassettes) |
| East/Horn | Tanzania (incl. Zanzibar, taarab) | Radio Tanzania / Tanzania Heritage Project (SoundCloud only, row 8) |
| East/Horn | Uganda | Wachsmann: BL clips; MAKWAA Makerere (not online); RMCA 1,666 hits |
| Southern | Botswana | No dedicated search (budget). ILAM blocked |
| Southern | Eswatini | No dedicated search (budget). ILAM blocked |
| Southern | Lesotho | No dedicated search (budget). ILAM blocked |
| Southern | Malawi | Nothing found online (search covered CABS/MBC); ILAM blocked |
| Southern | Mozambique | Rádio Moçambique archive (78s studied, not online); MEAP CEA Maputo (political recordings) |
| Southern | Namibia | BAB Dammann 1953–54 (on-site; namibia1953.com profiles, row 7). San recordings: flag |
| Southern | South Africa | SABC not public online; ILAM, SAMAP, Stellenbosch and UCT hosts all blocked to the sandbox; IA has scattered Afrikaans and kwela 78s |
| Southern | Zambia | CABS Lusaka broadcasts at SOAS (reference only); BAB copies at the National Archives of Zambia; RMCA 480 hits |
| Southern | Zimbabwe | Nothing found online (ZBC); ILAM blocked |
| Indian Ocean | Comoros | No dedicated search (budget). CREM only |
| Indian Ocean | Madagascar | CREM (IRSM 1959–60, Madagascar 1929–31 78s compilation: metadata only); Vienna repatriation 2012; BnF item "non diffusable" |
| Indian Ocean | Mauritius | MBC/National Archives not online; "NOUZIK" lovable.app pages (unverified promo); CREM séga LP (restricted) |
| Indian Ocean | Seychelles | No dedicated search (budget). Nothing found |

**Peoples and cultures checked through the above:**
- Yoruba (Ibadan MEAP, BAB shellac, AMIRA GBC records).
- Igbo (Nsukka MEAP, out of date range; Lautarchiv POW recordings).
- Hausa and Fulani (CREM).
- Akan (Nketia).
- Mande/griot (NCAC Gambia, Syliphone).
- Wolof (CREM, RTS).
- Zulu, Xhosa and Sotho (SABC; ILAM blocked).
- Shona (ILAM blocked).
- Swahili/taarab (Radio Tanzania).
- Amhara/azmari (CREM 1967).
- Somali (Radio Hargeisa).
- BaAka/Baka (CREM Arom; Sarno recordings are 1980s+).
- San (BAB/Namibia; ILAM).

None has an open heritage audio portal for pre-1971 recordings that I could find.

---

## 3. Per-archive notes

### RMCA / AfricaMuseum Tervuren (DEKKMMA), ask permission (verified)
- Portal: https://music.africamuseum.be/english/index.html (frames). Search: `https://music.africamuseum.be/english/get_recordings2.php?freeText=Rwanda` returns "4203 recordings found", 20 per page via `list_recordings.php?&partnr=N`, which needs a session cookie.
- Detail page: `detailrec.php?id=MR.1954.1.1-1`. Example record: Rwanda, Kigali, Mazane, Hutu, April 1954, collector "Zending Scohy-Stroobants", hunting song, full length 2:57. Fields cover language, title, performers, instruments and tempo.
- Clip: `https://music.africamuseum.be/recordings/MR.1954.1.1-1.mp3`: HTTP 200, `audio/mpeg`, 361,642 bytes, **30.05 s**, 96 kbps, `Accept-Ranges: bytes`. Many older items (e.g. `HR.1974.58.*`) show "N.A." (no clip).
- Terms (https://www.africamuseum.be/en/audio_order_form, verified):
  - working copies are for "scientific research or private use"; "publication, public broadcast, uploading online… requires the prior written consent of the rights holders";
  - "If the RMCA holds the copyright… This written authorisation will be granted via the CC-BY license";
  - the archives "may contain culturally sensitive material… must therefore not be subject to any form of appropriation that could harm the indigenous communities".
- Context: in 2021 RMCA gave a disk of 4,000+ Rwandan recordings to the Rwanda Cultural Heritage Academy. A Gradhiva article (https://journals.openedition.org/gradhiva/8659) says colonial metadata can be "false, incomplete or even racist", which slowed online release. The Be-MUSIC project (with the Brussels MIM) aims to put collections online.
- Contact: repro@africamuseum.be. Ask whether full-length streaming, or a hosted copy under CC BY, is possible for recordings where RMCA holds the rights.
- Sources: https://music.africamuseum.be/english/project.html, https://www.africamuseum.be/en/research/news/africamuseum_transfers_over_4000_sound_recordings_to_rwanda

### SierraLeoneHeritage.org, ask permission (verified)
- Example item: https://www.sierraleoneheritage.org/item/CVO%3A001/fula-bow-music. Fula bow music, recorded 1964-08-27 in Freetown by Cootje van Oven and John Welford, performer Amada Bah.
- Audio: `https://www.sierraleoneheritage.org/storage/cootje_van_oven_collection/audio/cvo_001.mp3`: HTTP/2 200, `audio/mpeg`, 10,536,649 bytes, `accept-ranges: bytes`, full length.
- Facets: "Cootje Van Oven Collection (236)", "Sound Recording 236", "Wax Cylinder Recordings 110", "British Library (110)".
- No licence statement anywhere; footer reads "Copyright © 2026". Created by "Reanimating Cultural Heritage" (AHRC 2009–12, directed by Prof. Paul Basu, SOAS) with the Sierra Leone National Museum, the British Museum, the BL Sound Archive and others.
- Contact: info@sierraleoneheritage.org. Ask whether the van Oven recordings can be streamed by a non-commercial app with attribution, and who holds the rights: the van Oven estate, Leiden (search-only), or the performers' communities.

### Music Time in Africa Archive (UMich), ask permission (blocked)
- `mtia.mivideo.it.umich.edu` returned "Just a moment… Enable JavaScript and cookies" (Cloudflare) and HTTP 403; `mtia.labs.si.umich.edu` timed out. The finding aid at findingaids.lib.umich.edu returned 403.
- From search: 691 programmes with audio, 1966–1996; the Sarkisian collection (220 linear ft) includes a "digitized selection of Leo Sarkisian's field recordings". A 2008 VOA–UMich agreement covered 360 field recordings. NEH funded the work with $260,000.
- Contact: musictimeinafrica@umich.edu.
- Sources: https://www.voanews.com/mtia-archive, https://radiopreservation.org/2021/03/15/music-time-in-africa-archive-delivers-voice-of-america-radio-show/

### UCLA MEAP African projects, ask permission (partly blocked)
- Verified project page: https://meap.library.ucla.edu/projects/ibadan-audiovisual-archives/. It links to collection `https://digital.library.ucla.edu/catalog/ark:/21198/z1jd6x7k`, which failed TLS verification in the sandbox (I did not bypass it).
- Other MEAP African audio projects (search-only, out of the date range):
  - Nsukka Igbo ethnography, 1972–83: https://meap.library.ucla.edu/projects/digitization-of-audio-recordings-of-ethnography-of-the-igbo-people
  - Radio Rurale de Kayes, 1988+: https://digitalorientalist.com/2024/10/04/digitising-the-archives-of-a-rural-radio-in-mali-a-transnational-project/
  - Eloyi cassettes.
- Rights: unknown. Follow up once the host is reachable.

### NCAC National Digital Archive of The Gambia, ask permission, low odds (verified)
- `https://www.fdr.uni-hamburg.de/api/records/?q=NCAC` returns total 1,091, every one `access_right: restricted`. The access condition reads: "Permit granted by National Centre for Arts and Culture (NCAC) of The Gambia. Access upon request: sfadera@ncac.gm or info@ncac.gm".
- Project page: https://www.aai.uni-hamburg.de/afrika/forschung/ncac-nda.html. The archive began in 1956 (Bakari Sidibe). Background: https://vad-ev.de/en/2019/03/04/the-ncac-national-digital-archive-the-gambia/

### Nketia Archives, University of Ghana: ask permission (partly verified)
- UGSpace community `6ed19391-c134-47a5-99e3-039114d0efe7`: the DSpace REST API lists 4 items, all modern (Kusaal Covid song 2021; Leteh language recordings 2015–21).
- Sources: https://ugspace.ug.edu.gh/communities/6ed19391-c134-47a5-99e3-039114d0efe7, https://www.journal.iasa-web.org/index.php/pubs/article/view/101

### Basler Afrika Bibliographien, ask permission, low odds
- namibia1953.com (verified) lists names and a few profiles made with families, with some SoundCloud embeds. It says: "You can email the archive in Basel to request… sound recordings".
- Sources:
  - BAB sound collection: https://map.baslerafrika.ch/library-holdings/sound-collection/ (522 to the sandbox)
  - University of Basel news: https://www.unibas.ch/en/News-Events/News/Uni-Research/New-platform-makes-Namibia-Archive-available-online.html
  - Nigerian shellac (via ZASB Basel): https://zasb.unibas.ch/en/research/resources/audio-and-video/
- Contact: archive@baslerafrika.ch.

### Tanzania Heritage Project / Radio Tanzania, not usable
- https://www.tanzaniaheritageproject.org/ (verified): audio only via soundcloud.com/tanzaniaheritageproject.
- Background: https://www.npr.org/2012/11/03/164174946/radio-tanzania-a-disappearing-history-on-tape

### Lautarchiv HU Berlin, not usable
- https://www.lautarchiv.hu-berlin.de/en/objekte/lautarchiv/53142/ (verified): Igbo song, narrative and numbers (PK 864/1–3) shown with audio icons, page header "Sign in for research access".

### Broadcasters (all not usable; search-only)
- SABC: catalogue "not open to public (Internet)"; access through on-site Dalet/Dira, with sales and licensing. https://libguides.sun.ac.za/SAMusicCollections/SABCRadio
- Others:
  - KBC (neglected, not digitised): https://peopledaily.digital/insights/world-day-for-audiovisual-heritage-kenyas-tapes-face-extinction/amp
  - NBC/FRCN: no portal found.
  - RTS Senegal (Ibrahima Wane warns of loss): https://senego.com/ibrahima-wane-lance-un-cri-dalerte-sur-la-memoire-sonore-du-senegal_1832293.html
  - CRTV Cameroon (digitisation with Iwalewahaus): https://deutschland.de/en/topic/culture/communication-media/cameroonian-sound-preservationists
  - Radio Hargeisa: https://english.ahram.org.eg/News/537131.aspx
  - Sudan / Khartoum folklore archive: https://ich.unesco.org/en/assistances/digital-preservation-of-folklore-and-traditional-music-archives-phase-i-00488
  - Rádio Moçambique (78 rpm study only).

### Other verified checks
- AMIRA Bayreuth: `https://amira.africamultiple.uni-bayreuth.de/api/media?media_type=audio/mpeg` returns `omeka-s-total-results: 40` (e.g. a 2026 upload "Fendler_mixdown.mp3").
- IU MCO: `https://media.dlib.indiana.edu/catalog.json?q=Africa` returns 296 hits, mostly lectures and radio talks from the 1950s–60s; no ATM African field recordings in the public results.
- ntama.de SLBS page: text survey of the 1986 copying project, no audio.

### Cultural-sensitivity flags for the app
- RMCA, CREM and BL hold initiation, secret-society and ritual recordings (Central Africa, Vodun Benin, Poro/Sande Sierra Leone).
- Recordings of San and BaAka/Aka peoples need community-consent consideration.
- WWI POW recordings (Lautarchiv) were made under coercion.
- The IA item `07Bam109DongboSoChantDeSocieteSecrete` is labelled as a secret-society chant.
- RMCA's own terms explicitly reserve community-protective conditions.

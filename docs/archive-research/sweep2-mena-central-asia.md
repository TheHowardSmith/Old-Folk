# Archive sweep, second pass: Middle East, North Africa and Central Asia (gaps)

Sweep date: 2026-11-02. This pass covers only the gaps listed in the first pass ([sweep-mena-central-asia.md](sweep-mena-central-asia.md), section 3.13 and the "never ran" note):
- Egypt and the Maghreb, searched in Arabic and French.
- Iraqi maqam.
- Oman.
- The Gulf states and Yemen.
- Turkey (TRT, university archives, Ottoman 78 projects).
- Tajikistan and Turkmenistan.
- Kurdish, Amazigh, Gnawa, Nubian, Bedouin and Assyrian heritage projects.

Searches ran in Arabic, French, Persian, Turkish, Russian and English. I treated every archive named in the first pass and in the brief's "already known" list as known. I only add to them where something new turned up.

**Status tags:**
- **verified:** I fetched the page and/or tested an audio URL with curl.
- **search-only:** known only from search results.
- **blocked:** the host refused, timed out or showed a bot check. I did not try to get around any block.

**Headline finding:** the first pass rated the Tunisian Phonothèque Nationale (CMAM) "on-site only". That is out of date. It now runs a public Telemeta catalogue at phonotheque.cmam.tn, the same software CREM-CNRS uses. The catalogue holds 15,866 items. 1,469 of them are marked "full" access, with direct MP3 files that play without login. Among them are pre-1934 Tunisian 78s and 1920s Egyptian 78s. Its legal notice forbids copying audio and commercial use unless explicitly allowed, so this is the region's strongest **ask-permission** candidate.

---

## 1. Ranked table

| # | Archive | Country / people | Content (rough) | Audio access | Rights | Verdict | Status |
|---|---|---|---|---|---|---|---|
| 1 | **Phonothèque Nationale de Tunisie, online catalogue** (CMAM, Ennejma Ezzahra), phonotheque.cmam.tn | Tunisia: malouf, Tunisian Jewish singers and Bedouin/southern field recordings. Also Egyptian, Algerian and other Arab 78s, the 1932 Cairo Congress, and copies of the Lautarchiv WWI POW recordings of Tunisian prisoners | 15,866 items in 482 collections and 25 corpora. Of these, 1,469 are "full" access, 507 "viewable" and 13,196 "metadata only". The "Tunisie avant 1934" corpus alone has 53 h 28 min (78s by Acher Mizrahi, Ahmed Ellouz, Ali Ben Arfa, Bechir Fahmi, the Banat Chammama, Messaoud Habib, Musique Beylicale and others) | **Direct MP3/OGG/FLAC** at `/archives/items/download/<code>.mp3` for full-access items. Tested D-0055 (Wadouda el-Manialawiya, Gramophone 1924): 206 `audio/mpeg`, no login. Items marked access "none" serve no audio | Legal notice: "AUDIO FILE COPYING FORBIDDEN IF NOT EXPLICITLY ALLOWED / COMMERCIAL USING OF THE ARCHIVES FORBIDDEN IF NOT EXPLICITLY ALLOWED", plus "usage … in respect of cultural heritage of the original communities". No per-item licence. Exclude Quranic and Sufi items, and treat the POW recordings as sensitive | **Ask permission** (top regional candidate): Manoubia Hermi, head of archives, manoubia.hermi@cmam.tn; also info@cmam.tn; tel. +216 71 740 102 | verified |
| 2 | **Excavated Shellac** (Jonathan Ward's collector blog; first pass listed it only as unassessed) | 78s from Egypt (4 posts), Morocco (3), Algeria (3), Tunisia (2), Iraq (3), Iraqi Kurdish (2), Iraqi Turkmen (1), Iran (2), Lebanon/Levant (3), Syria (2), Turkey (7), Oman (1), Tajikistan (1), Uzbekistan (2), Azerbaijan (3), Armenia (3), Georgia (2), Kazakhstan (2), Mauritania (1) | About 47 region posts, about 1905 to the 1950s. Each post is one disc side with notes | Each post links to a **Box.com share** (e.g. `app.box.com/s/gq8vqij870rnkzas6r6m`). There is no stable direct MP3 URL, and a Box share page is not a streamable file | No rights or licence statement. Transfers from the collector's own discs, offered as free downloads "in a spirit of … sharing" | **Ask permission** (small, but the only source found for Tajik, Turkmen-Iraqi and Omani 78s): excavatedshellac@yahoo.com | verified |
| 3 | **SALT Research, Cemal Ünlü Collection** (archives.saltresearch.org; new detail on the first pass's EAP lead) | Late-Ottoman and early Republican Turkish 78s | Many disc records, each with label and matrix data and 2 label scans. The British Library's EAP1637 page says the Ünlü project will digitise more than 3,000 discs | Search results show **label scans and metadata only**. No audio seen. Host returned **403** to me | Records say "Open Access" with Cemal Ünlü as rights holder. Credit Salt Research; ask them for any use beyond personal research | **Ask permission**, once audio exists: salt.research@saltonline.org | search-only / blocked |
| 4 | **Darülelhân record archive, Istanbul University** ("The Late-Ottoman Sound of Anatolia", Orient-Institut Istanbul / Istanbul University Rare Works Library) | Anatolian folk song and Ottoman art music | About 4,700 shellac 78s, including more than 600 unique recordings. Project dates: May 2025 – May 2026 | No public audio portal found. Istanbul University hosts are blocked here | Unknown | Ask permission later (via Orient-Institut Istanbul, oiist.org). Note the confusion: the BL page for EAP1637 describes the Ünlü collection, while Istanbul University uses the same number for Darülelhân | search-only |
| 5 | **Harvard Loeb Music Library, Arabic 78 Collection on Aviary** (harvardmusiclib.aviaryplatform.com; USA, a pointer for the region) | Arab and Arab-American 78s (Egypt, Levant, Iraq): mawwāl, qaṣīda, taqsīm, ṭaqṭūqa, and some Quranic recitation | About 600 catalogued discs, about 1903 to the 1950s. A subset is online (released January 2022 because pre-1923 US recordings entered the public domain); "many … available for streaming and download; some are restricted" | Aviary player and download. Host returned **403** (bot check) here | Pre-1923 items are presented as US public domain. Exclude Quranic recitation | Probably a **good fit** for pre-1923 items. Verify from an unblocked network; contact the Loeb Music Library (library.harvard.edu/libraries/loeb-music) | search-only / blocked |
| 6 | **Qatar Digital Library, Gulf music articles** (qdl.qa; already flagged in the first pass as blocked) | Kuwaiti and Bahraini sawt and sea music; Iraqi maqam (al-Qubanchi at the 1932 Cairo Congress) | Articles embed BL shellac transfers digitised by Rolf Killius: 62 Cairo Congress 78s and 1950s Kuwaiti discs (Abdullah Fadhala) | Embedded players; host **403** | These are British Library Sounds material (already known) | Treat as part of the already-known BL Sounds. No separate outreach | blocked |
| 7 | **ITU Turkish Music State Conservatory digital library** (tmdkarsiv.itu.edu.tr) | Turkish art and folk music; personal archives of Yalçın Tura, Demirhan Altuğ, Selma Ersöz, Alaeddin Yavaşca, Sadun Aksüt and others | 302,361 files and 48,927 catalogue records. The "Plak" category has 2,025 records (a "taş plak" search finds 847 hits); there is also a cassette category | Catalogue pages I sampled showed metadata only. **No audio file found** | Not stated on the pages I saw. There is a "Kullanım izni" (usage permission) field | Ask permission (low): contact form at tmdkarsiv.itu.edu.tr ("İLETİŞİM") | verified (catalogue); audio not found |
| 8 | Bibliotheca Alexandrina / CULTNAT "Digitization of Arab Music Heritage" (2005 project) | Egyptian and Arab music, mid-19th to late 20th century | Planned database of documented works | No working portal found. modernegypt.bibalex.org ("Memory of Modern Egypt", which lists sound recordings among its media) redirects to an **InternalError** page | Unknown | **Not usable now** (no reachable audio). Recheck later | verified (error page) |
| 9 | Kurdistan Music Archive (Amjad Assad, Erbil citadel; founded 1952) | Kurdish 78s and cassettes spanning about 100 years | Thousands of items, being digitised by one person | **No online access** found | Private | Not usable (offline private collection) | search-only |
| 10 | Dengbêj Gazîn edition (qed.perspectivia.net/gazin, Orient-Institut Istanbul / Max Weber Stiftung) | Kurdish dengbêj (a woman singer, 1959–2018) | 210 recordings with transcriptions and translations | Edition player; no direct files exposed | "© Max Weber Stiftung" | Not usable (recorded after 1970) | verified |
| 11 | Syrian Cassette Archives (syriancassettearchives.org) | Syrian, Assyrian, Kurdish, Armenian and Iraqi cassette music | More than 1,000 cassettes, 1970s–2000s | Online player | Project-held | Not usable (after 1970) | verified (site) |
| 12 | UCLA Henry Yonan Assyrian Songs Collection | Assyrian folk, religious and war ballads | 156 home-recorded 78 rpm lacquer discs, 1948–52 | **On-site listening copies only** | UCLA Ethnomusicology Archive | Ask permission (long shot; contact via schoolofmusic.ucla.edu/resources/ethnomusicology-archive) | search-only |
| 13 | Saudi Memory Unit, KFCRIS: Saad Sowayan recordings | Bedouin Nabati poetry, tales and desert memory | More than 440 h | Being transcribed; no public audio | KFCRIS | Not usable (recorded 1983–90, after the cutoff) | search-only |
| 14 | Yemen's Musical Memory (Rafeq al-Akori, AFAC grant 2024) and Yemeni Audio Index (AFAC grant 2023) | Yemeni 78s pressed in Aden 1935–60 (Sanaa, Aden, Lahij, Hadramaut); the Index covers 1935–90 | Not stated | **No website found** for either | Unknown | Not usable yet. Recheck; the grantee is linked to the Yemeni Centre for Musical Heritage | verified (AFAC pages) |
| 15 | Oman Centre for Traditional Music (OCTM, Muscat; founded 1983/84) | Omani traditional music field recordings | More than 24,000 photos and 725 audiovisual recordings, plus "a large number of sound recordings" (2005 figures); digitised into an internal database | Host (octm-folk.gov.om) **refused (502)**. No online archive found. Some material is on Smithsonian Folkways' UNESCO reissues (already known) | Government | Not usable online (ask the Ministry of Heritage and Tourism only as a long shot) | blocked / search-only |
| 16 | Radio Algérienne archives / Zaman FM | Algerian chaâbi, andalou, Kabyle, Bedouin and Chaoui music | About 350,000 sound documents; digitisation announced February 2026 | No public catalogue; rebroadcast on Zaman FM only | State broadcaster | Not usable (no online archive) | search-only |
| 17 | SNRT Morocco archives | Moroccan aïta, malhun, Andalusian and Amazigh music | Unknown; digitisation of TV tapes planned in 2022 | No public portal | State broadcaster (also the likely rights holder of IA `arabandalusian`, already known) | Not usable (no online archive) | search-only |
| 18 | Tajik archives: Rudaki Institute folklore archive (UCLA MEAP survey); Donish Institute music archive; TV & Radio Committee "Golden Fund" | Tajik folklore, shashmaqam, falak | Paper-heavy folklore archive (from the 1920s); sound archives being catalogued | Nothing online | State | Not usable (not online) | search-only |
| 19 | Turkmen National Conservatory collection (digitised 2017 with US Embassy support) | Turkmen bagşy (Mustak Aymedov, Patyshaguly Girmanov and others) | Unknown size and dates | Not online | State | Not usable (not online) | search-only |

### New sub-collections or details of already-known archives
- **CREM-CNRS (already known):**
  - Amazigh material: René Bertrand's 1958 Moroccan Berber recordings (digitised by BnF) and Mohammed Aït Youssef's 1966/68 recordings.
  - Gnawa and Chaoui recordings (e.g. `CNRSMH_E_1949_002_011_001`, "Chant Berbère Chaouïa").
  - Turkmen bakhshy and Tajik Badakhshan falak. The falak items (Jean During, `CNRSMH_I_2021_002_001_*`) were recorded in 1991, so they fall after the cutoff.
  - Poul Rovsing Olsen's 1962–63 Bahrain pearl-diver recordings, listed as "en cours de traitement" (being processed).
  - Most of these show as metadata only.
- **Lautarchiv WWI POW recordings (first pass: not usable):** the Tunisian Phonothèque mirrors the Tunisian prisoners' recordings (corpus `KPPK`, collection `PrisonniersTunisiens_1916-1918`). The same sensitivity applies: exclude them unless the communities and the archive agree.
- **BnF/Gallica 1932 Cairo Congress (already known):** CMAM also holds a corpus `Congres_Caire_1932`. The Guimet museum and the Musée de l'Homme hold copies.
- **Internet Archive:**
  - Searches found no new region collection dated 1970 or earlier for Iraq, Kurdish, Gnawa, Tajik, Turkmen, Assyrian, Oman, Kuwait or Bahrain. Gnawa hits are all modern radio shows (CC BY-NC-ND).
  - One stray item, `from-yemen-rada3`: Yemenite Jewish women's songs from Rada', recorded about 1960 by Prof. Yehuda Ratzhaby in Tel Aviv. Private upload, no rights field, 2 MP3s. Not usable without permission.

---

## 2. Country checklist (second pass)

| Country / group | Result |
|---|---|
| Egypt | Bibliotheca Alexandrina's Memory of Modern Egypt returns an InternalError page; no CULTNAT music portal found. The Arabic search found nothing else from BA. Egyptian 78s (e.g. Wadouda el-Manialawiya, 1924) are streamable from the **Tunisian Phonothèque** (ask). Harvard's Aviary Arabic 78s are blocked here. Excavated Shellac has 4 Egyptian posts. |
| Morocco | IRCAM: no sound archive online (the host was unreachable; searches found only events and the commercial Ṛṛways anthology). SNRT: no online archive. CREM (known) holds 1958–68 Amazigh recordings. IA `arabandalusian` remains the main lead (first pass). |
| Algeria | Radio Algérienne has digitised about 350,000 documents but has no public catalogue. CREM (known) has Kabyle and Chaoui material. CMAM holds a few Algerian items (e.g. DAT-154). Excavated Shellac has 3 posts. Nothing streamable found. |
| Tunisia | **The Phonothèque Nationale online catalogue: 1,469 full-access items with direct MP3s. Ask permission.** |
| Libya | Nothing found online (Arabic search on Libyan radio and national archives returned nothing relevant). |
| Iraq | No Iraqi maqam archive online. The national music archive was destroyed in 2003 and Bayt al-Maqam has no digital collection. Al-Qubanchi's 1932 Cairo recordings are on QDL (BL Sounds, blocked) and BnF (known). Excavated Shellac has 3 Iraqi, 2 Kurdish and 1 Turkmen post. |
| Kurdish | Kurdistan Music Archive (Erbil): offline private collection. The Dengbêj Gazîn edition dates from after 1970. The Radio Yerevan Kurdish archive (more than 900 songs, 1950s–70s) exists only as a book and KOM Müzik reissues (commercial). Institut kurde (first pass): on site only. No usable online source. |
| Oman | OCTM host refused; no online archive. Excavated Shellac has 1 Omani 78. |
| Kuwait, Bahrain, Qatar, UAE, Saudi Arabia | Only QDL articles with BL shellac transfers (blocked; already-known BL material). The CREM Olsen Bahrain recordings (1962–63) are still being processed. Saudi Bedouin poetry recordings (Sowayan) date from after 1970. Nothing usable found. |
| Yemen | Two AFAC-funded projects (Aden 78s 1935–60; the Yemeni Audio Index) have no public site yet. The Yemeni Centre for Musical Heritage has no online archive. One stray Yemenite-Jewish IA item. Nothing usable. |
| Turkey | The TRT archive site holds video and personalities only; TRT folk-song collections are sold on commercial CDs. The Ankara State Conservatory 1937–53 field recordings are undigitised (a copy is at TRT). ITU TMDK digital library: catalogue only, no audio found. SALT/Cemal Ünlü: label scans, host blocked. Darülelhân (Istanbul University): no portal. Excavated Shellac has 7 Turkish posts. |
| Tajikistan | Nothing online. Archives (Rudaki Institute, Donish Institute, TV/radio Golden Fund) are being catalogued. Field recordings online (CREM, Leiden Iranturan) date from after 1970. Excavated Shellac has 1 Tajik post. |
| Turkmenistan | Nothing online. The Conservatory's 2017 digitisation is not public. CREM's bakhshy items date from 1977 onwards. |
| Amazigh | CREM (known) only. IA `amazigh-archive` is texts only (first pass). IRCAM has nothing online. |
| Gnawa | Nothing from before 1970 online. CREM's gnawa items are restricted; IA hits are modern radio. |
| Nubian | Nothing usable. The ICCROM UNESCO 1962 radio tape "Nubian Prospectives" is a documentary programme, not music. Exeter's Archive of Nubian Language and Culture (Shinnie cassettes) is linguistic, on site. |
| Bedouin | Nothing from before 1970 online (Saudi Sowayan recordings 1983–90; Tunisian southern field recordings at CMAM are from 1999–2017). |
| Assyrian | UCLA Yonan collection (1948–52), on site only. Syrian Cassette Archives date from after 1970. Nothing usable online. |

---

## 3. Per-archive notes

### 3.1 Phonothèque Nationale de Tunisie: https://phonotheque.cmam.tn
- Platform: Telemeta 1.7.1 (Parisson/CREM), managed for CMAM by LiSa Digit. Interface in French, English, German and Arabic.
- Search all (`/search/quick/corpus/?q=+`): 15,866 items, 482 collections, 25 corpora, 10 fonds.
- Access facet (`selected_facets=item_acces_exact:…`): full 1,469; viewable 507; metadata 13,196.
- Corpora of interest:
  - `tunisie_av_1932`: "Premiers enregistrements de musiciens tunisiens (av. 1934)", recording years 1900–1934, total available duration 53:28:55. Collections include Abramino Berda, Acher Mizrahi, Ahmed Ellouz, Aïcha & Fattouma, Aïcha Nada, Ali Ben Arfa, Bachir Fahmi 78s, Chammama 78s, Fritna Darmon, Hjayej 78s, Journo 78s, Mohamed Qadri, Messaoud Habib 78s, Musique Beylicale, Rahmine Berda, Simon Amiel 78s, the Paul Trager collection, Brăiloiu, and Tunisian POWs 1916–18.
  - Other corpora: `Congres_Caire_1932`, `KPPK` and `Prisonniers_Zossen-Wunsdorf` (Lautarchiv copies: sensitive), `Fonds_Salah_El_Mahdi` (donated in 2000), `DL_Oeuvres_sonores` (legal deposit: modern commercial, exclude), `CMAM_Collecte_Terrain` (1999–2017, after the cutoff), and Carthage and Testour festival tapes (1975 onwards and 1978 onwards, after the cutoff).
- Item example: `/archives/items/D-0055/`, "يا بنية يا خفة" (Ya bunayya ya khiffa), Wadouda el-Manialawiya, Gramophone, 1924, Egypt, 78 rpm 25 cm. The page offers `/archives/items/download/D-0055.{mp3,ogg,flac,wav}`. A ranged GET of the mp3 returned **206 audio/mpeg**. Items with access "none" (e.g. `Chammama_web_01`, `D-0314`) have no player.
- Machine access: OAI-PMH `/oai/?verb=Identify` returns `text/xml`, but `ListRecords` returned an HTML error page when I tried. There is an RSS feed at `/rss`. HTML item pages carry a Dublin Core export (`/dc/`).
- Legal notices (`/pages/legal_notices`): site editor "National Sound Archives of Tunisia, Center for Arab and Mediterranean Music, Place Rodolphe d'Erlanger – Ennejma Ezzahra, Sidi Bou Saïd". Head of archives: Manoubia Hermi (manoubia.hermi (at) cmam.tn), tel. 71 740 102 / 71 746 051. "AUDIO FILE COPYING FORBIDDEN IF NOT EXPLICITLY ALLOWED. COMMERCIAL USING OF THE ARCHIVES FORBIDDEN IF NOT EXPLICITLY ALLOWED. Usage of the archives in the respect of cultural heritage of the original communities."
- Assessment:
  - Technically ideal: direct files, stable URLs, Dublin Core metadata.
  - Old Folk Air is non-commercial and caches nothing downloadable, but it still needs explicit permission.
  - Many pre-1934 Tunisian and Egyptian 78s are public domain by age in most countries.
  - Exclude Quranic and Sufi religious items, and the POW recordings.

### 3.2 Excavated Shellac: https://excavatedshellac.com
- Run by Jonathan Ward since 2007. Country categories and post counts for the region are in the table above (counted from `<article>` elements on each category page, first page only).
- Example post: `/2013/08/18/abdel-latif-el-banna-alfouad-min-yom-chafek-pts-1-2/`. Audio is a Box.com shared link (`https://app.box.com/s/gq8vqij870rnkzas6r6m`, 200 text/html, a Box viewer page). There is no rights statement. WordPress.com, so there is a sitemap.
- Contact: excavatedshellac@yahoo.com (on the About page).
- Value: one of very few sources found for Tajik, Iraqi-Turkmen, Iraqi-Kurdish and Omani 78s. To stream, the app would need the owner's permission and stable file URLs. The owner might be willing to place the files on IA.

### 3.3 SALT Research, Cemal Ünlü Collection: https://archives.saltresearch.org
- Search results showed disc records such as Münir Nureddin Bey and Nebile Hanım (Suzinak şarkı). Fields: label, catalogue number, matrix numbers, 2 images, "Open Access", rights holder Cemal Ünlü, accrual method donation. Note: "for any rights requests outside of personal research purposes, contact Salt Research".
- The site returned 403 to curl (Cloudflare), as in the first pass. Not bypassed.
- The British Library EAP page (eap.bl.uk/node/4896) describes William Sumits's project to digitise more than 3,000 Ünlü discs with their labels and ephemera.

### 3.4 Darülelhân / "The Late-Ottoman Sound of Anatolia"
- Istanbul University AVESIS project page: May 2025 – May 2026, researchers Özdemir and Demir Güven, with the Orient-Institut Istanbul and the Istanbul University Rare Works Library. About 4,700 shellac 78s, of which more than 600 are unique recordings of Anatolian folk and Ottoman art music.
- There is no public audio yet. A KCL 1932 Music Congress blog (2023) says the discs sit in a room of the original Darülelhân building.

### 3.5 Harvard Loeb Music Library, Arabic 78s
- Blog posts: archive.blogs.harvard.edu/loebmusic/2017/09/05/arabic-78-rpm-records-collection-a-newly-catalogued-treasure/ and /2022/01/19/early-arabic-sound-recordings-and-the-public-domain/. The 2022 post put a subset on harvardmusiclib.aviaryplatform.com.
- Aviary returned 403 here. Aviary normally offers a public media player and, per collection, downloads. Check from an unblocked network whether direct file URLs and rights statements (e.g. "No Copyright – United States") are exposed.
- Outside the region's institutions, but the content is Egyptian, Levantine and Iraqi. Flag it for the Americas/USA or cross-region follow-up.

### 3.6 Qatar Digital Library music articles
- Examples: "Microtones: The Piano and Muhammad Al-Qubanshi – the First Congress of Arabic Music and Early Recordings from Iraq" (62 Cairo 1932 discs digitised by Rolf Killius); "Hidden Treasures: Reflections on traditional music in Kuwait"; "Fann al-Baḥri: the great art of the sea".
- All 403 here. The audio is British Library material, so it is covered by BL Sounds (already known).

### 3.7 ITU TMDK "Türk Müziği Dijital Kütüphanesi": https://tmdkarsiv.itu.edu.tr
- Built on YORDAM. The ITU/İSTKA project (completed 31 March 2023) digitised 32 collections, 23 of them personal archives.
- The home page reports 302,361 files and 48,927 catalogue records. The "Plak" category has 2,025 records (Yalçın Tura 393, Demirhan Altuğ 105, Nadir Eserler 49 …), and these include Western classical discs.
- No audio link appeared on the pages I sampled. Records have "TRT No" and "Kullanım izni" fields. Worth an enquiry only if the archive confirms it holds pre-1970 folk 78s or field tapes with audio.

### 3.8 Bibliotheca Alexandrina
- Searched in Arabic and English: only the 2005 CULTNAT "Digitization of Arab Music Heritage" announcement (bibalex.org/news) and a 2009 Al Jazeera piece on 114 rare songs in "Memory of Modern Egypt".
- `https://modernegypt.bibalex.org/` redirects with JavaScript to `collections/home/default.aspx`, which redirects to `Error.aspx?err=InternalError`. www.bibalex.org itself answered 200.

### 3.9 Gulf, Oman, Yemen
- **OCTM:**
  - Mentioned by oman.de ("the centre's website has many excerpts").
  - A 2005 Smithsonian Folklife programme book gives the holdings figures.
  - The Oman Observer covered a tape and book exhibition.
  - Host octm-folk.gov.om: proxy 502.
- **Kuwait:** a 2023 Al Rai seminar called for digitising pre-1951 heritage song recordings (Yusuf al-Saree'). No archive exists online.
- **Bahrain:** Arabphone was the first label (from 1948/49); its catalogue is not online. Poul Rovsing Olsen's 1950s–60s recordings are at CREM (being processed) and Moesgaard (Denmark), and were reissued commercially.
- **Yemen:**
  - AFAC project pages 7469 (Yemen's Musical Memory) and 7310 (Yemeni Audio Index): no URLs.
  - Al-Ittihad (2010): Rafiq al-Akouri recorded more than 300 Sanaani melodies at the Yemeni Centre for Musical Heritage (recent recordings).

### 3.10 Turkey: other notes
- TRT Arşiv (2017 onwards): video, personalities, programmes. The TRT "Arşiv Serisi" folk CDs (e.g. no. 125, Erzurum, 5 CDs) are commercial.
- Ankara State Conservatory folklore archive: field trips 1937–53 led by Muzaffer Sarısözen. An ITU 2014 thesis says the archive "remained dormant" and a copy went to the TRT Turkish Folk Music Archive. Hacettepe holds a study of the field cards.
- KTÜ Karadeniz Music Archive (KARMA, recordings from 1902): mentioned in search results with copyright caveats. The host karma.ktu.edu.tr did not answer.

### 3.11 Central Asia
- **Tajikistan:**
  - UCLA MEAP "Digitization of the Folklore Archive, Tajikistan": inventory and pilot only.
  - UCA 2024 report (Kabilova): the Donish Institute's music archive is preparing an electronic catalogue.
  - Vestnik muzykalnoi nauki 2025: the TV and radio Golden Fund.
  - Leiden Iranturan (Jan van Belle, Pamir, 1990s) and CREM (During, 1991) date from after the cutoff.
- **Turkmenistan:**
  - RFE/RL: a private "bagşy museum" in Ashgabat.
  - Times of Central Asia (2017): a US-supported digitisation at the National Conservatory, not public.
  - Russian and English searches found no 78 rpm catalogue.
- **Kazakhstan** (pointer): the Kazakh National Conservatory has a page on the "Turkistan collection of musical records of R. Karutz" (early-20th-century phonograph recordings, probably held in Germany). I did not check it further.

### 3.12 Blocked or unreachable this pass (not worked around)
- **403:** archives.saltresearch.org, harvardmusiclib.aviaryplatform.com, qdl.qa, www.patreon.com, ohang.uz.
- **TLS name mismatch:** phonotheque.mmsh.huma-num.fr (MMSH Aix). The certificate does not cover the host, so the site could not be assessed. CLARIN and re3data list it as the "Mediterranean Research Centre for the Humanities' Phonothèque": more than 8,000 h of Mediterranean fieldwork since the late 1950s, about 4,000 documents online as of 2016. It should be checked from another network as an ask-permission lead (MMSH, Aix-en-Provence).
- **Proxy refused / no answer:** octm-folk.gov.om (502), www.ircam.ma, karma.ktu.edu.tr, www.zamanfm.dz.
- **Error page:** modernegypt.bibalex.org (InternalError); hazine.info (account suspended). Hazine's "Archives of Recorded Sound from the South and East Mediterranean: A Guide for Researchers" by Hazem Jamjoum would be a useful index if it comes back.

---

## Sources
- https://phonotheque.cmam.tn/search/quick/corpus/?page=1&q=+ ; https://phonotheque.cmam.tn/archives/corpus/tunisie_av_1932/ ; https://phonotheque.cmam.tn/archives/items/D-0055/ ; https://phonotheque.cmam.tn/pages/legal_notices ; https://www.leconomistemaghrebin.com/2016/12/07/pistes-musicales-tunisiennes-maghrebines-desormais-accessibles-citoyens/
- https://excavatedshellac.com/category/egypt/ (and the other category pages) ; https://excavatedshellac.com/about/ ; https://excavatedshellac.com/2013/08/18/abdel-latif-el-banna-alfouad-min-yom-chafek-pts-1-2/
- https://archives.saltresearch.org/handle/123456789/33570 (search result; blocked) ; https://eap.bl.uk/node/4896
- https://avesis.istanbul.edu.tr/proje/62c0fee9-ced7-4a87-b568-90f28e182ea6/the-late-ottoman-sound-of-anatolia-eap1637 ; https://1932muscon.kcl.ac.uk/?p=1353
- https://archive.blogs.harvard.edu/loebmusic/2017/09/05/arabic-78-rpm-records-collection-a-newly-catalogued-treasure/ ; https://archive.blogs.harvard.edu/loebmusic/2022/01/19/early-arabic-sound-recordings-and-the-public-domain/ ; https://mideast.wisc.edu/2022/01/21/early-arabic-recordings-now-in-public-domain/
- https://www.qdl.qa/en/microtones-piano-and-muhammad-al-qubanshi-–-first-congress-arabic-music-and-early-recordings-iraq ; https://qdl.qa/en/hidden-treasures-reflections-traditional-music-kuwait ; https://rolfkillius.com/research_gulf_music.html
- https://tmdkarsiv.itu.edu.tr/ ; https://tmdk.itu.edu.tr/en/news-detail/2022/01/12/itu-tmdk-bunyesinde-sadun-aksut-arsiv-ve-koleksiyonu-acildi ; https://polen.itu.edu.tr/items/5c1646fa-5b7d-45fb-9585-e92fc717de0b
- https://bibalex.org/news/index?page=1150 ; https://www.aljazeera.net/arts/2009/2/9/114-أغنية-نادرة-في-ذاكرة-مصر ; https://modernegypt.bibalex.org/
- https://en.wikipedia.org/wiki/Kurdistan_Music_Archive ; https://newarab.com/features/erbils-citadel-amjad-assad-keeps-forgotten-melodies-alive ; https://qed.perspectivia.net/gazin/ ; https://bianet.org/haber/kurdish-song-archive-of-radio-yerevan-published-as-a-book-226108
- https://syriancassettearchives.org/about ; https://oac.cdlib.org/findaid/ark:/13030/kt5v19s0wd/entire_text/
- https://kfcris.com/en/news/read/580
- https://www.arabculturefund.org/Projects/7469 ; https://www.arabculturefund.org/Projects/7310 ; https://www.aletihad.ae/articleamp/38043/2010/
- https://www.oman.de/en/society/culture/music-dance/traditional-music/ ; https://folklife-media.si.edu/docs/festival/program-book-articles/FESTBK2005_11.pdf ; https://www.omanobserver.om/ampArticle/51107
- https://news.radioalgerie.dz/fr/node/28728 ; https://news.radioalgerie.dz/fr/node/79639
- https://ledesk.ma/enoff/la-snrt-prepare-la-numerisation-de-ses-anciennes-archives/ ; https://www.maroc.ma/fr/actualites/lircam-met-lhonneur-le-patrimoine-oral-amazigh-et-son-role-dans-la-preservation-de-la-memoire
- https://www.aljazeera.net/culture/2012/8/22/المقام-العراقي-مهدد-بالاندثار ; https://christiancentury.org/node/20883
- https://meap.library.ucla.edu/projects/digitization-of-the-folklore-archive-tajikistan/ ; https://ucentralasia.org/publications/2024/march/preserving-the-musical-heritage-of-tajikistan-methods-and-approaches ; https://vestnik.nsglinka.ru/uploads/pdf/2025-T13-3/193-199.pdf ; https://iranturan.leiden.edu/music/
- https://www.rferl.org/a/25278826.html ; https://timesca.com/turkmenistan-us-helps-preserve-traditional-music-recordings-for-posterity
- https://archives.crem-cnrs.fr/archives/collections/CNRSMH_I_1997_002/ ; https://archives.crem-cnrs.fr/archives/items/CNRSMH_E_1949_002_011_001/ ; https://archives.crem-cnrs.fr/geo/collections/asie/bahrein
- https://www.re3data.org/repository/r3d100012526 ; https://alpha-centres.clarin.eu/centre/44
- https://archive.org/advancedsearch.php (queries for Gnawa, Kurdish, Tajik/Turkmen, Yemen, Assyrian, Gulf, taş plak) ; https://archive.org/metadata/from-yemen-rada3

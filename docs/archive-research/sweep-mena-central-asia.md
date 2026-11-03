# Archive sweep: Middle East, North Africa, Caucasus and Central Asia

Sweep date: 2026-11-02. Region: Morocco, Algeria, Tunisia, Libya, Egypt, Sudan, Lebanon, Syria, Jordan, Palestine, Israel (beyond NLI), Iraq, Iran (beyond Golha), Gulf states, Yemen, Turkey, Cyprus, Armenia, Georgia, Azerbaijan, Kazakhstan, Uzbekistan, Tajikistan, Kyrgyzstan, Turkmenistan, plus the peoples and traditions listed in the brief.

**Limits of this sweep. Please read them before relying on the "nothing found" lines.**
- The web-search budget for this run is shared by all the sweep agents, and it ran out partway through my sweep (after about 15 of my searches). I searched in Georgian/English, Turkish, Persian, Russian and Hebrew. My Arabic-language and French searches for Egypt and the Maghreb (Bibliotheca Alexandrina, IRCAM Morocco, CMAM Tunisia) never ran. For those countries I probed known institution sites directly with curl instead. Some "nothing found online" lines for Arab countries therefore mean "nothing found by direct probing". They do not mean a full search came back empty.
- Many national hosts in the region are blocked by the sandbox, or sit behind Cloudflare, bot checks or logins. Each one is listed and marked "blocked". I did not try to get around any of them.
- Status tags: **verified** means I fetched the page and/or tested an audio URL with curl. **search-only** means I know it only from search results or my own knowledge. **blocked** means the host refused, timed out or showed a bot check.

---

## 1. Ranked table

| # | Archive | Country / people | Content (rough) | Audio access | Rights | Verdict | Status |
|---|---|---|---|---|---|---|---|
| 1 | Internet Archive Great 78, region content (already known) | Arab, Armenian, Turkish, Persian, Syrian-Lebanese diaspora, Hebrew/Jewish, Georgian | About 343 Arabic, 506 Hebrew, 244 Turkish, 130 Armenian, 122 Persian, 72 Egyptian, 45 Syrian and 13 Georgian 78s (keyword counts), from about 1905 to the 1950s | Direct MP3/FLAC | IA's own terms (already known) | **Good fit** (already known; listed only so the region's coverage is visible) | verified (counts from IA advancedsearch) |
| 2 | IA `hessel-digital-library` (**new IA sub-collection**, created 2026-10-20) | 379 region items: Arabic, Armenian, Turkish, Persian, Syrian, Greek, Hebrew | 29,051 items in total. All 379 region items mirror the LoC National Jukebox (ids `loc-jukebox-*`), dated about 1910–1925 | Direct IA files | Each item carries CC Public Domain Mark 1.0 | **Good fit, but these are duplicates of LoC** (already known). De-duplicate rather than add | verified |
| 3 | Jewish Music Research Centre (JMRC), Hebrew University | Mizrahi, Sephardic, Persian-Jewish, Kurdish-Jewish, Yemenite, Bukharan, Georgian-Jewish and Ashkenazi traditions | 20 playlists with about 418 recordings, including 233 from the Edith Gerson-Kiwi archive ("Yaakov Huri recordings") and Gerson-Kiwi's recordings in Jerusalem and Tehran, 1950–61 | **Direct MP3** (`/sites/default/files/audios/*.mp3`, 200 audio/mpeg, no login) | No rights statement on the playlist pages. Originals are held by the NLI Sound Archive. A lot of synagogue liturgy and piyyut, so flag sacred items | **Ask permission**: jmrc@mail.huji.ac.il (NLI may also need to sign off) | verified |
| 4 | Komitas Museum-Institute, Yerevan | Armenian folk song and Armenian church chant | 14 historic recordings of Komitas and Armenak Shahmuradyan (about 1912–14) | **Direct MP3** (public.cf5.media, 206 audio/mpeg) | No item rights statement; site footer says ©. The recordings are about 110 years old and very likely public domain by age. 5 of the 14 are sacred chants (sharakan, liturgy, night office), so flag them | **Ask permission**: contact@komitasmuseum.am (small set, high value) | verified |
| 5 | IA `arabandalusian` (CompMusic / MTG-UPF) | Moroccan Andalusian (āla) music, Tetouan Conservatory orchestra and others | 161 items, of which about 118 are Moroccan national radio (RTM) recordings from the 1960s (titles marked "RTM 1960s"/"RTM"); the rest are from the 1970s–2010 | Direct IA MP3/OGG | **No licence or rights field** on any item. Uploaded by andres.ferraro@upf.edu; RTM (now SNRT) is the likely rights holder | **Ask permission**: mtg@upf.edu, then SNRT | verified |
| 6 | Armenian Museum of America, Sound Archive (Watertown, MA) | Armenian diaspora and Caucasus 78s, ashugh music, Gramophone Co. recordings from 1909 | Collection of about 3,000 discs; selections online in 9 or more feature articles | **Direct MP3** (static1.squarespace.com, 200 audio/mpeg) | "All Rights Reserved" | **Ask permission**: info@armenianmuseum.org | verified |
| 7 | Zemereshet (early Hebrew song) | Hebrew songs of the Yishuv and Palestine/Israel up to 1948 (Zionist folk song, early Israeli song) | Thousands of songs; a single song can have 28 recordings, including 78s from 1927 and Kol Israel recordings | **Direct MP3** (`/UserFiles/Media/...mp3`, 206 audio/mpeg) | "© Zemereshet and/or the creators and/or ACUM" (ACUM is Israel's collecting society). robots.txt is `Disallow: /` | **Ask permission**: contact@zemereshet.co.il | verified |
| 8 | Golistan Project (US non-profit) | Persian 78s from 1906, Iranian radio, regional (mahali) music | Several hundred music items (the pager shows 8 or more pages of 15), dated 1906 to the 1970s | **Vimeo player only** (no direct files) | No rights statement found | **Ask permission**: info@golistan.org | verified |
| 9 | Azerbaijan National Library, Electronic Music Library (`millikitabxana.az/musiqi`) | Azerbaijani khanandas (mugham), ashiqs, folk instruments | 124 khananda pages, including historic singers such as Islam Abdullayev (1876–1964), each with several tracks | Web player only (song ids loaded by script; no direct URL found) | "Bütün hüquqlar qorunur" (all rights reserved) | **Ask permission**: contact@anl.az | verified (page); audio URL not exposed |
| 10 | Musiqi Dünyası journal network (`musigi-dunya.az` and its sub-sites) | Azerbaijani mugham, ashiq, folk; anthologies of khanandas (Jabbar Garyaghdioglu, Kechachi oglu Mammad, Seyid Shushinski and others) | Audio issues from 1999 to 2024 plus the "Traditional Music of Azerbaijan" site with a mugham anthology | **Direct MP3** for the journal's audio issues (206 audio/mpeg). The anthology sub-sites run over HTTP only and some did not answer | Journal text is CC BY 4.0; **the recordings have no stated licence** (largely from the Azerbaijani radio "Gold Fund" and CD releases) | **Ask permission** (low/medium): [address on the musigi-dunya.az site] | partly verified |
| 11 | UCLA Ethnomusicology Archive on IA (`uclaethnomusicologyarchive`) | Sephardic (Isaac Sene home recordings, about 1950), plus Persian, Turkish and Armenian lectures | About 98 Sephardic-tagged items; most of the region's other items are modern lectures | IA **`stream_only`** | Item rights: "Copyrighted. Rights are owned by Emily Sene … permission to provide access online"; no commercial use | **Ask permission** (UCLA Ethnomusicology Archive, via schoolofmusic.ucla.edu/resources/ethnomusicology-archive) | verified |
| 12 | CMAM / Phonothèque Nationale, Ennejma Ezzahra, Tunis | Tunisian malouf and Tunisian 78s; holds the Baron d'Erlanger legacy; legal deposit and field collection | Large; numbers not published | **On-site only** (no online audio) | Unknown | **Ask permission** (long shot): info@cmam.tn | verified (site) |
| 13 | Institut kurde de Paris, library | Kurdish music (dengbêj and others) | Archive of "sounds and musical recordings" (size not stated) | **On-site only** | Unknown | Ask permission (long shot; contact via institutkurde.org) | verified (site) |
| 14 | Tbilisi State Conservatoire, International Research Center for Traditional Polyphony (polyphony.ge) | Georgian polyphony; wax cylinders 1923–52; prisoner-of-war cylinders 1916–18 (with the Berlin archive) | Released as 16 CDs; online only as SoundCloud/YouTube samples | SoundCloud/YouTube only | In an interview, the archivist says they do **not** want public online access | Not usable now (ask permission only as a long shot) | verified (site) / search-only (policy) |
| 15 | Sudan Memory (King's College London, NRO, SUDAAK) | Sudan | Photos, documents, films, Habibi Funk LP covers. **No audio collection found** | n/a | "Non-commercial purposes only" | **Not usable** (no audio recordings) | verified |
| 16 | KazNEB, Kazakhstan National Electronic Library | Kazakh kui | 2,679 "audio files", mostly tracks from the commercial 2009 CD set "1000 Kazakh traditional kuis" (EL production company) | Video-tag stream (`nabrk.kz/av-content/.../stream.mp4`) **returns 404** | Commercial anthology | **Not usable** (dead streams; commercial rights) | verified |
| 17 | AKYN / Blinken OSA (Manas epic) | Kyrgyz manaschi | Performances recorded 2017–18 | OSA catalogue | n/a | **Not usable** (recorded after 1970) | search-only |
| 18 | AUB Palestinian Oral History Archive (POHA) | Palestinian refugees | Oral-history interviews (2000s) | AUB catalogue | n/a | **Not usable** (oral history, recorded after 1970) | verified (site) |
| 19 | Dartmouth Jewish Sound Archive | Jewish music including Judeo-Arabic | Large | Registration with a proven scholarly purpose; host blocked here | Restricted | **Not usable** (login) | search-only / blocked |
| 20 | Lautarchiv, Humboldt University (Germany, outside my region) | WWI prisoner-of-war recordings of Georgian, Tatar, Arab, Kurdish and Armenian prisoners and others | About 10,000 discs and cylinders | Login needed for research; on request | Recordings made in POW camps, which the archive treats as sensitive | **Not usable** (login and sensitive). Pointer for the Europe sweep | verified (site) |

### New sub-collections of already-known archives (to check from an unblocked network)
- **British Library Endangered Archives Programme (EAP):**
  - **EAP1637 "The Late-Ottoman Sound of Anatolia"**: the Darülelhân record archive at Istanbul University, about 4,700 shellac 78s, including more than 600 unique recordings of Anatolian folk and Ottoman art music.
  - **Cemal Ünlü collection (EAP)**: a private collection of late-Ottoman and early Turkish 78s. Item records are mirrored at SALT Research (`archives.saltresearch.org`).
  - Both `eap.bl.uk` (it returned HTTP 202, apparently a challenge page) and SALT (Cloudflare) blocked me, so I could not see whether audio is online or what rights statement it carries. (search-only)
- **UCSB Library (the Cylinder Archive is already known):** the **Kutay Derin Kuğay Collection**, 429 Turkish and Greek 78s from 1906–1963, in UCSB Special Research Collections (library.ucsb.edu/node/7440). I did not confirm whether these are digitised online. (verified page; digitisation status unknown)
- **Qatar Digital Library (QDL, a partner of BL/QNL):** QNL says QDL includes "audio recordings" on the Gulf. These may be British Library Sounds material (already known). `qdl.qa` returned 403 here. (blocked)
- **NLI (already known):** piyut.org.il ("Invitation to Piyut", Sephardi/Mizrahi piyyut) now redirects into nli.org.il and sits behind Cloudflare. The JMRC (row 3) is a separate Hebrew University front end that serves its own MP3s of NLI-held field recordings.
- **CREM-CNRS (already known):** it holds shashmaqam (Uzbek/Tajik), Iranian, Kyrgyz and Maghreb collections. Many of these are listed "metadata only".
- **LoC (already known):** the Ragheb Moftah Coptic liturgical collection is Coptic liturgy, so it is **sensitive/sacred; flag or exclude**.
- **Gallica/BnF (already known):** holds the 1932 Cairo Congress of Arab Music recordings.

---

## 2. Country checklist

| Country / group | Result |
|---|---|
| Morocco | IA `arabandalusian` (RTM 1960s Andalusian music, no licence) is the main lead: ask MTG-UPF and SNRT. BNRM (bnrm.ma) is **blocked**. IA `amazigh-archive` holds 964 items, all texts with no audio. No IRCAM, Gnawa or Amazigh audio archive was found online. The Arabic/French search never ran (budget used up). |
| Algeria | The Bibliothèque nationale's "audiovisuel" page lists only images. Nothing found online. IA holds only a few Algerian 78s in Great 78. |
| Tunisia | CMAM Phonothèque Nationale (malouf, 78s) is on-site only: ask permission (long shot). |
| Libya | Nothing found online. |
| Egypt | Already-known sources cover the main material: Great 78 has about 72 Egyptian items, and BnF has the 1932 Cairo Congress. Bibliotheca Alexandrina's "Memory of Modern Egypt" returned an error page, so it is unverified. The IA collection `ahmedbassiouni` (described as Egypt 1900–1930s) is empty (0 items). No Coptic source other than LoC Moftah (sacred). |
| Sudan | Sudan Memory has no audio. Nothing usable. |
| Nubian | Nothing found online (1 Nubian-tagged 78 on IA). |
| Lebanon | AMAR (already known) returned 503 here. AUB POHA is oral history recorded after 1970. Nothing new. |
| Syria | Nothing found online beyond the Syrian-diaspora 78s on IA (about 45). |
| Jordan | Nothing found online (the national library host is **blocked**). |
| Palestine | Palestinian Museum Digital Archive is **behind a bot check**. POHA is oral history recorded after 1970. Nothing usable. |
| Israel (beyond NLI) | JMRC (ask; direct MP3s) and Zemereshet (ask; ACUM rights). The ANU Museum and Kan archive returned **403**. Dartmouth requires login. |
| Sephardic / Mizrahi | JMRC (Judeo-Spanish, Iranian-Jewish, Kurdish-Jewish, Yemenite and others), UCLA Isaac Sene recordings (stream-only, copyrighted), and the UW Sephardic Studies collection (Benmayor recordings, 1970s, so after the cutoff; search-only). |
| Iraq | Nothing found online. Iraqi maqam appears only in collector forums, which were not checked because the search budget ran out. |
| Iran (beyond Golha) | Golistan (ask; Vimeo). The Music Museum of Iran's 3,000 shellac transfers are at Hildesheim's Center for World Music (Germany), on-site only, search-only. The Majlis Library, NLAI and other `.ir` hosts are **blocked**. |
| Kuwait, Bahrain, Qatar, UAE, Oman, Saudi Arabia | QDL ("audio recordings" on the Gulf) is **blocked (403)**. AGDA (UAE) holds documents. The Darah host is **blocked**. No Oman Centre for Traditional Music archive was found online. Nothing usable verified. |
| Yemen | Nothing found online (CREM, already known, has some). |
| Turkey | EAP1637 Darülelhân and the Cemal Ünlü EAP collection (new EAP sub-collections, unverified), UCSB Kuğay 78s, and the Walter Feldman tapes at NYU (planned website; recorded after 1970). TRT archive hosts (trtarsiv.com, arsiv.trt.net.tr) are **blocked**; TRT Dinle is a commercial app. The Milli Kütüphane digital library offers images and needs a login, with no audio verified. Atatürk Kitaplığı and the Istanbul University OMAR hosts are **blocked**. The Texas Tech ATON (Turkish oral narrative) host is **blocked**. |
| Kurdish (dengbêj) | Institut kurde de Paris holds an on-site sound archive (long shot). The kurdistanmemory, dengbej and mezopotamyavakfi hosts are **blocked**. Nothing online found. |
| Assyrian | Nothing found online (one Assyrian 78 on IA). |
| Cyprus | The Cyprus Research Centre, CyBC/RIK, the State Archives and the Cyprus Library hosts are all **blocked**. Nothing verified. |
| Armenia | Komitas Museum (ask; direct MP3, about 1912) and the Armenian Museum of America (ask; direct MP3). About 130 Armenian 78s on IA (already known). The National Library host returned **403**. The ARAR digital library holds texts only. |
| Georgia | The IRCTP / Conservatoire archive is not offered online (SoundCloud samples only). The National Library's DSpace returns **410 Gone**. The National Archives sit behind **Cloudflare**. About 13 Georgian 78s on IA. |
| Azerbaijan | National Library Electronic Music Library (ask; player only) and the Musiqi Dünyası network (ask; some direct MP3). The National Library's legacy host (anl.az) returned **403**. |
| Kazakhstan | The KazNEB audio streams return 404 and come from a commercial 2009 CD anthology: not usable. The Central State Archive of film, photo and sound recordings is not online (search-only). |
| Uzbekistan | The National Library media portal (media.natlib.uz) is **unreachable**. The Institute of Art Studies phonoteka is not digitised online yet (search-only). CREM (already known) has shashmaqam. |
| Tajikistan | Nothing found online (the national library host is **blocked**). |
| Kyrgyzstan | Manas recordings online date from after 1970 (AKYN/OSA). The national library host is **blocked**. |
| Turkmenistan | Nothing found online (the culture-ministry host is **blocked**). |
| Bedouin | Nothing specific found online. |

---

## 3. Per-archive notes

### 3.1 Internet Archive: region content (already known; new sub-collections noted)
- I counted region content with keyword queries on `advancedsearch.php`, restricted to the 78rpm collection, 78-tagged items or items dated 1890–1970. The audio sits mostly in `78rpm`, `georgeblood`, `78rpm_bowling_green`, `78rpm_nsfa`, `78rpm_bostonpubliclibrary`, `78rpm_thomas-kemp`, `78rpm_leif_druedahl` and `78rpm_custis-family`.
- **`hessel-digital-library`** is new (public date 2026-10-20) and holds 29,051 items. It calls itself "A preservation mirror of openly licensed and public domain collections … verbatim rights statements". Every one of the 379 region items I sampled is a LoC National Jukebox mirror (`loc-jukebox-NNNNN-*`) carrying the PDM 1.0 licence. Good for streaming, but these are duplicates of LoC.
- **`arabandalusian`**: 161 items with MP3, OGG and PNG files. Example: `OrchestraOfTheTetouanConservatory_RTM1960s_TajelMlah`, described as "recorded by the Moroccan National Radio and Television network (RTM) in Morocco in the 1960s". Neither `licenseurl` nor `rights` is set on any item. The Dunya corpus page (dunya.compmusic.upf.edu/andalusian/info) says "All the data related to this corpus is openly available", which reads as a research-openness note rather than a licence. Contacts: mtg@upf.edu and alastair.porter@upf.edu.
- **`uclaethnomusicologyarchive`**: 7,411 items in `stream_only`. Sephardic items such as `calauem_000443` "Sephardic music: Esta Es Isaac Guenos En Turco" (about 1950) say "Copyrighted. Rights are owned by Emily Sene". Some of the archive's items are PDM (for example `calauem_000020`), but none of the region items I sampled were.
- Other IA collections I checked that are not usable: `amazigh-archive` (964 texts, 0 audio); `ahmedbassiouni` (Egyptian 78 collection, 0 items); `farya-faraji-collection` (modern "Songs of the World", fair-use rights note); `csufarmenianstudies` (no audio). Country-name searches outside the 78rpm collection turned up only radio-aporee field recordings, modern radio and podcasts. Almost nothing is dated 1970 or earlier.

### 3.2 Jewish Music Research Centre, Hebrew University: https://jewish-music.huji.ac.il/en/playlists
- Founded in 1964. Its recorded documents are curated by the NLI Music Department and Sound Archives. Its playlists can be filtered by tradition: Baghdadi, Bukharan, Djerba, Georgian, Kurdish, Moroccan, Persian, Yemenite, Samaritan, Karaite and others.
- Examples: "Edith Gerson Kiwi's Recordings of Musical Traditions from Iran" (24 recordings, Jerusalem 1950/1957 and Tehran 1961); "Yaakov Huri recordings – Recordings from the Edith Gerson Kiwi Archive" (233 recordings); "Sonic Ruins of Modernity: Judeo-Spanish folksongs" (23). About 418 recordings across 20 playlists.
- Audio test: `https://jewish-music.huji.ac.il/sites/default/files/audios/Amnon%20Netzer%20Shur%20CD-04815_G181-190_G0188%20Shur.mp3` returned 200, `audio/mpeg`, 6.4 MB, no login.
- No rights statement on the pages. The site says publications and sound files have been free to download since 2023. Machine access: HTML only (Drupal), and robots.txt does not block the audio.
- Sensitivity: much of it is synagogue liturgy and piyyut, so tag sacred items. Contact: jmrc@mail.huji.ac.il.

### 3.3 Komitas Museum-Institute: https://komitasmuseum.am/en/komitas-vardapet/audio
- 14 recordings: Komitas singing folk songs ("Ploughing Song from Lori", "Spring", "Crane" with Shahmuradyan) and church chant ("Bird" by Narekatsi, a sharakan, Holy Liturgy excerpts, night office chants).
- MP3s are served from `public.cf5.media/komitas-production/static/komitas-recordings/*.mp3`; I got 206 `audio/mpeg`.
- No per-item rights statement. The recordings date from about 1912–14; Komitas died in 1935. Flag the 5 sacred items.
- Contact: contact@komitasmuseum.am, +374 11 570570. A sitemap exists at /sitemap.xml.

### 3.4 Armenian Museum of America: https://armenianmuseum.org/sound-archive
- "Selections from the museum's roughly 3,000 disc collection of early Armenian recordings". Digitisation is supported by the SJS Charitable Trust. There are 9 or more feature posts (for example `/sound-archive/moorad-elanjian`), and Nikol Baveyan's 1909 Moscow Gramophone Co. ashugh recording is among them.
- Embedded Squarespace audio, for example `static1.squarespace.com/.../Hampoorem.mp3` (200, `audio/mpeg`).
- Footer reads "All Rights Reserved". Contact: info@armenianmuseum.org.

### 3.5 Zemereshet: https://www.zemereshet.co.il
- Volunteer project "to rescue the songs written and sung in Hebrew from the beginning of Zionism until the founding of the state".
- Song pages list multiple recordings. Example: "Agada", with 28 recordings including Hanina Karchevsky's 1927 78 and Kol Israel orchestras. MP3s live under `/UserFiles/Media/` (206 `audio/mpeg`).
- Footer: "© Zemereshet and/or the creators and/or ACUM". robots.txt disallows everything except the home page and sitemaps.
- Contact: contact@zemereshet.co.il (form at /contact_us.asp).
- Note: much of this is composed song rather than anonymous folk song.

### 3.6 Golistan Project: https://www.golistan.org
- "A virtual museum for the performing arts of Iran, from 1880 through 1990": 78 rpm records, radio programmes, regional (mahali) music, films and journals.
- Its year filter for music runs from 1906 onward. Example item: "'Arusi Dukhtar", HMV 7-211026/7, Tehran 1929, with full matrix and catalogue data.
- Audio plays through an embedded Vimeo player (`player.vimeo.com/video/347767096`); there are no direct files.
- No licence found (the terms page has no rights text). WordPress, with a sitemap at /sitemap_index.xml. Contact: info@golistan.org.

### 3.7 Azerbaijan National Library, Electronic Music Library: https://www.millikitabxana.az/musiqi
- Performer pages, for example `/musiqi/az/musics/performer/abdullayev-islam-219`: Islam Abdullayev (1876–1964) with tracks such as "Axşam oldu", "Qatar-Bayatı" and "Şüştər". There are 124 khananda and singer pages, plus ashiqs and folk instrumentalists.
- A JS player loads tracks by `data-song` id. I did not find a direct URL and did not dig further, to keep the request count low.
- Footer: "© 2025 … Bütün hüquqlar qorunur" (all rights reserved). Contact: contact@anl.az.

### 3.8 Musiqi Dünyası network: https://musigi-dunya.az (audio issues at /audio.html; sub-sites listed at /siteslist.html)
- Audio issues from 1999 to 2024, with MP3s at `www.musigi-dunya.az/new/music/issue…/N.mp3` (206 `audio/mpeg`). These are mostly modern compilations.
- `enene.musigi-dunya.az` ("Traditional Music of Azerbaijan", 2015, state-funded) carries a mugham anthology of historic khanandas (Jabbar Garyaghdioglu, Kechachi oglu Mammad, Islam Abdullayev, Seyid Shushinski, Bulbul, Khan Shushinski and others). Its player pages expose no file links, and some sub-sites (voices, mugam) did not answer here.
- The CC BY 4.0 licence applies to the journal articles, not to the recordings. Contact: [address on the musigi-dunya.az site].

### 3.9 CMAM, Phonothèque Nationale (Tunis): https://www.cmam.tn/content/fr/14/Phonotheque-Nationale.html
- Its mission covers collecting, restoring and "diffuser l'ensemble du patrimoine phonographique tunisien" (spreading Tunisia's whole recorded-sound heritage), through legal deposit, field collection and transferred collections. Access is on-site consultation only; there is no online catalogue of audio.
- Contact: info@cmam.tn.

### 3.10 Sudan Memory: https://www.sudanmemory.org
- A Goobi viewer with IIIF/REST. Partners: KCL, NRO, SUDAAK. Collections: NRO images, newspapers and manuscripts, Abri House, Rashid Studio, Habibi Funk covers and magazines, western Sudan community-museum films.
- Searching "song" and "audio" returned only record covers and films. "The content … is for non-commercial purposes only." Not usable for audio.

### 3.11 KazNEB: https://kazneb.kz/kk/catalogue/result?dType=15
- 2,679 "audio files", for example "Ақсақ қаз" from "Қазақтың дәстүрлі 1000 күйі" (EL production company, 2009, 41 CDs, under the "Мәдени мұра" programme).
- The player's source `https://nabrk.kz/av-content/1510479/stream.mp4` returned 404.

### 3.12 Blocked or unreachable hosts (not worked around)
- **HTTP 403 / Cloudflare / bot check:** archive.gov.ge (National Archives of Georgia, phonograph collection), archives.saltresearch.org (Cemal Ünlü 78s), nli.org.il and piyut.org.il, anumuseum.org.il, kan.org.il/archive, qdl.qa, anl.az, library.am, palarchive.org (Deflect bot check), eap.bl.uk (202 challenge).
- **HTTP 410 Gone:** nplg.gov.ge and dspace.nplg.gov.ge (Georgia's national library, renamed in 2025; its new hosts nlg.gov.ge and iverieli.nlg.gov.ge were refused by the proxy).
- **Proxy refused / no answer:** trtarsiv.com, arsiv.trt.net.tr, ataturkkitapligi.ibb.gov.tr, omar.istanbul.edu.tr, sirlanmissesler.istanbul.edu.tr, aton.ttu.edu, bnrm.ma, nlj.gov.jo, cyprusresearchcentre.com, rik.org.cy, cyprusarchives.gov.cy, kurdistanmemory.org, dengbej.org, mezopotamyavakfi.org, nla.am, nlt.tj, kitepkana.kg, turkmenmedeniyet.gov.tm, nlai.ir, ical.ir, dlib.ical.ir, iranarchive.org, darah.org.sa, archives.gov.ae, lebarchive.org, media.natlib.uz, djsa.dartmouth.edu, phonotheque.mmsh.huma-num.fr, and plain-HTTP sub-sites of musigi-dunya.az.

### 3.13 Leads not checked (search budget used up)
- Bibliotheca Alexandrina music library and Arab 78 holdings.
- The IRCAM (Morocco) audio archive.
- The Oman Centre for Traditional Music.
- Iraqi maqam archives.
- Assyrian music archives.
- Collector forums (sama3y, zamanalwasl). These are very likely not usable: they need a login and the rights are unclear.
- Excavated Shellac (excavatedshellac.com): a collector's blog with Azerbaijani, Armenian, Georgian, Iranian, Iraqi and Moroccan 78s. Its pages answered, but I did not assess audio access or rights. It belongs in the cross-region pile.
- Phonothèque MMSH, Aix-en-Provence: Maghreb/Mediterranean field recordings. Its host was unreachable.

---

## Sources
- https://archive.org/advancedsearch.php (queries above); https://archive.org/metadata/hessel-digital-library ; https://archive.org/details/arabandalusian ; https://archive.org/details/uclaethnomusicologyarchive
- https://dunya.compmusic.upf.edu/andalusian/info
- https://jewish-music.huji.ac.il/en/playlists ; https://jewish-music.huji.ac.il/en/playlist/25136 ; https://jewish-music.huji.ac.il/en/about-us/83
- https://komitasmuseum.am/en/komitas-vardapet/audio
- https://armenianmuseum.org/sound-archive
- https://www.zemereshet.co.il
- https://www.golistan.org ; https://www.golistan.org/collection/?type=Listen&sub_type=Music
- https://www.millikitabxana.az/musiqi
- https://musigi-dunya.az/audio.html ; https://musigi-dunya.az/copyright.html ; https://enene.musigi-dunya.az
- https://www.cmam.tn/content/fr/14/Phonotheque-Nationale.html
- https://www.institutkurde.org/en/library/
- https://polyphony.ge ; https://easterndaze.net/recordings-belong-to-the-people-an-interview-with-sound-archivist-ketevan-davitashvili
- https://www.sudanmemory.org
- https://kazneb.kz/kk/catalogue/result?dType=15
- https://www.library.ucsb.edu/node/7440 (Kuğay collection)
- https://avesis.istanbul.edu.tr/proje/62c0fee9-ced7-4a87-b568-90f28e182ea6/the-late-ottoman-sound-of-anatolia-eap1637
- https://digitalhumanities.nyu.edu/projects/turkish-music/
- https://i.auca.kg/en/research_faculty_akyn ; https://catalog-legacy.osaarchivum.org/catalog/O8pZgyZa
- https://www.uni-hildesheim.de/en/center-for-world-music/archive-/-collections/translate-to-englisch-das-music-museum-of-iran/
- https://www.lautarchiv.hu-berlin.de
- https://libraries.aub.edu.lb/poha/
- https://www.qnl.qa/en/explore/digital-collections

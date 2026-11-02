# Archive sweep: South Asia (India, Pakistan, Bangladesh, Nepal, Bhutan, Sri Lanka, Maldives, Afghanistan)

Sweep date: 2026-11-02. Method: WebSearch (about 45 queries), then curl checks against archive pages, APIs and audio files, plus Internet Archive advancedsearch and metadata API calls.

How sure each entry is:
- **verified**: I fetched the page, API or audio file myself (HTTP status and content type checked).
- **search-only**: known only from search results or secondary sources.
- **blocked**: the sandbox or the site stopped access (DNS block, connection reset, TLS error, bot check).

Limits of this sweep:
- The shared WebSearch budget ran out part way through. Tibetan exile institutions (LTWA, TIPA, THL), Bangladesh national bodies (Bangla Academy, Shilpakala Academy, Bangladesh Betar) and Prasar Bharati's archive pages each got only one search or none. They should be re-checked in a follow-up.
- WebFetch could not resolve most hosts here, so all page checks were done with curl.

Hosts that were blocked or unreachable:
- `sangeetnatak.gov.in`: TLS certificate-chain error. I did not bypass it.
- `ncaa.gov.in`: DNS failure.
- `ndl.iitkgp.ac.in`: connection reset.
- `jcda.in` and `jyotichitraban.assam.gov.in`: connection reset.
- `aavanaham.org`: connection reset.
- `reel2real.prm.ox.ac.uk`: proxy returned 502.
- `eap.bl.uk`: returned 202 to curl, and WebFetch is blocked.
- `ola.lib.uchicago.edu`: Anubis bot check. I did not try to get past it.

---

## 1. Ranked table

| # | Archive | Country / region | Content (≤1970 part) | Audio access | Rights | Machine access | Verdict | Status |
|---|---|---|---|---|---|---|---|---|
| 1 | **DSAL: Gramophone Recordings from the Linguistic Survey of India** (U. Chicago / CRL; originals at the British Library) | All India, plus Burma, Nepal hill languages, Lakshadweep | 242 recordings from 1913–1929 in about 97 languages. At least 45 are songs: Bhojpuri, Maithili, Magahi, Kumaoni, Jaunsari, Gurung, Magar, Newari, Khas, Santali, Ho, Mundari, Kharia, Gadaba, Kondh, Savara, Toda, Bhil, Tulu, Braj rasiya, Bundeli and others. The rest are stories and specimen speech. | Direct MP3 (and MP4 with a still image), no login. Example: `https://dsal.uchicago.edu/images/reference/lsi/audio/113AK.mp3` returns 206 audio/mpeg. | DSAL says it uses Creative Commons "whenever possible" and states any further restrictions per resource. The recordings are about 100 years old, so their copyright has expired in the UK, India and NZ. | JSON API: `/LSI/api/search?q=`, `/LSI/api/recordings/volume/{vol}`, `/LSI/api/recording/{id}`. Returns metadata: language, narrator, district, title, file. No key needed. | **Good fit** | verified |
| 2 | **Internet Archive `MudritSangeet`**: Society of Indian Record Collectors (Dr Suresh Chandvankar), inside Public Resource's `JaiGyan` | All India, plus Nepal and Pakistan regional records | About 1,693 audio items, mostly 78 rpm transfers from about 1904 to the 1960s, plus catalogue scans. Mostly film, light-classical and classical. There are regional and folk sides: Nepali songs on HMV/Hindustan shellac (`n-80203-oje-19195-...`, 20 tracks), Sindhi (Bhagat Kanwar Ram), Gujarati bhajans, Marathi, Tamil, Kannada, qawwali. | IA MP3/OGG, streamable. | Most items have no licence field. 16 carry the PD Mark. India's term for sound recordings is 60 years from publication, so anything published before 1966 is public domain in India. NZ's term is 50 years. | IA advancedsearch, metadata API, OAI. | **Good fit (filtered)**: take pre-1966 regional and folk sides only, skip film songs. | verified |
| 3 | **Cambridge "The Nagas" database** (Naga Videodisc; wwwe.lib.cam.ac.uk/nagas) | Nagaland, Manipur | 50 MP3s across 45 records. Within the date rule: 3 J.H. Hutton wax cylinders from 1919 (Angami, Chang, Sema; originals at the Pitt Rivers Museum) and 8 Fürer-Haimendorf recordings from 1970 (Konyak, Zemi). 17 anonymous recordings are undated (Angami, Ao, Lhota, Phom, Rengma, Sangtam, Sema, Yimchunger, Zeliangrong). The rest are from 1985–87, some from a Maison des Cultures du Monde commercial release. | Direct MP3, no login: `https://wwwe.lib.cam.ac.uk/nagas/media/naga/sound/u1.mp3` returns 206 audio/mpeg. | "The Project encourages not for profit, educational and research uses... copyright remains with the original owners... or Rivers Video Project". This is not an open licence. | Static HTML pages (record/rNNNNN.html), no API. | **Ask permission** (Cambridge, plus the Pitt Rivers Museum for the Hutton cylinders). Flag cultural sensitivity (see notes). | verified |
| 4 | **NCAA / IGNCA material mirrored on IA `IndiaCulture`** (Public Resource, "Cultural Resources of India") | All India | 7,969 audio items, identifiers `dni.ncaa.*`. Contributors: Rupayan Sansthan Jodhpur (RS, about 1,081), ICCR (1,238), IGRMS Bhopal (729), CCRT (714), Kalakshetra, SNA, IGNCA and others. Only about 105 are dated 1970 or earlier, mostly classical or Kalakshetra. Folk items dated before 1971 include `dni.ncaa.RS-1-AC` *Kalbelia Folk Songs* (1968) and `RS-2-AC` *Mali community marriage songs* (1969). Many folk items are undated. | IA MP3/OGG, streamable. | All 1,200+ RS items and some others carry CC BY-NC 4.0 in IA's licence field. The embedded NCAA metadata says "Rights: Rupayan Sansthan". Public Resource posted the library for "non-commercial... fair dealing" use. | IA APIs. ncaa.gov.in itself is blocked here. | **Ask permission** (IGNCA/NCAA, Rupayan Sansthan). Could become a good fit for the CC BY-NC items if NCAA confirms the licence. | verified (IA); blocked (ncaa.gov.in) |
| 5 | **EAP1153 Rupayan Sansthan** (Komal Kothari open reels, 1958–1976) | Rajasthan (Manganiyar, Langa, Bhil and others) | 405 open reels digitised, plus documents and photos. | Unconfirmed whether the audio streams online. Copies are at the BL, ARCE and Rupayan. | Not established. | EAP catalogue. | **Ask permission**. This is a sub-collection of the known EAP. | search-only |
| 6 | **EAP190 "Young India" label 78s** (1935–55) and **EAP468 Odeon India 78s** (1912–38) | All India | About 725 discs (1,450 sides) and 704 discs (1,408 sides). | Reported as listenable on BL Sounds / EAP. | BL terms. | BL / EAP. | Sub-collections of the known **BL/EAP**: route through the existing BL work. | search-only |
| 7 | **CIIL National Tape Archive** (Bhasha Sanchika DSpace, Mysuru) | All India, strong on the Northeast and Munda languages | 233 items: songs and talk in Santali, Munda, Ho, Garo, Jaintia, Sema Naga, Thadou, Tangkhul, Deori, Kannada and others. Recording dates are not given (`00-00-0000`), probably 1970s–90s. | Direct WAV through the DSpace REST API, no login, but very large (about 800 MB per file). | `license.txt` says only "Copyright CIIL". | DSpace 7 REST: `/server/api/core/items/{uuid}`, `/server/api/discover/search/objects`. | **Ask permission** (low priority: unknown dates, linguistic elicitation). | verified |
| 8 | **Sangeet Natak Akademi archive** | All India | Says it holds about 5,000+ hours of audio since the 1950s, including folk and tribal. | Not public online. Some material is on YouTube and commercial CDs. 21 SNA items are on the IA mirror (#4). | "Copyright Sangeet Natak Akademi". | None found. | **Ask permission** | blocked (TLS error) / search-only |
| 9 | **Music of Bhutan Research Centre** (Thimphu) | Bhutan | 1968 Calcutta studio sessions (the first studio recordings of Bhutanese zhungdra and boedra), plus its own field archive from 2008 onward. | Sold as a commercial CD; lyrics and notes downloadable. | All rights reserved. | None. | **Ask permission** (1968 sessions only) | verified (page) |
| 10 | **Univ. of Washington Ethnomusicology Archives**: Lee Lybarger *Folk Music of Pakistan* 1963–68; Lorraine Sakata Afghanistan 1966– | Pakistan (Sindh and others), Afghanistan | Field tapes. | On-site or by request. | Restricted. | Archives West finding aids. | **Ask permission** | search-only |
| 11 | **Mark Slobin Fieldwork Archive, Music in the Afghan North 1967–72** | Afghanistan (Uzbek, Tajik and others) | Field recordings, notes, photos, film. | Alexander Street (institutional login). The Wesleyan site was taken down in 2020. | © Mark Slobin. | None. | **Ask permission** (Slobin, via markslobin.com) | search-only |
| 12 | **Pitt Rivers Museum, Hutton Naga cylinders** (reel2real / Audioboom) | Nagaland | 14–15 cylinders from 1918–19, about 12 online. | Audioboom embeds have been reported. | PRM terms. | Not checked. | **Ask permission** (probably covered by the Europe sweep) | blocked (502) |
| 13 | **U. Chicago Online Language Archive**: *Folk songs of Bengal* (1964) | West Bengal / Bangladesh | 13 folk songs. | "By request". | Not checked. | Not checked. | Ask permission (low) | blocked (bot check) |
| 14 | **Lok Virsa** (Islamabad) | Pakistan | About 3,000 hours. The remastered "Original Recordings of Folk Singers" are mostly from after 1974, since Lok Virsa was founded in 1974. | YouTube channel and app only. | Government; not stated. | None. | **Not usable** (dates after 1970, YouTube only). Contact info@lokvirsa.org.pk if earlier material turns up. | verified |
| 15 | **Radio Pakistan / PBC Peshawar digital library** | Khyber Pakhtunkhwa (Pashto) | Says it holds 8,000–10,000 hours digitised, including a 1918 spool. | Not public. | Restricted. | None. | Not usable now. Maybe ask later. | search-only |
| 16 | **Prasar Bharati Archives (AIR)** | India | About 17,000+ hours. | Commercial CDs, Raagam stream, YouTube. 26 `dli.akashvani.*` classical items are on the IA mirror. | Commercial. | None. | **Not usable** (commercial licensing) | search-only |
| 17 | **IA `VintageSense` 78s** (263 items; inside the known `78rpm`) | India / Pakistan | Mostly film songs before 1960 and into the 1960s, with a little folk. | IA MP3. | The uploader applied CC BY or BY-NC-ND without owning the rights. | IA. | Not usable as a source; only the occasional folk side. | verified |
| 18 | **IA `radio-ceylon`** (87 items) | Sri Lanka | Off-air recordings of SLBC programmes (for example *Jana Gee*, recorded in 2017) and transfers of Sinhala commercial records. | IA MP3. | The uploader applied a PD Mark to commercial records. SLBC broadcasts are SLBC's copyright. | IA. | **Not usable** | verified |
| 19 | **Humboldt Lautarchiv**: WWI prisoner-of-war recordings of South Asian soldiers, 1915–18 | Punjab, Nepal (Gurkha), Bengal, Khasi, Baluch, Pashto and others | 282 titles on 193 discs. | "Sign in for research access". | Restricted; ethically sensitive. | None. | **Not usable** (login; flag as sensitive). Contact lautarchiv@hu-berlin.de. | verified |
| 20 | **Digital Himalaya, Music** | Bhutan (Laya), Nepal (Thangmi) | Recordings from the 2000s. | Streams on site. | Site is CC BY-NC-ND 3.0. | HTML. | Not usable (too recent) | verified |
| 21 | **The Travelling Archive** (Moushumi Bhowmik) | Bengal, Bangladesh | Field recordings from 2003 onward, plus essays on Bake's 1931–56 recordings. | Streams on site. | Copyright shared by performer and archive. | HTML. | Not usable (too recent) | verified |
| 22 | **Bhasha Research / Adivasi Academy (Tejgadh), Vaacha archive** | Gujarat and Adivasi India | 500+ hours, recent. | About 100 audios online; on-campus tablets. | Not stated. | None. | Not usable (too recent) | search-only |
| 23 | **Afghan Records Gallery**, **Afghan Cassette Archive** | Afghanistan | Collectors' 78, 45 and 33 rpm records and cassettes, 1950s–70s. | YouTube and SoundCloud embeds. | Commercial records; rights unclear. | None. | Not usable | verified (ARG) |
| 24 | **UVA Mandala, Oral Cultures of Bhutan** (Loden Foundation) | Bhutan | 2,000+ recordings from 2015–19. | Open. | Open access. | Mandala. | Not usable (too recent) | search-only |
| 25 | **Ethnographic Sound Archives Online** (ProQuest/Alexander Street; includes Jairazbhoy 1963–64 India material) | India | Various. | Institutional login. | Commercial. | None. | Not usable | search-only |

Already-known archives with new South Asian sub-collections to note:
- **British Library Sounds**: Arnold Bake South Asian Music Collection (Bengal, Nepal, Sri Lanka and others, 1925–56).
- **EAP**: EAP1153, EAP190, EAP468, the Gramophone Museum Kerala 78 rpm project, EAP590/EAP901 (Music Museum of Nepal; mostly post-1970 cassette and MiniDV), and EAP1247 (Baul song notebooks, text only).
- **CREM-CNRS**: Radio Pakistan folk 78s from about 1930–52 (`CNRSMH_I_1952_001`), Deben Bhattacharya 1954 India, Courlander 1950 Tamil, Koechlin's Maldives 1976–77, and an Andamanese song from 1953–54. Many of these are metadata-only or restricted.
- **ARCE**: holds copies of the Hutton and Rupayan material.

---

## 2. Country and region checklist

### India (state by state)
| State / UT | Result |
|---|---|
| Kerala | No online state archive found (Kerala Folklore Academy has nothing online). Gramophone Museum, Plassanal, has 78s through EAP (search-only). There is a Lakshadweep (Amindivi) song on DSAL LSI. |
| Tamil Nadu | Nothing state-run found. Tamil folk songs are on the IA NCAA mirror (IGRMS-A_120, 1992) and Tamil 78s are in `MudritSangeet`. CREM has Courlander 1950 (metadata only). Kalakshetra dance-drama recordings from 1958–66 are on the IA mirror (classical). |
| Karnataka | Nothing found for the Janapada Academy or Parishath. CIIL Kannada item (Mysuru). Tulu and Bunt songs on DSAL LSI. |
| Andhra Pradesh / Telangana | No dedicated archive. Gadaba, Kondh and Savara songs on DSAL LSI. CREM 1934 Telugu 78s (metadata only). |
| Maharashtra | Powade.com (Marathi powada archive, search-only, rights mixed). Marathi 78s in `MudritSangeet`. Khandeshi and Marathi Bhil songs on DSAL LSI. Grindmill Songs (PARI) is post-1970. |
| Gujarat | Bhasha / Adivasi Academy (too recent). Gujarati and Gujarati Bhil songs on DSAL LSI. Gujarati bhajans on 78 in `MudritSangeet`. |
| Rajasthan | Rupayan Sansthan (EAP1153; NCAA via IA, CC BY-NC label, mostly after 1970; Kalbelia 1968 and Mali 1969 items). ARCE is already known. Mewati and Ahirwati songs on DSAL LSI. |
| Punjab (India) | Panjab Digital Library has no audio found. Punjab Sangeet Natak Akademi plans YouTube only. WWI POW recordings at the Lautarchiv (not usable). |
| Haryana | Ahirwati and Mewati songs on DSAL LSI. Nothing else. |
| West Bengal | The Travelling Archive (too recent). Bake on BL Sounds (known). Jadavpur SCTR Bengali 78s (copy at the BL; no public portal found). UChicago OLA 1964 Bengal songs (bot check). Bengali 78s in `MudritSangeet`. |
| Assam | Jyoti Chitraban Digital Archive (jcda.in; blocked). Deori item at CIIL. |
| Nagaland | Cambridge Naga database (ask permission). Pitt Rivers Hutton cylinders. CIIL Sema Naga. Tibeto-Burman items on DSAL LSI. |
| Manipur | Thadou and Tangkhul at CIIL. Zemi recordings in the Cambridge Naga DB. |
| Meghalaya (Khasi, Garo, Jaintia) | Garo and Jaintia at CIIL. No Khasi archive found online. Khasi POW recordings at the Lautarchiv (not usable). |
| Mizoram | No archive found. UNT's Mizo Language Resource (1970–90 speech, one hymn performance) is outside the date range or not folk. |
| Tripura | Nothing found online. |
| Arunachal Pradesh | Nothing found from before 1970. ELAR deposits are recent. |
| Sikkim | CCRT "Folk Songs and Dances of Sikkim" (1989) on the IA NCAA mirror. Nothing from before 1971. |
| Odisha | Oriya birah song on DSAL LSI. CCRT Saraikella Chhau (1995) on the IA mirror. Nothing state-run found. |
| Bihar | Magahi, Maithili and Bhojpuri songs on DSAL LSI (1913–29). CIIL Santali (Bihar). No Bhojpuri archive found. |
| Jharkhand | Ho, Mundari, Kharia, Santali and Nagpuria songs on DSAL LSI. CIIL Munda and Ho. Bake's 1931 Santali recordings at Kairabani are on BL Sounds (known). |
| Uttar Pradesh | Braj rasiya, Bundeli and Bhojpuri on DSAL LSI. Bundelkhandi lok geet (1992) on the IA mirror. |
| Madhya Pradesh / Chhattisgarh | IGRMS tribal music on the IA mirror (1990s). Baghelī song on DSAL LSI. |
| Himachal Pradesh | Nothing found online. |
| Uttarakhand | Kumaoni and Jaunsari songs on DSAL LSI. SNA's 1969 *Songs of Kumaon* is a print and record catalogue entry only. |
| Jammu & Kashmir | CCRT "Soofiana Kalam from Kashmir" (1980) on the IA mirror. No archive from before 1971 found. |
| Ladakh | Morup Namgyal's AIR Leh archive (over 1,000 songs) was lost in a 2002 fire. CREM items only. Nothing usable. |
| Goa | The Central Library has digitised Konkani shellac (not online). Konkani 78s may be in `MudritSangeet`. Nothing usable confirmed. |
| Andaman & Nicobar | CREM Andamanese song from 1953–54 (restricted). Nothing usable. |
| Lakshadweep | DSAL LSI Amindivi songs (1913–29). Good fit. |
| Delhi | DSAL LSI includes the Delhi dastango Bakr Ali (story, not song). |
| Tibetan exile (Dharamsala and elsewhere) | Only LoC's Tibet Oral History Project (interviews) and commercial TIPA releases turned up. Budget ran out before LTWA and THL could be checked. **Re-check.** |

### Other countries
| Country | Result |
|---|---|
| Pakistan, Punjab | CREM Radio Pakistan 78s from about 1930–52 (known). Lok Virsa (YouTube, after 1974). VintageSense/SIRC Punjabi 78s on IA. |
| Pakistan, Sindh | Lybarger 1963–68 tapes at UW (ask). Sindhi 78s (Bhagat Kanwar Ram) in `MudritSangeet`. |
| Pakistan, Khyber Pakhtunkhwa | PBC Peshawar digital library (not public). Pashto POW recordings at the Lautarchiv (not usable). |
| Pakistan, Balochistan | Baluchi POW recordings at the Lautarchiv only. Nothing else found. |
| Pakistan, Gilgit-Baltistan / AJK | Nothing found online. |
| Bangladesh | The Travelling Archive (too recent). Shilpakala Academy has 13,000+ folk songs (DVDs and the National Archive; not online). Bangla Academy and Bangladesh Betar were not checked because the budget ran out. Nothing usable found. |
| Nepal | Music Museum of Nepal (EAP, mostly post-1970). Nepal Music Archive (Wegner collection; not online). Radio Nepal (nothing online). **Usable:** Gurung, Magar, Newari and Khas songs on DSAL LSI (1913–29), and Nepali shellac 78s in `MudritSangeet`. |
| Bhutan | Music of Bhutan Research Centre, 1968 sessions (ask). Mandala and Digital Himalaya (too recent). |
| Sri Lanka | Kulatillake tapes (digitised; not online). SLBC (nothing online). IA `radio-ceylon` (not usable). Noolaham reachable but no audio checked; Aavanaham blocked. Nothing usable found. |
| Maldives | Nothing found online. CREM Koechlin 1976–77 (metadata only, and after 1970). PARADISEC Dhivehi deposits (known; recent). |
| Afghanistan | Slobin 1967–72 (ask). UW Sakata 1966– (ask). Radio Afghanistan's 8,500 hours (not public). Collector blogs (not usable). Nothing directly usable found. |

---

## 3. Per-archive notes

### 3.1 DSAL: Gramophone Recordings from the Linguistic Survey of India (good fit)
- Portal: https://dsal.uchicago.edu/LSI/ (the old `/lsi/` path returns 404; the presentation was rebuilt in 2026 as a Vue single-page app). Contact: dsal@uchicago.edu.
- Content: George Grierson's LSI discs, recorded 1913–1929 and held at the British Library Sound Archive. The API lists 242 recordings across 9 volumes:

  | Volume | Recordings |
  |---|---|
  | II Mon-Khmer & Tai | 10 |
  | III Tibeto-Burman | 37 |
  | IV Munda & Dravidian | 86 |
  | V Indo-Aryan eastern | 27 |
  | VI mediate | 8 |
  | VII southern | 15 |
  | VIII north-western | 5 |
  | IX central | 41 |
  | XI Karen | 13 |

  At least 45 titles are songs, for example "Hō songs (Mage durang)", "Santali songs", "Chait song, Bhojpuri of Shahabad", "Jhumar song, Nagpuria", "Hōli song, Maithilī", "Song in Gurung / Magar / Newārī", "Marriage song, Gadabā", "Tōḍa song". The rest are the Prodigal Son parable and local stories.
- Audio: `https://dsal.uchicago.edu/images/reference/lsi/audio/<file>`, as MP3 or as MP4 (audio over a still image; the app treats .mp4 as video). Byte-range requests return 206. No login.
- API (verified): `GET /LSI/api/search?q=song` (returns at most 100 results), `GET /LSI/api/recordings/volume/{volumeText}`, `GET /LSI/api/recording/{id}`. Fields include `recording_file`, `recording_title`, `language`, `narrator`, `narrator_origin_district`, `location_province`, `lsi_volume_title`.
- Rights: DSAL's copyright page says "Whenever possible, the Digital South Asia Library ... use[s] a Creative Commons license... any applicable license [is] described on the web page of that resource." The LSI page gives no specific licence. The recordings date from 1913–29, so their copyright has expired under UK, Indian and NZ law.
- Suggested step: a courtesy email to DSAL to confirm streaming is fine and how the British Library should be credited. Some colonial-era recordings may need sensitivity review (for example ritual songs), but nothing flagged as sacred was seen.

### 3.2 Internet Archive `MudritSangeet`: Society of Indian Record Collectors (good fit, filtered)
- https://archive.org/details/MudritSangeet. The creator field reads "Dr. Suresh Chandvankar, Hon. Secretary". It is a sub-collection of Public Resource's `JaiGyan`.
- 1,693 audio items, many 78 rpm transfers named by label and catalogue number (HMV N-, Columbia GE-, Odeon, Young India, Hindustan), plus *The Record News* journal and catalogue scans.
- Folk and regional examples:
  - `n-80203-oje-19195-1-jeevan-ko-sahara-tara-devi-...`: 20 Nepali songs on HMV and Hindustan shellac.
  - `20220120_20220120_1231`: Bhagat Kanwar Ram (Sindhi).
  - `hmvn-61-2`: Gujarati bhajan.
  - `angel-3-aex-5075-ismail-azad-qawal`: qawwali.
  - Marathi and Tamil sides.
- Rights: most items carry no licence. 16 carry the PD Mark 1.0. Use date of publication. Indian Copyright Act s.27 gives sound recordings 60 years from publication, so pre-1966 records are public domain in India. NZ gives 50 years. Exclude film songs, which are not folk anyway, and anything undated.
- Machine access: IA advancedsearch (`collection:MudritSangeet AND mediatype:audio`) and the metadata API (verified).

### 3.3 Cambridge "The Nagas" database (ask permission)
- https://wwwe.lib.cam.ac.uk/nagas/ (Exploratour). Audio index: `/coll/4/audio/detail/all/index.html`. Files: `/media/naga/sound/*.mp3` (verified 206 audio/mpeg).
- I checked the production metadata of all 45 sound records:
  - 3 Hutton cylinders, 1919: R65041 "Hoiyi Olle" (Angami, Khonoma), R65027 (Chang), R65042 (Sema). Acquired from the Pitt Rivers Museum Archive.
  - 8 Fürer-Haimendorf recordings, 1970: Konyak and Zemi.
  - 17 anonymous, undated: "anonymous recordings of Naga songs and musical instruments", with notes by G. Ashin Panmei.
  - The rest: 7 tracks from the 1985 Maison des Cultures du Monde LP *Inde-Nagaland: Chant des tribus Sema et Zeliang* (commercial, exclude); 2 Ursula Betts (Graham Bower) recordings from 1986; 10 from 1987.
- Rights (page_8 of the introduction): "The Project encourages not for profit, educational and research uses of the material. The copyright in the materials remains with the original owners (see acknowledgements) or Rivers Video Project, Department of Social Anthropology, Free School Lane, Cambridge, CB2 3RF." No email was found on the site.
- Sensitivity: Naga songs include war, head-hunting and feast-of-merit songs. The Pitt Rivers "Recirculating sound recordings in NE India" project (Christian Poske, 2022) returned the Hutton cylinders to communities through the Highland Institute, Kohima. Ask about community wishes before streaming.

### 3.4 NCAA / IGNCA via IA `IndiaCulture` (ask permission)
- https://archive.org/details/IndiaCulture ("Cultural Resources of India", curated by Public Resource; notes say "posted for non-commercial purposes only and facilitates fair dealing usage").
- 7,969 audio items. Identifier prefixes:

  | Prefix | Source | Audio items |
  |---|---|---|
  | `dni.ncaa.RS-*` | Rupayan Sansthan, Jodhpur (Komal Kothari collection) | 1,081 |
  | `ICCR-*` | ICCR | 1,238 |
  | `IGRMS-*` | Indira Gandhi Rashtriya Manav Sangrahalaya, Bhopal (tribal music) | 729 |
  | `CCRT-*` | CCRT | 714 |
  | `SNA-*` | Sangeet Natak Akademi | 21 |
  | `KF-*` | Kalakshetra | |
  | `SPK-*` | | |
  | `dli.akashvani.*` | AIR | 26 |

- Only about 105 items are dated 1970 or earlier: 44 Kalakshetra, 39 SPK (classical), and a handful from AIR, IGNCA, SNA and SKSS. Folk items before 1971: `dni.ncaa.RS-1-AC` *Kalbelia Folk Songs* (4 Apr 1968, Borunda, recordist Manohar Lalas; side A and side B MP3s) and `dni.ncaa.RS-2-AC` *Marriage Songs of Mali Community by Women* (1969). Of the RS items, 334 are undated and 51 date from the 1970s.
- Rights: every RS item has `licenseurl` CC BY-NC 4.0. The embedded NCAA record says "Rights: Rupayan Sansthan" and "Creator: Rupayan Sansthan". It is unclear whether CC BY-NC was set by NCAA or by the uploader. The source portal ncaa.gov.in did not resolve from this sandbox.
- Verdict: ask IGNCA/NCAA (contact through ignca.gov.in; no address verified) and Rupayan Sansthan whether the CC BY-NC statement is theirs. If it is, the Rupayan items from 1970 or earlier become a good fit. Almost everything else in the collection is after 1970.

### 3.5 Rupayan Sansthan / EAP1153 (ask permission; known EAP)
- 405 open reels from 1958–1976 (Komal Kothari), plus 721 documents and 1,594 photos. Copies at Rupayan, ARCE and the BL. A UCLA MEAP project (cassettes) also exists: https://meap.library.ucla.edu/projects/rupayan-sansthan-india.
- Online audio status not confirmed: eap.bl.uk was blocked here. The reels before 1971 are exactly in scope (Manganiyar, Langa, Bhil epics such as Bagrawat).

### 3.6 CIIL National Tape Archive, Bhasha Sanchika (ask permission, low)
- https://sanchika.ciil.org (DSpace 7). A search for "National Tape Archive" returns 233 items.
- Example (verified): item `8cd58563-...` "Song & talk in Santali (Bihar)". The WAV is 812 MB, served at `/server/api/core/bitstreams/{uuid}/content` (206 audio/x-wav). `dc.date.created` is `00-00-0000`. The licence bitstream says "Copyright CIIL".
- Northeast and Munda coverage is unusually good, but the recordings are linguistic elicitations with unknown dates.

### 3.7 Sangeet Natak Akademi (ask permission)
- sangeetnatak.gov.in failed TLS verification (incomplete certificate chain). I did not bypass it.
- Secondary sources: about 4,973 hours of audio by 1990, recorded since the 1950s, with folk and tribal music included. The site says all material is "copyright to Sangeet Natak Akademi". A few SNA items are on the IA mirror (#3.4), for example `dni.ncaa.SNA-I_12-SP` (D.V. Paluskar bhajans, 1955, CC BY-NC).

### 3.8 Music of Bhutan Research Centre (ask permission)
- https://musicofbhutan.org/songs-of-the-third-kings-court: 15 songs from the 1968 Calcutta studio sessions, which were the first studio recordings of traditional Bhutanese song. Restored and sold on CD; lyrics and notes downloadable. A commercial product, so it can only be used with permission. The site shows no real contact email (only a template placeholder); use its Contact page.

### 3.9 Pakistan institutions
- **Lok Virsa** (verified): the "Relaunching of Original Recordings" project (https://lokvirsa.org.pk/?p=7678) remasters analog tapes for an app and YouTube (@lokvirsaofficial). Its digital library holds 260 digitised books, not audio. Contact: info@lokvirsa.org.pk, 051-9249209. The recordings date from after 1974.
- **Radio Pakistan**: PBC Peshawar digital library (2018) holds 8,000+ files including Pashto folk. A national digital archive was announced in 2023. Nothing public found. Sources: arabnews.pk/node/1370926, thefridaytimes.com (8 Jul 2023).
- **CREM** `CNRSMH_I_1952_001`: Radio Pakistan folk 78s from about 1930–52 (Punjabi, Bengali, Pashto, Sindhi). Access is listed as full but rights as restricted. Known archive.

### 3.10 Afghanistan
- Slobin (Alexander Street: "The Mark Slobin Fieldwork Archive, Music in the Afghan North, 1967–1972"; © Mark Slobin). markslobin.com hosts some of the materials.
- UW Ethnomusicology Archives: Sakata tapes from 1966 (logsheets at lib.washington.edu/static/public/ethno/logsheets/).
- The Radio Afghanistan Archive Project (8,500 hours; music.washington.edu publication) is not public.
- Afghan Records Gallery (afghanrecordsgallery.com, verified): collector posts with YouTube and SoundCloud embeds. Not usable.

### 3.11 Lautarchiv, Humboldt University (not usable, sensitive)
- https://www.lautarchiv.hu-berlin.de/en/objekte/lautarchiv/7711/ (verified): "Gurkha Lied", PK 285/1, 3 Jun 1916, singer Birkhe from Darjeeling. Metadata only; "Sign in for research access".
- These are coerced prisoner-of-war recordings, so treat them as sensitive. Contact: lautarchiv@hu-berlin.de. The Europe sweep may also list this archive.

### 3.12 Sri Lanka
- IA `radio-ceylon` (verified): `folk-music-Sri-Lanka` holds off-air SLBC "Jana Gee" broadcasts from 2017. `Sinhala-Gramaphone-2` holds 109 commercial Sinhala record transfers with a PD Mark added by the uploader. Not usable.
- The Kulatillake open-reel field recordings (Sinhala, Tamil, Burgher baila, Vedda) have been digitised (sljars.sljol.info/articles/31) but are not online.
- The Folk Music Conservation Library (Ministry of Culture, 2010) is not online.

### 3.13 Other notes
- **Arnold Bake** (Bengal 1931–34 and 1956, Nepal, Sri Lanka): on British Library Sounds, which is already known. Some Nepal tapes (C52/NEP/67–68) are not uploaded. SOAS holds field notes. Indian Tribal Heritage (indiantribalheritage.org) aggregates links to the Bake Santali 1931 and Hutton audio; it is not a source archive.
- **Deben Bhattacharya** (1954 onward): originals at the BnF (known Gallica/BnF) and CREM. Commercial reissues by ARC and Frémeaux.
- **Digital Himalaya music** (https://www.digitalhimalaya.com/collections/music/, verified): Laya (Bhutan), Thangmi (Nepal) and others, all from the 2000s. The site is CC BY-NC-ND 3.0.
- **The Travelling Archive** (verified): "The copyright of all field recordings made by The Travelling Archive is held jointly by the performer/s and the archive." Recordings from 2003 onward.

---

## 4. Sources (main)
- DSAL LSI: https://dsal.uchicago.edu/LSI/ ; https://dsal.uchicago.edu/about.html ; CRL note https://www.crl.edu/node/6790
- IA collections: https://archive.org/details/MudritSangeet ; https://archive.org/details/IndiaCulture ; https://archive.org/details/dni.ncaa.RS-1-AC ; https://archive.org/details/radio-ceylon
- Cambridge Nagas: https://wwwe.lib.cam.ac.uk/nagas/coll/4/audio/detail/all/index.html ; copyright: https://wwwe.lib.cam.ac.uk/nagas/coll/4/xintroduction/detail/all/page_8.html
- Pitt Rivers Hutton project: https://prm.ox.ac.uk/node/2220641 ; https://journal.iasa-web.org/index.php/pubs/article/view/144
- CIIL: https://sanchika.ciil.org/items/8cd58563-5e92-4621-82fb-a6e36c545533
- EAP: https://eap.bl.uk/node/2596 (EAP190), https://eap.bl.uk/node/3011 (EAP468), https://eap.bl.uk/project/EAP1153, https://eap.bl.uk/node/3676 (EAP901)
- Lok Virsa: https://lokvirsa.org.pk/?p=7678 ; https://lokvirsa.org.pk/digital-library/
- Music of Bhutan RC: https://musicofbhutan.org/songs-of-the-third-kings-court
- Lautarchiv: https://www.lautarchiv.hu-berlin.de/en/objekte/lautarchiv/7711/
- Slobin: https://search.alexanderstreet.com/view/work/bibliographic_entity%7Ccontext_detail%7C3568094
- Travelling Archive: https://www.thetravellingarchive.org/ ; Digital Himalaya: https://www.digitalhimalaya.com/collections/music/
- IGNCA Kalasampada / NCAA background: https://ignca.gov.in/en/divisionss/cultural-informatics/kalasampada/ ; https://www.gktoday.in/the-national-cultural-audiovisual-archives-ncaa-project-of-india-becomes-world-s-first-trusted-digital-repository/
- Sangeet Natak Akademi: https://sangeetnatak.gov.in/sections/documentation (blocked here; from search snippet)
- Afghan Records Gallery: https://www.afghanrecordsgallery.com/
- Kulatillake restoration: https://sljars.sljol.info/articles/31

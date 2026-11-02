# Archive sweep, second pass: Asia and the Pacific (gaps from the first pass)

Swept 2026-11-02 for Old Folk Air. This pass covers only the gaps listed in [sweep-south-asia.md](sweep-south-asia.md), [sweep-east-asia.md](sweep-east-asia.md) and [sweep-southeast-asia-pacific.md](sweep-southeast-asia-pacific.md):
- Tibetan exile institutions
- Bangladesh's national bodies
- Prasar Bharati / All India Radio
- China's minority peoples
- Macau
- Japan: NHK, Tokyo University of the Arts and Tobunken
- Dongguk's Korean 78s archive
- Southeast Asia: Laos, Timor-Leste and Brunei
- The Pacific: Solomon Islands, Cook Islands, Torres Strait, Tonga and Samoa

Every archive named in those files or in [sweep-brief.md](sweep-brief.md) is treated as known. Where this pass found a new sub-collection of a known archive, it says so but does not re-assess it.

Method: 89 WebSearch queries, about 30 of them in local languages (Tibetan, Bengali, Hindi, Chinese, Japanese, Korean, Portuguese, Tetum, Malay, Lao, Tongan, Samoan, Cook Islands Māori). These were followed by curl checks of pages, APIs and audio files (HTTP status, content type, range requests) and Internet Archive (IA) advancedsearch queries. No bot check, login or block was bypassed.

Status tags:
- **verified**: the page, API or file was fetched.
- **search-only**: known only from search results.
- **blocked**: the host refused access or could not be reached.

**Bottom line:**
- **No new archive is a clean "good fit".**
- **Strongest new lead: Kyoto University's KURRA 1940–43 folk-song field discs.** They are already streamable as direct MP4 files with IIIF manifests, but the terms are research-only, so ask first.
- **Second lead: Arts Council Korea's DA-Arts collection of 274 Korean 78s** (login to play; ask).
- Most pre-1970 material for the Pacific gap countries sits in archives that are already known (Auckland AMPS, Ngā Taonga, PARADISEC, CREM, British Library).
- **The Torres Strait material is community-controlled and should be excluded.**

---

## 1. Ranked table

No new good fits.

### Ask permission

| # | Archive | Country | Content (1970 or earlier) | Audio access | Rights | Machine access | Verdict / contact | Status |
|---|---|---|---|---|---|---|---|---|
| 1 | **Kyoto University Research Resource Archive (KURRA): "Sound recordings on Folklore Research around the Kinki Region, 1940–1943"** (Fuji Naomoto / Hirayama Toshijirō) **NEW** | Japan (Kyoto, Gifu, Shimane, Hyōgo, Nara) | 107 Riken alumite field discs, 1940–43: old folk songs, work songs (摺臼唄 hand-mill songs, 糸つむぎ唄 spinning songs), dance songs (掛踊), 神事歌謡 Shinto ritual songs. About 40 file-level items are online. | **Direct MP4 files (audio over a still image), no login.** Range request returns 206 video/mp4. | "Open for research. Using the materials for business/commercial purposes is prohibited. Unauthorized copying and re-distributing in altered electronic forms are also prohibited." | **IIIF Presentation 3 AV manifests**, ArchivesSpace, CSV export | **Ask permission (top candidate).** kurra-info@inet.museum.kyoto-u.ac.jp. Flag the Shinto ritual songs. | verified |
| 2 | **DA-Arts 한국예술디지털아카이브 (Arts Council Korea / ARKO Arts Archive): Renaissance (르네쌍스) shellac collection** **NEW** | South Korea | 274 Korean 78 rpm tracks (Park Yong-chan collection), c.1910s–1940s: pansori, japga, minyo, pungnyu, early popular songs. Digitised 2020. | Streaming player; **login needed to play** (not attempted) | No copying or transmission without permission | HTML; JSON streaming API sits behind the session | **Ask permission.** Phone +82-2-760-4750 (on the site); no public email found | verified (list page; 274 items) |
| 3 | **UW Ethnomusicology Archives: Jeanette Snyder Tibet recordings, 1964** (coll. 1968005; new sub-collection of known UW) | India (Tibetan exiles, Kalimpong and Darjeeling) | 5 reels, 2 h 43 min: lhamo opera *Padma 'od bar* by the Kyormolungpa troupe. Digitised 2016. | On-site / by request | "Access is restricted." | Archives West EAD | **Ask permission.** ethnoarc@uw.edu. Flag lhamo preludes (ritual invocation). | verified (finding aid) |
| 4 | **UW Ethnomusicology Archives: Melvyn Goldstein Tibet recordings** (coll. 1968002; new sub-collection of known UW) | Tibetan exiles | 6 reels: Western Tibetan *stod gzhas* folk songs with dramnyen, Lhasa beggar-style songs, *sgor gzhas* circle dance, lhamo drums, namthar. Undated; accessioned 1968, so 1968 or earlier (inferred). | On-site / by request | "Access is restricted." | Archives West EAD | **Ask permission.** ethnoarc@uw.edu (and Goldstein, Case Western). Flag the namthar track (religious). | verified (finding aid) |
| 5 | **CREM: Jacques Lemoine Laos collection** (new sub-collection of known CREM, indexed in Pratinada) | Laos | 140 items, 1964–69: Hmong vocal (66) and qeej (26), Lao instrumental (24), Lahu, Tai Lue | CREM player; Pratinada WAV | Every item is **In Copyright, Non-Commercial Use Permitted** (CNRS) | Pratinada Omeka S API, CREM OAI | **Ask CREM** (crem.lesc[at]cnrs.fr). Flag the funeral and ritual qeej as sensitive. | verified |
| 6 | **Tibetan Institute of Performing Arts (TIPA)** | India (Dharamsala; founded 1959) | Says it holds an archive of old audio and video recordings. Early-1960s 45 rpm *Pema Thang, Four Folk Songs from Tibet* (copy at CREM, CNRSMH_E_2018_001_001). | YouTube only | Not stated; TIPA holds the rights to its early discs | None | **Ask permission.** tibetanarts@tibet.net. Flag ritual pieces (ache lhamo preludes, toshey). | verified (site, contact page) |
| 7 | **Solomon Islands National Museum tapes** (BL EAP project digitised by PARADISEC; new sub-collection of known PARADISEC/EAP) | Solomon Islands | Up to 200 tapes of custom songs and panpipes; catalogue years mostly 1970s, so few, if any, items qualify | PARADISEC (Cloudflare) | Search snippets: "The Solomon Islands National Museum licenses the use of this recording for non-commercial educational purposes" | PARADISEC API (known) | **Ask SINM through PARADISEC** (low yield) | search-only / blocked |
| 8 | **Ngā Taonga: Cook Islands national audiovisual collection** (deposited 2013; new sub-collection of known Ngā Taonga) | Cook Islands | 1960s–2000s: Constitution celebrations, Gospel Day, ariki ceremonies, Radio Cook Islands tapes | Catalogue shows "private viewing only" | Needs Cook Islands National Archives membership and the Archivist's approval (secondary source) | Ngā Taonga (known) | **Ask permission.** Ministry of Cultural Development, mocd.staff@cookislands.gov.ck (search-only address) | search-only |
| 9 | **LTWA Audio-Visual Archive** (Library of Tibetan Works and Archives) | India (Dharamsala) | More than 53,000 hours, mostly oral histories (from 1976) and Buddhist teachings. Says it holds "songs, music" and gramophone records, but no pre-1971 song list is online. | SoundCloud `ltwa_archive` (4,371 tracks; visible playlists are teachings) and YouTube | Not stated | None | **Ask permission (low).** av_archive@tibetanlibrary.org: ask only about early secular song. Exclude teachings as sacred. | verified |
| 10 | **Dongguk University 한국음반아카이브연구소 (Korean Record Archive Research Institute)**, shellac digital archive system | South Korea | 1907–1945 SPs: about 13,000 titles, about 4,000 with audio (2011 figures) | **Current URL not found**; candidate hosts failed | Unresolved in 2011 | Unknown | **Ask permission (low)**, once a live site appears. The institute is active (director Bae Yeon-hyung). DA-Arts (#2) is the practical alternative. | search-only; KRM project record verified |
| 11 | **Radio Televisyen Brunei (RTB)** | Brunei | Holds vinyl and old media (anniversary exhibition); nothing listed online | None | Not stated | None | **Ask later, if ever.** korporat@rtb.gov.bn | verified (site, email) / search-only (holdings) |
| 12 | **SIBC archive** | Solomon Islands | Broadcast tapes back to about independence, including "traditional chants"; being digitised in Australia (2025). Mostly after 1970. | None online | Not stated | None | **Ask later.** sibcnews@solomon.com.sb (newsroom, the only public address seen) | search-only |

### Not usable

| # | Archive | Country | Reason | Status |
|---|---|---|---|---|
| 13 | **Prasar Bharati Archives / AIR Central Archives** | India | Audio is "available for sale on commercial and non-commercial basis along with licenses" under a rate card (PDF now 404). Site is "All Rights Reserved". Public access is commercial CDs, YouTube `DDARCHIVES` and the WAVES app. No open audio and no public archive email; the only route is a paid licence. | verified (archives page); archives.prasarbharati.org and waveott.in blocked |
| 14 | **Bangladesh Betar** (Transcription Service) | Bangladesh | Reportedly holds about 50,000 folk-song tapes (2005 report), but nothing is online. The site has live streams, an app and one YouTube playlist. | verified (site) / search-only |
| 15 | **Bangla Academy** (Folklore Division) | Bangladesh | No audio found online; its folklore output is print (*Haramoni*, Folklore Sankalan). Site unreachable (TLS errors, proxy refusal), so no contact verified. | blocked |
| 16 | **Bangladesh Shilpakala Academy** | Bangladesh | Its 13,481-song collection consists of new recordings from 2020 on (DVDs, deposited with the National Archives). | blocked (TLS) / search-only |
| 17 | **National Archives / National Library of Bangladesh** | Bangladesh | Paper records only. | blocked (TLS) |
| 18 | **THL Audio-Video on UVA Mandala** | Tibet / Himalaya | About 494 folk-song items, all from the 2000s–2010s. AV host behind an AWS WAF bot check. | thlib.org verified; Mandala blocked |
| 19 | **Tibet Oral History Project** (tibetoralhistory.org) | Exile communities | Video interviews from 2000 onward, not songs; "All rights reserved worldwide". | verified |
| 20 | **Tibet Oral History Archive Project (Goldstein, Case Western)** | Tibet / exile | Interviews only; already covered via LoC. | search-only |
| 21 | **IA `lp_tibetan-folk-and-minstrel-music_various`** | Tibet / Ladakh | 1967 commercial LP of the Crossley-Holland 1961 recordings (originals at CREM, known); no licence. | verified |
| 22 | **Koizumi Fumio Memorial Archives** (Tokyo Univ. of the Arts) | Japan / worldwide | 11,926 tape files (1948–80), listening on-site only; "no lending or copying outside the room". Contact form koizumi.geidai.ac.jp/guide/contact-form/. | verified |
| 23 | **Tokyo Univ. of the Arts Library SPs** | Japan | Classical; not online. | search-only; db.lib.geidai.ac.jp blocked (502) |
| 24 | **Tobunken, Dept. of Intangible Cultural Heritage** | Japan | Takeuchi collection SPs and tapes not online yet (digitisation "aims at" future release). Online AV is modern and mostly opening excerpts only. Contact mukei_tobunken@nich.go.jp for later. | verified |
| 25 | **NHK Archives** | Japan | Player for copyrighted clips. No 日本民謡大観 survey audio online. NHK/Columbia min'yō 78s are already on NDL Rekion (known). | partly verified |
| 26 | **Showa Kan digital archive** **NEW** | Japan | Folk-song SPs listed (e.g. 津山民謠 (Tsuyama folk song), Columbia PR788), but "館内限定音源" (audio on-site only). Rights `incr`/`undet` in Japan Search. | verified |
| 27 | **KRM td067 intangible-heritage DB** (NRF Korea) | South Korea | YouTube links only. | verified |
| 28 | **Archives of Macao** (archives.gov.mo) | Macau | Documents and photos; no sound fonds online. Contact info.am@icm.gov.mo, +853 2859 2919. | verified |
| 29 | **Memória de Macau** | Macau | Host unreachable (as in the first pass). | blocked |
| 30 | **Hmong Education Archives & Library** (St Paul) | USA (Hmong diaspora) | Physical only, mostly post-1975 (hmongarchives.org@gmail.com). | verified |
| 31 | **ECura sung-lyrics database** | China (Yi, Miao, Bai) | 2020s recordings. | search-only |
| 32 | **NIU SEAsite "Traditional Music and Songs in Laos"** / National Library of Laos ATML | Laos | Recordings from 1999–2002; "for study use only. NOT FOR COMMERCIAL USE." | verified |
| 33 | **TAEC Luang Prabang** | Laos | Modern recordings; SoundCloud only. | verified (hosts) / search-only |
| 34 | **RTP Arquivos** | Timor-Leste (Portuguese colonial radio) | Could not assess: HTTP 404 on every path, including the root. Retry later. | blocked |
| 35 | **Arquivo & Museu da Resistência Timorense** | Timor-Leste | No music holdings. **Its old domain amrtimor.org is now an unrelated spam site; don't link to it.** | verified (domain) / search-only |
| 36 | **Ruy Cinatti collection** (IELT, NOVA Lisbon) | Timor-Leste | 1950s–60s Kemak and Bunak oral traditions being transcribed; no audio online. Watch the project. | search-only |
| 37 | **Radiodiffusion Internasionaal Annexe** (collector blog) | Brunei, Laos | Rips of 1960s–70s commercial pop records; no rights statement. | verified |
| 38 | **Digital Pasifik: Viking Sevenseas** (about 900 tracks) | Cook Is., Samoa, Tonga, Solomons, Fiji and others | YouTube embeds only; "Copyright status: Unknown"; commercial label. | verified |
| 39 | **NFSA australianscreen: 1898 Torres Strait clip** | Torres Strait | Direct MP3, but the terms say "you must not publish them elsewhere or redistribute clips in any way". Indigenous material. **Exclude.** | verified |
| 40 | **IA `maino-of-yam-yamaz-sibarud`** | Torres Strait | Private re-upload of the NFSA clip; no licence, no community approval. **Exclude.** | verified |
| 41 | **Jeremy Beckett recordings, 1958–61** (AIATSIS MS 5363; CREM LP record CNRSMH_E_1976_017_008) | Torres Strait | AIATSIS by appointment, community-controlled; CREM record is metadata only, "Restreint". **Exclude.** | search-only / verified (CREM) |
| 42 | **Gab Titui Cultural Centre** | Torres Strait | CD/DVD packages for sale only; community-controlled (info@gabtitui.gov.au). | verified |
| 43 | **Tonga Broadcasting Commission; Tonga Traditions Committee / Palace Office** | Tonga | Nothing online. Copies of the ZCO/A3Z tapes (1961–77) and the Palace Office recordings are at Auckland AMPS (known). | verified (tbc.to) / search-only |
| 44 | **Radio Samoa (2AP), NUS, SNARA, RLS Museum** | Samoa | Nothing online. Copies of the 2AP "Songs of the Mau" 78s (1936–62) and Radio Samoa tapes are at AMPS (known). | search-only |
| 45 | **ASCC Samoan Studies Institute** | American Samoa | 1998–99 recordings; restricted; page now 404. | search-only |

---

## 2. Country and gap checklist

### South Asia
| Gap | Result |
|---|---|
| Tibetan exile: LTWA | Large AV archive, mostly teachings and oral history; SoundCloud and YouTube only. Ask only about early secular song (low). |
| Tibetan exile: TIPA | Holds an old-recordings archive, nothing online beyond YouTube. Early-1960s 45 at CREM. Ask tibetanarts@tibet.net. |
| Tibetan exile: THL | Folk-song items all from the 2000s–2010s; AV host behind a bot check. Not usable. |
| Tibet oral history projects | Interviews, not songs. The related finds are the 1964 Snyder and pre-1968 Goldstein song tapes at UW (restricted; ask ethnoarc@uw.edu). No Aufschnaiter recordings found. |
| Bangladesh: Bangla Academy | Nothing online; site unreachable. |
| Bangladesh: Shilpakala Academy | Recordings from 2020 on, not online. Not usable. |
| Bangladesh: Bangladesh Betar | About 50,000 folk-song tapes reported; nothing online. Not usable now. |
| India: Prasar Bharati / AIR | Commercial and non-commercial paid licences only; no open audio. Not usable. |

### East Asia
| Gap | Result |
|---|---|
| China: Dong 侗族 | Nothing online found. Guizhou "411工程" (Guizhou culture-project) village folk-song digitisation has no public database. |
| China: Zhuang 壮族 | Nothing online found. |
| China: Naxi 纳西 | Nothing online found. Any Dongba chant found later must be flagged as ritual. |
| China: Yi 彝族 | Nothing from 1970 or earlier online. ECura is from the 2020s. The BL EAP Yi project is not public. |
| China: Miao/Hmong 苗族 | Nothing from 1970 or earlier online on the China side. The 1933 Ling Chunsheng / Ruey Yifu Xiangxi Miao survey belongs to IHP Academia Sinica (known); ask whether it made recordings when contacting IHP. |
| China: Inner Mongolia 长调 | Nothing online found; library databases hold texts only. |
| Macau | Nothing found online. Archives of Macao has no audio; Memória de Macau is blocked; no TDM, Patuá or naamyam (南音) sound archive found. |
| Japan: NHK | Nothing new. No 日本民謡大観 survey audio online; NHK min'yō 78s are on Rekion (known). |
| Japan: Tokyo Univ. of the Arts | Koizumi Archive is on-site only; library SPs are classical. Not usable. |
| Japan: Tobunken | Nothing from 1970 or earlier online yet. |
| Japan (bonus) | **KURRA Kyoto 1940–43 field discs: ask permission (top candidate).** Showa Kan: on-site only. |
| Korea: Dongguk 78s archive | Current home not found; the institute is active but no live database answered. **Alternative: DA-Arts, 274 Korean 78s (ask).** |

### Southeast Asia
| Country | Result |
|---|---|
| Laos | No open source. CREM Lemoine 1964–69 (140 items, InC-NC): ask CREM, flag ritual qeej. ATML/SEAsite are 1999–2002 and study-only. Lao-language searches found nothing. |
| Timor-Leste | **Nothing new found online.** CREM 1960s (first pass) remains the only source. RTP Arquivos could not be assessed (404s). |
| Brunei | **Nothing found online.** RTB could be asked later (korporat@rtb.gov.bn). |

### Pacific
| Country | Result |
|---|---|
| Solomon Islands | Nothing new and open. SINM tapes via PARADISEC (non-commercial educational licence; few, if any, items 1970 or earlier); SIBC digitising, not online; Zemp at CREM (known). |
| Cook Islands | Nothing open online. National AV collection at Ngā Taonga (private viewing, the Archivist must approve); McLean 1967 at AMPS (known); Viking LPs on YouTube only. |
| Torres Strait | **Nothing usable; exclude.** All material is community-controlled with no clear approval for open access. ATSILIRN Protocol 12 bars secret, sacred or sensitive items from going online. |
| Tonga | **Nothing found online** outside AMPS (known). Tongan-language search found nothing. |
| Samoa | **Nothing found online** outside AMPS (known). Samoan-language search found nothing. |

---

## 3. Per-archive notes

### 3.1 KURRA, Kyoto University: 1940–43 Kinki folk-song field discs (ask permission; top candidate) [verified]
- Finding aid: https://keep.rra.museum.kyoto-u.ac.jp/repositories/2/resources/43 (FoLs SRD 2021/2). Digital collection: https://da.rra.museum.kyoto-u.ac.jp/collection/repositories/2/resources/43
- Example item: https://da.rra.museum.kyoto-u.ac.jp/metadata/repositories/2/archival_objects/125810 (disc 39: タウス引唄 (Yagyū), 糸つむぎ唄 (spinning song) and おかけ踊 (Tsukigase, Nara), 1943-07-13).
- IIIF manifest: https://da.rra.museum.kyoto-u.ac.jp/iiif/metadata/repositories/2/archival_objects/125810/movie/manifest.json (200 JSON). Media file: https://da.rra.museum.kyoto-u.ac.jp/ark:/62587/ar20195/40450/do000481800_movie.mp4 (206 video/mp4, no login). Both checked again while writing this report.
- History: digitised in 2013 by stylus playback to linear PCM and re-released in 2020. The recordings do not overlap with Machida Kashō's 1940 *Nihon min'yō shūsei* (Collection of Japanese folk songs).
- Terms: quoted in the table. Reproduction for publication or exhibition needs consent from the Kyoto University Japanese History department.
- Pitch: Old Folk Air is free and non-commercial, does not re-host, and plays through an encrypted cache, which is close to the "no redistribution" spirit of the terms.
- Contact: **kurra-info@inet.museum.kyoto-u.ac.jp** (https://www.rra.museum.kyoto-u.ac.jp/contact/, verified).
- Sensitivity: exclude or review the 神事歌謡 (Shinto ritual songs). Work and dance songs are fine.
- Integration note: the IIIF AV manifests give a clean machine route. The MP4s contain an audio track over a still image.

### 3.2 DA-Arts Renaissance Korean shellac collection (ask permission) [verified]
- List: https://www.daarts.or.kr/renaissance/list (274 items). Intro: https://www.daarts.or.kr/renaissance/intro. Example: https://www.daarts.or.kr/renaissance/view/133 (산염불, Regal C188, c.1934).
- Playback needs a login ("로그인 후 이용가능 합니다"); the stream comes from POST `/api/streaming/multimedia`. I did not log in.
- Background: https://www.newdaily.co.kr/site/data/html/2024/12/30/2024123000028.html (restored in 2020 under ARKO's arts-records digitisation project; ARKO also posts YouTube playlists).
- Pre-1945 Korean SPs may well be out of copyright in Korea, but Korea's transitional rules for old sound recordings were not checked, so this is unconfirmed. ARKO's own terms still apply. Ask for a non-commercial streaming permission. Contact: +82-2-760-4750; no public email found.

### 3.3 Dongguk Korean Record Archive (search-only)
- Project record: https://www.krm.or.kr/krmts/link.html?dbGubun=SD&m201_id=10018895&res=y (2008 NRF project, PI 배연형 Bae Yeon-hyung).
- Background: 2011 article https://www.khan.co.kr/article/201102061900235/amp ; 2023 talk https://mediahub.seoul.go.kr/archives/2008238
- Hosts tried, none working: www.sparchive.co.kr (connection reset), sparchive.dongguk.edu (502), www.kdisc.or.kr (502), sparchive.or.kr and www.koreanrecord.or.kr (no connection).
- No shellac database was found at the Academy of Korean Studies or the Korean record studies society.

### 3.4 UW Ethnomusicology Archives, Tibetan sub-collections (ask permission) [verified finding aids]
- Snyder 1964: https://archiveswest.orbiscascade.org/ark:80444/xv688292
  - Tapes 1–4: lhamo at Tharchin's house, Kalimpong, February 1964.
  - Tape 5: live excerpts, June 1964.
  - Digitised to WAV, October 2016.
- Goldstein: https://archiveswest.orbiscascade.org/ark:80444/xv922698. The "1968 or earlier" date is inferred from the accession number and Goldstein's PhD fieldwork.
- Contact: **ethnoarc@uw.edu** (on the finding-aid pages; checked again).
- The finding-aid text is CC BY 4.0, but that does not cover the audio.
- Before streaming, ask whether TIPA or the performers' families should be consulted.

### 3.5 TIPA and LTWA (ask permission) [verified]
- TIPA:
  - https://tipa.asia/about-us/ ("an archive with both digital and old-school audio and video recordings").
  - Contact page https://tipa.asia/contact-us/: **tibetanarts@tibet.net**.
  - The CREM item CNRSMH_E_2018_001_001 (Pema Thang 45, rights holder Bho Ki Dhoegar Tsokpa, TIPA's predecessor) is the most concrete early item to ask about.
- LTWA:
  - https://tibetanlibrary.org/audio-visual/ ("more than 53,000 hours ... primarily consisting of oral histories ... teachings ... songs, music").
  - Head of the AV department: **av_archive@tibetanlibrary.org**.
  - SoundCloud: https://soundcloud.com/ltwa_archive/
  - IASA Bulletin 53 (2005, search-only) mentions gramophone records and reel tapes.
  - Most holdings are Buddhist teachings: exclude them as sacred.

### 3.6 THL and Tibet oral history (not usable)
- THL:
  - https://thlib.org/audio-video/ (200).
  - av.mandala.library.virginia.edu and mandala.library.virginia.edu return 202 with `x-amzn-waf-action: challenge` (bot check, not bypassed).
  - Folk-song projects (Rebgong, Ngawa, Gyelrong, Sichuan nomads) are all from the 2000s onward.
- Oral history:
  - TOHP: https://www.tibetoralhistory.org/
  - TOHAP: https://case.edu/artsci/tibet/projects/tibet-oral-history-archive-project-tohap
- IA: a search for Tibetan audio dated 1970 or earlier found only `lp_tibetan-folk-and-minstrel-music_various` (commercial, no licence).

### 3.7 Bangladesh (not usable)
- **Bangla Academy:**
  - banglaacademy.gov.bd had a TLS error; www.banglaacademy.org.bd was refused by the proxy; the portal mirror says "Domain is not available".
  - Bengali searches (বাংলা একাডেমি ফোকলোর আর্কাইভ (Bangla Academy folklore archive) and others) found only print folklore.
- **Shilpakala Academy:**
  - https://thefinancialexpress.com.bd/national/bsa-begins-archiving-folklore-songs-in-dhaka-division-1601038173 ; https://thedailystar.net/node/1434583
  - Recordings from 2020 on.
- **Bangladesh Betar:**
  - https://betar.gov.bd/ (200; www.betar.gov.bd has a TLS error).
  - The 2005 Daily Star report https://www.thedailystar.net/node/192112 says the Transcription Service has "tapes of 50,000 folksongs". There is no public archive, and no archive email beyond webmail.
- **IA:** searches for Abbasuddin, Abdul Alim, bhawaiya, bhatiali and palligeeti found nothing from 1970 or earlier. The only hit is `dni.ncaa.ICCR-452-AC` (undated, inside the known `IndiaCulture`).
- For now, the only open route to pre-1971 East Bengal radio material is the CREM Radio Pakistan 78s (known, from the first pass).

### 3.8 Prasar Bharati / AIR (not usable) [verified]
- https://prasarbharati.gov.in/prasar-bharati-archives/air-archives/ : "Commercial Releases & Marketing: Audio assets ... available for sale on commercial and non-commercial basis along with licenses."
- The rate card https://prasarbharati.gov.in/wp-content/uploads/2021/07/Policy_RateCard_AIR_Archives.pdf now returns 404.
- Zonal sound archives: Kolkata, Mumbai (VBS) and Chennai.
- Other outlets: YouTube https://www.youtube.com/c/DDARCHIVES ; WAVES OTT (has a Folk category; waveott.in blocked, 502).
- No generic archive email is published. A paid non-commercial licence could be asked about through the Central Archives, Akashvani Bhawan, New Delhi, but the cost is unknown.

### 3.9 Japan: Tokyo University of the Arts, Tobunken, NHK, Showa Kan
- **Koizumi Archive:**
  - https://koizumi.geidai.ac.jp/collections/onkyo/opentape/
  - Terms: https://koizumi.geidai.ac.jp/guide/terms/ (research and non-profit educational use only, listening in the room only).
- **Tobunken:**
  - https://www.tobunken.go.jp/ich/video/ and /ich/archive/ ; plan: https://www.tobunken.go.jp/japanese/projectlist/2021/m03.pdf
  - Contact: mukei_tobunken@nich.go.jp. Worth re-checking in a year or two.
- **NHK:**
  - https://www.nhk.or.jp/archives/ ; NDL guide https://ndlsearch.ndl.go.jp/rnavi/avmaterials/post_962
  - Toyama University (KAKEN 25370103) digitised Machida Kashō's NHK-survey audio for research use only.
- **Showa Kan:**
  - https://search.showakan.go.jp/search/record/detail.php?id=90007672 (audio on-site only).
  - Found through the Japan Search API (`jpsearch.go.jp/api/item/search/jps-cross?keyword=民謡&f-contents=sound`, 4,689 hits, mostly NDL/Rekion PDM items, already known).

### 3.10 China's minority peoples (nothing found) [search-only]
- About a dozen searches in Chinese found nothing online from 1970 or earlier for the Dong, Zhuang, Naxi, Yi, Miao or Inner Mongolian peoples. They covered 侗族大歌 (Dong grand songs), 壮族 嘹歌 (Zhuang songs), 纳西古乐 东巴 (Naxi ancient music, Dongba), 苗族古歌 飞歌 (Miao ancient songs), 彝族 民歌 (Yi folk songs), 蒙古族长调 (Mongol long song), 中国民族民间歌曲集成 原始录音 (original recordings for the Anthology of Chinese folk songs), 中央民族大学 (Minzu University) and 老唱片 数字化 (old-record digitisation).
- IA advancedsearch for the Chinese and English names, limited to audio dated 1890–1970, returned 0 relevant hits.
- The largest corpus is still CTMSA (first pass; login required, all rights reserved).

### 3.11 Macau [verified / blocked]
- Archives of Macao: https://www.archives.gov.mo/cn/ ; catalogue https://www.archives.gov.mo/WebAS/ (searches sometimes reset the connection). Contact **info.am@icm.gov.mo**, +853 2859 2919 (from https://dictionary.dscd.gov.mo/archives.json). No sound fonds found.
- Memória de Macau: memoryofmacao.mo and macaomemory.mo, no connection.
- Macau naamyam (Au Kuan Cheong): https://www.icm.gov.mo/en/events/detail/6692 (heritage page; no archive audio).

### 3.12 Laos [verified]
- CREM Lemoine:
  - Example item: https://archives.crem-cnrs.fr/archives/items/CNRSMH_I_1968_023_007_03/ (Hmong kh'eng solo, Saiyaboury, 1964, "Access type full").
  - Pratinada query: `https://omekas.seasia-hearing.org/api/items?fulltext_search=Lemoine&per_page=200` (142 results, all InC-NC).
  - Dates: 1964 (36), 1965 (41), 1966 (12), 1969 (51).
  - Ritual qeej (funeral, *tso plig*) must be flagged.
- SEAsite: https://seasite.niu.edu/lao/culture/traditional_Music/music_collection.htm (study use only; recordings 1999–2002; the sample MP3 returned 404).
- TAEC: https://www.taeclaos.org/ and https://soundcloud.com/taeclaos
- Lao National Radio: lnr.org.la is reachable, but no archive was found.

### 3.13 Timor-Leste
- RTP Arquivos: https://arquivos.rtp.pt/ returned HTTP 404 on every path [blocked]. This is the one place where pre-1975 Portuguese colonial radio material from Timor might sit. Retry from another network, or ask RTP.
- AMRT: amrtimor.org is now an unrelated SEO/spam site [verified]. Do not link to it.
- Cinatti project: https://ielt.fcsh.unl.pt/?p=16220 [search-only]
- Veiga de Oliveira's 1960s recordings are of mainland Portugal, not Timor [search-only].

### 3.14 Brunei [verified]
- RTB: https://www.rtb.gov.bn/ (korporat@rtb.gov.bn).
- DBP: https://www.dbp.gov.bn/ (info@dbp.gov.bn). No audio collection found.
- The guessed hostnames for Brunei Museums and Arkib Negara did not resolve; the names may simply be wrong.
- Malay searches (lagu rakyat Brunei, adai-adai, arkib RTB) found no audio archive.

### 3.15 Solomon Islands and Cook Islands
- **SINM:**
  - EAP page: https://www.eap.bl.uk/node/3346 (202 challenge).
  - PARADISEC collection SINM: https://catalog.paradisec.org.au/repository/SINM (Cloudflare).
  - Licence wording is from search snippets.
- **SIBC:** https://www.sibconline.com.sb/ ; digitisation story: https://indepthsolomons.com.sb/race-to-save-solomons-history-as-radio-reigns-supreme/
- **Cook Islands at Ngā Taonga:** https://ifacca.org/news/2022/11/19/cook-islands-old-audiovisuals-from-the-1960s-to-be/ ; https://www.ngataonga.org.nz/explore-stories/stories/preservation/cook-islands-archivists-stay-cool-in-crisis/
  - This is NZ-held and NZ-relevant, which makes it a natural conversation for an NZ app.
  - **The access decision belongs to the Cook Islands National Archives.** Ask them, not Ngā Taonga alone.
- **Digital Pasifik Viking:** https://digitalpasifik.org/blog/viking-sevenseas-exploring-music-from-the-pacific ; example item https://digitalpasifik.org/items/800351 ("Copyright status: Unknown").

### 3.16 Torres Strait (exclude) [verified / search-only]
- **NFSA clip:**
  - Page: https://aso.gov.au/titles/music/1898-torres-strait-recordings/clip1/ ; the MP3 on cloudfront returns 206 audio/mpeg.
  - Terms: "you must not publish them elsewhere or redistribute clips in any way".
  - The curator's note stresses that "Torres Strait Islander cultural leaders be allowed to re-contextualise" the collection.
- **IA re-upload:** https://archive.org/details/maino-of-yam-yamaz-sibarud (no licence).
- **Haddon cylinders (BL C80, known):** the True Echoes project worked "with permission from the Council of Elders on Mer and Torres Strait Regional Authority" (https://journal.iasa-web.org/index.php/pubs/article/view/160). That permission is not an open licence.
- **Beckett:**
  - AIATSIS MS 5363 finding aid (https://aiatsis.gov.au/sites/default/files/catalogue_resources/MS%205363%20Papers%20of%20Jeremy%20Beckett.pdf) warns that some material may be distressing.
  - CREM record https://archives.crem-cnrs.fr/archives/collections/CNRSMH_E_1976_017_008/ is "Access type metadata", "Restreint (enregistrement édité)".
- **Gab Titui:** https://www.gabtitui.gov.au/ (info@gabtitui.gov.au). **TSRA:** https://www.tsra.gov.au/ (no keeping place online).
- **Protocol:** ATSILIRN Protocol 12 (https://nsla.org.au/?p=935): no secret, sacred or sensitive material online.
- **Recommendation:** exclude all Torres Strait material unless a community body such as the Mer Council of Elders, TSRA or Gab Titui explicitly agrees.

### 3.17 Tonga and Samoa (nothing online beyond known AMPS) [search-only]
- **AMPS Tonga:**
  - TBC ZCO/A3Z tapes from 1961, AMPS 2022/101: https://archives.library.auckland.ac.nz/repositories/6/top_containers/17504
  - Palace Office recordings: https://archives.library.auckland.ac.nz/archival_objects/tupou_posesi_fanua_recordings_for_the_palace_offic
- **AMPS Samoa:**
  - "Songs of the Mau" (2AP 78s, 1936–62): https://archives.library.auckland.ac.nz/archival_objects/songs_of_the_mau
  - Radio Samoa recordings: https://archives.library.auckland.ac.nz/resources/radio_samoa_recordings
  - Moyle 1966–69.
- **Ownership caution:** Moyle's paper on Pacific radio archives (https://ses.library.usyd.edu.au/handle/2123/1512) records ownership disputes over the station tapes. Any AMPS outreach for these should also involve TBC and Radio Samoa.
- **IA false positives:** `loc-jukebox-188903-tonga` is a 1921 Philippine piece, and `rosettaproject_toi_*` is the Zambian Tonga language. Samoan-titled 78s are Western or Hawaiian novelty records. Neither is usable.

---

## 4. Internet Archive
No new relevant collection ids. Queries were limited to audio dated 1890–1970:
- Tibetan; Bangladeshi singers and genres; AIR folk.
- Chinese minority names in Chinese and English; Macau; patuá; 南音 (naamyam).
- Tonga, Samoa, Cook Islands, Solomon, Torres Strait, Lao, Timor, Brunei.

The only on-topic hits were `lp_tibetan-folk-and-minstrel-music_various` (commercial), `maino-of-yam-yamaz-sibarud` (exclude), and commercial Cook Islands LPs in `unlockedrecordings` with no licence.

## 5. Blocked or unreachable hosts
- **Bot checks / challenges:**
  - av.mandala.library.virginia.edu and mandala.library.virginia.edu (AWS WAF)
  - eap.bl.uk (202 challenge)
- **Login walls (not attempted):** DA-Arts playback; Columbia DLC.
- **TLS errors:** banglaacademy.gov.bd, shilpakala.gov.bd, nanl.gov.bd, www.betar.gov.bd.
- **Proxy 502 / refused:**
  - South Asia: www.banglaacademy.org.bd, archives.prasarbharati.org, waveott.in
  - Korea: sparchive.dongguk.edu, www.kdisc.or.kr
  - Japan: db.lib.geidai.ac.jp
  - Other: culture360.asef.org
- **Connection reset / no connection:**
  - Macau: memoryofmacao.mo, macaomemory.mo; archives.gov.mo search pages (intermittent)
  - Korea: www.sparchive.co.kr, sparchive.or.kr, www.koreanrecord.or.kr
  - China: www.ihchina.cn, www.culturedc.cn
  - Samoa: rlsmuseum.org
- **Timeouts and errors:** www.newsonair.gov.in (timeout); arquivos.rtp.pt (404 on every path); nit.com.au (403); mesc.gov.ws (500); guides2.nihu.jp (maintenance).
- **Guessed hostnames that did not resolve** (possibly just wrong names): museums.gov.bn, archives.gov.bn, arkib.gov.bn, sinma.gov.sb, museum.gov.sb, palace.gov.to, culture.gov.ck, library.gov.ck, cookislandslibrary.org, bnl.gov.la, arquivonacional.gov.tl, ascc.as.

## 6. Outreach shortlist (public contact addresses)
| Priority | Archive | Contact | Ask |
|---|---|---|---|
| 1 | KURRA, Kyoto University | kurra-info@inet.museum.kyoto-u.ac.jp | Non-commercial streaming of the 1940–43 work and dance songs through the IIIF manifests; exclude the Shinto ritual songs |
| 2 | DA-Arts / ARKO Arts Archive | +82-2-760-4750 (no email published) | Non-commercial streaming of the 274 Korean 78s |
| 3 | UW Ethnomusicology Archives | ethnoarc@uw.edu | Snyder 1964 lhamo and Goldstein folk-song tapes; also ask about community consultation |
| 4 | CREM-CNRS | crem.lesc[at]cnrs.fr | Lemoine Laos 1964–69 (InC-NC items); this can go in the same request as the first-pass CREM asks |
| 5 | TIPA | tibetanarts@tibet.net | Early-1960s recordings, including the Pema Thang 45 |
| 6 | Cook Islands National Archives / Ministry of Cultural Development | mocd.staff@cookislands.gov.ck (search-only) | 1960s Cook Islands AV collection held at Ngā Taonga |
| 7 | LTWA AV Archive | av_archive@tibetanlibrary.org | Whether any pre-1971 secular song or gramophone recordings exist |

# Archive sweep: East Asia and Inner Asia

Region: China (national, provincial, minority peoples), Tibet (worldwide), Mongolia, Hong Kong, Macau, Taiwan, South Korea, North Korea, Japan beyond NDL (Okinawa/Ryukyu, Ainu, universities).

Date: 2026-11-02. Status labels: **verified** (I loaded the page or API with curl), **search-only** (seen only in search results), **blocked** (the sandbox could not reach the host).

## Limits of this sweep (read first)

- **I hit the web-search budget partway through.** The shared limit is 200 searches per turn across all agents. After that I could only check hosts I already knew, using curl. These planned searches never ran: Dongguk University's Korean record archive URL, the KOGL licence type on Korean public sound files, Dong, Zhuang, Naxi, Yi, Miao/Hmong and Inner Mongolian projects in detail, Macau's own archives, NHK min'yō, and Tokyo University of the Arts / Tobunken. To finish these, send a follow-up message or raise `CLAUDE_CODE_MAX_WEB_SEARCHES_PER_SESSION`.
- **WebFetch could not resolve most hosts in the region:** archive.gugak.go.kr, archives.ihp.sinica.edu.tw, ainugo.nam.go.jp, shimuchi.lib.u-ryukyu.ac.jp, sls.hkpl.gov.hk, old.thlib.org. I used curl through the proxy instead, and most of those answered (results below). To use WebFetch on them, the session's network settings would need to allow them.
- **Hosts that failed completely through the proxy:** memory.culture.tw (502), tmi.ncfta.gov.tw (502), www.hongkongmemory.hk (502), www.memoryofmacao.mo (502), www.zhongguoyinyue.com (502), audio-video.thlib.org (502), dl.lib.ntu.edu.tw (TLS refused).
- **Hosts that answered only some of the time:** archive.gugak.go.kr, www.ctmsa-cnaa.com and data.go.kr often reset the connection.
- **Bot checks I did not try to get past:** openmuseum.tw (and tmi.openmuseum.tw) redirects to a `/validUser` check, and HKPL's digital collection runs a JavaScript/cookie check.

## 1. Ranked table

| # | Archive | Country | Content | Audio access | Rights | Machine access | Verdict | Status |
|---|---|---|---|---|---|---|---|---|
| 1 | Internet Archive `chinesehistsocietysc` (Chinese Historical Society of Southern California, via California Revealed) | China / Chinese diaspora (Cantonese) | 21 items. About 9 are Cantonese opera/song 78s (e.g. 入仙賀寿 1902, 困南陽, 唐明皇長恨, 金葉菊). The rest are US 78s from 1902–1923. | Direct IA MP3/OGG, no login | Per item: "Public Domain. No restrictions on use." + CC PDM 1.0. One item (calachss_000001) is "copyright status unknown". | IA advancedsearch / metadata API | **Good fit** (small) | verified |
| 2 | Gongu Madang 공유마당 (Korea Copyright Commission) | South Korea | 139 hits for 유성기 (shellac); 20 are music. These include KCC-digitised 1951 Columbia (Japan) SP "Japanese Melodies Vol.2" tracks with Arirang (e.g. "The Song of Arirang", wrtSn 13159011). There is also a 만료저작물 (expired-copyright) filter: 7 music hits for 민요 (folk song), 3 for 판소리 (pansori). | Direct MP3 through `/gongu/wrt/cmmn/wrtFileMediaPlay.do?fileTp=audio/mp3&filePath=…`, no login | Per item. The SP items say "CC BY 라이선스로 제공" (offered under CC BY). Most other CC BY folk items are modern. | HTML only (search form, POST player) | **Good fit** for the few pre-1970 CC BY / 만료 items. Low volume, check each item. | verified |
| 3 | 臺灣音聲100年 Taiwan Sound 100 Years (National Museum of Taiwan History, audio.nmth.gov.tw) | Taiwan | Taiwanese 78s and later records from the 1890s on: Hokkien songs, 歌仔戲 (Taiwanese opera), 南管/北管, comic dialogues, Japanese-era songs. Example: 安平小調, Columbia T1165. Size not confirmed: NMTH holds about 1,000 Taiwanese-market 78s. | Free streaming player. The file URL comes from a WordPress AJAX call with a nonce, so there is no plain public MP3 link. | Site footer: "國立臺灣歷史博物館著作權所有 … All Rights Reserved" | WordPress HTML. Some WP REST endpoints exist; I found no audio route. | **Ask permission** (NMTH, through the nmth.gov.tw / tcmb.culture.tw contact forms) | verified |
| 4 | Academia Sinica, Institute of History and Philology Archives (archives.ihp.sinica.edu.tw) | Taiwan / China | 460 shellac records (opera, folk stories, dialects; Victor, Gramophone, Columbia/Commercial Press, Pathé, Beka…). Also 716 aluminium discs (1930s dialect field surveys by Chao Yuen-ren, Li Fang-kuei and Lo Chang-pei, including folksongs). | Not online, apart from one sample clip | Not stated | HTML catalogue only | **Ask permission**: archives@ihp.sinica.edu.tw, tel. +886-2-27829555 ext. 280 | verified (curl) |
| 5 | National Gugak Center archive 국립국악원 아카이브 (archive.gugak.go.kr) | South Korea (and North Korea) | About 380,000 items: SP-record transfers (a 2023 exhibition alone used 61 tracks from ~30 discs), field recordings, ~54,000 North Korean audio/video items. | Some streaming online. The rest is on-site, and North Korean material is on-site by reservation only. | Not confirmed. Korean public bodies use KOGL (공공누리) types 1–4, and the type is set per item. | Portal HTML; there may be a data.go.kr dataset (15121519), not verified | **Ask permission**: tel. +82-2-580-3375, fax +82-2-580-3079 (both shown on the portal) | partly verified (portal answered, then reset connections) |
| 6 | 世界的记忆·中国传统音乐录音档案 Chinese Traditional Music Sound Archives (Chinese Academy of Arts, Music Research Institute; www.ctmsa-cnaa.com) | China (50+ ethnic groups) | UNESCO Memory of the World (1997): 1950s-onward field recordings from nearly every province (wire, tape, discs). Platform has been in trial operation since 2022. | Vue app with login/registration. Could not confirm streaming without login. | "Copyright © 2022 中国艺术研究院 All Rights Reserved" | None public (SPA) | **Ask permission** (low odds; contact through the China Academy of Arts) | partly verified (landing page only, then resets) |
| 7 | 國家文化記憶庫 Taiwan Cultural Memory Bank (tcmb.culture.tw) | Taiwan | Aggregates central and local government plus community materials, including 口傳歌謠 (oral songs). About 49% are CC non-commercial or freer. | Not confirmed for audio | Per-item CC licence labels (CC0/PDM/BY/BY-NC) | Angular app backed by searchapi.culture.tw (undocumented); data.gov.tw datasets list image/content licence fields | **Ask permission / verify**: worth a targeted look for pre-1970 audio with CC BY/PDM | partly verified (SPA loads; memory.culture.tw blocked) |
| 8 | 1967 民歌採集運動 Folk Song Collection Movement tapes (Shih Wei-liang & Hsu Tsang-houei; NTNU Music Digital Archive Center; originals now at Taiwan's National Archives Administration) | Taiwan (Hakka, Hoklo, Indigenous: Atayal, Amis, Paiwan…) | 56 reels; about 260 songs published since 2019 with Council of Indigenous Peoples support | Not online as far as I found (published as CDs/books) | Indigenous songs may be registered under Taiwan's Indigenous Traditional Intellectual Creations Protection Act. Community consent is needed. | n/a | **Ask permission**, only with Indigenous community protocols | search-only |
| 9 | UW Ethnomusicology Archives, Tibetan holdings (Jeanette Snyder 1964 Kalimpong/Darjeeling; Melvyn Goldstein; Lawrence Epstein) | Tibet (diaspora) | Reel tapes digitised to WAV (Snyder 1964 fits the 1970 cutoff) | On-site / UW-only streaming | Not stated | Archives West finding aids | **Ask permission** (UW Ethnomusicology Archives, tel. +1-206-543-0974) | search-only |
| 10 | Dongguk University Korean Record Archive Research Center 한국음반아카이브연구소 | South Korea | 1907–1945 SP records: 13,000+ titles catalogued, ~4,000 with audio (2011 figures). Pansori, minyo, japga, popular songs. | A 2011 report describes title search with playback. Current URL not found. | Unknown; the 2011 report says rights for in-copyright items were unresolved | Unknown | **Ask permission**, after finding the current URL | search-only |
| 11 | Hong Kong Memory 香港記憶 (hkmemory.hk) | Hong Kong | ICH audio, e.g. nanyin; Dou Wun recordings (1975, after the cutoff) | Streaming inside a JS/MMIS iframe | Terms: "Video and audio records found on Hong Kong Memory are copyrighted materials… not allowed to make copy or download" | None | **Not usable** (AV copyrighted, and the key nanyin material is 1975) | verified |
| 12 | HKPL Digital Collection (ex-MMIS, sls.hkpl.gov.hk) | Hong Kong | 9,000+ hours of digitised AV, including Cantonese opera | JS/cookie check; mostly in-library or licensed | Library terms | None | **Not usable** (bot check, licensed / on-site) | blocked |
| 13 | CUHK Chinese Music Archive 中國音樂資料館 | Hong Kong | 78s, reel tapes, cassettes; Cantonese opera and nanyin | On-site; finding aids say "restricted" | Restricted | ArchivesSpace finding aids | **Not usable** online (possible later outreach) | search-only |
| 14 | HKMU Cantonese Opera Repository | Hong Kong | Scripts and English translations, no audio | n/a | Partly request-only | n/a | **Not usable** (no audio; host returns 403) | verified (403) |
| 15 | National Ainu Museum Ainu Language Archive (ainugo.nam.go.jp) | Japan (Ainu) | Recordings made from 1976 on (sakoro/yukar, songs, prayers, conversations) | YouTube embeds; login for some functions | Prayers and ritual items are culturally sensitive | HTML | **Not usable** (after 1970; YouTube; sacred content) | verified |
| 16 | Piłsudski Ainu wax cylinders (1902–03, Sakhalin/Hokkaido; reproduced by Hokkaido Univ. team) | Japan / Russia (Ainu) | 65 cylinders | Not online (published as book/CD) | Sacred/ceremonial content likely | n/a | **Not usable** (not online; would need Ainu consultation) | search-only |
| 17 | Univ. of the Ryukyus, Ryukyu/Okinawa Digital Special Collections (shimuchi) | Japan (Okinawa) | Iha Fuyu, Miyara and other collections: documents and kunkunshi scores | Images only, no audio | Per item | Japan Search / NDL linkage | **Not usable** (no audio) | verified |
| 18 | Okinawa Prefectural Library historic SP service | Japan (Okinawa) | Delivered through NDL Rekion. Includes 1930s–50s Ryukyuan and Yaeyama min'yō. | Through Rekion | Through NDL | Rekion | Already known (NDL Rekion) | search-only |
| 19 | Kyoto City Univ. of Arts, Research Centre for Japanese Traditional Music (rijtm.kcua.ac.jp) | Japan | SP-record holdings used in seminars | No online audio found | n/a | n/a | **Not usable** online (possible outreach) | search-only |
| 20 | Minpaku (National Museum of Ethnology) sound-materials catalogue database | Japan | Field and commercial recordings; Bunkachō 民謡緊急調査 (emergency folk-song survey) tapes | On-site; the folk-song survey is restricted because of sensitive content | Restricted | Catalogue DB | **Not usable** | search-only |
| 21 | Columbia Univ. DLC ("Japanese folk music", "Anthology of Korean traditional music") | Japan / Korea | Commercial LP transfers | Requires Columbia login | US copyright notice | n/a | **Not usable** (login) | verified |
| 22 | Mongol Music Archive (mongolmusicarchive.com, D. Staszewski) | Mongolia | Herder music, 2010s | Vimeo | Not open | n/a | **Not usable** (after 1970; Vimeo) | search-only |
| 23 | Mongolian National Broadcaster radio "golden fund" (Алтан фонд) | Mongolia | 3,000+ folk songs on tape (urtyn duu, bogino duu) | Not online | Broadcaster | n/a | **Not usable** online (possible outreach) | search-only |
| 24 | THL Tibetan folk music project (John Flower, "Preserving Living Traditions") | Tibet | Folk genres: work songs, wedding songs, Gesar | Host audio-video.thlib.org blocked (502) | Search snippet says "unrestricted use" | n/a | **Not usable** as it stands: recordings are almost certainly after 1970. Verify the dates if the host is allowed. | blocked / search-only |
| 25 | Tibetan Endangered Music Project (G. Roche) / Tibetan Music Preservation crowdfund | Tibet | 2007+ recordings | Unclear | Unclear | n/a | **Not usable** (after 1970) | search-only |
| 26 | China Record Corp. 中国历史唱片保护工程 (historic-records protection project; Pathé/EMI Shanghai masters) | China | Most pre-1949 Pathé/Baak Doi masters | Commercial; no open streaming found | Corporate copyright | n/a | **Not usable** | search-only |
| 27 | 中国音网 (Huayun / Central Conservatory / NLC) | China | Recordings from 1899 on, with a folk-song database | Subscription for institutions | Commercial | n/a | **Not usable** (subscription; host blocked) | blocked |
| 28 | National Library of China, oral-tradition project for 28 smaller ethnic groups (e.g. Yugur) | China | Recent recordings | Not online | n/a | n/a | **Not usable** (modern, not online) | search-only |
| 29 | Xinjiang Arts University, Wan Tongshu 1950s wire recordings (48 wires, Uyghur) | China (Uyghur) | 1950s Uyghur music | Digitised for teaching, not online | n/a | n/a | **Not usable** online | search-only |
| 30 | Koryo Tours "Yanggak Island Discs" | North Korea | DPRK vinyl, mostly state pop | SoundCloud only | Not open | n/a | **Not usable** | search-only |
| 31 | IA `uclaethnomusicologyarchive` (UCLA Ethnomusicology Archive via California Revealed) | (US-held) China / Japan | The East Asian items are 1981+ (Hua Wenyi Kunqu, taiko) and `stream_only` | IA stream-only | Copyrighted, used by permission | IA API | **Not usable** for this region (after 1970, stream-only) | verified |

## 2. Country checklist

| Country / area | Result |
|---|---|
| **China – national** | CTMSA (Chinese Academy of Arts MoW sound archive) is online in trial mode but behind a login and All Rights Reserved: ask permission, low odds. China Record Corp. and 中国音网 are commercial. Nothing openly licensed found. |
| China – Uyghur (Xinjiang) | Nothing online and open. The Wan Tongshu 1950s wires are offline; CREM holdings are known and some are restricted. |
| China – Inner Mongolia / Mongol | Nothing specific found (searches cut short by the budget). |
| China – Yi, Miao/Hmong, Dong, Zhuang, Naxi | Nothing online found. Only modern MIR/ECura-style datasets turned up. **Not searched in depth because the budget ran out.** |
| China – Tibet (TAR and Tibetan areas) | Nothing open from inside China. |
| **Tibet – worldwide** | The pre-1970 material is at UW (Snyder 1964; ask permission), CREM (Crossley-Holland 1961; known, mostly restricted) and Penn/Adam Matthew (licensed). THL is blocked and its recordings are probably after 1970. TEMP is modern. The Tibetan Oral History Project (LoC) is interviews, under LoC (already known). |
| **Mongolia** | Nothing usable online. The MNB radio golden fund is offline; Mongol Music Archive is modern; CREM Hamayon 1967–70 is known and restricted. |
| **Hong Kong** | Hong Kong Memory (AV copyrighted), HKPL (bot check / on-site), CUHK CMA (restricted), HKMU (no audio). **New small good fit:** Cantonese opera 78s in IA `chinesehistsocietysc` (US-held). |
| **Macau** | Nothing found online (Macao Memory host blocked; searches cut short). |
| **Taiwan** | Taiwan Sound 100 Years (streamable, All Rights Reserved): ask. IHP Academia Sinica discs: ask. Taiwan Cultural Memory Bank (per-item CC): verify. 1967 folk-song tapes: ask, with Indigenous protocols. NTU digital library blocked (TLS). OpenMuseum / Taiwan Music Institute behind a bot check. |
| **South Korea** | Gongu (CC BY, direct MP3): small good fit. National Gugak Center: ask. Dongguk SP archive: URL not found, ask. MBC 한국민요대전 is 1989–96 and commercial. NLK has no SP audio found. |
| **North Korea** | Nothing online from the DPRK. The largest holding (National Gugak Center, Seoul) is on-site only. Koryo Tours SoundCloud only. |
| **Japan (beyond NDL)** | Okinawa (served through Rekion, already known); Ryukyu Univ. (images only); Ainu Language Archive (after 1970, YouTube, sacred); Piłsudski cylinders (offline); Kyoto RCJTM and Minpaku (no online audio / restricted); Columbia DLC (login). Nothing new and usable. |

## 3. Per-archive notes

### Good fits

**IA `chinesehistsocietysc`: Chinese Historical Society of Southern California** (verified)
- Collection page: https://archive.org/details/chinesehistsocietysc. Uploaded by team@californiarevealed.org, 2023.
- 21 audio items. The Chinese ones are calachss_000002 困南陽, _000003 唐明皇長恨, _000004 猩猩女追夫, _000005 名士風流, _000012 入仙賀寿 (1902), _000019 懷人空洒相思淚, _000020 惜分飛 and _000021 金葉菊. Most are undated, but the label families suggest pre-1930.
- Rights: every item except calachss_000001 is `licenseurl=http://creativecommons.org/publicdomain/mark/1.0/` with "Public Domain. No restrictions on use."
- Machine access: `https://archive.org/advancedsearch.php?q=collection:chinesehistsocietysc&fl[]=identifier,title,date,licenseurl&output=json`.
- This is a new sub-collection inside IA, not part of Great 78.

**Gongu Madang 공유마당 (Korea Copyright Commission)** (verified)
- Search: https://gongu.copyright.or.kr/gongu/search/search/list.do?menuNo=200070&kwd=유성기. 139 results; music = 20; licence facets CC-BY 21, 공공누리 118.
- Example item: https://gongu.copyright.or.kr/gongu/wrt/wrt/view.do?wrtSn=13159011&menuNo=200020. Description: "컬럼비아 레코드(일본) 1951년에 제작한 Japanese Melodies Vol.2 … 아리랑…" (a 1951 Columbia (Japan) shellac, Japanese Melodies Vol.2, including Arirang). "이용조건: 한국저작권위원회의 … CC BY 라이선스로 제공됩니다" (offered by the Korea Copyright Commission under CC BY).
- Player: POST `/gongu/wrt/wrtCl/musicPlayerCont.do` (wrtSn) returns `<source src="/gongu/wrt/cmmn/wrtFileMediaPlay.do?fileTp=audio/mp3&filePath=…">`. That is a direct MP3 with no login.
- Other counts: 민요 1,040 results (music 179; CC-BY 151; 만료저작물 7); 판소리 music 37 (만료 3). Many CC BY items are modern donated recordings, so each needs a date check.
- Caveat: volume is small and the content is mixed. The 1950s KCC SP transfers are mostly light/popular arrangements of folk tunes.
- Contact: KCC copyright centre, tel. 1800-5455.

### Ask permission

**Taiwan Sound 100 Years 臺灣音聲100年: National Museum of Taiwan History** (verified)
- Site: https://audio.nmth.gov.tw/ and library https://audio.nmth.gov.tw/audio-library/ (browse by decade from 1890–1899). Example item: https://audio.nmth.gov.tw/audio/安平小調/ ("留聲機唱片 發行編號 古倫美亞 (利家-紅)T1165", i.e. a 78 rpm disc, Columbia (Lijia red label) T1165).
- Plays freely in the browser through a nonce-protected WordPress AJAX player. There is no public file URL.
- Footer: "國立臺灣歷史博物館著作權所有 © 2014 … All Rights Reserved."
- NMTH runs an online licensing service: 908,316 digital items licensed by the end of 2020. A free non-commercial streaming licence for 78s from before about 1945 is a plausible request.
- Taiwan's Copyright Act protects recordings 50 years from publication, so pre-1970 commercial 78s are probably out of copyright in Taiwan. NMTH's own digitisation rights still apply. Contact through nmth.gov.tw or https://tcmb.culture.tw/zh-tw/contactus.

**Academia Sinica IHP Archives** (verified with curl; WebFetch DNS-blocked)
- https://archives.ihp.sinica.edu.tw/en/archives/detail/39 (shellac records, 460) and its sibling pages (716 aluminium discs from the 1930s linguistic field surveys, "including folksongs"; vinyl language courses; 1950–60 lecture tapes).
- Not online. Contact: archives@ihp.sinica.edu.tw, +886-2-27829555 ext. 280.
- The aluminium discs (Chao Yuen-ren and others, 1930s) are a unique pre-1949 Chinese dialect and folksong field corpus. This is a strong outreach target.

**National Gugak Center archive** (partly verified)
- https://archive.gugak.go.kr/portal/main loaded once; the root and other pages reset connections. It links to search (`/portal/search/newTotalSearch`), online exhibitions (`/portal/online/searchOnline`) and a donor collection.
- Phone 02-580-3375 and fax 02-580-3079 appear on the page.
- Sources:
  - https://www.koreatimes.co.kr/entertainment/music/20200811/more-than-82000-materials-on-north-korean-music-unveiled (North Korean materials on-site by reservation)
  - https://mediahub.seoul.go.kr/archives/2008238 (2023 SP exhibition, 61 tracks)
  - https://www.mcst.go.kr/site/s_policy/comm/studentNews/studentNewsView.jsp?pSeq=5098 (about 380,000 items)
- Ask for the KOGL type on SP transfers. Type 1 (attribution only) or type 2 (non-commercial) would both allow the app.

**CTMSA, Chinese Academy of Arts** (partly verified)
- https://www.ctmsa-cnaa.com/ landing page: "世界的记忆 中国传统音乐录音档案 … Copyright © 2022中国艺术研究院. All Rights Reserved". The Vue header handles login and registration.
- Background: https://www.ihchina.cn/news_details/24889.html (launched in trial mode on 2022-04-23 as a 公益性 (public-benefit, non-profit) audio platform).
- Contents: recordings from almost every province, 50+ ethnic or cultural groups, 1950s onward.
- Outreach odds are low, but it is the single biggest relevant corpus.

**Taiwan Cultural Memory Bank 國家文化記憶庫** (partly verified)
- https://tcmb.culture.tw/zh-tw (Angular SPA). Licence pages are at /zh-tw/license and /zh-tw/copyright, with per-item CC labels.
- The JS bundle shows the backend at `https://searchapi.culture.tw` (undocumented) and file host `tcmbfile.culture.tw`. memory.culture.tw itself is blocked (502).
- Worth checking by hand or by email for 曲盤 (78 rpm disc) and 歌謠 (song) audio marked CC BY or PDM.

**1967 Folk Song Collection Movement tapes** (search-only)
- https://news.pts.org.tw/article/644392 and https://www.1111.com.tw/news/jobns/152245: 56 reels returned from Bonn in 2013, transferred by NTNU, originals donated to the National Archives Administration in 2023, about 260 songs published.
- Includes Indigenous songs, so Indigenous community consent is required.

**UW Ethnomusicology Archives, Tibetan** (search-only)
- https://archiveswest.orbiscascade.org/ark:80444/xv922698 (Goldstein, Tibet). The Snyder 1964 and Epstein finding aids exist on Archives West.
- https://music.washington.edu/facilities/ethnomusicology-archives

**Dongguk Korean Record Archive** (search-only)
- https://www.khan.co.kr/article/201102061900235/amp (2011: 13,000 titles / 4,000 audio, 1907–1945, searchable with playback). The current URL could not be found before the search budget ran out.

### Not usable (detail where useful)

- **Hong Kong Memory**: https://www.hkmemory.hk/en/terms_of_use.html says "Video and audio records found on Hong Kong Memory are copyrighted materials, and they are not allowed to make copy or download." The Dou Wun nanyin recordings (Bell Yung) date from 1975.
- **HKPL Digital Collection**: https://mmis.hkpl.gov.hk/ redirects to https://sls.hkpl.gov.hk/digital-collection/, which runs a JS/cookie check (not attempted).
- **CUHK Chinese Music Archive**: e.g. https://archives.lib.cuhk.edu.hk/repositories/5/archival_objects/165407 (restricted).
- **HKMU Cantonese Opera Repository**: https://repository.lib.hkmu.edu.hk/copera returns 403; it holds scripts only.
- **Ainu Language Archive**: https://ainugo.nam.go.jp/pages/about_media.html (recordings since 1976; published in 2016 as 74 Saru-dialect items, ~30 h). Playback is through YouTube (user_guide). It includes prayers.
- **Ryukyu Univ. shimuchi**: https://shimuchi.lib.u-ryukyu.ac.jp/ (documents and scores only).
- **Okinawa Prefectural Library SPs**: https://ryukyushimpo.jp/living-style/entry-1677924.html. These are HiRAC/NDL Rekion files (already known; about 6,000 public online).
- **Minpaku**: https://ndlsearch.ndl.go.jp/rnavi/avmaterials/post_962. The Bunkachō folk-song emergency-survey tapes are on-site only because of sensitive content.
- **Columbia DLC**: https://dlc.library.columbia.edu/catalog/cul:95x69p8g35 and cul:msbcc2ft2x ("… to access this content", login required).
- **Mongolia**: https://mongoliajol.info/index.php/MJAC/article/view/4436/3968 (MNB radio fund, 3,000+ songs, offline). The Mongol Music Archive is 2010s on Vimeo.
- **Tibet**: CREM Crossley-Holland 1961, e.g. https://archives.crem-cnrs.fr/archives/collections/CNRSMH_E_1973_004_029/ (media restricted; CREM is already known). The Penn Crossley-Holland tapes are behind the Adam Matthew licence.
- **China Record Corp.**: https://2015.iasa-web.org/en/node/97.html (protection project; no open access).
- **CCMusic / ECura**: https://zenodo.org/records/5654924 (CC BY 4.0, but a modern MIR instrument and pop dataset, not traditional field recordings).

### Already-known collections: what I noticed (no re-research)

- **Internet Archive Great 78 / georgeblood**: about 25 Korean-language and about 77 subject:"japanese" 78s. Already known, nothing new.
- **IA `hessel-digital-library`**: holds 45 East Asian-titled items, but they are bit-exact mirrors of LoC National Jukebox files (e.g. loc-jukebox-312598-chinese-recording, 1903, PDM). Use LoC as the source instead.
- **CREM-CNRS**: holds many East Asian collections (Korean 한국민요대전 (Korean Folk Song Anthology) sets marked restricted; Hamayon Mongolia 1967–70 restricted; Uyghur muqam sets marked "full"; Tibetan 1960–61). Already known.
- **NDL Rekion**: also serves Okinawa Prefectural Library's SPs and colonial-era Korean and Taiwanese records, and a 1948 Ainu upopo record (https://mina.ndl.go.jp/en/books/R100000039-I8266960).

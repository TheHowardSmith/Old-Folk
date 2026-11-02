# Reuse and streaming terms of four archives: findings for Old Folk

Checked on 2026-11-02, mostly with direct HTTP fetches of the archives' own pages and some JSON/XML endpoints. Quotes are copied from the fetched pages. Translations are mine. Items marked **[UNVERIFIED]** could not be confirmed from a primary source. Items marked **[BLOCKED]** were behind a bot check, a maintenance page or the sandbox network policy. None of those was bypassed.

This is a reading of the published terms, not legal advice. Note that "public domain / protection expired" in the source country does not mean public domain in every country where the app is used.

---

## Verdicts

### 1. CREM-CNRS (Archives sonores du CNRS – Musée de l'Homme): **ASK PERMISSION. Do not stream directly.**

The site-wide legal notice reserves all use of the archives and forbids copying audio files unless explicitly allowed: "TOUTE UTILISATION DES ARCHIVES EST RESERVEE" / "ALL RIGHTS RESERVED … AUDIO FILE COPYING FORBIDDEN IF NOT EXPLICITLY ALLOWED". Every page footer carries an ethical restriction: use is "réservé dans le respect du patrimoine culturel des communautés d'origine".

"Public" (`consultation libre`) only controls who may listen on the site. It is not a licence. The separate per-collection rights field ("Droit d'utilisation", exported as `dc:rights` licence) only takes restrictive values in every record sampled: *Copie interdite*, *Restreint*, *Restreint (enregistrement édité)*, *Restreint (édition hors commerce)*, *Publication interdite*, *Communication libre*, *None*.

No Creative Commons licence was found:
- None in about 2,000 OAI-PMH records sampled across the catalogue.
- All 21,446 CREM records in Europeana carry `rightsstatements.org/vocab/InC/1.0/` ("In Copyright").
- `robots.txt` disallows `*.mp3`.

The site does offer an iframe player for public items ("Partager le lecteur audio"). That is the closest thing to sanctioned off-site playback, but nothing says it covers a third-party app.

Who to write to:
- Esther Magnière, Responsable des archives du CREM: esther.magniere (at) cnrs.fr. The Help page names her as the contact for access.
- crem.lesc (at) cnrs.fr, the director Victor Stoichita's address and the general CREM address.

Ask specifically about pre-1970 items in `consultation libre` collections, whether the embed player may be used in an app, and whether any communities or collectors have to agree. Expect a no for anything whose collection is "Copie interdite" or "Publication interdite".

### 2. NDL Rekion (歴史的音源): **The content is free to reuse. ASK before streaming from NDL's servers; otherwise link out.**

NDL's general reuse page says plainly that content whose access status is 「インターネット公開（保護期間満了）」 "may be reused freely" and that no application to NDL is needed. NDL confirms that the copyright and neighbouring-right terms of these recordings have expired. About 6,137 recordings are in this category, including about 440 tagged 民謡/俚謡 (folk songs).

However:
- The NDL Digital Collections terms send Rekion users to Rekion's own site policy. That policy says NDL does **not** provide audio as mp3 or other files "for reproduction or personal use".
- Playback is HLS only. There is no download button.
- No official audio API or bulk audio route was found. The official APIs (NDL Search: OpenSearch/SRU/OAI-PMH) give **metadata only**. Non-commercial, non-revenue use of that metadata needs no application, but NDL asks continuous API users to register.
- Linking to recordings is explicitly free.

Nothing in the terms forbids reuse. Nothing grants permission to hotlink NDL's HLS stream from a third-party app either. Either deep-link each track to `https://rekion.dl.ndl.go.jp/pid/{n}`, or first ask NDL (Kansai-kan, Digital Library Division) whether in-app streaming from their endpoint is acceptable. NDL also asks reusers to respect moral rights, privacy and human rights even for public-domain items, and to credit NDL as the source.

### 3. Archive of Indian Music (AIM): **LEAVE OUT, unless AIM grants permission and supplies the files.**

Every page footer says: "All tracks on this website are available only on audio streaming, unless otherwise mentioned. Download and/or commercial misuse of the tracks by the users is illegal and liable to penalty." The site also says "Copyright © … Archive of Indian Music. All rights reserved."

There is no terms-of-use page and no licensing or permission-request policy. Audio on the site is embedded SoundCloud players. The AIM SoundCloud account ("Archive Of Indian Music", 1,677 tracks) marks the tracks checked `license: all-rights-reserved`, `downloadable: false`, `policy: MONETIZE`, `embeddable_by: all`. No AIM YouTube channel was found **[UNVERIFIED that none exists]**.

Re-hosting would mean ripping from SoundCloud, which SoundCloud's terms forbid (see the platform section below). Embedding the SoundCloud widget in a genre-dedicated app needs SoundCloud's written consent. The only clean route is written permission from AIM plus files supplied by AIM. Contacts from the site: info@archiveofindianmusic.org, support@archiveofindianmusic.org, and founder Vikram Sampath ([personal address on the AIM site]).

### 4. Irama Nusantara: **LEAVE OUT.**

Listening needs an account. The site's prompt says: "Daftar / masuk dulu ya! Untuk putar lagu, baca artikel, dan nikmati fitur-fitur lainnya." ("Sign up / sign in first! To play songs, read articles and enjoy other features.")

The only published terms are a disclaimer:
- It presents the archive as non-commercial preservation under Indonesian Laws No. 43/2009 and No. 13/2018.
- It works on takedown-on-request ("If there are copyright … holders who object … we can immediately delete the work").
- It grants no reuse or streaming rights to anyone.

The sign-up form refers to "Persyaratan dan Ketentuan Penggunaan" ("Terms and Conditions"), but the link is `href='#'` and no standalone terms document was found. The collection is Indonesian **popular** music from the 1920s to the 2000s, much of it likely still in copyright. Irama itself does not claim the recordings are public domain. Streaming off-site would mean going around the login wall. Contact if needed: info@iramanusantara.org.

### SoundCloud and YouTube (for archives that publish only there)

**Neither allows downloading, ripping or recording streams for re-hosting** without permission:
- SoundCloud: "You must not copy, rip or capture …". Its API terms forbid caching, offline playback and "stream ripping".
- YouTube: "You are not allowed to: access, reproduce, download … any Content except … as expressly authorized … or with prior written permission". YouTube's API policies forbid audio-only extraction, background play and storing copies.

Embedding is allowed within limits. SoundCloud's widget may **not** be used to build "a content service of which Content from the Platform forms a material part", or a destination dedicated to "a particular genre", without SoundCloud's written consent. Both conditions describe Old Folk.

---

## 1. CREM-CNRS: quoted terms and sources

### 1.1 Legal notices (mentions légales)
URL: https://archives.crem-cnrs.fr/pages/legal_notices. The French and English pages differ in their final clauses, so both are quoted.

French (Accept-Language: fr):
> Directeur de publication — Victor Stoichita (directeur du CREM) — e-mail : crem.lesc (at) cnrs.fr
> Responsable des archives du CREM — Esther Magnière (CREM) — e-mail : esther.magniere (at) cnrs.fr
> Chef de projet (2006-2020) — Joséphine Simonnot — e-mail : josephine.simonnot (at) cnrs.fr
> …
> **TOUTE UTILISATION DES ARCHIVES EST RESERVEE, CONTACTER LE CREM POUR TOUT RENSEIGNEMENT**
> Usage des archives réservé dans le respect du patrimoine culturel des communautés d'origine.

Translation: "Publication director: Victor Stoichita (CREM director). Head of CREM archives: Esther Magnière. … ALL USE OF THE ARCHIVES IS RESERVED; CONTACT CREM FOR ANY INFORMATION. Use of the archives is reserved, with respect for the cultural heritage of the communities of origin."

English version (original wording):
> ALL RIGHTS RESERVED, PLEASE CONTACT CREM FOR ANY REQUEST
> AUDIO FILE COPYING FORBIDDEN IF NOT EXPLICITLY ALLOWED
> COMMERCIAL USING OF THE ARCHIVES FORBIDDEN IF NOT EXPLICITLY ALLOWED
> Usage of the archives in the respect of cultural heritage of the original communities.

The publisher named on the page is "Centre de Recherche en Ethnomusicologie (CREM - LESC UMR 7186 CNRS Université Paris Ouest), MSH Mondes - 21, allée de l'Université, 92023 Nanterre cedex". Hosting is by Huma-Num. The software is Telemeta, under the AGPL v3; that licence covers the code, not the recordings.

No separate "CGU" or "conditions d'utilisation" page was found. The legal notices and the help page are the only terms pages linked from the site.

### 1.2 Home page: open versus restricted access
URL: https://archives.crem-cnrs.fr/

French:
> Actuellement, plus de 32 000 enregistrements sont en libre accès. Plus de 24 500 sont consultables avec un code d'accès. Pour l'obtenir écrivez à crem.lesc (at) cnrs.fr en expliquant les motifs de votre demande.

Translation: "At present, more than 32,000 recordings are freely accessible. More than 24,500 can be consulted with an access code. To obtain one, write to crem.lesc (at) cnrs.fr explaining the reasons for your request."

The English home page gives older figures: "Today, about 27,000 recordings are available for listening on free access. The others are only available with an access code."

Ownership (English home page):
> The deposites made between 1931 and 2009 at the musée de l'Homme are in co-ownership with the Muséum National d'Histoire Naturelle, under the title «Sound Archives of the Musée de l'Homme»; those deposited after that date now depend on the CNRS.

Also on the home page: "Display audio player options are developed by the DIADEMS program and they available with a user account."

### 1.3 Help page: contact for access
URL: https://archives.crem-cnrs.fr/pages/help

French:
> Pour obtenir un accès spécifique à l'ensemble des archives du CREM hébergé sur ce site, merci de contacter : Esther Magnière <esther.magniere (at) cnrs.fr>

Translation: "To obtain specific access to all the CREM archives hosted on this site, please contact Esther Magnière."

English: "In order to get access to some resticted items on this site or get technical assistance, please contact: Esther Magnière <esther.magniere (at) cnrs.fr>"

### 1.4 Item and collection fields
Example item: https://archives.crem-cnrs.fr/archives/items/CNRSMH_E_1955_001_001_016_02/ (Gun/Goun song, Porto-Novo, recorded 1952, issued on 78 rpm in 1954)
- `Type d'accès` ("access type"): **consultation libre** ("free consultation"). A restricted item shows **Consultation restreinte** together with "Vous n'avez pas accès aux médias de cet item. Contactez les administrateurs du site pour obtenir plus de droits." ("You do not have access to this item's media. Contact the site administrators to obtain more rights.") Example: https://archives.crem-cnrs.fr/archives/items/CNRSMH_I_2018_004_008_05/
- The Dublin Core view (https://archives.crem-cnrs.fr/archives/items/CNRSMH_E_1955_001_001_016_02/dc/) shows:
  - `rights license`: **Restreint (enregistrement édité)** ("Restricted (published recording)")
  - `rights accessRights`: **public**

  This item is open to listen to but restricted in its usage rights.
- The parent collection (https://archives.crem-cnrs.fr/archives/collections/CNRSMH_E_1955_001_001/) has, under "Mentions légales": `Droit d'utilisation` ("usage right"): **Restreint (enregistrement édité)**. Its archivist note reads "Edition MH, licence Africavox, vérifier si réédition ?" ("Musée de l'Homme edition, Africavox licence, check whether reissued?"), which shows that third-party label rights are still being tracked.

How Telemeta derives these fields, from the open-source code at github.com/Parisson/Telemeta, `telemeta/models/dublincore.py`:
```
def media_access_rights(media):
    if media.public_access == 'full':     return 'public'
    if media.public_access == 'metadata': return 'restricted'
    return 'private'
...
Element('rights', item.collection.legal_rights, 'license'),
Element('rights', media_access_rights(item.collection), 'accessRights'),
```
So `accessRights` (public, restricted or private) only reflects the `public_access` setting (full, metadata, mixed or none), which decides whether audio plays on the site. The licence comes from the collection's `legal_rights` vocabulary.

The code also has a rolling auto-publication rule (`telemeta/views/core.py`, `get_item_access`): "public access is automaticcaly given when time between recorded year and current year is over the settings value PUBLIC_ACCESS_PERIOD". The default `TELEMETA_PUBLIC_ACCESS_PERIOD = 51` is set in the repo's `app/settings.py`. That rule opens listening on the site. It is not a reuse licence. **[UNVERIFIED]** whether CREM's v1.6.9 deployment uses the same value.

### 1.5 OAI-PMH sample
Endpoint: `https://archives.crem-cnrs.fr/oai/`. Identify returns repositoryName "CREM-CNRS", adminEmail webmaster@parisson.com and completeListSize 84,399. Four `ListRecords` pages of 500 records each were read (offsets 0, 20000, 50000 and 80000). (licence, accessRights) pairs seen:

| dc:rights (licence) | dc:rights (accessRights) | count |
|---|---|---|
| Copie interdite | restricted | 750 |
| Restreint | restricted | 348 |
| Restreint (enregistrement édité) | restricted | 463 |
| Copie interdite | public | 208 |
| Restreint | public | 100 |
| Restreint (enregistrement édité) | public | 26 |
| None | restricted | 56 |
| Restreint / Copie interdite / Restreint (enr. édité) | private | 43 |
| Publication interdite | restricted | 3 |
| Restreint (édition hors commerce) | public / restricted | 2 |
| Communication libre | restricted | 1 |

Translations: Copie interdite = "copying forbidden"; Restreint = "restricted"; enregistrement édité = "published recording"; édition hors commerce = "non-commercial edition"; Publication interdite = "publication forbidden"; Communication libre = "free communication".

**No Creative Commons or other open licence value appeared.** Even *Communication libre* is not a reuse licence, and that one item was not publicly accessible.

### 1.6 Europeana
Europeana Search API (public demo key), query `DATA_PROVIDER:"Center for Research in Ethnomusicology"`: **21,446 records, all with rights `http://rightsstatements.org/vocab/InC/1.0/`** (In Copyright). CREM provided these through Europeana Sounds, 2014–2017.

### 1.7 Embed / "Partager"
Each item page has a "Partager" ("Share") dialog with two fields:
- "Partager le lien" ("share the link")
- "Partager le lecteur audio" ("share the audio player"):
  `<iframe width='361' height='215' … src='http://archives.crem-cnrs.fr/archives/items/<ID>/player/346x130/'></iframe>`

The page player loads `/archives/items/download/<ID>.mp3`. `robots.txt` contains `Disallow: *.mp3$` and `Disallow: */WIDTHxHEIGHT/`. The embed is offered without stated conditions, but the legal notice still reserves all use.

### 1.8 CNRS / MNHN / Musée de l'Homme policies and community consent
- The only ethics statement found on the CREM site is the footer line quoted above. It appears on every page: "Usage des archives réservé dans le respect du patrimoine culturel des communautés d'origine."
- **[BLOCKED]** The LESC/CREM pages (https://lesc-cnrs.fr/fr/laboratoire/lesc-crem, …/pole-documentaire, and the English "research-data-archives-and-library-services") answered HTTP 503 with "Le site du LESC est en cours de maintenance…" ("The LESC site is under maintenance…").
- **[BLOCKED]** The Bulletin de l'AFAS article by Aude Julien Da Cruz Lima (https://journals.openedition.org/afas/4032), linked from the CREM home page, and HAL papers by J. Simonnot (halshs.archives-ouvertes.fr) are behind Anubis bot checks. They were not bypassed.
- **[UNVERIFIED]** A search-engine summary said a CREM corpus notice gives performers, people whose voices were recorded, and their heirs a *droit de retrait* (right to have online files withdrawn), with requests to crem.lesc@cnrs.fr. The notice was not found on the fonds and corpus pages checked, so it is not quoted here.
- No MNHN or CNRS institution-wide reuse policy specific to ethnomusicological recordings was found online.

---

## 2. NDL Rekion: quoted terms and sources

### 2.1 Rekion site policy (利用上の注意)
Page: https://rekion.dl.ndl.go.jp/sitepolicy. Content served from https://rekion.dl.ndl.go.jp/static/ja/sitepolicy.html.

> **著作権について** 歴史的音源ホームページの著作権は、特記されていない限り国立国会図書館に帰属します。なお、歴史的音源に収録している個々の音源の著作権（著作隣接権）は、各音源の著作権者（著作隣接権者）に帰属しています。

Translation: "**Copyright.** Copyright in the Rekion website belongs to the NDL unless otherwise stated. Copyright (neighbouring rights) in the individual recordings belongs to each recording's copyright holder (neighbouring-rights holder)."

> **リンクについて** 歴史的音源ホームページ（ https://rekion.dl.ndl.go.jp/ ）、収載音源へのリンクは自由に張っていただけます。… 個々の音源へリンクを張る際は、ブラウザのURL欄にある「https://rekion.dl.ndl.go.jp/info:ndljp/pid/{数字}」の形のURLをご利用ください。

Translation: "**Links.** You may freely link to the Rekion website and to the recordings it contains. … When linking to an individual recording, use the URL of the form 'https://rekion.dl.ndl.go.jp/info:ndljp/pid/{number}'."

> **収録音源の転載等について** 転載や個人利用のためにmp3等のファイルで提供するサービスは、当館では行っておりません。

Translation: "**Reproduction of the recordings.** The NDL does not offer a service providing the recordings as mp3 or other files, whether for reproduction or for personal use."

> **免責事項** 免責事項については、「国立国会図書館デジタルコレクション 利用上の注意」に準じます。

Translation: "**Disclaimer.** Follows the disclaimer of the NDL Digital Collections terms of use."

### 2.2 Rekion "About" page (歴史的音源について)
Page: https://rekion.dl.ndl.go.jp/ja/aboutRekion (static/ja/aboutRekion.html)

> 3.著作権・公開範囲について 収録音源の著作権（著作隣接権）は、各音源の著作権者（著作隣接権者）に帰属しています。著作権（著作隣接権）の保護期間満了が確認された音源は、インターネットに公開しています。それ以外は配信提供参加館、および国立国会図書館の館内でのみ聴くことができます。

Translation: "3. Copyright and access scope. Copyright (neighbouring rights) in the recordings belongs to each rights holder. Recordings whose copyright (neighbouring-rights) term has been confirmed as expired are published on the internet. Others can only be heard at partner libraries and inside the NDL."

Background: about 50,000 SP-era recordings (c. 1900–1950) were digitised by HiRAC (NHK, JASRAC, Geidankyo, the Japan Traditional Cultures Foundation, VIPO and RIAJ). NDL does not hold the source discs.

The page offers downloadable recording lists (metadata only):
- https://rekion.dl.ndl.go.jp/static/files/rekion_internet_20240726.csv: internet-public recordings, 6,137 rows. Columns: 商品番号 (catalogue no.), タイトル (title), 作詞・作曲・編曲・実演家 (lyricist/composer/arranger/performer), 製作者（レーベル） (label), リリース日 (release date), 管理ID (management ID), URL, ジャンル (genre). Genres include 「統制語：民謡、国民音楽（日本）」 (controlled term: folk song / national music of Japan), 299 rows, and 「俚謡」 (rural folk song), 140 rows.
- https://rekion.dl.ndl.go.jp/static/files/rekion_all.csv: all recordings.

### 2.3 NDL general reuse rule: 「国立国会図書館ウェブサイトからのコンテンツの転載」 ("Reproducing content from the NDL website")
Japanese: https://www.ndl.go.jp/use/reproduction (redirected from /jp/use/reproduction/index.html). English: https://www.ndl.go.jp/en/use/reproduction

> 2.1. 国立国会図書館デジタルコレクションに掲載されているコンテンツについて … 公開範囲が「インターネット公開（保護期間満了）」となっている場合は、ご自由にご利用いただけます。当館への申請は不要です。

Translation: "2.1 Content in the NDL Digital Collections. … Where the access status is 'Internet-public (protection term expired)', you may use it freely. No application to the NDL is necessary."

NDL's own English wording: "Content for which the Use status is インターネット公開（保護期間満了） is in the public domain and may be reused freely. In such cases, you need not contact the National Diet Library."

Table on the same page:
> ログインなしで閲覧可能 ／ インターネット公開（保護期間満了） ／ 著作権保護期間が満了していますので、自由にご利用いただけます。

Translation: "Viewable without login / Internet-public (protection term expired) / The copyright term has expired, so you may use it freely."

> 3. ご注意とお願い　…著作権保護期間が満了したコンテンツにつきましても、著作者の人格的利益や、プライバシーその他の人権等にご配慮のうえご利用ください。… 当館ウェブサイトからコンテンツを転載する場合は、利用した画面が国立国会図書館ウェブサイトから転載したものであることを明示するよう、ご協力をお願いいたします。

NDL's English: "Reusing parties should also consider moral rights, human rights, and privacy issues even when reusing content in the public domain. Reusing parties are solely responsible for problems that arise with third parties. Please credit the NDL as the source when reusing any content, in whole or in part, from NDL websites."

The Rekion front-end code labels this status `"rights":{"message":{"pdm":"インターネット公開（保護期間満了）"}}`, i.e. as a Public Domain Mark (`pdm`) status. The other status is `"permission":"国立国会図書館内/図書館・個人送信限定"` ("NDL premises / library and individual transmission only").

**Interpretation:** The reuse page is written for NDL Digital Collections content in general, and Rekion is part of that platform. The NDL Digital Collections terms, however, say: 「国立国会図書館デジタルコレクション内の資料のうち、歴史的音源のご利用については「歴史的音源 サイトポリシー」をご覧ください。」 ("For use of the Historical Recordings among the NDL Digital Collections materials, see the Rekion site policy.") That policy (2.1 above) adds only "no file service". So the recordings are free to reuse, but NDL supplies no files, and nothing addresses third-party apps streaming from NDL's HLS servers. **[UNVERIFIED]** whether NDL would object to hotlinking. Ask.

### 2.4 NDL Digital Collections terms of use (利用上の注意)
https://dl.ndl.go.jp/sitepolicy (static/ja/sitepolicy.html)
> ※国立国会図書館デジタルコレクション内の資料のうち、歴史的音源のご利用については「歴史的音源　サイトポリシー」をご覧ください。

> 国立国会図書館デジタルコレクションの収録資料やサイト画面について、私的使用のための複製の範囲を超える場合（転載等）は許諾が必要となります。… （1）収録資料の転載について 公開範囲ごとの利用方法については、「国立国会図書館ウェブサイトからのコンテンツの転載」… をご覧ください。いずれの場合も、原則として当館への申請は不要です。

Translation: "Copying of materials or site screens beyond private use (reproduction etc.) requires permission. … (1) Reproduction of materials: for how each access status may be used, see 'Reproducing content from the NDL website' … In all cases, in principle no application to NDL is needed."

The NDL website content terms (https://www.ndl.go.jp/sitepolicy/terms) put NDL-authored content under PDL 1.0, Japan's Public Data License, which is compatible with CC BY. Third-party rights are excluded.

### 2.5 Downloading audio
- Rekion help (https://rekion.dl.ndl.go.jp/help, static/ja/help.html): delivery is 「HLS（HTTP Live Streaming）による配信・再生」 ("streaming and playback via HLS"). The viewer table lists seek, play/pause, mute, volume and speed controls only. **There is no download control.**
- The site policy (2.1) says NDL provides no mp3 or other files.
- `https://dl.ndl.go.jp/api/iiif/<pid>/manifest.json` returns 404 "NG" for a Rekion pid (3576361). No IIIF audio manifest was found.

### 2.6 APIs and bulk data
- Official API list (https://www.ndl.go.jp/use/api): the NDL Search API is the relevant one. It has search APIs (OpenSearch, SRU) and a harvest API: 「ハーベスト用API NDLサーチに格納されたメタデータのダウンロードができます。OAI-PMH で提供しています。」 ("The harvest API lets you download metadata stored in NDL Search, via OAI-PMH.") This is **metadata only**.
- NDL Search API terms (https://ndlsearch.ndl.go.jp/help/api):
  > (2) 個人・非営利団体等の方　データ利用により収益を得ない場合 … 営利目的にあたらないため、利用申請は不要です。

  Translation: "(2) Individuals and non-profits, where no revenue is earned from the data … this is not commercial use, so no application is needed."

  > (3) 継続的にアクセスを行われる方 … 事前の利用申請の要否にかかわらず NDLサーチAPI利用申請フォーム よりご連絡先、ご利用内容等をお知らせいただけますようご協力をお願いします。

  Translation: "(3) Continuous access: whether or not an application is required, please tell us your contact details and usage through the NDL Search API application form."

  > 本APIを利用するサイトやアプリケーションには、NDLサーチのAPIを用いていることを明記してください。

  Translation: "Sites and applications using this API must state that they use the NDL Search API."
- No official audio API or bulk audio dataset was found. The CSV recording lists (2.2) are the only bulk route, and they are metadata.

### 2.7 Who to ask
Rekion contact page (https://rekion.dl.ndl.go.jp/contact, static/ja/contact.html):
> 歴史的音源に関するお問い合わせ先 … 〒619-0287 京都府相楽郡精華町精華台8-1-3 国立国会図書館 関西館　電子図書館課 ／ お問い合わせフォーム（https://www.ndl.go.jp/form/jp/service/contact/index.html）※「「国立国会図書館デジタルコレクション」のシステムに関すること及び収録されているデジタル資料中の不備等に関すること」ラジオボタンを選択してください。

Translation: "Rekion enquiries: National Diet Library Kansai-kan, Digital Library Division, 8-1-3 Seikadai, Seika-cho, Soraku-gun, Kyoto 619-0287. Use the enquiry form and select the radio button 'About the NDL Digital Collections system and defects in digital materials'."

The Rekion front end also links "Inquiries regarding the reuse of content available via the Internet from the NDL Digital Collections" to https://dl.ndl.go.jp/en/contact.html#toiawase2.

---

## 3. Archive of Indian Music: quoted terms and sources

- Home, About and Contact pages (https://archiveofindianmusic.org/, /contents/About%20Us, /contents/Contact). Footer on every page:
  > Copyright © 2012 Archive of Indian Music. All rights reserved.
  > All tracks on this website are available only on audio streaming, unless otherwise mentioned. Download and/or commercial misuse of the tracks by the users is illegal and liable to penalty.
- About page: "the Archive of Indian Music (AIM) has been established as a private and not-for-profit Trust …" Its objectives include "To preserve these vintage recordings and make them available for listening to students, scholars, researchers, musicians and interested public".
- No terms-of-use, licensing or permission-request page exists. The site navigation is About, People, Founders, Board of Trustees, Advisors, Artists, Events, Forums, Contact, Partners, Sign in, Subscribe, Donate, Media. `robots.txt` allows everything.
- Contact page (emails shown through Cloudflare email obfuscation, decoded as a browser would): **[personal address on the AIM site]**, **info@archiveofindianmusic.org**, **support@archiveofindianmusic.org**. Registered office: C502, Adarsh Esplanade, 12th 'A' Main, 25th Cross, HSR Layout Sector 7, Bangalore 560102.
- Where the audio lives: artist pages (e.g. https://archiveofindianmusic.org/artist_sound_clips/253) embed `w.soundcloud.com/player/?url=…api.soundcloud.com/playlists/…`. SoundCloud profile: https://soundcloud.com/archive-of-indian-music ("Archive Of Indian Music", full_name "Vikram Sampath", 1,677 tracks, about 450k followers). Profile text: "The Archive is a custodian of these treasures and makes these available for all through its different dissemination arms." No licence statement.
- Two sample track pages (…/3635-khyal-multani-by-zohra, …/3637-ghazal-by-zohra-bai-g-c-8) carry SoundCloud metadata: `"license":"all-rights-reserved"`, `"downloadable":false`, `"streamable":true`, `"embeddable_by":"all"`, `"policy":"MONETIZE"`.
- YouTube: no AIM channel at `@archiveofindianmusic`, `@ArchiveofIndianMusicAIM` or `c/ArchiveofIndianMusic` (all 404). **[UNVERIFIED]** that no official AIM YouTube presence exists.
- Context, not a term: Deccan Herald (2021) says "The digitised collection is available online on Soundcloud, for free". It quotes Sampath: "Working independently, there is little that an individual can do due to copyright claims." A search summary attributes to Sampath the claim that AIM focuses on pre-1950s recordings because Indian sound-recording copyright lasts 60 years. **[UNVERIFIED]** The statute (Copyright Act 1957, s.27) could not be fetched: **[BLOCKED]** copyright.gov.in does not resolve under the sandbox network policy.

---

## 4. Irama Nusantara: quoted terms and sources

- Disclaimer ("Pernyataan"), https://www.iramanusantara.org/disclaimer. Indonesian original:
  > Semua informasi di situs ini dipublikasikan dengan itikad baik dan untuk tujuan informasi umum saja, sebagai bagian dari upaya penyelamatan dan preservasi arsip rilisan musik Indonesia untuk tujuan non-komersil, sesuai dengan UU No. 43 Tahun 2009 dan UU No. 13 Tahun 2018. Jika ada pemegang Hak Cipta, Hak Royalti, dan Hak Kekayaan Intelektual lainnya yang merasa berkeberatan jika karyanya ditampilkan dalam perpustakaan digital kami, silakan menghubungi kami dan kami dapat secepatnya menghapus karya yang dimaksud tersebut.
  > …
  > Dengan menggunakan situs kami, Anda menyetujui ketentuan kami dan menyetujui persyaratannya.

  The site's own English version (https://www.iramanusantara.org/en/disclaimer):
  > All the information on this website is published in good faith and for general information purpose only, as part of efforts to save and preserve Indonesian music release archives for non-commercial purposes, in accordance with Indonesian Government Law no. 43 of 2009 and Indonesian Government Law no. 13 of 2018. If there are copyright, royalty, and other intellectual property rights holders who object to their work being displayed in our digital library, please contact us and we can immediately delete the work in question.
  > …
  > By using our website, you hereby consent to our disclaimer and agree to its terms.

  Law 43/2009 is the Archives Law (Kearsipan) and Law 13/2018 is the legal deposit law for printed and recorded works (Serah Simpan Karya Cetak dan Karya Rekam). That identification is mine, not stated on the site. Neither law gives third parties a right to reuse.
- Sign-in, shown on every page in the site's login pop-up: "Daftar / masuk dulu ya! Untuk putar lagu, baca artikel, dan nikmati fitur-fitur lainnya." ("Sign up / sign in first! To play songs, read articles and enjoy other features.") **Playing audio requires an account** (e-mail, Google or Facebook sign-in).
- Sign-up terms: "Dengan mengklik daftar, kamu menyetujui Persyaratan dan Ketentuan Penggunaan Irama Nusantara." (English UI: "By clicking Sign Up, you agree to the Terms and Conditions of Irama Nusantara.") In the site's JavaScript the link is `href='#'`, and no standalone Terms and Conditions document was found. **[UNVERIFIED]** whether the modal shows more text after sign-in. No account was created.
- Scope (home and About pages): "Arsip Digital Musik Populer Indonesia" ("Digital Archive of Indonesian Popular Music"), "musik populer Indonesia yang dirilis dari era 1920-an hingga 2000-an" ("Indonesian popular music released from the 1920s to the 2000s").
- Contact: info@iramanusantara.org, from the disclaimer (Cloudflare-obfuscated, decoded), plus a contact form on /about. The footer reads "©2016- 2026 Seluruh hak cipta milik Irama Nusantara" ("All rights reserved by Irama Nusantara"). `robots.txt` has only Cloudflare content-signal comments and no directives.

---

## 5. SoundCloud and YouTube: quoted terms

### SoundCloud Terms of Use (https://soundcloud.com/terms-of-use, "Last Amended: August 17, 2026")
> (i) You must not copy, rip or capture, or attempt to copy, rip or capture, any Content from the Platform or any part of the Platform, other than by means of download or store for offline listening in circumstances where the relevant Uploader has elected to permit downloads or offline listening of the relevant item of Content.

> (iv) You must not employ scraping or similar techniques to aggregate, repurpose, republish or otherwise make use of any Content.

> Any Content other than your Content is the property of the relevant Uploader … Such Content may not be downloaded, reproduced, distributed, transmitted, re-uploaded, republished, displayed, sold, licensed, made available or otherwise communicated to the public or exploited for any purposes except via the features of the Platform from time to time and within the parameters set by the Uploader on the Platform, or otherwise in accordance with the licenses granted herein or with the express written consent of the Uploader.

On the Players and Widget:
> You may not, without the prior written consent of SoundCloud, use the Players or the Widget in such a way that you aggregate Content from the Platform into a separate destination that replicates substantially the offering of the Website, or comprises a content service of which Content from the Platform forms a material part. Similarly, you may not, without the prior written consent of SoundCloud, use the Players or the Widget to embed Content into any website or other destination dedicated to a particular artist (…) or to a particular genre.

### SoundCloud API Terms of Use (https://developers.soundcloud.com/docs/api/terms-of-use, "Effective Date: 30 March 2024")
> Your app must not include file-save functionality, or otherwise designed to cache, download or persistently store any User Content. … your app must not offer offline access to any User Content … regardless of whether Uploaders have set that User Content as allowed to be downloaded on the SoundCloud platform.

> You must not use the SoundCloud API to rip, capture, or copy any User Content from any part of the SoundCloud platform …

> You must not use the SoundCloud API for any so-called stream ripping, stream capture or similar applications designed to make permanent copies of User Content.

### YouTube Terms of Service (https://www.youtube.com/t/terms, "Effective as of December 15, 2023")
> You may view or listen to Content for your personal, non-commercial use. You may also show YouTube videos through the embeddable YouTube player.
> The following restrictions apply to your use of the Service. You are not allowed to:
> - access, reproduce, download, distribute, transmit, broadcast, display, sell, license, alter, modify or otherwise use any part of the Service or any Content except: (a) as expressly authorized by the Service; or (b) with prior written permission from YouTube and, if applicable, the respective rights holders;
> - circumvent, disable, fraudulently engage with, or otherwise interfere with any part of the Service … including security-related features or features that (a) prevent or restrict the copying or other use of Content …;
> - access the Service using any automated means (such as robots, botnets or scrapers) except … ;
> - use the Service to view or listen to Content other than for personal, non-commercial use (for example, you may not publicly screen videos or stream music from the Service);

### YouTube API Services Developer Policies (https://developers.google.com/youtube/terms/developer-policies, page "Last updated 2026-11-01 UTC")
> You and your API Clients must not … download, import, backup, cache, or store copies of YouTube audiovisual content without YouTube's prior written approval, make content available for offline playback …

> separate, isolate, or modify the audio or video components of any YouTube audiovisual content … ; promote separately the audio or video components … ; create, include, or promote features that play content, including audio or video components, from a background player …

The YouTube API rules bear directly on an audio-only folk player: no audio-only extraction, no background playback.

---

## Access log: blocked or unverified

| Item | Status |
|---|---|
| lesc-cnrs.fr (CREM/LESC pages) | **[BLOCKED]** HTTP 503 "site en cours de maintenance" |
| journals.openedition.org/afas/4032 | **[BLOCKED]** Anubis bot check (not bypassed) |
| halshs.archives-ouvertes.fr (Simonnot papers) | **[BLOCKED]** Anubis bot check (not bypassed) |
| copyright.gov.in (Indian Copyright Act) | **[BLOCKED]** DNS fails under the sandbox network policy |
| CREM "droit de retrait" corpus notice | **[UNVERIFIED]**: from a search summary only; not found on pages checked |
| CREM live value of the Telemeta auto-publication period | **[UNVERIFIED]**: repo default is 51 years |
| NDL position on third-party HLS hotlinking | **[UNVERIFIED]**: not addressed in any terms; ask NDL |
| AIM YouTube presence | **[UNVERIFIED]**: none found |
| Irama Nusantara full T&C text | **[UNVERIFIED]**: link is `#`; no document found; no account created |

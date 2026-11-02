# Archive sweep, second pass: sub-Saharan Africa gaps

Date: 2026-11-02. This pass covers the gaps the first pass ([sweep-africa.md](sweep-africa.md)) could not search:
- 16 unsearched countries: Chad, CAR, Equatorial Guinea, São Tomé and Príncipe, Guinea-Bissau, Togo, Mauritania, Djibouti, Eritrea, South Sudan, Burundi, Botswana, Lesotho, Eswatini, Comoros and Seychelles.
- A proper search of Cape Verde, Liberia, Malawi, Zimbabwe, Mauritius and Madagascar.
- Four European museums: the Berlin Phonogramm-Archiv, Världskulturmuseerna (Sweden), the Musée du quai Branly, and more of the AfricaMuseum Tervuren (RMCA).

Searches were in English, French, Portuguese, Spanish (Equatorial Guinea), German and Swedish. Archives already assessed in the first pass, and those in the brief's "already known" list, are treated as known.

Status labels:
- **verified**: I loaded the page, file or API with curl from the sandbox.
- **search-only**: seen only in web-search results.
- **blocked**: the host refused the sandbox, failed TLS, timed out or returned an error. Nothing was bypassed.

**Headline finding.** As in the first pass, I found **no new good fit**. For pre-1971 recordings from the gap countries, nearly everything sits in European institutions already known to the project:
- CREM-CNRS, which has many new sub-collections, listed below;
- RMCA Tervuren;
- the British Library;
- ILAM.

The one large new institutional lead is the **Berlin Phonogramm-Archiv**. Its online catalogue has metadata and photos only, no audio. National broadcasters in the gap countries hold 1960s–70s tapes, but none has put audio online. Two are mid-digitisation: Malawi's MBC and Eritrea's RDC.

---

## 1. Ranked table

| # | Archive | Content | Audio access | Rights | Machine access | Verdict | Status |
|---|---|---|---|---|---|---|---|
| 1 | **Berliner Phonogramm-Archiv** (Ethnologisches Museum, SMB, Berlin) | About 16,000 wax cylinders, 1900–1930s. About a third come from Africa; a DAI estimate puts about 2,200 African cylinders before WWI. Examples: 200+ Togo recordings ("Archiv Ewe I/II", "Smend Togo I/II", "Stoevesandt Togo", "Witte Togo"); 186 South Cameroon cylinders 1907–09 ("Archiv Kamerun", "Waldow Kamerun"); "Archiv Kongo" 1908–09; "Archiv Ful" (N. Cameroon); "Archiv Senegambien" 1910; Rietsch East Africa 1931–36. 1970s reel tapes too (e.g. the 1972 Radio Swaziland "African Music Rostrum" tape and Rycroft's Zulu–Swazi tapes) | **No audio online.** search.smb.museum records (e.g. `obj-2448907` "Archiv Kamerun", recorded 1909) carry only **photos of the cylinders** (JPG/PNG). Digitised audio is reportedly listenable on request or on site, and through CD releases (*Walzenaufnahmen aus Südkamerun 1907–1909*, 2022) | Catalogue text: CC BY-NC-SA 3.0 (museum-digital); photos: CC BY-NC-SA 4.0. **No licence for the audio.** Colonial-era recordings, so repatriation and ethics questions apply (a 2019 Lomé workshop discussed returning the Togo files) | search.smb.museum (Nuxt SPA with embedded JSON); smb.museum-digital.de; Deutsche Digitale Bibliothek (has an API). `api.smb.museum` reset the connection | **Ask permission.** The largest pre-1920 African corpus not yet approached. Ask whether digitised Togo, Cameroon and Senegambia cylinders could be streamed non-commercially with credit, and whether the source communities have been consulted. Contact: **em@smb.spk-berlin.de** (Ethnologisches Museum, Abteilung Musikethnologie, Medien-Technik und Phonogramm-Archiv) | Verified (catalogue, images only); search-only (contact, holdings) |
| 2 | **RMCA / AfricaMuseum Tervuren**: per-country holdings for the gap countries (already known; extends row 1 of the first pass) | Catalogue counts by country (`get_recordings.php?country=N`): Burundi 1,322, Chad 491, CAR 347, Zimbabwe 429, Malawi 535, Madagascar 239, Swaziland 69, Lesotho 84, Botswana 135, Togo 53, Liberia 43, Mauritania 72, Eritrea 67, Cape Verde 57, São Tomé 41, Seychelles 29, Comoros 15, Equatorial Guinea 1, Guinea-Bissau 2 (free text). **0** for Mauritius, Djibouti and South Sudan. Inventory years (MR.1950–1970) are acquisition dates, not recording dates | **30-second clips exist mainly for Rwanda, Uganda and Burundi.** For the gap countries, page 1 lists "N.A." / "Fragment: not available online". Some entries are tracks from commercial LPs, e.g. Chad `MR.1961.1.2-A6`, from Italiaander's *Afrika tanzt und singt* | As in the first pass: research or private use; CC BY where RMCA holds the copyright. **Be-MUSIC** (RMCA + MIM, BELSPO, 2023–2027, €558,800) is building a public platform for the 37,000 sound recordings and has a sound-archive copyright manager. No launch found | HTML only | **Ask permission** (same email as the first pass: **repro@africamuseum.be**). Add a question on Be-MUSIC's planned online release and licence | Verified (counts); search-only (Be-MUSIC) |
| 3 | **Malawi Broadcasting Corporation tape archive** (digitised with Nedland Kultur and the Norwegian Embassy) | "Thousands" of MBC radio sessions and field recordings from the 1960s–70s | **Not online.** Material reaches the public only through new arrangements (the band Takula) and an Afropop feature | Broadcaster-owned | — | **Ask permission (low odds).** No public contact found. Route through MBC (Blantyre) or Nedland Kultur | Search-only |
| 4 | **Eritrea Research and Documentation Centre (RDC)**: UNESCO-supported audio digitisation | "More than 60,000 analogue audio recordings of music works". Date span not stated | Not online | State-held | — | **Not usable now** (offline; status "on going"). Watch for later | Search-only |
| 5 | **Radio Télévision de Djibouti archive** (Ostinato "Djibouti Archives" series) | Thousands of Somali and Afar reels. Released material dates from 1977–94 (the 4 Mars anthology) | **Not online.** Commercial LP/Bandcamp reissues only. Foreign access was closed until 2019 | Ostinato holds a release agreement | — | **Not usable** (not online; mostly post-1970) | Search-only |
| 6 | **Rádio Nacional de São Tomé e Príncipe music archive** | Reel archive started by technician Firmino Bernardo; rediscovered by M. Bialoborska (PhD ISCTE, covers 1859–1990) | Not online. Reissues are commercial (Bongo Joe *Léve Léve*) | Broadcaster | — | **Not usable** (not online) | Search-only |
| 7 | **UCLA MEAP: "Rescuing Guinea-Bissau's Sound Archives"** (Rádio Libertação) | PAIGC liberation-struggle radio, 1960s–70s. Mostly speech and political content | Planned for UCLA Digital Library (host fails TLS in the sandbox, see the first pass) | Unknown | — | **Not usable** for folk streaming (political radio, unclear rights). The MEAP project page returned no text to curl | Search-only |
| 8 | **Robert Kauffman Zimbabwe field recordings, 1960–62** (UCLA Ethnomusicology Archive, coll. 1968.2; UW copies) | Shona mbira solos and ensembles, school songs, work songs; Oct 1960 – Nov 1962 | Not online. UW holds digitised copies and log sheets (PDF online) | At Kauffman's request the recordings will be **sold through the MBIRA archive, with proceeds to the performers' descendants** | Archives West finding aid | **Not usable.** A benefit-sharing sale model is in place; free streaming would undercut it | Search-only |
| 9 | **Archives Virtuelles de la Musique Malgache (AVMM)** (Schmidhofer and Razanajao, Univ. of Vienna) | Malagasy field recordings and 78s. Copies given to the Bibliothèque nationale malgache (2002, 2007, 2012) and the Musée d'Art et d'Archéologie (2012) | Unknown | "©2003–2023 AVMM All rights reserved" (search snippet) | — | **Not assessed.** `www.avmm.org` failed TLS (certificate name mismatch); http returned 404; WebFetch got 503 | Blocked |
| 10 | **Världskulturmuseerna** (Etnografiska museet, Carlotta database) | Large Congo holdings (Swedish Mission Covenant: Laman photographed a cylinder phonograph at Kingoyi in 1909), 220,000 objects in all | **No audio objects found.** Carlotta shows objects, photos and persons. Laman-era digitisation has covered photos, film and Kikongo notebooks, not sound | Some data marked CC Public Domain Mark. **Ritual and ceremonial items are hidden** from the public database | Carlotta HTML (guest access) | **Not usable** (no sound online). If ever needed, ask whether Laman's cylinders survive: **carlotta@etnografiska.se** | Verified (Carlotta); search-only (cylinders) |
| 11 | **Musée du quai Branly – Jacques Chirac** | "Audio" base: mostly published discs (e.g. *Le chant des enfants du monde*). Dakar–Djibouti mission (1931–33) recordings are said to be held there; the field recordings that are online are in CREM (Musée de l'Homme fonds) | Old notice URLs redirect (301) to `collections.quaibranly.fr`, a JavaScript-only React app. No audio found without JS | Unknown | JS app only. I did not reverse-engineer its API | **Not usable / not assessed.** The relevant African field recordings are reachable through CREM (already known) | Verified (redirect, JS shell) |
| 12 | **Internet Archive: `unlockedrecordings`** (Boston Public Library vinyl; new IA sub-collection for Africa) | African LPs dated to 1970 or earlier, e.g. `lp_folklore-de-madagascar_ny-antsaly`, `lp_ethiopia-vol-3-music-of-eritrea_jean-jenkins`, `lp_ekonda-tribal-music-of-the-congo_various`, `lp_folk-music-of-the-western-congo_bantu`, `lp_music-from-petauke-of-northern-rhodesia-vo_various`, `lp_vol-1_keita-fodeba-and-his-african-ensemble`. About 33 hits for "Africa" | **Direct MP3s, no login** | **No licence.** Out-of-print commercial LPs, many from ethnographic labels still in print elsewhere (Folkways, OCORA, Ethnic Folkways) | IA API | **Not usable** under the open-licence rule. Flag for the app's IA filter: `unlockedrecordings` items are not public domain | Verified |
| 13 | **Internet Archive: other single items** | `sounds-of-the-watussi-from-ruanda-urundi` (Folkways FE 4428, 1952; community upload in `opensource_audio`); `BulawayoSweetRhythmsBandSkokiaan1954` and `...InTheMood1954` (in `78rpm`, Zimbabwe popular music) | Direct MP3 | Watussi: Smithsonian-owned LP uploaded without licence. Bulawayo 78s: no statement | IA API | Watussi: **not usable** (rights-held LP). Bulawayo 78s: **already covered** by the app's IA 78rpm source (popular, not folk) | Verified |
| 14 | Other checked | Univ. of Hildesheim Center for World Music "Malawi Collection" (24 tapes by Moya Malamusi, b. 1958; post-1970). Edinburgh DataShare Dinka songs (2010s project; page unavailable). UNESCO Video & Sound Collections (Botswana item is a 2016 documentary, "contact the rights holder"). BL Sounds John Brearley Botswana (1982+). Wits "Swaziland Oral History Project" (interviews, transcripts only) | — | — | — | **Not usable** (out of date range or not music) | Mixed |
| — | **Blocked hosts** | ILAM Commons `ilamcommons.ru.ac.za` and `vital.seals.ac.za` (proxy 502, CONNECT failed); SAMAP `samap.ukzn.ac.za` (connection reset); `www.avmm.org` (TLS mismatch); `api.smb.museum` (reset); `datashare.ed.ac.uk` handle (page unavailable) | | | | Not assessed | Blocked |

### New sub-collections of already-known archives (noted, not re-researched)

**CREM-CNRS** (search-only). Access per item is "full", "metadata" or "restreint". These are the main pre-1971 holdings for the gap countries:

| Country | CREM holdings |
|---|---|
| Chad | Fonds `CNRSMH_Tchad`. Robert Jaulin, Sara-Deme 1956 (6 unpublished items, digitised 2007); Alfred Adler, Mbay 1964 (metadata only); Duvelle & Vuylsteke *Anthologie de la musique du Tchad* 1968 (`CNRSMH_E_1968_004_006`; includes girls' initiation songs, **flag**); Monique Brandily, Tibesti 1961–79 |
| Mauritania | Gilbert Rouget 1952 "Chant de griots maures" (`CNRSMH_I_1969_039_*`, **access full**); Pierre Potentier 1954 LP set (full); Duvelle 1965 and Perret 1975–76 (metadata only) |
| Togo | Pierre Sallée mission 1962 (`CNRSMH_E_1963_001_006`); Raymond Verdier, Kabiyè, from 1960. Also copies of the Berlin "Smend Togo I" (`CNRSMH_E_2006_010_001_53`) in the *Music! Berlin Phonogramm-Archiv* boxed CD |
| CAR | Dournon and Arom 1964–67 (restricted; copies at Musée Boganda); Duvelle 1962 (metadata); 1931 Exposition coloniale 78 "Oubanghi-Chari: conversation" (`CNRSMH_I_1932_001_001_01`) |
| Comoros | Cormillot, Tadjiddine and Thévenet 1946–83 (`CNRSMH_E_2011_002_045`, partly restricted); Louise Weiss voyage, Dec 1961 (`CNRSMH_I_1981_023_*`) |
| Burundi | Vuylsteke 1967 *Burundi: musiques traditionnelles* (`CNRSMH_E_1988_012_001`, edited/restricted). Ingoma royal drums are sacred/royal: **flag** |
| Lesotho | François Ellenberger, Basutoland 1959 (`CNRSMH_I_1960_007_*`) |
| Seychelles | Bernard Koechlin 1976–77 (out of range) |
| Liberia | Packard Okie *Folk Music of Liberia* (Folkways, metadata only) |
| Eritrea | Wolf Leslau, Tigre ballad 1947; Jenkins *Music of Eritrea* 1970 LP |
| South Sudan | Francis Mading Deng, Dinka 1972 (metadata only) |
| Mauritius | Séga ravanne collections, all 1981+ (out of range) |
| Madagascar | IRSM 1959–60 and the 1929–31 78s compilation (first pass) |

**ILAM / Hugh Tracey** (known; hosts blocked). Search results show ILAM Commons items for:
- Eswatini 1958 (Mbabane, Entonjeni, Pigg's Peak);
- Lesotho 1956–59 (Upper Qeme, Maseru district);
- Malawi 1950, 1957, 1958 (Mang'anja, Cewa, Yao; Mzimba, Zomba);
- Zimbabwe 1948–63 (Ndau, Sena, Tonga, Shona mbira).

These are the richest pre-1971 sources for those four countries and should be covered by any ILAM approach.

**British Library Sounds** (known):
- Hans-Joachim Heinz, !Xo (Botswana), 1960s, 13 recordings; copyright transferred to the University of Botswana. San recordings: **flag**.
- John Brearley, Botswana, 1982+ (out of range).
- Dinka songs from South Sudan (modern).

**Library of Congress** (known): Arthur S. Alberts, Liberia 1948–49 (Endangered Music Project).

**Phonogrammarchiv Vienna** (known): Madagascar repatriation in 2012; AVMM is a related Vienna project (row 9).

**Lautarchiv HU Berlin** (first pass, not usable): about 450 WWI recordings of African prisoners (Senegal, Somalia, Togo, Congo), studied in A. Hoffmann, *Knowing by Ear* (2024).

---

## 2. Country checklist (this pass)

| Country | Result |
|---|---|
| Chad | No national online archive. CREM fonds Tchad (Jaulin 1956, Adler 1964, Duvelle/Vuylsteke 1968, Brandily 1961–79); RMCA 491 entries (no clips) |
| Central African Republic | No Radio Centrafrique archive online. CREM Arom/Dournon 1964–67 (restricted; copies at Musée Boganda), Duvelle 1962, 1931 Exposition 78; RMCA 347 (no clips). BaAka recordings: **flag** |
| Equatorial Guinea | Nothing found online. Research by Isabela de Aranzadi and a private Fang instrument collection, no sound archive. RMCA 1 entry |
| São Tomé and Príncipe | Rádio Nacional reel archive (not online; row 6); RMCA 41 (no clips); IA none |
| Guinea-Bissau | MEAP Rádio Libertação (political radio, row 7); nothing else online; RMCA 2 |
| Togo | Berlin Phonogramm-Archiv: 200+ German-colonial recordings (row 1, ask); CREM Sallée 1962 and Verdier 1960; Radio Lomé/RTT archive not found; RMCA 53 |
| Mauritania | CREM Rouget 1952 (full access) and Potentier 1954; no national archive online; RMCA 72 |
| Djibouti | RTD archive (Ostinato commercial reissues, mostly 1977+, row 5); RMCA 0; IA 0 |
| Eritrea | RDC digitisation of 60,000+ recordings (not online, row 4); CREM Leslau 1947, Jenkins 1970; IA `unlockedrecordings` Jenkins LP (no licence); RMCA 67 |
| South Sudan | Nothing pre-1971 online. CREM Deng 1972 (metadata only); Edinburgh Dinka and BL Dinka (modern); Khartoum folklore archive (first pass) |
| Burundi | RMCA 1,322 entries, some with 30-second clips (best-covered gap country); CREM Vuylsteke 1967; RTNB archive not online; IA Folkways "Watussi" 1952 (not usable) |
| Botswana | Radio Botswana's traditional-music archive (aired on *Dipina le Maboko* for 50+ years; not online); BL Heinz !Xo 1960s; Toronto Bechuanaland reel 1962–63 (finding aid only); RMCA 135 |
| Lesotho | ILAM Tracey 1956–59 (blocked); CREM Ellenberger 1959; Folkways *Music of Lesotho* (known); Lesotho Broadcasting holdings not online; RMCA 84 |
| Eswatini | ILAM Tracey 1958 (blocked); Berlin EM 1972 Radio Swaziland tape (catalogue only); Wits oral-history transcripts; RMCA 69 |
| Comoros | CREM Cormillot et al. 1946–83, Weiss 1961; CNDRS (Moroni) not found online; RMCA 15 |
| Seychelles | Seychelles National Archives digitising, but no audio found; CREM Koechlin 1976–77 (out of range); RMCA 29 |
| Cape Verde (proper search) | Nothing found online. Early mornas were commercial 78s (e.g. Fernando Quejas, Columbia, 1952; diaspora label Verdatones, New Bedford); no national fonoteca online; RMCA 57; IA 0 |
| Liberia (proper search) | Nothing new online. IU Liberian Collections/ATM (not public); LoC Alberts 1948–49 (known); CREM Okie (metadata); RMCA 43 (no clips); ELBC archive not found |
| Malawi (proper search) | MBC 1960s–70s tapes digitised with Norway, not online (row 3, ask, low odds); ILAM Tracey 1950–58 (blocked); Hildesheim Malamusi tapes (post-1970); Malawi Folktales and Folksongs database (not checked in depth); RMCA 535 |
| Zimbabwe (proper search) | Kauffman 1960–62 (to be sold via MBIRA for descendants; row 8); ILAM Tracey 1948–63 (blocked); National Archives of Zimbabwe and ZBC not online; IA Bulawayo 78s 1954 (popular); RMCA 429 |
| Mauritius (proper search) | Nothing pre-1971 online. CREM séga holdings all 1981+; private "Kaya/Menwar Archives" (Avneesh Bacha, cassettes, modern); MBC and Nelson Mandela Centre not found; RMCA 0 |
| Madagascar (proper search) | AVMM (blocked, row 9); copies at the Bibliothèque nationale malgache and the Musée d'Art et d'Archéologie (not online); CREM IRSM 1959–60 (known); IA `unlockedrecordings` *Folklore de Madagascar* LP 1970 (no licence); RMCA 239 |

**European museums (this pass):**
- Berlin Phonogramm-Archiv: ask (row 1).
- Världskulturmuseerna: no sound online (row 10).
- Quai Branly: JS-only catalogue, African field audio is in CREM (row 11).
- RMCA: per-country counts done, Be-MUSIC platform pending (row 2).

---

## 3. Per-archive notes

### Berliner Phonogramm-Archiv: ask permission (catalogue verified, images only)
- `https://search.smb.museum/en/object/obj-2448907`: title "Archiv Kamerun", Aufnahmejahr 1909. Embedded assets `id.smb.museum/digital-asset/5724374…78` are JPG/PNG photos, credited "Staatliche Museen zu Berlin, Ethnologisches Museum / Lisa Koletzki CC BY-NC-SA 4.0". No audio file in the page data.
- `https://smb.museum-digital.de/object/211991` ("Archiv Kongo", Objektart "Phonographenwalze – Alte Kopie"): text under CC BY-NC-SA 3.0; "Jede Form der kommerziellen Nutzung… bedarf der Rücksprache mit dem Museum."
- CD release: `https://search.smb.museum/object/obj-2869468` (*Walzenaufnahmen aus Südkamerun 1907–1909*).
- Togo collections and the Lomé workshop on restitution: https://journals.openedition.org/gradhiva/8669. The article says many Togo items are "digitized and accessible as audio files" but gives no public URL, and notes access "remains extremely difficult for users based in Africa".
- Arab-world collections were digitised under the AGYA "Access to Waxes" project (https://agya.info/research/research-projects-by-year/access-to-waxes-arab-collections-of-the-berlin-phonogramm-archiv). That is a model for a possible Africa equivalent.
- Contact (search-only, from an SMB object record): **em@smb.spk-berlin.de**. Ask for the department of Prof. Lars-Christian Koch (Musikethnologie, Medien-Technik und Phonogramm-Archiv).
- Sensitivity: German colonial context; the 1915–18 POW recordings are at the Lautarchiv, not here. Ask about community consultation before any streaming.

### RMCA Tervuren, per-country counts (verified)
- Country IDs I mapped from `https://music.africamuseum.be/english/get_recordings.php?country=N`:
  - 4 Burundi; 10 Chad; 13 Zimbabwe; 17 Mauritania; 18 Madagascar; 20 CAR; 24 Malawi; 29 Eritrea; 30 Swaziland; 31 Liberia; 46 Lesotho; 47 Botswana; 48 Togo;
  - 66 São Tomé; 67 Comoros; 68 Equatorial Guinea; 73 Cape Verde; 96 Seychelles.
  - The catalogue also has large Caribbean holdings: Aruba 321, Bonaire 420, Curaçao 330.
- Free-text searches for "Mauritius" and "Djibouti" return 0 recordings found; "Guinea-Bissau" returns 2.
- Example detail page: `detailrec.php?id=MR.1961.1.2-A6` (Chad, Bornou, collector Rolf Italiaander, circle dance). It reads "Fragment: not available online" and cites the commercial LP *Afrika tanzt und singt*.
- Be-MUSIC: https://www.belspo.be/belspo/brain2-be/projects/Be-MUSIC_E.pdf. A rights workshop involved the RMCA copyright manager for sound archives: https://cria.org.pt/en/restitution-and-repair-workshops-be-music-online-sound-archives-and-the-intellectual-property-rights

### Malawi Broadcasting Corporation tapes: ask permission, low odds (search-only)
- https://www.afropop.org/articles/title-saving-the-malawi-tapes
- https://www.tanzaniaheritageproject.org/?p=644
- Partners: Malawi National Commission for UNESCO, National Library Service, Music Crossroads Malawi, Department of Museums and Monuments.
- I found no public contact for the archive itself.

### Eritrea RDC digitisation (search-only)
- UNESCO project: https://core.unesco.org/en/project/3240115195 (60,000+ analogue music recordings, USD 23,000, status on going).

### Djibouti RTD archive (search-only)
- https://www.albawaba.com/index%2Ephp/editors-choice/djiboutis-radio-archive-reveals-rich-sound-past-1434968
- https://ostinatorecords.bandcamp.com/album/the-dancing-devils-of-djibouti

### São Tomé Rádio Nacional (search-only)
- Bialoborska thesis: https://www.repositorio-iul.iscte.pt/bitstream/10071/25201/1/phd_mgdalena_anna__bialoborska.pdf
- https://daily.bandcamp.com/features/bongo-joe-sao-tome-and-principe-reissues-feature

### Guinea-Bissau, MEAP (search-only)
- https://meap.library.ucla.edu/projects/rescuing-guinea-bissaus-sound-archives (curl returned no readable text).

### Kauffman Zimbabwe (search-only)
- https://ethnomusicologyreview.ucla.edu/content/highlights-ethnomusicology-archive-robert-kauffman-collection
- https://nwdh.dp.la/item/d5baf68f2db917d83a66e851c834868f
- Log sheet example: https://www.lib.washington.edu/static/public/ethno/logsheets/62-6.7.pdf

### AVMM, Madagascar (blocked)
- `https://www.avmm.org/home_english.html`: curl error 60 (certificate does not match the host name); http returned 404; WebFetch returned 503. Background: https://library.columbia.edu/libraries/global/virtual-libraries/african_studies/countries/madagascar/edu.html

### Världskulturmuseerna (verified)
- `https://carlotta.smvk.se/carlotta-em/web/object/2006435` (place authority for Kongo-Kinshasa) lists 11,749 references, all persons, expeditions and objects. I found no sound objects.
- The footer gives **carlotta@etnografiska.se** and links the CC Public Domain Mark. The database notice says objects linked to ritual or ceremonial activity "have been hidden in the publicly accessible database".
- Laman phonograph photo (1909) and the digitisation of photos and film: https://researchportalplus.anu.edu.au/en/publications/reframing-the-encounter-from-repressed-colonial-pile-to-a-collabo/

### Musée du quai Branly (verified, not assessed)
- `https://m.quaibranly.fr/en/explore-collections/base/Audio/action/show/notice/000154207-...` returns 301 to `https://collections.quaibranly.fr/`, a React app ("You need to enable JavaScript"). `sitemap.xml` and `/api` return 404.
- I stopped there and did not reverse-engineer the app.
- Dakar–Djibouti recordings on CREM: e.g. https://archives.crem-cnrs.fr/archives/items/CNRSMH_I_1931_001_025_01

### Internet Archive (verified via advancedsearch and metadata API)
- `unlockedrecordings` description: "Works may be in the public domain already, in which case, these restrictions do not apply". So the default is restricted.
- `lp_folklore-de-madagascar_ny-antsaly` and `lp_ethiopia-vol-3-music-of-eritrea_jean-jenkins`:
  - collections `album_recordings, vinyl_bostonpubliclibrary, unlockedrecordings`;
  - no `licenseurl`; MP3s public.
- Country-name searches for audio dated 1890–1970 returned 0 relevant items for Cape Verde, Malawi/Nyasaland, Mauritania, Djibouti, Botswana/Bechuanaland, Swaziland, Comoros, Seychelles, São Tomé and Guinea-Bissau.

### Cultural-sensitivity flags (new this pass)
- Chad: girls' initiation songs in the Duvelle/Vuylsteke 1968 anthology.
- Burundi: royal ingoma drums (sacred and royal context).
- CAR: BaAka/Aka recordings.
- Botswana: San/!Xo recordings (Heinz).
- Berlin: German-colonial cylinders (Togo, Cameroon), where restitution talks are under way.
- Världskulturmuseerna: hides ritual material by policy. Respect the same exclusions if any audio is ever offered.

# Archive sweep, second pass: Latin America and the Caribbean

Date: 2026-11-02. Second pass for Old Folk Air, following [sweep-brief.md](sweep-brief.md). This pass fills the gaps listed in the first pass, [sweep-latin-america-caribbean.md](sweep-latin-america-caribbean.md):
- Central America.
- Venezuela, Ecuador, Bolivia, Paraguay and the Guianas.
- Cuba, the Dominican Republic, Haiti beyond Duke, the French Antilles, the Bahamas, Barbados and the Dutch Caribbean.
- Mexican state and university collections.
- Brazil's regional archives.
- The European holders that the first pass flagged.

Every archive named in the brief's "already known" list or in the first pass is treated as known. Only new sub-collections of those archives are noted, in table 1d.

**Method and limits**
- **Languages.** Searches were run in Spanish, Portuguese, French, Haitian Creole, Dutch, Papiamentu and Swedish, with roughly 90 web searches in all. Known hosts were then probed directly with `curl` and WebFetch, a few requests per site.
- **Status labels.** **verified** means the page, API or metadata was fetched in this pass, and for audio a ranged GET returned 206. **search-only** means the claim comes from search results or literature only. **blocked** means the host could not be reached.
- **Blocked hosts.** Many national hosts were unreachable from the research sandbox: proxy refusals (502), connection resets, TLS errors, 401/403 responses, or Cloudflare/Anubis bot checks. None were bypassed, and no user agents were changed. The blocked hosts are listed per country in the checklist and notes. The countries most affected were Cuba, the Dominican Republic, Haiti, Nicaragua, Costa Rica and the French Antilles. Those countries need a recheck from an unrestricted network before "nothing found" can be treated as final.
- **Spot checks.** The key good-fit facts were re-run independently when this report was assembled:
  - UNM OAI-PMH set.
  - The Bolivian IA uploader query: 109 items, 86 CC BY-NC-ND 4.0, 1 CC BY-NC 4.0, 22 unlicensed.
  - The Aruba IA collection `bnaitotromp`: 86 audio items, none licensed, 69 dated 1967–69 and 6 dated 1970.

**Headline**
- **Good fits.** Very little pre-1971 traditional audio from these countries is both online and openly licensed. There are three good fits, all small:
  - Bolivian 78s from Ajayus de Antaño on the Internet Archive.
  - K'iche' Maya oral narratives from Guatemala at UNM (spoken word).
  - The early items in the Soule Archive from the US Virgin Islands, reached through dLOC's new API.
- **Best ask-permission leads.** These are mostly Dutch-Caribbean and Panamanian holdings, plus one Brazilian 78 label:
  - Biblioteca Nacional Aruba's Ito Tromp tapes (1967–70, already on IA).
  - The Brenes/Zárate Panama field recordings.
  - MuseCom's Casa A Electrica 78s.
  - The Zikinzá collection (Curaçao and Bonaire).
  - The Koch-Grünberg 1911–13 cylinders in Berlin.

## 1. Ranked table

### 1a. Good fits

| # | Archive (country) | Content | Audio access | Rights | Machine access | Verdict | Status |
|---|---|---|---|---|---|---|---|
| 1 | **UNM Digital Repository: K'iche' Maya Oral History Project** (USA, about Guatemala) | 151 K'iche' oral narratives (folktales, origin stories such as "The Origin of the Marimba", beliefs) from Nahualá, Sololá. Recorded by James Mondloch with Miguel Guarchaj Ch'o'x and Diego Guarchaj. Every item is dated 1968 (project 1968–73). Speech, not music | Direct MP4 (h264) file per item on libvideo.unm.edu, no login. Range request returns 206 (about 18 MB per item). Also PDF transcripts | Per item: "copyrights … remain with LAII … digital surrogates … licensed under CC BY-NC-ND 4.0" | OAI-PMH (`/do/oai/`, set `publication:laii_kichemaya`, 151 records, no key). The page HTML gives the MP4 URL in a jwplayer config | **Good fit** for spoken-word folklore under non-commercial use with credit, without edits. Flag the items on beliefs and ritual (e.g. #104 "Lords of the Mountains") for cultural review. Not music | verified |
| 2 | **"Memoria Musical de Bolivia: Colección Ajayus de Antaño" on Internet Archive** (Bolivia; IA uploader account of the Ajayus de Antaño 78 RPM Bolivia project, no separate collection id; items sit in `78rpm` / `audio_music`) | 109 Bolivian 78 rpm sides dated 1916–1950s: estudiantinas, cuecas, huayños, bailecitos, bandas, Raúl Shaw Moreno etc. (dates by decade: 1910s 28, 1920s 6, 1930s 33, 1940s 7, 1950s 35) | Direct MP3 + Ogg on archive.org, no login (ranged GET 206 audio/mpeg) | Per item: 86 CC BY-NC-ND 4.0, 1 CC BY-NC 4.0, 22 no licence field. Commercial 78s, so the underlying sound-recording rights are the uploader's claim | IA advancedsearch (`uploader:78rpmbolivia@gmail.com`), metadata API | **Good fit** for the 87 CC-licensed items (non-commercial, unmodified streaming with credit). The 22 unlicensed items are IA-default: treat like other Great 78-style uploads or ask the uploader via the IA item page | verified |
| 3 | **The Soule Archive: Folklife of St. Croix and St. Thomas** (University of the Virgin Islands, via dLOC). US Virgin Islands | ~124 audio items: quelbe, quadrille, calypso, cariso, folk songs, stories. Mostly MaryJane Soule's fieldwork 1977–89. **Only ~5 items fall in 1970 or earlier**: T147–T150 Marie Richards songs, recorded 1955 by Ray and Hazel Higdon (~31 MP3 tracks); T146 "Jost Van Dyke" and "Carnival in St. Thomas" (1968); T051 "Typical Virgin Islands Tunes" (dated 1964, but the note says it is a 1984 LP compilation, so the date needs checking) | **Direct MP3** on ufdcimages.uflib.ufl.edu, no login (206 audio/mpeg) | Blanket per-item statement: performers "consented to the deposit … available to the public for only educational and noncommercial purposes" | dLOC JSON API (exactsearch, citation, downloads), OAI-PMH. No key | **Good fit** (small, non-commercial). A courtesy note to dLOC/UVI is advisable: dloc@fiu.edu (address given in dLOC rights text) | verified |

### 1b. Ask permission (in priority order)

| # | Archive (country) | Content | Audio access | Rights | Machine access | Verdict | Status |
|---|---|---|---|---|---|---|---|
| 4 | **Biblioteca Nacional Aruba: Coleccion Ito Tromp** (IA collection `bnaitotromp`, with `bibliotecanacionalaruba`, `arubiana`, `musicaruba`). Aruba | 156 items, 86 of them audio. Ethnographic tapes 1967–75 (~78 items dated 1967–70) by Gregorio "Ito" Tromp: interviews in Papiamento, plus music. Examples: Dera Gay (San Juan harvest song) at Santa Fe, Pavia, 24 June 1969; Coby Angela "Den Kunuku – Tumba" 1969; Kantika di Pasku; Koor Colegio Arubano 1967; Hubert Booi songs and poems | **Direct MP3 on archive.org**, no login (206 audio/mpeg). Uploaded by the library itself (p.scholing@bibliotecanacional.aw) on 2026-10-26 | **No rights or licence field** on the Ito Tromp items. Other BNA/ANA uploads carry CC BY-NC-ND 4.0 or PDM | IA advancedsearch and metadata API | **Ask permission**: arubiana@bna.aw (Dept. Arubiana) or info@bibliotecanacional.aw, from https://www.bibliotecanacional.aw/pages/contact/ | verified |
| 5 | **Biblioteca Nacional de Panamá Ernesto J. Castillero R., Centro Audiovisual** (Panama) | Holds the full "Documental de Música Folklórica Panameña" by Gonzalo Brenes Candanedo and Manuel F. Zárate (1950s field recordings, Guaymí/Ngäbe, Kuna, mestizo mejorana, tamborito, salomas). Deposited 2001 as a non-commercial 85-CD set, "Colección Digital Gonzalo Brenes: Música Folclórica Panameña" | **On-site only**: "solo pueden ser utilizados dentro del recinto … cabinas individuales". A library card is required | Not stated | None (catalogue host bdpa.binal.ac.pa unreachable) | **Ask permission**: referencia@binal.ac.pa (on binal.ac.pa). High value, but it would need digitised files hosted somewhere | verified (access policy), search-only (holdings) |
| 6 | **MuseCom: Casa A Electrica collection** (Museu da Comunicação Social Hipólito José da Costa, Porto Alegre, RS, Brazil) | About 120–130 78-rpm discs from Casa A Electrica, the first record label in Rio Grande do Sul (c. 1913–24): gaúcho regional songs, cômicas, military bands, plus Spanish-language and German-Brazilian (kerb) items. Hardy Vedana collection. The "Casa Eléctrica Digital" project (2021) digitised the discs it could locate; 50 tracks were published on the project, and the rest were also digitised | The Brasiliana Museus aggregator holds only metadata and label images. MuseCom's own Tainacan site (acervos.musecom.rs.gov.br) returns **401** for pages and API. The project catalogue PDF returns 404. No public audio found | Per item, in Portuguese: content "disponível para reprodução, exibição e para a produção de trabalhos derivados dele, desde que sejam para fins não comerciais", with credit required. That is close to CC BY-NC in spirit. 1910s phonograms are almost certainly PD in Brazil | Brasiliana Museus Tainacan REST API (keyless, verified). MuseCom API 401 | **Ask permission** (best Brazilian lead): musecom@sedac.rs.gov.br. Ask for MP3 access or IA deposit | verified (metadata, rights field, 401). Audio not found |
| 7 | **Zikinzá Collection** (Paul Brenneker and Elis Juliana; NAAM; hosted on the University of Curaçao DCDP, SobekCM). Curaçao and Bonaire | 356 of ~1,400 field recordings online: songs (kantika, tambú, seú etc.), stories and interviews. Recorded in the 1950s–60s; some catalogue dates are 1974 (deposit date?). Clips of about 1–5 min | MP3 behind **Google Cloud signed URLs that expire after 1 hour** (206 audio/mpeg when fresh). No stable URL | "All rights reserved by the source institution." Also possible community/ritual sensitivity (tambú, seú) | SobekCM HTML. The OAI endpoint was not found (404) | **Ask permission**: info@naam.cw (from naam.cw). Rights holder NAAM. The Nationaal Archief Curaçao holds a related "Juliana-Brenneker" collection: nationaalarchiefcuracao@duck.com (as printed on nationaalarchief.cw/contact) | verified |
| 8 | **IAI Berlin, "Wax cylinder recordings from Latin America" (Berliner Phonogramm-Archiv CD series), Koch-Grünberg 1911–1913 volume** (Germany, for Venezuela/Brazil, Roraima–Orinoco) | 6 volumes (Argentina 1905–09, Brazil x2, Peru, Cora/Huichol, Tierra del Fuego). The Koch-Grünberg volume (PI 1925829081) covers the 1911–13 Roraima/Orinoco expedition: Taulipang, Arekuna, Makuxi, Yekuana and others, plus "Venezuelan folk songs" (catalogue VII WS 142 "Koch-Grünberg Brasilien II / Venezuela") | Goobi viewer only. Five tracks are explicitly withheld as "ethically sensitive". The IIIF manifest and METS return 403 "restricted due to access conditions". No direct audio URL found | Record says "Access restriction: all rights reserved" and "copyright … not evaluated". The DDB/SMB catalogue record says CC BY-SA 4.0 for metadata, but no audio is attached there | Goobi viewer (IIIF images 200; manifest, METS and plaintext 403) | **Ask permission**: Ethnologisches Museum em@smb.spk-berlin.de. The IAI has only a contact page: https://www.iai.spk-berlin.de/en/contact.html. Sacred and sensitive material must be screened | verified (pages); audio blocked |
| 9 | **Belize Archives and Records Service (BARS), Audiovisual Unit and new Belize Music Archive Lab** (Belize) | Its collections page lists "Folk Songs by Mahogany Cutters 1957", "Music by Local Artists 1957 to date", a "1931 Hurricane Sound Documentary", and Radio Belize news 1983–98. The music archive lab opened in 2026 (with NICH's Belize Music Project, Ivan Duran) | Nothing online. Plans mention future online access and a box set, "100 Years of Music in Belize" | Unknown. Government archive | None | **Ask permission**: info@archives.gov.bz (from https://archives.gov.bz/contact-us/). The 1957 mahogany-cutter songs are the target | verified (pages), search-only (holdings) |
| 10 | **IEB-USP (Instituto de Estudos Brasileiros)**, São Paulo, Brazil | Mário de Andrade's record collection. 1940 Lorenzo Turner disc (Mário de Andrade, Rachel de Queiroz and Mary Pedrosa singing folk songs from Ceará, Minas, Pernambuco and Paraíba, copy from Indiana ATM). Vozoteca (about 12,000 voices) | The 1940 disc's two MP3 links on the news page now return **404**. No streaming catalogue | Unknown. The Turner disc came via Indiana University ATM | HTML | Ask permission: arquivoieb@usp.br (archive), iebdigitaliza@usp.br (digitisation) | verified (pages, emails, 404) |
| 11 | **Biblioteca Digital de Venezuela "César Rengifo"** (Biblioteca Nacional de Venezuela) | Small "Audio" category of about 15 items: speeches (Andrés Eloy Blanco), "Memorias de la Música Latina en Venezuela" interviews (modern), "Chicha Maya: folklore de la Guajira venezolana" LP. Little folk music before 1971 | Direct MP3/WAV in WordPress uploads (ranged GET 206 audio/mpeg) | "Derechos: Instituto Autónomo Biblioteca Nacional…". The site says it publishes only content free of copyright or authorised by the creators. No licence named | HTML only (WordPress; REST /wp-json 404); category feed | Ask permission (small yield): atencion.alciudadano@bnv.gob.ve, orientacionyreferencia@bnv.gob.ve | verified (one item fetched; second visit got connection resets) |
| 12 | **MUSEF "Territorios Sonoros"** (Museo Nacional de Etnografía y Folklore, La Paz, Bolivia) | 3,494 geo-tagged clips from the MUSEF cassette archive (AC-MUSEF/OS/CAS-…): Aymara, Quechua, Tacana and other festival music, pinkillos, sikus, cantos. No recording dates given. The cassette series is mostly from the 1980s–90s ("Misiones de urgencia"), so likely post-1970 | Direct M4A clips (/sonidos-web/…m4a, ranged GET 206 audio/x-m4a), no login | Site footer "Todos los derechos reservados". No item rights. Indigenous ritual music present | Data embedded as JSON in the page (Nuxt payload, paginated: total 3494, 699 pages) | Ask permission, but only if they can identify pre-1971 recordings, which looks unlikely: info@musef.gob.bo | verified |
| 13 | **Funarte / CNFCP (Museu de Folclore Edison Carneiro)** (RJ) | CNFCP: about 70,000 audiovisual documents, *Documentário Sonoro do Folclore Brasileiro* (Théo Brandão and others). Funarte "Brasil Memória das Artes" sound archive, digitised from 2004 | Funarte Tainacan (desenvolvimento.funarte.gov.br) returns 502 to curl and 403 to WebFetch | Unknown (CREM marks the Documentário Sonoro LPs "restreint", per the first pass) | Tainacan (blocked) | Ask permission (rerun when reachable) | blocked |
| 14 | **Archivo Oswaldo Lares (ArchivOlares)** (Venezuela, family archive) | More than 1,000 rural folk tracks recorded around Venezuela, 1960–1980s (tambor, fulía, parranda, golpe). Mostly 1969 and later | SoundCloud embeds only. Commercial CD/LP on TAL label | Family-held; commercially released | None (WordPress page) | Ask permission (only for 1960s field tapes): archivolares@gmail.com | verified (site); content search-only |
| 15 | **Folk Research Centre (Msgr. Patrick Anthony FRC)**, Saint Lucia | Site has a "Digital Archive > Traditional Music/Traditional Audio" section; contents not visible (JS-rendered, no audio links in HTML). Also a YouTube channel | Not determinable | © FRC | HTML | Ask permission (low priority): programmesassistant@folkresearchcentre.org or folkresearch.assist@gmail.com (from folkresearchcentre.org/contact) | verified (site), contents unverified |
| 16 | **Radio Educación fonoteca**, Mexico | More than 140,000 phonographic items: music, radio drama, interviews, historical commemorations. Slow digitisation (about 36% by 2008) | No public online audio found | Unknown. Radio Educación authorised public use of its share of the Montejano holdings (Crónica, search-only) | – | Ask permission (low priority). Website has an expired TLS certificate | blocked. Search-only |
| 17 | **Biblioteca Pública del Estado de Jalisco "Juan José Arreola"** (UdeG): Fondo Francisco Navarro and Radio UdeG archive | About 16,000 discs (sones, huapangos, corridos, rancheras, danzones). Radio Universidad de Guadalajara archive | Not confirmed online | Unknown | – | Ask permission (low priority) | search-only. bpej.udg.mx has a TLS failure |
| 18 | **Fonoteca Adda Navarrete** (Escuela Superior de Artes de Yucatán) | Yucatán's first public fonoteca: trova yucateca, regional music | Not online | Unknown | – | Ask permission (low priority). esay.edu.mx returns 403 | search-only / blocked |
| 19 | **Discoteca L. C. Vinholes (UFPel)**, Pelotas, RS, Brazil | About 26,000 items incl. about 6,000 78s (1930s–50s). Digitisation project reported (ANPPOM 2019) | Unknown | Unknown | – | Ask permission (low priority) | blocked (403). Search-only |
| 20 | **Fundação Joaquim Nabuco / Villa Digital** (Recife, PE) | Northeast documentation. No sound collection found online | – | – | – | Ask (low priority): sic@fundaj.gov.br (from search) | blocked (connection reset) |
| 21 | **EBC / Rádio Nacional archive** (and MIS-RJ, which holds part of it) | Rádio Nacional programmes since 1936 (UNESCO MoW 2018) | No online catalogue found. memoria.ebc.com.br returns 502. mis.rj.gov.br is a new-building teaser only | Unknown | – | Ask permission (low priority) | blocked / verified (MIS-RJ page) |
| 22 | **CECEG (ex-Centro de Estudios Folklóricos), USAC** (Guatemala) | A 1977 bulletin mentions a cintateca (tape library) of musical recordings. Phonograph recordings of San Pedro Chuarrancho ensembles are mentioned | Nothing online. ceceg.usac.edu.gt reset the connection | – | – | **Ask permission** (low priority). No public email was retrieved, because the host was unreachable | blocked / search-only |
| 23 | **Världskulturmuseerna (Etnografiska museet) Carlotta database** (Sweden; Nordenskiöld Chaco) | The "Ljud" record type exists (43 hits for "fonograf": Bolinder Colombia, Karl Tirén, Haslund-Christensen 78s). No Nordenskiöld or Chaco sound records found ("Chaco ljud", "Nordenskiöld ljud", "fonografrulle", "vaxrulle"). A 2024 wax-cylinder condition report exists | No audio player seen | Unknown | Carlotta HTML search (session-form based) | Ask permission only (nothing online): carlotta@etnografiska.se (address shown in the Carlotta footer) | verified (catalogue); audio not found |

### 1c. Not usable (or not reachable)

| # | Archive (country) | Content | Audio access | Rights | Machine access | Verdict | Status |
|---|---|---|---|---|---|---|---|
| 24 | **SINABI Fonoteca Virtual de Costa Rica** (Biblioteca Nacional, Costa Rica) | 444 "fragmentos" (launched 2011): Guanacaste marimba and quijongo, Valle Central, Afro-Limón calypso and quadrilles, Indigenous (Boruca, Cabécar, Chorotega, Guaymí, Maleku, Térraba), San Carlos, bands. Sources are mostly LPs and CDs from the 1980s–2000s (CBS 198x, MCJD 1982 *Música popular afrolimonense*, INDICA 1983). Some undated "Música folklórica costarricense" tracks from Roberto Le Frank | Direct MP3s with no login (206 audio/mpeg), but these are clips of about 0.8–1.4 MB | Site footer reads "Derechos reservados". The tracks are commercial releases | HTML pages only, with MP3 paths in the page source. No API | **Not usable**: clips of mostly post-1970 commercial releases, all rights reserved | verified |
| 25 | **Archivo Mesoamericano** (Indiana University, Avalon) with MUPI (El Salvador), IHNCA (Nicaragua) and CIESAS (Mexico) | 346 items: MUPI 209 (Radio Venceremos/Sistema Radio Venceremos productions), IHNCA 98, CIESAS 39. Mostly 1983–1987 | Avalon streaming player. Every item has resource type "moving image" | Per item (not inspected) | Avalon JSON (`/catalog.json`, `/collections.json`), no key | **Not usable**: all video, 1980s, civil-war documentary | verified |
| 26 | **MUPI: Museo de la Palabra y la Imagen** (El Salvador) | Radio Venceremos complete audio (1981–92), Fondo de Músicos Vicentinos (700+ manuscript scores, 18th–20th c.), oral testimonies | No audio catalogue online. museo.com.sv answers, but its pages are **injected with casino spam** (the site looks compromised) | – | – | **Not usable**: no pre-1970 folk audio found online. Material is post-1980 or scores | verified (site), search-only (holdings) |
| 27 | **UW Ethnomusicology Archives, Robert Garfias field collection** (USA) | Guatemala marimba 1972 (72-10.x), Masaya, Nicaragua 1975 (75-23.x) | Logsheet PDFs only. Audio is on-site | – | Static PDFs | **Not usable**: after 1970 and not online | search-only |
| 28 | **CIRMA, Antigua Guatemala** | Three holdings: Biblioteca, Fototeca, Archivo Histórico. No fonoteca or sound collection is named. Colonial music material is scores on microfilm | Catalogue sits behind a shared login printed on the homepage (user and password published). No audio found | – | React app | **Not usable**: no sound archive found | verified (homepage), search-only |
| 29 | **SINART / Radio Nacional and Archivo Nacional de Costa Rica** | Archivo Nacional holds SINART fonds and an audiovisual fonds. Archivodigital.go.cr has a catalogue of the "Quien canta su mal espanta" series of music-history interviews | radionacional.go.cr: proxy 502. sinart.go.cr: 403. archivodigital.go.cr: Cloudflare "Just a moment" bot check | – | – | **Not usable** now: blocked, and holdings are mostly post-1970 | blocked |
| 30 | **IHNCA-UCA / FONOMUSIC** (Nicaragua) | Fondo Histórico Documental de la Música Nicaragüense (FONOMUSIC) is listed by IAML. The UCA and its archives were confiscated in 2023 | ihnca.edu.ni: proxy 502 and DNS failure | – | – | **Not usable**: offline. Institutional status unclear | blocked |
| 31 | **Casa K'ojom** (Guatemala) | Maya music centre (Samuel Franco). An old "archivos" page used to offer downloads | kojom.org returns "Page cannot be displayed". casakojom.com: proxy 502 | – | – | **Not usable**: site dead | verified (dead) |
| 32 | **IHAH** (Honduras) | Heritage institute. No sound archive found | Nothing | – | – | **Not usable**: nothing online. Contact info@ihah.hn if ever needed | verified (site), search-only |
| 33 | **UNAH Cinemateca "Enrique Ponce Garay", Audioteca** (Honduras) | Vinyl, cassettes, CDs. Founded 2014 | On-site | – | – | **Not usable**: nothing online. Contents unknown | search-only |
| 34 | **Fundación Afrodisíaco: Dora & Manuel Zárate archive** (Panama) | About 300 cassettes of 1950s–70s field interviews and music. Digitisation project 2019 | No digital archive published (as of the 2019 press) | – | – | **Not usable** yet. Watch | search-only |
| 35 | **Biblioteca Digital ABNB** (Archivo y Biblioteca Nacionales de Bolivia, Sucre) | Books, manuscripts, press. Recordings from the 1903 Créqui-Montfort mission were repatriated from France in Nov 2024 (search snippet), but none are seen online | Unknown | Unknown | Omeka S; /api blocked by Cloudflare challenge (403) | Not usable now (bot check; no audio seen). Revisit later | blocked |
| 36 | **Radio Nacional del Paraguay fonoteca** (via Casa Bicentenario de la Música) | 500–700 recovered discs (shellac, 33, 45), being catalogued since 2022–25 | Not online yet. Planned portal will give 30-s demos only, with full sides on request for research | Unknown | None | Not usable (not online; planned demos only). Re-check later | search-only |
| 37 | **Portal Guaraní** (Paraguay, private cultural portal) | Large set of Paraguayan polcas and guaranias, many old recordings reissued on compilations (for example "Sonidos de Ayer", Félix Pérez Cardozo with Eladio Martínez) | Obfuscated `veraudio.php?file=<base64>` MP3 with `controlslist=nodownload` (206 audio/mpeg) | "Todos los derechos reservados". Tracks are taken from commercial compilations | HTML only | Not usable (all rights reserved; re-hosted commercial compilations) | verified |
| 38 | **Scenkonstmuseet** (Sweden; Ayestarán copies) | Site search for "Ayestarán" returns nothing. The holding of Ayestarán copies is not confirmed by any source | None | – | – | Not usable (no trace online). Ask only if CDM Uruguay outreach fails: info@scenkonstmuseet.se | verified (site search) |
| 39 | **Fundación Bigott** (Venezuela) | Traditional culture workshops and publications. A YouTube channel. No sound archive online | YouTube only | – | – | Not usable (no archive online). The public address is fundacion_bigott@bat.com if wanted | verified (homepage) |
| 40 | **Fondo Isabel Aretz, UNTREF archive** (Argentina, relevant to Venezuela via Aretz) | AtoM descriptions of Aretz's instantaneous discs (cuecas, vidalas, La Rioja etc.) | Only PDF scans of sleeves and index cards. No audio | – | AtoM HTML | Not usable (no audio online) | verified |
| 41 | **Beeld en Geluid / openbeelden.nl** (Netherlands, for Suriname) | Through Europeana, only 6 Suriname-related open sound items, all Radio Oranje / Herrijzend Nederland speeches from 1941–45 (PD Mark). No music | openbeelden.nl unreachable (connection reset). zoeken.beeldengeluid.nl is a catalogue JS app | PD Mark on those 6 | Europeana API (known) | Not usable for music | blocked (openbeelden); Europeana verified |
| 42 | **Nationaal Archief Suriname** | Paper archives, genealogy. A cooperation with Beeld en Geluid on AV material is announced, but no audio is online | None | – | – | Not usable (no audio online): info@nationaalarchief.sr | verified |
| 43 | **Caife label archive (Quixosis / Daniel Lofredo Rota)** (Ecuador) | About 300 master tapes, Quito 1960–68: sanjuanitos, pasillos, Andean ensembles | Commercial reissues (Honest Jon's) | Commercial, rights held | – | Not usable (commercial) | search-only |
| 44 | **Leiden Digital Collections / KITLV** (Netherlands, for Suriname) | The Caribbean collection is said to include audio | Host unreachable (connection reset) | – | – | Blocked: re-check later | blocked |
| 45 | **Archives territoriales de Guyane / Musée des Cultures Guyanaises / MCMG** (French Guiana) | Paper archives. The MCMG offers on-site INA access | On-site only | – | – | Not usable (on-site; hosts unreachable) | blocked / search-only |
| 46 | **Archivo Nacional Aruba** on IA (`archivo-nacional-aruba`) | 13 audio items, nearly all government recordings from 1978 on. One early item: tumba "Den Temp'i guera ki" (1939 song by Padu Lampe; recording date unclear). National anthem 1978 (PDM) | Direct MP3 on IA | Mixed: CC BY-NC-ND 4.0, PDM, or none | IA API | Not usable as a source (almost nothing ≤1970). Fold the one tumba item into the BNA request | verified |
| 47 | **dLOC: Radio Haiti Archive mirror** (Duke, via dLOC). Haiti | 1,808 Radio Haïti programmes. **Only 5 are dated ≤1970**: 1957 election speeches, a 1962 writers' evening, a 1966 Roger Gaillard interview, a 1970 Malraux interview. All spoken word | **Direct MP3** via dLOC (ufdcimages, 206). This sidesteps the Duke Anubis check through UF's own mirror, which is the archive's public copy | CC BY-NC-SA 4.0 per item, with a caveat about third-party content | dLOC API, OAI | Not usable (spoken word, not folk music). The rights and access model is good if the scope ever widens | verified |
| 48 | **dLOC: Universidad del Aire (CMQ radio lectures)** (UCLA via dLOC). Cuba | 210 radio lecture programmes, 1949–60 (Jorge Mañach and others) | dLOC app. No files listed in the citation for the sample checked | dLOC blanket "may or may not be protected by copyright" | dLOC API | Not usable (spoken lectures, rights unclear) | verified |
| 49 | **Cabrera–Tarafa Collection: *Música de los cultos africanos en Cuba*** (UM Cuban Heritage Collection; "Beyond the Archive", CUNY Manifold). Cuba | 14 LPs, 11+ hours of Afro-Cuban ritual music (Lucumí/Santería, Abakuá, Arará, Palo), Matanzas and Havana, c. 1956 | UM portal hosts unreachable. The Manifold project (reachable, API 200) has only 13 resources (pdf/image/audio samples) | Ritual/sacred. Folkways reissue is commercial | Manifold JSON API | **Not usable**: sacred/initiatory repertoire, UM host blocked | verified (Manifold), search-only (UM), blocked |
| 50 | **Centro León: Archivo Sonoro Fradique Lizardo** (Fondo Fradique Lizardo del Folklore, UNESCO MoW LAC). Dominican Republic | 700+ field recordings (salves, atabales, accordion merengue/carabiné, palos), 481 catalogued. The volumes checked were **recorded 1975–77** | **SoundCloud sets only** (Vols I–V); on-site Mediateca | "© Centro Cultural Eduardo León Jimenes. Todos los derechos reservados." | HTML / SoundCloud | **Not usable** (mostly post-1970, SoundCloud only, all rights reserved). If pre-1971 Lizardo tapes exist, ask: centroleon@centroleon.org.do (from centroleon.org.do/mediateca) | verified |
| 51 | **CICAM, EGREM archive**, Cuba | Phonograms from the 1940s on (tapes, acetates, vinyl). Partly digitised, no public online access described | None online found | Commercial label holdings | None | Not usable (no online access, commercial). Host blocked | search-only, blocked |
| 52 | **Museo Nacional de la Música / Biblioteca Musical y Archivo de Sonido (Odilio Urfé)**, CIDMUC, Biblioteca Nacional José Martí, Fundación Fernando Ortiz, Casa de las Américas, Radio Progreso/ICRT, Cuba | National holdings exist (EcuRed, Cubanet); no online listening found | None found | Unknown | None | Not usable now. All hosts unreachable from sandbox | blocked, search-only |
| 53 | **Archives territoriales de Martinique** (patrimoines-martinique.org, Ligeo) | Civil-status, slavery and photo collections. The "Archives numérisées" page lists no sound material | None | – | HTML | Not usable (no audio found). Contact via https://www.patrimoines-martinique.org/page/contactez-nous | verified |
| 54 | **Archives départementales de la Guadeloupe** (archivesguadeloupe.fr reachable; archives.guadeloupe.fr blocked) | Home page mentions only civil-register digitisation | None found | – | HTML | Not usable (no audio found) | verified (homepage) |
| 55 | **Lameca: Médiathèque Caraïbe** (Basse-Terre), Guadeloupe | Official repository of the ACE Lomax 1962 French Antilles repatriation (30 h; known via ACE). Biguine dossiers | Unknown | Unknown | Unknown | Unverified: host reset connections | blocked |
| 56 | **Brasiliana Museus** (IBRAM federal museum aggregator, Brazil) | Aggregates the Tainacan catalogues of IBRAM and adhering museums. Sound items found: Museu Forte Defensor Perpétuo (Paraty, RJ) "Acervo sonoro e audiovisual", e.g. Chiba Cateretê field recording **1976**; "O Modo de Fazer" interviews **1983**. Also MuseCom 78 metadata (132 hits for "78 rotações") | Direct MP3 on wp-content (HTTP 206 audio/mpeg tested). No login | Per-item "Condições de reprodução" field, which is often empty. No blanket licence seen | Tainacan REST API `/wp-json/tainacan/v2/items/?search=…` and `/wp/v2/media/<id>`, keyless, with X-WP-Total paging (verified) | Not usable now (sound items found are after 1970). **Monitor**: a good harvesting point if pre-1970 museum audio is added. Contact: cgsim@museus.gov.br | verified |
| 57 | **Museu Afro-Digital (UFBA): Lorenzo Dow Turner collection 1940–41**, Bahia, Brazil | 121 audio items: Turner's 1940–41 recordings in Salvador and Cachoeira. Interviews and songs with candomblé leaders (Martiniano Eliseu do Bonfim, Joãozinho da Goméia, Sr. Falefá…), incl. "Cantiga de Oxossi" | Direct MP3 (tested 206 audio/mpeg, e.g. `/wp-content/uploads/tainacan-items/6776/8205/C-44.mp3`) | Site footer: "Todos os direitos reservados". Items say "Acervo da Fundação Lorenzo Dow Turner". Rights field empty. **Sacred candomblé material** | Tainacan REST API, keyless (verified) | **Not usable** (all rights reserved, and sacred/religious content needs community consent). If pursued at all: afrodigital@ufba.br together with the Turner family/foundation, with a candomblé community review | verified |
| 58 | **El Colegio de México: Repositorio, Fonoteca del CELL / Seminario de Tradiciones Populares**, Mexico | 890 audio items, about 879 from the Seminario: son jarocho (342), décimas, coplas, canción tradicional, topadas. Places: Tlacotalpan, Santiago and San Andrés Tuxtla, Tres Zapotes (Veracruz), Cárdenas (San Luis Potosí). **Dates 1981–1996** (plus 2003 lectures) | Public MP3 download (`/downloads/<id>`, tested 206 audio/mpeg). No login | Footer: "Licensed under the Creative Commons Attribution 4.0" unless otherwise indicated | Hyrax/Blacklight `catalog.json` facets and search (verified). Handles | **Not usable now** (everything is after 1970). **Monitor**: if CELL deposits older Seminario or Vicente T. Mendoza material, it would be a clean good fit | verified |
| 59 | **Memórica** (AGN, memoricamexico.gob.mx), Mexico | National aggregator, mostly images and texts. The audio facet is tiny: 15 audio hits for "corrido", 9 for "cilindro", 1 for "huapango". Collaborators include state fototecas, CONABIO, UNAM | Not confirmed (no audio item opened) | Per collaborator: "Derechos reservados" or a CC licence chosen by the collaborator (Licencias de uso page, verified) | HTML search with facets. WebFetch gets 403, curl gets 200 | Not usable (negligible pre-1970 folk audio found). Low priority | verified (pages), audio search-only |
| 60 | **Fonoteca de la Pirekua** (UNAM ENES Morelia / LANMO), Michoacán, Mexico | Pirekua recordings of duets and groups in about 11 Purépecha localities (Arantepacua, Ihuatzio, Santa Fe de la Laguna…), recorded 2022 | Web player, per-item PDF documentation | Not stated | HTML | Not usable (contemporary recordings) | verified |
| 61 | **Descarga Cultura.UNAM**, Mexico | About 1,350 titles: readings, lectures, OFUNAM concerts, history talks. No pre-1970 folk/field recordings found | MP3 file list embedded in each album page (`data-files`) | Gaceta UNAM reports that each item was given a CC licence when moved into the UNAM repository (search-only) | HTML. No search API | Not usable (modern productions, not folk) | verified (site), search-only (licences) |
| 62 | **UCLA MEAP: Ricardo Montejano collection** (recheck), Mexico | 3,300 recordings of social movements and Indigenous/rural culture, **recorded from 1970 onwards**. Includes "sacred indigenous ceremonies". Host: Fonoteca Nacional | UCLA Digital Library item: TLS failure (curl), 503 (WebFetch) | No rights statement found | Unknown | **Not usable** (after the cut-off, with sacred content) | MEAP page verified. DL blocked |
| 63 | **Radio UNAM / Voz Viva de México**, Mexico | Radio UNAM's Fonoteca Alejandro Arias holds folklore programmes. Voz Viva is literary and political voices (UNESCO MoW) | Voz Viva online (literature, not music) | Unknown | – | Not usable (not folk music). Radio UNAM programmes would need permission | search-only. radiounam.unam.mx connection failed |
| 64 | **Fonoteca Juan León Mariscal** (Fundación Alfredo Harp Helú Oaxaca) | Oaxacan music on LP, CD and cassette, plus an on-site terminal for the Fonoteca Nacional | The FAHHO DSpace community has 1 item (a video) | FAHHO DSpace items are CC BY-NC-ND 2.5 MX | DSpace (OAI endpoint answers 200) | Not usable (no audio online, mostly commercial records) | verified |
| 65 | **MIS-SP (Museu da Imagem e do Som de São Paulo)** | 5,545 audio/disc records, e.g. Miécio Caffé collection of 78s (sambas and marchas, 1940s), oral history | Catalogue only: "disponível apenas para consulta local" | "É vedada a reprodução…" (Lei 9.610) | Drupal HTML | **Not usable** (on-site only, reproduction prohibited) | verified |
| 66 | **Arquivo Nacional (SIAN)**, Brazil | Fundo Agência Nacional sound: about 508 discs and 316 tapes, mainly speeches | No online audio confirmed | Access restrictions per SIAN | SIAN HTML | Not usable (speeches, not folk) | search-only (sian.an.gov.br answers 200) |
| 67 | **IA user uploads of INAH LPs** (uploader "culturaaportesmex", e.g. `musica-huasteca-1969`, `corrido-de-la-toma-de-zacatecas-1914-fonoteca-inah-16`) | About 36 items from *Testimonio Musical de México* / Fonoteca INAH series | Direct MP3 | No licence. INAH holds the rights | IA API | **Not usable** (unauthorised uploads). They do show which INAH volumes exist, for an INAH request | verified |
| 68 | **IA user uploads of Brazilian LPs** (`hifidelity_potpourri`, `opensource_audio`) | 103 hits dated 1890–1970 for Brazilian terms: Luiz Gonzaga, Ely Camargo folklore LPs, bossa nova. Some carry an obviously wrong CC0 | Direct MP3 | In copyright. Uploader-applied licences are not credible | IA API | **Not usable** | verified |

### 1d. Known archives: new sub-collections and mirrors

These belong to archives on the brief's "already known" list (Internet Archive/Great 78, Library of Congress, CREM, Lomax/ACE, Europeana). They are listed for de-duplication and filtering, not as new sources.

| Archive (country) | Content | Audio access | Rights | Machine access | Verdict | Status |
|---|---|---|---|---|---|---|
| **Hessel Digital Library mirror of LoC National Jukebox on IA** (`hessel-digital-library`) (new IA sub-collection, covers Venezuela, Colombia, Ecuador repertoire) | 29,071 items in total, mirrored from the LoC National Jukebox (known). Includes c. 15 Venezuelan Victor sides from 1917–1920 (joropos and valses by Lionel Belasco, O. M. Arrieta etc.) and many Spanish-language pasillos and valses from 1907–1917 | Direct MP3 on archive.org | PD Mark 1.0, with the LoC rights text copied verbatim | IA APIs | Good fit, but it duplicates LoC (known). Prefer streaming the LoC original. Use the IA ids only as a discovery index for Venezuelan and Ecuadorian titles | verified |
| **Hessel Digital Library** (IA `hessel-digital-library`) | 29,071 items mirroring the LoC National Jukebox 78s (1900s–20s). Includes ~18 tagged "spanish (cuba)" and other Cuban/Caribbean items | Direct MP3 on IA | PDM on every item (copied from LoC) | IA API | Mirror of a known source (LoC). Use only as a fallback CDN | verified |
| **IA `hessel-digital-library`** (new IA mirror) | 29,071 items, of which 1,694 are 78rpm: a mirror of LoC National Jukebox items with verbatim rights statements. 574 items match Mexican terms | Direct files on IA | PDM 1.0 on every item sampled (1,999 of 2,000) | IA API | Good fit but **a duplicate of a known source** (LoC). Use only to dedupe against LoC | verified |
| **CREM-CNRS sub-collections for Central America** (France, known archive) | Panama: CNRSMH_I_1960_003, Zárate's 1955 selection from the Brenes/Zárate Documental (17 items, Guaymí and Kuna, unpublished). Guatemala: Lehmann 1954 45s (CNRSMH_E_1958_012_001/002, Maya and Mam sung prayers). Panama: Blaise 1953 Vogue LP | Zárate: "Access type full", so it plays on the CREM site. Lehmann 45s: access full. Blaise: metadata only | Zárate: "Copie interdite". Lehmann: "Restreint (enregistrement édité)". Mam prayers may be sacred | Known (Telemeta, OAI) | **Ask permission** (CREM, known source). The Zárate set is the only copy of the Panama corpus that can be heard online. Exclude or flag the Mam prayers | verified |
| **IA Great 78 sub-collections with Mexican and Brazilian content** (`78rpm_bowling_green`, `78rpm_custis-family`, `78rpm_leif_druedahl`, `78rpm_bostonpubliclibrary`, `78rpm_chomowicz_ready`) | 78rpm items matching Mexican terms: about 1,272 (incl. 107 huapango/son/jarabe, 99 corrido, 8 Guty Cárdenas). Brazilian terms: about 605 (samba, choro, maxixe, baião…), with Bowling Green 289 and Druedahl 97 | Direct MP3 | Same as Great 78 (known) | IA API | Known source. Listed only as useful query filters | verified |
| **Internet Archive** (no new country collections) | Only items already known: Great 78 (`78rpm`, `georgeblood`; Hurtado Bros. Royal Marimba Band of Guatemala 1916–46, "Central American Marimba Band", Marimba "La Centro Americana" 1924, "Tamborito (Panama)" Maria Cruz) and `hessel-digital-library` (National Jukebox mirror). Community uploads: `MarimbaCentroamericana-MoralesLopezAguasDormidas` (1929, PDM by uploader), `hojita-de-tamarindo` (Tobías Plicet 1957 LP, in `hifidelity`, rights doubtful), `el-carbonero-el-salvador` (1930, CC BY-ND by uploader, doubtful) | Direct files | Uploader-asserted | IA API | Known (Great 78). Community uploads: not usable unless verified | verified |
| IA: Cuban / merengue / Haitian **LPs and 78s** in `unlockedrecordings`, `album_recordings`, `georgeblood` (Great 78) | e.g. Ti Roro "Best Drummer in Haiti" (1956), "Cérémonie Vaudou en Haïti" (1968), Al Castellanos merengues (1955–56), Cuarteto Matamoros 78s | Lending or stream-only for LPs. Great 78 is known | In copyright (LPs). Great 78 is already covered | IA API | Not usable (LPs) / known (Great 78) | verified |
| **dLOC: "Alan Lomax in Haiti"** (Vodou Archive) | 1936 Lomax Haiti (the ACE/LoC content is already known) | No files exposed | "All rights reserved by the submitter" | dLOC API | Not usable here; use the known LoC/ACE route | verified |
| **Phonobase** (phonobase.org, France; already reaches users via Europeana) | French 78s and cylinders with transfers. Includes Antilles-themed titles (e.g. "À la Martinique", 1912). Biguine coverage unknown (search is JS-driven) | Direct MP3 path in page (`/audios/AD-2013/2013_7037.mp3`) | Not checked | HTML | Lead for the Europe sweep; covered through Europeana | verified (page) |

---

## 2. Country checklist

### Central America

| Country | One-line result |
|---|---|
| Guatemala | No open national sound archive. CIRMA has no fonoteca. CECEG/USAC unreachable. AGCA and MCD hosts blocked. Casa K'ojom dead. New good fit: **UNM K'iche' Maya Oral History Project** (spoken narratives, CC BY-NC-ND, 1968–73). Music only in known sources (Great 78 Hurtado Bros., CREM Lehmann 1954, LoC). |
| Belize | Nothing streamable online. **BARS** holds "Folk Songs by Mahogany Cutters 1957", and a music archive lab opened in 2026. **Ask** info@archives.gov.bz. NICH Belize Music Project plans public access. Garifuna and Kriol material online is post-1970 or in dLOC (known, blocked). |
| Honduras | Nothing found online. IHAH has no sound archive. The UNAH Audioteca is on-site. Garifuna material only in known sources (Folkways/LoC, CREM 2000). |
| El Salvador | Nothing pre-1970 online. MUPI audio is Radio Venceremos (1981–92), and its site is spam-compromised. MUPI video is on Archivo Mesoamericano. CCESV has no sound archive found. |
| Nicaragua | Nothing found online. IHNCA/FONOMUSIC offline after the UCA confiscation (host blocked). Biblioteca Enrique Bolaños has no audio found. Marimba de arco recordings exist only post-1975 (UW, Scruggs). |
| Costa Rica | SINABI Fonoteca Virtual has direct MP3s, but they are clips of mostly post-1980 commercial releases, "Derechos reservados" (not usable). UCR AHM and SINART/Radio Nacional hosts are unreachable. archivodigital.go.cr has a bot check. |
| Panama | Key corpus is the Brenes/Zárate *Documental de Música Folklórica Panameña* (1950s): full set on-site at BN Panamá (**ask** referencia@binal.ac.pa), a 17-item selection at CREM ("copie interdite"; **ask** CREM). SERTV host unreachable. Afrodisíaco Zárate project has nothing published. |
| US/European holders | UNM (good fit, speech). CREM sub-collections (ask). Archivo Mesoamericano (video, not usable). UW Ethnomusicology (post-1970, on-site). LoC, Smithsonian, Lomax and AILLA are known. Världskulturmuseerna (Kuna) and Berlin: nothing found in one search. |

### South America and European holders

| Country | Result |
|---|---|
| Venezuela | BNV Biblioteca Digital has a few direct MP3s (speeches, interviews, one Guajira LP): ask, low yield. ArchivOlares (1960–80s field recordings, SoundCloud): ask. Fundación Bigott, FUNDEF/Centro de la Diversidad Cultural, Radio Nacional (rnv.gob.ve) and Fundación Isabel Aretz/Ramón y Rivera: nothing online (cdc.gob.ve and rnv.gob.ve unreachable). The best open source is Venezuelan Victor 1917–20 sides via LoC Jukebox (known; IA mirror `hessel-digital-library`). Koch-Grünberg 1911–13 is at IAI Berlin (ask). |
| Ecuador | Nothing found online. BN Eugenio Espejo and Casa de la Cultura hosts unreachable. FLACSO repository returns 403. PUCE repository reachable but has no folk audio. Banco Central/Ministerio fondo, HCJB archive and Cinemateca: no online audio. Caife tapes are commercial only. A few pasillos are in LoC Jukebox (known). |
| Bolivia | **Good fit:** 87 CC BY-NC-ND/BY-NC Bolivian 78s (1916–1950s) on IA from Ajayus de Antaño. MUSEF Territorios Sonoros has 3,494 direct M4A clips, but they are likely post-1970 and all rights reserved (ask). ABNB digital library is behind a Cloudflare check. Fundación Patiño host unreachable. CEDOAL and Radio Illimani: nothing found online. |
| Paraguay | Nothing usable online. Biblioteca Nacional host resets connections. Museo Barbero site has no audio. Archivo Nacional has no audio. The Radio Nacional fonoteca is still being catalogued (portal planned, demos only). Portal Guaraní is all rights reserved. There are almost no Paraguayan 78s on IA. |
| Guyana | Nothing found online. nationalarchives.gov.gy returns 403 and nalis.gov.gy is unreachable. No GBC/NCN archive online. UH ScholarSpace Guyanese Creole audio is behind a bot check. The Audrey Butt 1960–63 recordings are at CREM (known; restricted). |
| Suriname | Nothing found online. Nationaal Archief Suriname has no audio. Beeld en Geluid: openbeelden is unreachable and Europeana shows only 1940s speeches. Delpher has radio bulletins as text only. Leiden/KITLV is unreachable. The Wereldmuseum collection search is a JS app with no audio found. Herskovits 1928–29 is at Indiana (known, on-site). |
| French Guiana | Nothing new online. Archives territoriales and Musée des Cultures Guyanaises hosts are unreachable. MCMG offers INA access on site only. CREM (known) holds Mazière 1952 and Hurault 1957–65 Boni/Wayana recordings. |
| European holders | Berlin: the Koch-Grünberg 1911–13 CD volume is online in the IAI viewer, but restricted ("all rights reserved", some tracks withheld): ask em@smb.spk-berlin.de. SMB-digital record VII WS 142 has no audio. Världskulturmuseerna: no Nordenskiöld/Chaco audio online. Scenkonstmuseet: no Ayestarán trace. |

### Caribbean

| Country | Result |
|---|---|
| Cuba | Every Cuban institutional host is unreachable (BNJM, Museo de la Música, CIDMUC, EGREM, F. Ortiz, Casa de las Américas, ICRT). No online listening found by search. US holdings: UM Cabrera–Tarafa is sacred (not usable) and UM hosts are blocked. dLOC Universidad del Aire is spoken word. IA has only the Great 78 (known) and Hessel (LoC mirror). |
| Dominican Republic | AGN and BN-PHU return 403. Museo del Hombre Dominicano is unreachable. Centro León Fradique Lizardo sound archive is SoundCloud-only, mostly 1975–77, all rights reserved (not usable). Merengue 78s are only in Frontera/Great 78 (known). Nothing else found online. |
| Haiti | BnH and ANH are blocked. FOKAL and CIDIHCA are reachable but show no audio archive. The Radio Haiti audio is now reachable through dLOC (CC BY-NC-SA, direct MP3), but only 5 pre-1971 items, all spoken word. Lomax 1936 and CREM Courlander are known. Nothing found online in French or Kreyòl. |
| Martinique | Archives territoriales (patrimoines-martinique.org) has no audio. Bibliothèque Schoelcher is unreachable. Nothing found online beyond known CREM/ACE/Gallica. |
| Guadeloupe | AD Guadeloupe has no audio online. Lameca is blocked (holds the Lomax 1962 copies). Nothing found online beyond known ACE. |
| Bahamas | Department of Archives is unreachable. University of The Bahamas is reachable but has no audio collection found. ZNS: nothing found online. Lomax 1935 is known (LoC). Florida Memory (Bahamian-Floridian folklife) is behind a Cloudflare bot check. |
| Barbados | Barbados Archives host blocked. Cave Hill: no audio collection found. Nothing found online. |
| Saint Lucia | Folk Research Centre has a "Traditional Audio" page with no visible content (ask, low priority). Lomax 1962 is known. |
| Grenada, St Vincent, Antigua, Dominica, St Kitts and Nevis | Nothing found online beyond the known Lomax 1962 (ACE). Not searched in depth. |
| US Virgin Islands | **Soule Archive on dLOC: good fit**, but only ~5 items ≤1970 (1955 Marie Richards songs, 1968 recordings, a 1964-dated compilation). |
| British Virgin Islands | Only via Soule T146 "Jost Van Dyke" (1968). Nothing else found. |
| Aruba | **BNA Ito Tromp tapes 1967–70 on IA: ask permission** (direct MP3, no licence). Archivo Nacional Aruba on IA has almost nothing ≤1970. |
| Curaçao | **Zikinzá Collection (NAAM/UoC DCDP): ask permission** (1950s–60s, all rights reserved, signed URLs). Nationaal Archief Curaçao is reachable but has no online audio (holds Juliana-Brenneker). The "Ranson Curaçao Music Heritage Archive" (tambú vinyl) exists only as a thin event listing and could not be fetched (403). |
| Bonaire | Fundashon Kas di Kultura is unreachable. Bonaire recordings are part of Zikinzá (above). |
| Netherlands (holders) | Beeld en Geluid main site reachable, but data.beeldengeluid.nl returns 403 and openbeelden.nl is blocked. Delpher is text only. Leiden digital collections returned an empty reply. Nothing found in Dutch searches. |
| dLOC (pan-Caribbean) | Rechecked: reachable through the new API (see header). Pre-1971 audio is very thin. "Audio" type counts are inflated by mis-typed newspaper records (the *Haiti Sun* is tagged "Radio Program"/Audio). |

### Mexico (states/universities) and Brazil (regions)

#### Mexico: states and universities

| Country | one-line result |
|---|---|
| Mexico: UNAM (Fonoteca, FaM, IIE, Descarga Cultura, Radio UNAM, ENES Morelia) | Descarga Cultura is modern productions (not usable). Voz Viva is literary. Radio UNAM has no online archive (host failed). The FaM repository link is dead (404). ENES Morelia Fonoteca de la Pirekua has 2022 recordings only |
| Mexico: El Colegio de México (CELL) | **Best infrastructure found**: 890 CC BY 4.0 MP3s with a JSON API, but all dated 1981–2003. Monitor |
| Mexico: Veracruz / Universidad Veracruzana | Nothing found online at UV (the son jarocho discography is a book). The only open son jarocho audio is ColMex's (post-1970) |
| Mexico: CIESAS, Instituto Mora | Nothing found online |
| Mexico: Michoacán (El Colegio de Michoacán, pirekua) | ColMich has a fonoteca, but nothing online (host failed). Fonoteca de la Pirekua is contemporary. XEPUR radio archive is undigitised |
| Mexico: Jalisco (UdeG, BPEJ, mariachi) | BPEJ Fondo Francisco Navarro (16k discs) and the Radio UdeG archive: no online audio found, host has a TLS failure. Ask permission (low priority) |
| Mexico: Yucatán (UADY, ESAY, trova) | Fonoteca Adda Navarrete (ESAY) has no online access (403). Trova 78s exist on IA Great 78 (8 Guty Cárdenas items) and UCLA Frontera (known) |
| Mexico: Oaxaca | Fonoteca Eduardo Mata (IAGO): nothing online. FAHHO Fonoteca Juan León Mariscal: no audio online |
| Mexico: Chiapas (CELALI, UNICACH) | CELALI host 502. UNICACH: nothing found. Material is in INAH and Stanford collections (known) |
| Mexico: Nuevo León, Sonora, Tamaulipas, Puebla | Nothing found online (Nuevo León fonoteca was historically on-site only) |
| Mexico: Radio Educación | 140k-item fonoteca, slowly digitised, no public audio. Site TLS certificate expired. Ask permission (low priority) |
| Mexico: Memórica | Audio is negligible. Licences are per collaborator |
| Mexico: UCLA MEAP Montejano (recheck) | 1970-onwards recordings. UCLA Digital Library blocked. Not usable |
| Mexico: Internet Archive | No Mexico-specific institutional collection. Mexican 78s are inside Great 78 sub-collections (known). `hessel-digital-library` is an LoC mirror. INAH LP uploads are not usable |

#### Brazil: states and regions

| Country | one-line result |
|---|---|
| Brazil: Rio Grande do Sul | **MuseCom Casa A Electrica 78s (1910s–20s), non-commercial reuse statement: ask permission** (musecom@sedac.rs.gov.br). UFPel Discoteca Vinholes blocked (403). IGTF and MIS-RS hosts 502 / nothing found |
| Brazil: São Paulo | MIS-SP is on-site only (verified, not usable). IEB-USP: ask (arquivoieb@usp.br). Cachuera!: nothing online found. Brasiliana USP host unreachable |
| Brazil: Rio de Janeiro | MIS-RJ (Rádio Nacional custody): no online catalogue. EBC memória 502. Funarte/CNFCP blocked. UFRJ Luiz Heitor 78s (1940s field discs): no online catalogue found |
| Brazil: Bahia | Museu Afro-Digital (UFBA) Turner 1940–41 MP3s: rights reserved and sacred, so not usable. Herskovits, Landes and Pierson collections there are images only |
| Brazil: Pernambuco | Fundaj blocked. Nothing found online |
| Brazil: Alagoas | Museu Théo Brandão (UFAL) publicly launched sound recordings in Sept 2026 (search-only, online access unconfirmed). Worth a rerun |
| Brazil: Maranhão (bumba-meu-boi) | Nothing found online. The 1938 Missão recordings are at CCSP (known) |
| Brazil: Pará (Museu Goeldi) | Host 502. Nothing assessed |
| Brazil: Ceará, Goiás (MIS-CE, MIS-GO) | MIS-CE host 502. Nothing found online |
| Brazil: Minas Gerais (UFMG) | Nothing found online |
| Brazil: Paraíba | Rádio Tabajara 78/33 rpm collection: diagnostic thesis only, nothing online |
| Brazil: national aggregators | Brasiliana Museus (IBRAM) has an open API and MP3s, post-1970 so far: monitor. Arquivo Nacional SIAN holds speeches. Biblioteca Digital Luso-Brasileira returns 403 |
| Brazil: Internet Archive | No Brazil-specific institutional collection. About 605 Brazilian-genre 78s inside Great 78 sub-collections (known). LP uploads are not usable |

### Cross-cutting

| Item | Result |
|---|---|
| dLOC (Digital Library of the Caribbean) | **Reachable again via its new API** (`api.dloc.patron.uflib.ufl.edu`): open JSON search and OAI-PMH, with direct MP3s on ufdcimages.uflib.ufl.edu. The old `api.dloc.com` is still blocked. Pre-1971 folk audio on dLOC is thin. |
| European holders | Berlin (IAI/Phonogramm-Archiv): Koch-Grünberg online but all rights reserved (ask). Världskulturmuseerna: no Nordenskiöld audio online. Scenkonstmuseet: no trace of Ayestarán copies. Beeld en Geluid: only 6 wartime speeches about Suriname; openbeelden.nl blocked. Leiden/KITLV blocked. |

---

## 3. Per-archive notes

### Central America

#### UNM K'iche' Maya Oral History Project
- Item page: https://digitalrepository.unm.edu/laii_kichemaya/131 (HTTP 200). Rights text: "The copyrights of the recordings and transcripts remain with LAII … licensed under the Creative Commons CC-BY-NC-ND 4.0 license." Publication date shown is 1-1-1968.
- The audio is embedded through jwplayer: `https://libvideo.unm.edu/vod/LAII/017las-aventuras-de-un-joven_massconversion_h264.mp4`. A ranged GET returned 206 video/mp4. HEAD shows Accept-Ranges bytes and Content-Length 18,242,511. No login. The file is an MP4 container (likely audio with a still image). It needs an audio-capable MP4 player or extraction on the fly.
- OAI-PMH: `https://digitalrepository.unm.edu/do/oai/?verb=ListRecords&metadataPrefix=oai_dc&set=publication:laii_kichemaya` returned 100 records plus a resumption token (completeListSize=151). Every dc:date is 1968-01-01, which is probably a blanket date. The project page says 1968–1973.
- Background: https://laii.unm.edu/news/2020/04/jim-mondloch-the-kiche-maya-oral-history-project.html (search-only). The old laii.unm.edu/kiche.html returns 404.
- Caveats: these are spoken folktales, not songs. ND means no edits, so stream whole files. Some items cover belief and ritual and need a cultural-sensitivity flag. Items dated after 1970 cannot be excluded by date because of the blanket 1968 date.

#### SINABI Fonoteca Virtual (Costa Rica)
- https://www.sinabi.go.cr/biblioteca%20digital/fonoteca/ (200). Subpages: musica guanacasteca/Marimba.aspx, Quijongo.aspx, musica tradicional/Tradcional.aspx, musica afrocaribena/Cuadrillas.aspx, musica indigena/Boruca.aspx and others.
- MP3 example: `/biblioteca%20digital/fonoteca/musica%20afrocaribena/cuadrillas/Cuadrilla%20Primera%20figura.mp3`. Ranged GET returned 206 audio/mpeg, Content-Length 799,973. Marimba "El torito.mp3" is 1,411,866 bytes. Both are clips.
- The 2011 press release (Comunicado de Prensa.pdf) says "444 fragmentos de piezas musicales". Sources cited per track include CBS 198_, Producciones Sibó 199_, MCJD 2000, INDICA 1983, and *Música Popular Afrolimonense* (MCJD/OEA 1982). The site footer says "Derechos reservados". robots.txt allows all.

#### Belize Archives and Records Service / NICH
- https://archives.gov.bz/our-collections/ (200) lists the audiovisual records: "Folk Songs by Mahogany Cutters 1957", "Music by Local Artists 1957 to date", "1931 Hurricane Sound Documentary", "Original Recordings of Independence Celebrations 1981", "Radio Belize (BCB) News Broadcasts 1983 – 1998".
- Contact: info@archives.gov.bz, on https://archives.gov.bz/contact-us/ (200).
- NICH Belize Music Project: https://nichbelize.org/the-belize-music-project (200). It plans "a music archive for public access" and a box set, "100 Years of Music in Belize". The Music Archive Lab at BARS was reported in 2026 by San Pedro Sun and Greater Belize (search-only). nichbelize.org/contact-us/ returned 404.
- The Dangriga Garifuna VHS project (Modern Endangered Archives) covers the late 1970s onward and requires community consent. It is out of range.

#### Biblioteca Nacional de Panamá: Brenes/Zárate Documental
- https://binal.ac.pa/colecciones/coleccion-audiovisual/ (200): materials on VHS, DVD, CD and cassette "solo pueden ser utilizados dentro del recinto del Centro Audiovisual". A library card is required. Email referencia@binal.ac.pa appears on binal.ac.pa pages.
- The collection description comes from search results (85 non-commercial CDs, received 2001 through Cervecería Nacional). The CREM record also says "La collection originale complète est déposée à la bibliothèque nationale du Panama".
- The catalogue host bdpa.binal.ac.pa and bibliotecanacional.gob.pa were unreachable (proxy 502 / no answer).

#### CREM-CNRS, Central American sub-collections (known archive)
- https://archives.crem-cnrs.fr/archives/collections/CNRSMH_I_1960_003/ (200): "Série de matériel folklorique de Panama, sélection du Prof. Manuel Zarate 1955". 17 items, 32 min, Guaymí and Kuna. Recordists Brenes Candanedo and Zárate. Unpublished. "Access type full". "Legal rights: Copie interdite".
- CNRSMH_E_1958_012_001 (Lehmann, Guatemala Musique populaire, 1954 45 rpm): access full, "Restreint (enregistrement édité)". CNRSMH_E_1958_012_002: Mam sung prayers (probably sacred). CNRSMH_I_1970_053: master tape, metadata only, copie interdite. CNRSMH_E_1969_022_001: Blaise, Panama 1953 Vogue LP, metadata only.
- Geo listings: https://archives.crem-cnrs.fr/geo/collections/amerique/{panama,guatemala,honduras,belize,nicaragua}/ (200). The costa-rica slug returned 404 and el-salvador returned 404/500. All other CA collections there are post-1970 or commercial LPs.

#### Archivo Mesoamericano (Indiana University)
- https://archivomesoamericano.org/collections.json (200, JSON). It lists 3 collections: MUPI (pz50gw084), IHNCA (0k225b04p) and CIESAS. `/catalog.json?per_page=100` gives total 346. The resource-type facet is "moving image" 345. Top dates are 1983–87 and 2020.
- This is the only online route to any IHNCA audiovisual material, and it is all video.

#### MUPI (El Salvador)
- https://museo.com.sv (200). Pages such as /retornarle-musica-a-la-memoria/ contain injected Greek casino-spam text, so the site appears compromised. Do not deep-link to it.
- Its holdings come from search: Radio Venceremos 1981–92 (also at UT HRDI), and the Fondo de Músicos Vicentinos (scores). No pre-1970 folk audio was found.

#### CIRMA (Guatemala)
- https://cirma.org.gt/ (200, React app). The page JSON lists "Tres acervos": Biblioteca de Ciencias Sociales, Fototeca Guatemala, Archivo Histórico. The homepage publishes a shared catalogue login. /fonoteca loads only the app shell. No sound holdings were named. The colonial "Música Colonial Archive" there is scores on microfilm (CPDL, search-only).

#### Unreachable or blocked hosts (tested with curl)
- Proxy CONNECT 502: ihnca.edu.ni / www.ihnca.edu.ni (also DNS failure), ahm.ucr.ac.cr, emusica.ucr.ac.cr, radionacional.go.cr, mcd.gob.gt, agca.gob.gt, casakojom.com, bdpa.binal.ac.pa, repositorio.ihnca.edu.ni.
- Connection reset: ceceg.usac.edu.gt, sertv.gob.pa.
- 403: sinart.go.cr. Cloudflare bot check: archivodigital.go.cr (not attempted further).
- kojom.org returns 200 with "Error. Page cannot be displayed".

#### Internet Archive
- I ran advancedsearch queries by country, and with year:[1900 TO 1975], garifuna, marimba, tamborito and similar terms. No institutional Central American collection turned up.
- Relevant items found are all in known collections: Great 78 (e.g. `78_fletita-one-step_hurtado-bros-royal-marimba-band-of-guatemala-j-b-hurtado_gbia3033421a`, `78_tamborito-panama_maria-cruz-orchestra_gbia0210319a`, `78_la-paloma_marimba-la-centro-americana-yradier_gbia3020063b`) and `hessel-digital-library` (LoC Jukebox mirror, PDM).
- Community uploads with doubtful rights: `MarimbaCentroamericana-MoralesLopezAguasDormidas`, `hojita-de-tamarindo`, `el-carbonero-el-salvador`.

#### Other leads, search-only
- UW Ethnomusicology logsheets: https://www.lib.washington.edu/static/public/ethno/logsheets/72-10.1.pdf (Guatemala 1972) and 75-23.9.pdf (Nicaragua 1975). Both are post-1970.
- LoC AFC Guatemala guide: https://www.loc.gov/folklife/guides/Guatemala.html (known archive; K'iche' 1948 recordings, by request).
- Panamá América 2019 on the Afrodisíaco Zárate project: https://panamaamerica.com.pa/variedades/archivo-musical-invaluable-legado-de-los-esposos-zarate-en-proceso-de-rescate-por
- UNAH Audioteca article: https://tecdanli.unah.edu.hn/dmsdocument/14744-…
- IAML Nicaragua (FONOMUSIC): https://www.iaml.info/nicaragua

### South America and European holders

#### Ajayus de Antaño / "Memoria Musical de Bolivia" on Internet Archive
- Query `https://archive.org/advancedsearch.php?q=uploader%3A78rpmbolivia%40gmail.com&...` returned numFound 109. Licences: 86 CC BY-NC-ND 4.0, 1 CC BY-NC 4.0, 22 none.
- Example items: `orquesta_boliviana_-_yotala_yotalita_23-b` (1930), `flores_bolivianas_32b_202001` (1919), `maria_paz_gainsborg_-_a_los_campos_me_retiro_08-a` (1916), `estudiantina_hayden_-_ancha_chiri_06-b` (1935), `banda_boliviana_-_sara_19-b`, `raul_shaw_moreno_-_verde_mar_05-b` (1955).
- Item description: "Memoria Musical de Bolivia - Colección Ajayus de Antaño. FOCUART 2019" (FOCUART is the La Paz municipal culture fund). Subjects: `78rpm`, `bolivia`.
- `https://archive.org/download/orquesta_boliviana_-_yotala_yotalita_23-b/orquesta_boliviana_-_yotala_yotalita_23-b.mp3` → 206 audio/mpeg. Each item has both MP3 and Ogg.
- Background: Ajayus de Antaño is a two-person collector project (Isaac and Fernando) with Facebook, Blogspot and YouTube channels, digitising since 2012 (Bolivian Express, https://bolivianexpress.org/magazine-sub-item/771; search-only).
- Caveats: ND forbids derivatives. Unmodified streaming through an encrypted cache should be fine, but do not trim or normalise. Some 1950s items may still be in copyright in Bolivia; the uploader's CC licence is the archive's own statement under our rule. The 22 unlicensed items need the same judgement as other un-licensed 78 uploads.

#### Hessel Digital Library (IA `hessel-digital-library`)
- The collection was created on 2026-10-20. It describes itself as "A preservation mirror of openly licensed and public domain collections … with verbatim rights statements". numFound 29,071.
- Example `loc-jukebox-35266-nacional-joropo`: Lionel Belasco Orchestra, 1920. The description reproduces the LoC Jukebox source URL (https://www.loc.gov/item/jukebox-35266/) and a public-domain statement under the MMA. The file is an MP3.
- Venezuelan matches (subject venezuela + 78rpm): about 14, for example "El jarro mocho", "Caricari", "Flores de Pascuas" (1917). Ecuadorian and Colombian pasillos: dozens from 1907–1917.
- Use: LoC is already known, so stream from loc.gov. This mirror is only a convenient index.

#### IAI Berlin, wax cylinder recordings (Koch-Grünberg)
- News pages: https://www.preussischer-kulturbesitz.de/news-detail/artikel/2025/10/20/sharing-historical-sound-recordings-from-latin-america-globally.html and https://www.iai.spk-berlin.de/en/who-we-are/news-from-the-iai/detail/20-10-2025_historische-tonaufnahmen-aus-lateinamerika-global-teilen.html. They say the recordings are "available worldwide in open access", but no licence is named.
- Collection: https://digital.iai.spk-berlin.de/viewer/collections/wax-cylinder-recordings-from-latin-america/ (200). Record PIs: 192582814X (Peru), 1925829081 (Brazil: Koch-Grünberg 1911–1913, ed. Mendívil 2006), 1925830802 (Argentina 1905–09), 1925832481 (Brazil, Lühning 2013), 1925833216 (Cora/Huichol), 1925833615 (Tierra del Fuego).
- The page for 1925829081 (200) says: "Access restriction all rights reserved"; "The copyright and related rights status of this record has not been evaluated"; "Some individual audio recordings cannot be played due to ethically sensitive material: track 4 (cylinder 38), track 5 (cylinder 23), track 21 (cylinder 17), track 22 (cylinder 19), track 23 (cylinder 29)". The CC BY-NC-SA 4.0 on the collection page applies only to a photo credit.
- API: `/api/v1/records/1925829081/files/images/00000001.tif/full/750,/0/default.jpg` returns 200. `/api/v2/records/1925829081/manifest/` returns 403 "Serving this image is restricted due to access conditions". `/plaintext/` returns 403. The toc page returns 403 to WebFetch and 200 to curl, but has no audio URLs.
- SMB catalogue: https://search.smb.museum/object/obj-750144 ("Koch-Grünberg Brasilien II", VII WS 142, 1913, Desana/Tukano/"Venezolaner") has no audio. The DDB-CCC mirror (https://ccc.deutsche-digitale-bibliothek.de/en/item/3SUCYFACPZT2237GLILW4ZCC3J3KM3WT) shows CC BY-SA 4.0 with no media. Contact on the SMB page: em@smb.spk-berlin.de.
- api.smb.museum returned 503.

#### Biblioteca Digital de Venezuela "César Rengifo" (BNV)
- https://www.bnv.gob.ve returned 200 (contacts atencion.alciudadano@bnv.gob.ve, orientacionyreferencia@bnv.gob.ve). Digital library: https://bibliotecadigital.bnv.gob.ve/ (WordPress 4.9).
- Audio category: https://bibliotecadigital.bnv.gob.ve/?cat=1259, 2 pages of about 15 items. Examples: ?p=11802 "Memorias de la Música Latina en Venezuela [grabación]: Eusebio Enrique Iriarte" with a direct MP3 at `/wp-content/uploads/Eusebio-Enrique-Iriarte-Culebra.mp3` (ranged GET 206 audio/mpeg). ?p=13199 "Un Venezolano y su mensaje" (LP, WAV). ?p=1513 Andrés Eloy Blanco speech (tape). The search also listed "Chicha Maya [grabación]: folklore de la Guajira venezolana". A follow-up request then got a connection reset, so I stopped.
- Rights text: "Biblioteca Digital de Venezuela es respetuosa de la Ley sobre el Derecho de Autor … solo se liberan contenidos exentos de su cumplimiento, salvo en aquellos casos que sus creadores autoricen formalmente".
- /wp-json/wp/v2/media returned 404.

#### MUSEF Territorios Sonoros (Bolivia)
- https://www.musef.gob.bo/musef-territorios-sonoros returned 200. The embedded JSON gives total 3494, per_page 5, last_page 699. Fields: codigo (AC-MUSEF/OS/CAS-…), titulo, descripcion, departamento/municipio/comunidad, lat/long, instrumentos, lenguas, audio_url. There is no date field.
- `https://www.musef.gob.bo/sonidos-web/G14/4849-LA-02.m4a` → 206 audio/x-m4a.
- Footer: "© 2026 MUSEF. Todos los derechos reservados." Contact: info@musef.gob.bo. A search snippet says the MUSEF sound archives come from the 1980s–90s "Misiones Antropológicas de Urgencia" (Caminos del MUSEF article), so they are probably outside the ≤1970 rule. Some content is ritual (Todos Santos rezos etc.), so sensitivity screening is needed.
- The old host musef.org.bo did not respond.

#### ArchivOlares (Venezuela)
- https://archivolares.org returned 200. It has 5 SoundCloud embeds and mailto:archivolares@gmail.com. Search snippets (ABRA Caracas, NYUAD, Goethe-Institut) say Oswaldo Lares (1932–) recorded more than 1,000 tracks from 1960 to 1980. TAL label CD "Música de Venezuela 1972–1981". Most material is after 1970.

#### Fundación Bigott / FUNDEF / Centro de la Diversidad Cultural / Radio Nacional de Venezuela
- fundacionbigott.com gave a TLS handshake failure. fundacionbigott.org returned 200: YouTube link and mailto:fundacion_bigott@bat.com, no sound archive.
- cdc.gob.ve, diversidadcultural.gob.ve and rnv.gob.ve: no response (000).
- UNESCO's AV catalogue lists CDC productions (https://www.unesco.org/archives/multimedia/producer/centro+de+la+diversidad+cultural,+venezuela; search-only). No online archive found for FUNDEF/INIDEF (Aretz/Ramón y Rivera).

#### UNTREF Fondo Isabel Aretz
- https://www.archivobiblioteca.untref.edu.ar/index.php/la-rioja-n-s-andacollo-1-y-2 returned 200. The digital object is a PDF (application/pdf, 129.9 KiB) of the card/sleeve. No audio.

#### Portal Guaraní (Paraguay)
- https://portalguarani.com/1326_eladio_martinez__/19789_tesay__polca_de_eladio_martinez.html returned 200. `<audio controlslist="nodownload" src="/veraudio.php?file=…">` decodes to a path "…/SERAFIN FRANCIA CAMPOS - SONIDOS DE AYER 4/TESAY….mp3"; the stream answers 206 audio/mpeg. Footer: "Todos los derechos reservados".

#### Radio Nacional del Paraguay fonoteca
- https://prensamercosur.org/2025/11/19/un-monumental-rescate-de-memoria-sonora-musicologos-encuentran-discos-perdidos-de-radio-nacional-del-paraguay/ (republished from ABC Color): 500–700 discs, researchers Miguel Antar and Nicolás Salaberry, Latin Grammy grant. The planned portal will give 30-s demos, with full sides on request for non-commercial research. No URL yet.

#### Other Paraguay hosts
- bibliotecanacional.gov.py: http 302 redirects to https, which then returns "connection reset". museobarbero.org.py returned 200 with "Colecciones" and "biblioteca.php" but no audio. archivonacional.gov.py returned 301 (not explored further). The IA search for polca, guarania or Pérez Cardozo found only a handful of community uploads with no rights, from 1965 and later.

#### ABNB Bolivia
- https://www.archivoybibliotecanacionales.org.bo returned 200. Its digital library https://digital.biblioteca-abnb.org.bo/ (Omeka S) API returned 403 with a Cloudflare "Just a moment…" challenge, so I stopped.
- A search snippet (Revista PH, iaph.es) says that in November 2024 recordings deposited in France, including one from the 1903 Créqui-Montfort mission, were transferred to the ABNB.
- fundacionpatino.org: no response.

#### Ecuador
- Unreachable (000): biblioteca.culturaypatrimonio.gob.ec, ccpgn.gob.ec, cceguayas.org. repositorio.flacsoandes.edu.ec returned 403. repositorio.puce.edu.ec returned 200 (theses; no folk audio searched).
- Contrapulso (2026; search-only) says Ecuador "no desarrolló el archivo fonográfico como institución" and that its record memory is held by private collectors (e.g. Antonio Godoy) and the Banco Central "Fondo Musical".
- HCJB: its own history mentions an archive of studio sessions of traditional music, but no digitised access was found (https://hcjb.org/?p=81802; search-only).
- Caife: NPR and Sounds and Colours, commercial reissues.

#### Guyana
- nationalarchives.gov.gy returned 403 and nalis.gov.gy returned 000. walterrodneyfoundation.org returned 200 (a foundation, not the National Archives building).
- UH ScholarSpace Guyanese Creole item: bot check ("Checking your browser"), so blocked. av.lib.umd.edu item returned 400.
- No GBC/NCN archive found. shortwavearchive.com has hobbyist off-air recordings from 1972 and later (out of scope).

#### Suriname and Dutch holders
- nationaalarchief.sr returned 200: genealogy and paper archives only, contact info@nationaalarchief.sr. A NAS–Beeld en Geluid four-year AV cooperation is announced (keynews.sr; search-only).
- openbeelden.nl (and www., /feeds/oai/, /api/) and Leiden digitalcollections returned connection reset (000). zoeken.beeldengeluid.nl returned 200 (JS catalogue). data.beeldengeluid.nl returned 403.
- Europeana API (wskey api2demo), Suriname + TYPE:SOUND: 511 hits, mostly Naturalis birdsong. Only 6 are from Sound & Vision: Radio Oranje / Herrijzend Nederland speeches 1941–45 (PD Mark), no music.
- collectie.wereldmuseum.nl returned 200 but is an Angular app; no audio results could be read.
- delpher.nl/nl/radiobulletins returned 200: text radio bulletins only.

#### French Guiana
- archives.ctguyane.fr, archives-guyane.fr, mcg-guyane.fr and musee-des-cultures-guyanaises.fr: no response. www.ctguyane.fr returned 200.
- The MCMG offers on-site INA access (outremers360.com; search-only). CREM (known) holds the Mazière 1952 "Musique populaire de Guyane" (rights "Restreint (enregistrement édité)") and Hurault 1957–65. archives.crem-cnrs.fr returned 502 from the proxy this session.

#### Världskulturmuseerna (Nordenskiöld)
- https://collections.smvk.se/carlotta-em/web returned 200.
- Free-text searches: "fonografrulle" gives 2 archive documents (a 2024 wax-cylinder condition report and a 1912 general catalogue). "Chaco ljud" gives 3 objects (whistles, mouth organ by Gustaf Flood) and 2 documents. "Nordenskiöld ljud" gives 3 hits, none of them sound. "fonograf" gives 76 hits, 43 of type "Ljud" (Bolinder Colombia, Karl Tirén Lappland 1913, Haslund-Christensen ZS-V78-… 78s, 1936–37 lacquer discs). I could not open the playback for those; the result list is session-form based.
- Contact shown in the footer: carlotta@etnografiska.se.

#### Scenkonstmuseet
- https://scenkonstmuseet.se/?s=Ayestar%C3%A1n returned 200 with no results. Contact info@scenkonstmuseet.se. Two web searches found no Swedish holding of Ayestarán copies, so the first-pass claim is unconfirmed.

#### Internet Archive, other checks
- Searches by country plus audio plus date ≤1970: Venezuela 59 hits (mostly Jukebox mirrors, LPs, Great 78 joropos, e.g. `78_alma-llanera_marfil-y-valencia-pedro-elias-gutierrez_gbia0038486b`, `78_corrio-llanero_rosita-serrano-pedro-e-gutierrez_gbia7039304a`; Great 78 is known). Ecuador 47 (Jukebox mirrors). Paraguay 10. Guyana 5. French Guiana 2. Suriname: nothing before 1971.
- `3-lps-de-isabel-aretz` (opensource_audio): no rights info, so not usable without checking.
- `venezuela-combate-…-1966` (political songs, hifidelity): no rights, so not usable.

### Caribbean

#### Soule Archive (UVI / dLOC): good fit, small
- Aggregation `isoulearchive`. Example item: https://dloc.com/AA00115853/00001 (T147–T150, Marie Richards, 1955).
- API citation: https://api.dloc.patron.uflib.ufl.edu/AA00115853/00001/citation. RightsManagement: "The persons recorded and/or photographed herein consented to the deposit of their recordings and/or photographs in a public archive in which the materials are available to the public for only educational and noncommercial purposes."
- Downloads: https://api.dloc.patron.uflib.ufl.edu/AA00115853/00001/downloads lists 31 MP3s.
- Audio test: `curl -r 0-1023 https://ufdcimages.uflib.ufl.edu/AA/00/11/58/53/00001/T147-01-Beautiful-Isles.mp3` returned 206 audio/mpeg. The path pattern is bibid split into pairs of characters, then vid.
- Search used: `exactsearch?filter=mediatype:Audio&datehi=1970-12-31` returned 249 hits, of which Soule had 5. Pre-1971 Soule items: AA00115853 (1955), AA00115852 T146 (1968), AA00115609 T051 (1964?), plus 2 others.
- Guide to the recordings: https://dloc.com/AA00113984/00001/pdf/0.
- Caveats: "educational and noncommercial" fits a free, non-commercial app. The Marie Richards songs are her own compositions; her heirs may hold copyright, but the deposit consent covers non-commercial public access.

#### dLOC API (recheck)
- https://api.dloc.patron.uflib.ufl.edu/ returns a JSON index of endpoints.
- `/types` returns 190 types. None is a music type; there are "Oral histories" (111) and "Radio Programs" (5,290).
- `/oai?verb=ListSets` returns OAI-PMH sets (UFDC-wide). `/oai?verb=Identify` returned 404 HTML.
- `exactsearch` takes `default`, `filter=field:value`, `datelo` and `datehi`. There is no paging parameter (`page`, `rows` → "unknown search field"), and each response returns 25 hits plus facet buckets.
- Keyword checks within mediatype:Audio: biguine 0, tambu 0, gwoka 0, merengue 6 (all mis-typed newspapers), lewoz 1 (ACE Lomax Guadeloupe 1962 "Y Bai Ça", CA90001330, already known via ACE).

#### Biblioteca Nacional Aruba: Ito Tromp collection (IA): ask permission
- Collection pages: https://archive.org/details/bnaitotromp and https://archive.org/details/bibliotecanacionalaruba.
- Example item: https://archive.org/details/BNA-DIG-ITOTROMP-TAPE-048 ("Dera Gay (datá for di 1862) tuma durante fyesta di San Juan … 24 juni 1969"). Metadata: genre "audio;music", language pap, subjects "field recordings", "intangible heritage"; no `rights` or `licenseurl`.
- Ranged GET on `.../BNA-DIG-ITOTROMP-TAPE-048-001%20part%201.mp3` returned 206 audio/mpeg.
- Counts: 86 audio items. By year: 1967 (40), 1968 (21), 1969 (8), 1970 (6), 1971–75 (11). Licence field empty on all 86.
- The source page, coleccion.aw, returns 403.
- Other music items: TAPE-052 "Coby Angela – Den Kunuku – Tumba" (1969), TAPE-016, TAPE-049 (Musika Popular), TAPE-083 "Kantika di Pasku", TAPE-015 "Koor Colegio Arubano".
- Contact, decoded from Cloudflare-obfuscated mailto links on https://www.bibliotecanacional.aw/pages/contact/: info@bibliotecanacional.aw and arubiana@bna.aw.
- `musicaruba` audio (41 items) is 2010 instrument-lesson fragments (CC BY-NC-ND), out of range.

#### Zikinzá Collection (NAAM / University of Curaçao DCDP): ask permission
- Collection page: https://dcdp.uoc.cw/zikinza_collection. States: "356 of the approximately 1400 audio recordings made over a six-year period during the 1950s and 1960s by Paul Brenneker and Elis Juliana … Since 2010 … managed by NAAM."
- Example item: https://uoc.sobeklibrary.com/AA00003719/00001 ("Zé lo komató", Kantika, Papiamentu, 00:59, date 1974, donor NAAM, identifier T0314). Rights: "All rights reserved by the source institution."
- Audio is a storage.googleapis.com signed URL (X-Goog-Expires=3600). A fresh link gave 206 audio/mpeg. It is not stable, so not streamable without per-request scraping.
- An in-collection search for "tambu" returned 0.
- NAAM contact: info@naam.cw (decoded from https://naam.cw/).
- Nationaal Archief Curaçao memory-of-the-world page mentions the "Juliana-Brenneker" collection. Contact page address: nationaalarchiefcuracao@duck.com.
- Sensitivity: tambú was historically banned, and some songs relate to ritual and wake practices. Ask NAAM about community restrictions.

#### Radio Haiti via dLOC
- https://api.dloc.patron.uflib.ufl.edu/AA00074686/00001/citation gives RightsManagement "https://creativecommons.org/licenses/by-nc-sa/4.0/ This recording may contain material that was not originally created by Radio Haiti …".
- MP3 https://ufdcimages.uflib.ufl.edu/AA/00/07/46/86/00001/RL10059-RR-0003_01.mp3 returned 206 audio/mpeg.
- Pre-1971 items: AA00071056 and AA00071057 (1957 elections), AA00074684 (1962), AA00074686 (1966), AA00074691 (1970). All spoken word.

#### Cabrera–Tarafa (UM Cuban Heritage Collection)
- Manifold project API: https://cuny.manifoldapp.org/api/v1/projects/musicadeloscultos returned 200; "Beyond the Archive", 13 resources, kinds pdf/image/audio.
- The UM event (https://events.miami.edu/event/beyond-the-archive-the-cabrera-tarafa-collection-of-afro-cuban-music-circa-1956) says the collection is fully digitised. UM hosts timed out.
- This is ritual/initiatory music: exclude under the sacred-material rule.

#### Centro León: Fradique Lizardo
- Fund page: https://centroleon.org.do/fradique-lizardo/ (50,000+ records; 481 sound recordings catalogued; Grammy Foundation-funded cassette conservation).
- Sound archive page: https://centroleon.org.do/archivo-sonoro-fradique-lizardo-volumen-i-ii-iii/ (redirects to the …-iv-v page). It describes "una selección de los más de 700 registros sonoros".
- Volume II (https://centroleon.org.do/archivos-sonoros-fradique-lizardo-volumen-ii/, via WebFetch) links to SoundCloud set user-124598724/sets/archivos-sonoros-fradique-lizardo-volumen-ii. Recordings dated 8 Jul 1975 and 18/29 Sep 1977. Footer: "Todos los derechos reservados."
- Contact: centroleon@centroleon.org.do (decoded on /mediateca/).

#### Cuba (institutions)
- All hosts failed (reset or 502).
- Search: EGREM's CICAM archive (est. 2008, 1940s on) is partly digitised with no public access described (https://resources.culturalheritage.org/emg-review/volume-8-2023-2024/digital-preservation-of-cuban-music-case-study-of-the-collection-of-the-center-for-information-and-conservation-of-musical-archives-cicam-of-the-recording-and-musical-editions-company-egrem/).
- Museo Nacional de la Música, Biblioteca Musical y Archivo de Sonido: https://www.ecured.cu/Museo_de_la_M%C3%BAsica. Nothing streamable found.

#### Haiti (institutions)
- fokal.org and cidihca.com answer 200, but their home pages show no sound archive. CIDIHCA offers "recherche dans nos archives" and accepts audiovisual donations; no online audio was seen.
- Searches in French and Kreyòl ("achiv son mizik tradisyonèl") found only CREM, Lomax, Duke Radio Haiti, the Frisner Augustin Archive (1984 lessons, out of range) and a Brooklyn vinyl project.

#### French Antilles
- https://www.patrimoines-martinique.org/page/archives-numerisees has no sound items.
- https://www.archivesguadeloupe.fr/ mentions only civil-register digitisation.
- ACE/CBMR French Antilles repatriation (Lomax 1962) is deposited at Lameca (https://www.culturalequity.org/initiatives/repatriation/acecbmr-french-antilles-repatriation). Already known via ACE.
- madelen.ina.fr is reachable but is INA's subscription service, so not usable.
- Phonobase (https://phonobase.org/en/fiche/9407) is reachable with an inline MP3 path. It is flagged for the Europe sweep; rights not checked.

#### IA checks
- `merengue` ≤1970: 38 items (Great 78 / unlockedrecordings).
- `biguine/beguine`: 353, nearly all "Begin the Beguine" covers.
- `danzon/guaracha/son cubano` ≤1970: 40 (Great 78, loc-jukebox mirrors, user 78 uploads such as CuartetoMatamorosCapullitoDeAleliVICTOR30398A, rights unstated).
- Haiti ≤1970: 14 (LPs in unlockedrecordings, in copyright).
- Bahamas: radio captures only, plus `lp_lou-adams-plays-bahamiana-calypso` (1952 LP).
- Martinique/Guadeloupe: modern only.
- No dedicated Cuban, Dominican or Haitian 78 collection id found beyond Great 78.

#### Not fetched or limits
- I used about 12 web searches.
- Not deeply searched: Grenada, St Vincent, Antigua, Dominica, St Kitts and Nevis, Sint Maarten and Saba, Barbados Museum.
- excavatedshellac.com (a collector blog with a Curaçao tag and 78 transfers) was not checked: private collector, rights unclear.

### Mexico (states/universities) and Brazil (regions)

#### MuseCom: Casa A Electrica (Porto Alegre, RS), ask permission, verified metadata
- Brasiliana Museus API: `https://brasiliana.museus.gov.br/wp-json/tainacan/v2/collection/5658688/items/?search=Casa%20El%C3%A9trica%20Digital` returned X-WP-Total 130. Example item "Um baile na roça (2ª parte)", MCOM 0153-S: goma-laca, mechanical recording, Porto Alegre, [191-], "Artistas da casa".
- Rights field (verbatim): "Este conteúdo está disponível para reprodução, exibição e para a produção de trabalhos derivados dele, desde que sejam para fins não comerciais. Obrigatória a citação da referência para fins acadêmicos e expositivos."
- Several items say the disc "não foi localizado e, por isso, não integrou o projeto 'Casa Elétrica Digital'". That implies the others were digitised.
- Source site `https://acervos.musecom.rs.gov.br/` returned 401 on the page, the item page (`/casa-a-electrica/127321-2/`) and the Tainacan API. No bypass attempted.
- Catalogue PDF `https://www.musecom.com.br/uploads/Catalogo_Casa_Eletrica_V6_14-09.pdf` returned 404. Contact email from musecom.com.br: musecom@sedac.rs.gov.br.
- Background: Luana Zambiazzi dos Santos, *A "Casa A Electrica" e as primeiras gravações fonográficas no sul do Brasil* (UFRGS, 2011), http://hdl.handle.net/10183/28678. Hardy Vedana, *A Eléctrica e os Discos Gaúcho* (2006, 3 CDs).
- Pitch: 1913–24 phonograms are PD under Brazilian law. Ask for the MP3 masters to be made public in their Tainacan (or deposited at IA), with per-item credit.

#### Brasiliana Museus (IBRAM), monitor, verified
- API is keyless: `/wp-json/tainacan/v2/items/?search=…&perpage=…`. Media URLs come from `/wp-json/wp/v2/media/<id>`.
- Audio test: `https://brasiliana.museus.gov.br/wp-content/uploads/2026/09/Chiba-Caterete-Francisco-Jose-de-Bulhoes-Chiba-1-Tarituba-1976.mp3` returned 206 audio/mpeg. Item from Museu Forte Defensor Perpétuo (Paraty), collection "Acervo sonoro e audiovisual". The source page https://museufortedefensorperpetuo.acervos.museus.gov.br/acervo-sonoro-audiovisual/chiba-caterete-1/ returned 200.
- "gravação sonora" returned 43 hits, mostly the 1983 "O Modo de Fazer" interviews. Response header `x-robots-tag: noindex`. The proxy saw a couple of mid-exchange closures under load, so keep requests slow.
- Contacts on the site: cgsim@museus.gov.br, blog@brasiliana.museus.gov.br.

#### Museu Afro-Digital (UFBA), Lorenzo Turner 1940–41, not usable, verified
- New Tainacan site https://museuafrodigitalufba.org/. The collection list at `/wp-json/tainacan/v2/collections` shows Turner (ids 6776 and 123467, 121 items each).
- MP3 test: `https://museuafrodigitalufba.org/wp-content/uploads/tainacan-items/6776/8205/C-44.mp3` returned 206 audio/mpeg.
- Items are grouped by informant: Sr. Falefá 15, Joãozinho da Goméia 11, Manoelzinho 11, Martiniano Eliseu do Bonfim 9, and others. "Cantiga de Oxossi" is also present.
- Footer: "Copyright © 2025 Museu AfroDigital. Todos os direitos reservados."
- The Herskovits, Landes, Pierson, Frazier and UNESCO collections hold only images (checked the mimetype counts).
- Old site https://museuafrodigital.ufba.br/ lists contact afrodigital@ufba.br. Related project: Cassio Nobre, "Memórias Afro-Atlânticas" (UFBA), which prioritises returning the recordings to candomblé communities.

#### El Colegio de México repository (CELL fonoteca), monitor, verified
- Example item: https://repositorio.colmex.mx/concern/audios/c247dw05q, "04 El palomo" (1991, Comoapan, son jarocho instrumental). MP3 `stp_0106_1991_04_el_palomo_00_17_20.mp3`. Download `/downloads/pr76f624t` returned 206 audio/mpeg.
- `catalog.json?f[human_readable_type_sim][]=Audio` returned total_count 890. date_created facet: 1992 (335), 1991 (288), 1988 (96), 1990 (77), 1996 (48), 1981 (15), 1982 (11), 2003 (10), 1984 (9), 1989 (1). **Nothing before 1981.**
- Footer: "Licensed under the Creative Commons Attribution 4.0". Contact: Biblioteca Daniel Cosío Villegas, tel. +52 55 5449 3000.

#### Memórica, verified (pages)
- Licence page https://memoricamexico.gob.mx/es/memorica/Licencias_de_uso: licences are per collaborator ("Derechos reservados" or Creative Commons).
- Searches via `/swb/memorica/Resultados?q=` returned audio facets of 1 for "huapango", 9 for "cilindro" and 15 for "corrido". WebFetch got 403. curl worked.

#### Fonoteca de la Pirekua, verified
- https://fonotecadelapirekua.enesmorelia.unam.mx/ lists localities and tracks. Fichas (e.g. `generarpdfacto.php?id=1822`) are 2022 field documentation.

#### Descarga Cultura.UNAM, verified (site)
- Album pages embed `data-files` MP3 lists, e.g. https://www.descargacultura.unam.mx/1910-los-festejos-del-centenario-21169. Search is JavaScript-only. Content is spoken word and concerts.
- Licensing news: https://www.gaceta.unam.mx/descarga-cultura-y-voz-viva-se-integran-al-repositorio-institucional/

#### UCLA MEAP Montejano, blocked
- https://meap.library.ucla.edu/projects/the-ricardo-montejano-collection/ returned 200 and says recordings were made "between 1970 and the present" and include "sacred indigenous ceremonies".
- Collection link https://digital.library.ucla.edu/catalog/ark:/21198/z1j44g9k: curl TLS error (unable to get local issuer certificate), WebFetch 503.
- Press: https://www.cronica.com.mx/cultura/fonoteca-nacional-pondra-linea-archivos-fondo-ricardo-montejano.html

#### Radio Educación, blocked
- https://www.radioeducacion.edu.mx/ fails with "certificate has expired".
- Press: https://dplnews.com/el-gran-acervo-cultural-de-radio-educacion-se-digitaliza-lento-por-falta-de-recursos/

#### FAHHO / Fonoteca Juan León Mariscal, verified
- https://repositorio.fahho.mx/handle/123456789/37979 holds one MP4 video under CC BY-NC-ND 2.5 MX. The community list shows "Fonoteca Juan León Mariscal [1]". OAI `?verb=Identify` returned 200.

#### BPEJ / Radio UdeG (Jalisco), search-only
- https://comsoc.udg.mx/print/74365 (Fondo Francisco Navarro donation) and https://comsoc.udg.mx/print/25858 (Radio UdeG archive agreement). bpej.udg.mx fails TLS.

#### Fonoteca Adda Navarrete (Yucatán), search-only
- https://ru.iibi.unam.mx/jspui/bitstream/IIBI_UNAM/CL28/1/15_archivos_digitales_paul_rodriguez.pdf. esay.edu.mx returned 403.

#### IEB-USP, ask permission, verified
- https://www.ieb.usp.br/mario-de-andrade-rachel-de-queiroz-e-mary-pedrosa-cantam-para-lorenzo-turner/ describes the 1940 Rio recording, a copy from Indiana ATM. The linked `download-documento-noticia/ladoa_1429225910.mp3` returns 404.
- Emails from https://www.ieb.usp.br/contato/contato-2/ and /acervo/: arquivoieb@usp.br, iebdigitaliza@usp.br, caviebconsulta@usp.br.

#### MIS-SP, not usable, verified
- https://acervo.mis-sp.org.br/audio says "O acervo do MIS está disponível apenas para consulta local gratuita… É vedada a reprodução".
- Example: https://acervo.mis-sp.org.br/audio/haja-pao-choro (Odeon 1944, Miécio Caffé collection), "Consulta local com agendamento". No audio files on the page.

#### Discoteca L. C. Vinholes (UFPel), blocked
- https://wp.ufpel.edu.br/discoteca/acervo/ returned 403.
- Sources: https://anppom.org.br/anais/anaiscongresso_anppom_2019/5682/public/5682-20777-1-PB.pdf and https://wp.ufpel.edu.br/spca/files/2018/11/a-coleção-de-fonogramas-de-78-rpm_Mario-Maia.pdf

#### Fundaj, Funarte/CNFCP, EBC, Goeldi, MIS-CE, IGTF, CELALI, Fonoteca Nacional, INAH, blocked
- Connection reset: fundaj.gov.br, villadigital.fundaj.gov.br, mediateca.inah.gob.mx.
- CONNECT 502: desenvolvimento.funarte.gov.br (WebFetch 403), museu-goeldi.br, mis.ce.gov.br, igtf.rs.gov.br, celali.chiapas.gob.mx, fonotecanacional.gob.mx, misrj.rj.gov.br.
- HTTP 502: memoria.ebc.com.br.
- HTTP 403: cnfcp.gov.br (https), bdlb.bn.gov.br, inah.gob.mx.
- Failed (000): brasiliana.usp.br, cachuera.org.br.
- Fundaj SIC email (from its 2019 ouvidoria report, search-only): sic@fundaj.gov.br.

#### Museu Théo Brandão (UFAL), search-only
- https://tribunadosertao.com.br/alagoas/2026/09/01/971158-museu-theo-brandao-promove-encontro-sobre-memoria-e-ancestralidade reports the public launch of sound recordings from the collection, Sept 2026. Online availability is unconfirmed. Recheck.

#### UFRJ: Luiz Heitor Corrêa de Azevedo collection, search-only
- 1940s field 78s from Goiás (1942), Ceará (1943) and Minas (1944), held at the UFRJ Laboratório de Etnomusicologia. No online catalogue found. https://www.unirio.br/caeg1/memoriasocial/dissertacoes/2007/219

#### Arquivo Nacional, search-only
- Fundo Agência Nacional sound catalogue (speeches): https://www.gov.br/arquivonacional/pt-br/servicos/copy_of_instrumentos-de-pesquisa/catlogo-de-documentos-sonoros.pdf

#### Internet Archive queries, verified
- `hessel-digital-library`: description "A preservation mirror of openly licensed and public domain collections… verbatim rights statements". Sampled items are `loc-jukebox-*`, so this is a mirror of LoC (known).
- Counts in `collection:78rpm`:
  - Mexican terms: 1,272 (georgeblood 644, hessel 574, BPL 174, Bowling Green 150).
  - Brazilian terms: 605 (Bowling Green 289, Druedahl 97, nsfa 86).
  - huapango / son jarocho / son huasteco / jarabe: 107. corrido: 99. Guty Cárdenas: 8.
- Search tool: the advancedsearch API, run through a small aggregator script in the scratchpad.

---

## 4. Suggested next steps
1. **Add the good fits.**
   - Bolivian 78s: IA query `uploader:78rpmbolivia@gmail.com`. Use the 87 CC-licensed items, streamed unmodified because of the ND licence.
   - UNM K'iche' narratives: OAI set `publication:laii_kichemaya`. Stream the MP4s as audio. These are spoken word, so they may suit a "stories" channel. Flag the ritual items.
   - Soule Archive pre-1971 items via the dLOC API: the 1955 Marie Richards songs and the 1968 items. Send a courtesy note to dloc@fiu.edu.
2. **Outreach, in priority order** (contact addresses as found on each institution's public pages):
   1. Biblioteca Nacional Aruba: arubiana@bna.aw or info@bibliotecanacional.aw. Ask for a CC licence on the `bnaitotromp` IA items from 1967–70, which are already on IA.
   2. Biblioteca Nacional de Panamá: referencia@binal.ac.pa. Ask about the Brenes/Zárate Documental de Música Folklórica Panameña. Also ask CREM to open its Zárate 1955 subset (CNRSMH_I_1960_003).
   3. MuseCom, Porto Alegre: musecom@sedac.rs.gov.br. Ask about the Casa A Electrica 78s (c. 1913–24). The per-item statement already allows non-commercial reproduction with credit, but the audio is not public.
   4. NAAM, Curaçao: info@naam.cw. Ask about the Zikinzá collection. Review the tambú material for sensitivity first.
   5. Ethnologisches Museum / Berliner Phonogramm-Archiv: em@smb.spk-berlin.de. Ask about the Koch-Grünberg 1911–13 cylinders. Respect the tracks already withheld as sensitive.
   6. Belize Archives and Records Service: info@archives.gov.bz. Ask about the 1957 mahogany-cutters' folk songs and the new Music Archive Lab.
   7. IEB-USP: arquivoieb@usp.br. Ask about the 1940 Turner disc and Mário de Andrade's records.
3. **Recheck from an unrestricted network** the hosts blocked here:
   - Cuban institutions, AGN/BNPHU in the Dominican Republic, BnH and the Archives nationales d'Haïti, Lameca, and the Martinique archives.
   - ABNB Bolivia, Funarte/CNFCP, Fundaj, Fonoteca Nacional and Mediateca INAH (Mexico).
   - The Costa Rican and Nicaraguan sound archives.
4. **Monitor**:
   - El Colegio de México CELL repository: CC BY MP3s with an API, but everything so far is from 1981 on.
   - Brasiliana Museus Tainacan API.
   - Museu Théo Brandão (Alagoas), which launched its sound recordings in September 2026.
   - Radio Nacional del Paraguay's planned record-library portal.

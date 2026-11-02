# Archive sweep: Latin America and the Caribbean

Date: 2026-11-02. Researcher: sub-agent sweep for Old Folk Air.

**Read this first: limits of this sweep**
- **The web-search budget ran out partway through.** The 200-search limit is shared across all agents in this turn. After that, the only checks were direct `curl` requests to hosts I already knew about. No search engines were scraped. Several countries got only host checks or background knowledge, and the checklist marks them "NOT SEARCHED (budget)". A follow-up pass is needed for Central America, Venezuela, Ecuador, Bolivia, Paraguay, Cuba, the Dominican Republic, Haiti (in part), the Guianas, the French Antilles archives, the Bahamas, Barbados and the European ethnological museums.
- **Many regional hosts are blocked or broken from the sandbox.** These are noted per archive:
  - Proxy refused the connection (502): fonotecanacional.gob.mx, trapalanda.bn.gov.ar, archivoide.pucp.edu.pe, api.dloc.com.
  - Connection reset: repositorio.inah.gob.mx.
  - TLS certificate-chain errors (verification was not disabled): cdm.gub.uy, babel.banrepcultural.org, museunacional.ufrj.br.
  - Bot checks (not bypassed): media.dlib.indiana.edu (Cloudflare "verify you are human") and repository.duke.edu (Anubis).
  - HTTP 403: floridamemory.com.
  - DNS failure for WebFetch only: manioc.org, ailla.utexas.org, nljdigital.nlj.gov.jm. `curl` reached all three.
- **Status labels.** **verified** = I fetched the pages, metadata or audio myself. **search-only** = from search results or institutional literature, not checked live. **blocked** = the host could not be reached as described above.

---

## 1. Ranked table

| # | Archive (country) | Content | Audio access | Rights | Machine access | Verdict | Status |
|---|---|---|---|---|---|---|---|
| 1 | **Autry Museum: Charles F. Lummis wax cylinders**, IA collection `autrynationalcenter` (via California Revealed). California, with Mexican/Californio Hispanic repertoire | ~362 audio items. Mostly 1904–05 Spanish-language Californio/Mexican folk songs (Manuela García, Adalaida Kamp, Rosendo Uruchurtu, Villa sisters…). Also Native American songs. | Direct MP3 on archive.org (HTTP 206 audio/mpeg tested) | Per item: "Public domain. No restrictions on use." + PDM 1.0. A few items are CC BY-NC-ND (e.g. WC.N.2) | IA advancedsearch / metadata API, no key | **Good fit** for the Hispanic songs. **Exclude or flag the Native American cylinders**: possible sacred content, and they belong to the North America sweep | verified |
| 2 | **Claremont Heritage: Padua Hills Theatre Tapes**, IA collection `claremontheritage` (California Revealed). Mexican Players, California | 44 tapes: rehearsals and performances of Mexican folk songs and dances (La Bamba, Las Posadas, zapateado jarocho), some field recordings. Theatre active 1931–74. Only 3 tapes carry dates (1954, 1962, 1968) | Direct MP3 (`*_access.mp3`), tested 206 | PDM 1.0, "Public domain. No restrictions on use." | IA API | **Good fit, with a date check**: most tapes are undated, so check each one against the ≤1970 cut-off | verified |
| 3 | IA collection `emilios78popular` ("Emilio's popular 78s"), a user upload | 1,222 items. ~51 match Latin terms (tango, criollo etc., 1920s–50s), mostly German/European popular 78s | Direct MP3 | Mostly PDM. Some CC BY-NC-ND. Applied by the uploader | IA API | Good fit (minor) for the pre-1971 Latin items. Same caveats as Great 78 | verified |
| 4 | **Instituto de Cultura Puertorriqueña: Archivo Virtual** (archivoicp.com), Puerto Rico | Edison Blue Amberol cylinders of the "Serie Puertorriqueña" (1909–13, from the AGPR: seis chorreao, seis bombeao, danzas, La Borinqueña). ICP LP series (Música Folklórica: plenas, jíbaro, cuatro, Monserrate Deliz children's songs; 1960s–80s) | Direct MP3 on Squarespace CDN, tested 206. No login | "Derechos Reservados © 2021 … reproducción sin autorización … prohibida". The 1909 US-recorded cylinders are probably public domain in the US, but ICP asserts rights | HTML only | **Ask permission**: grabaciones@icp.pr.gov | verified |
| 5 | **Señal Memoria (RTVC/INRAVISIÓN)**, Colombia | National radio and TV archive, >250,000 sound items. Radiodifusora Nacional (1940–), HJCK (1950–, >15,000 items incl. traditional Colombian music), CONTI archive | Curated pieces with direct MP3 on S3 (tested 206). The full catalogue sits behind an external catalogue and a "Licenciamiento" (licensing) service | No open licence on the pages. The CONTI radio archive was reportedly placed in the public domain by its owner | HTML. External catalogue not tested | **Ask permission**: senalmemoria@rtvc.gov.co | verified (site, MP3). Rights search-only |
| 6 | **Fonoteca Nacional**, Mexico | National sound archive. Fondo Beno Lieberman / Enrique Ramírez de Arellano / Eduardo Llerenas (regional Mexican music, UNESCO Memoria del Mundo 2016). Thomas Stanford collection (>5,000 field recordings, mostly Indigenous, MoW 2010). Fondo Ricardo Montejano (rural and Indigenous culture; a UCLA MEAP partnership is putting it online) | Online catalogue with built-in player (per literature). Most material on-site only (Audioteca Octavio Paz) | Unknown. Stanford material needs Indigenous-community care | Unknown | **Ask permission** (contact page on fonotecanacional.gob.mx). The Montejano material may surface via UCLA MEAP: check meap.library.ucla.edu | blocked (502). Search-only |
| 7 | **Fonoteca del INAH / Mediateca INAH** (now repositorio.inah.gob.mx), Mexico | ~20,000 recordings, field and published. Series *Testimonio Musical de México* (1964–, 65–76 volumes: Indigenous and mestizo music, e.g. Cora/Huichol, Totontepec band 1973). Raúl Hellmer collection | Islandora repository with booklets online. Audio availability not verified | INAH holds the economic rights ("titularidad de los derechos patrimoniales") | Islandora (OAI likely, not tested) | **Ask permission** (Fonoteca INAH). Indigenous material needs a community-sensitivity review | blocked (connection reset). Search-only |
| 8 | **INPI Fonoteca Henrietta Yurchenco / Archivo Etnográfico Audiovisual**, Mexico | 12,816 recordings of Indigenous peoples' music, incl. 1890s recordings and the 1970s Encuentros de Música y Danza Indígena (UNESCO MoW) | No confirmed online audio | Unknown. **Community/sacred restrictions likely** | – | Ask permission (low priority, sensitive) | search-only |
| 9 | **UWI St Augustine, Alma Jordan Library, UWISpace** (DSpace 7), Trinidad and Tobago | OP oral collection (~145 items, mostly 1980s–2000s oral history). Relevant: **OP61 Herskovits Toco songs c.1939** (15 cassettes). OP47 Trinidad Spanish peasants (1967–86, parang). OP41 Belize Black Carib (Garifuna) music (1988, too late) | MP3 bitstreams open without login, but OP61 has only a ~385 KB file (sounds like a short excerpt) | "Please contact the Main Library … for permission". "Available for research" | DSpace REST API (`/server/api`), no key | **Ask permission** (West Indiana & Special Collections, Alma Jordan Library). Only small amounts are in range | verified |
| 10 | **Instituto Nacional de Musicología "Carlos Vega"**, Argentina | 1,693 field discs (1930s–60s) by Carlos Vega and Isabel Aretz, plus tapes. A 2024–25 digitisation and cataloguing project is under way (UBA) | No online listening found | Unknown | – | **Ask permission** (inmcv.cultura.gob.ar). Strong content | blocked (inmcv host 000). Search-only |
| 11 | **Centro Nacional de Documentación Musical Lauro Ayestarán (CDM)**, Uruguay | >3,000 field recordings 1943–66 (Ayestarán). Digitised copies also exist at the Swedish Museum of Music (Scenkonstmuseet). The Library of Congress holds the Ayestarán score collection | Not confirmed online | Unknown | – | **Ask permission**: CDM (MEC Uruguay), also Scenkonstmuseet | blocked (TLS chain). Search-only |
| 12 | **Biblioteca Nacional de Chile: Archivo de Música** (beyond Memoria Chilena) + U. de Chile Archivo Sonoro de Música Tradicional Chilena + PUCV Fondo Margot Loyola | BN: Instituto de Investigaciones Musicales field recordings 1947–70, all regions incl. Mapuche. U. de Chile: restored 78s (Odeon/Victor tonadas, cuecas). PUCV: Loyola's field recordings | BN Digital (bibliotecanacionaldigital.gob.cl) reachable, but no audio collection identified | "Algunos derechos reservados" (BND footer) | BND HTML | **Ask permission** (BN Archivo de Música). Mapuche material needs community review | partly verified (BND reachable). Rest search-only |
| 13 | **Instituto de Etnomusicología PUCP (IDE)**, Peru | >1,550 audio recordings: Archivo de Música Tradicional Andina, Josafat Roel Pineda 1955–82 (Amazonas, Áncash, Cusco, Puno…), Arguedas-era recordings. Quechua/Aymara/Amazonian | AtoM catalogue (archivoide.pucp.edu.pe). Audio access unknown | Unknown. Indigenous content | AtoM (OAI-PMH possible) | **Ask permission** (IDE PUCP) | blocked (502). Search-only |
| 14 | **Biblioteca Nacional de Colombia: Centro de Documentación Musical** | ~20,000 audiovisual documents incl. field recordings. Part of the catalogue is on the open OPAC | Mainly on-site listening | Unknown | OPAC | Ask permission | blocked (bibliotecadigital host 000). Search-only |
| 15 | **Museu Nacional dos Povos Indígenas (ex-Museu do Índio, FUNAI)**, Brazil | ~300 sound documents (SPI fund, language documentation such as Mbyá-Guarani) | Tainacan catalogue. Audio not confirmed online | Indigenous material, **community consent needed** | Tainacan (has a REST API) | Ask permission (low priority): arquivo@museudoindio.gov.br | search-only. Tainacan host blocked |
| 16 | **Discoteca Oneyda Alvarenga (Centro Cultural São Paulo)**, Brazil | Missão de Pesquisas Folclóricas 1938 (Mário de Andrade): Northeast/North field recordings, ~7 h issued on CD in 2006 | No online streaming found | Municipal holding. The CD edition is commercial (SESC) | – | Ask permission (CCSP) | search-only |
| 17 | **AILLA** (UT Austin), pan-Latin America | Large archive of Indigenous-language recordings incl. songs. Four access levels | **Free account login required** to play or download even Level 1 files. Levels 2–4 are community-controlled | Per-file access levels | Islandora/new app. OAI unknown | Not usable for anonymous streaming. Possibly ask (with a community-consent policy) | search-only (host returns a JS app) |
| 18 | **Archivo RTA / Archivo Prisma** (Radio y Televisión Argentina) | Mostly TV video from the 1970s on, some audio | Streaming in page | "© Radio y Televisión Argentina … Todos los derechos reservados" | HTML | **Not usable**: all rights reserved and mostly post-1970 | verified |
| 19 | **National Library of Jamaica Digital Collection** (Omeka) | 3,462 items, **all still images** (API: Sound and Oral History item types = 0) | No audio | © NLJ, all rights reserved | Omeka REST API `/api/` | **Not usable** (no audio) | verified |
| 20 | **Manioc** (Université des Antilles: Martinique, Guadeloupe, Guyane) | 25,508 open documents. Its "Audios" section holds only recent Campus FM radio broadcasts | Streaming | – | HTML | **Not usable** (no pre-1971 music) | verified |
| 21 | IA `meridamexico` | 19 Mexican LPs, mostly 1970s–80s commercial | MP3 | No licence, in copyright | IA | Not usable (post-1970, commercial) | verified |
| 22 | **Duke: Radio Haiti Archive** | Radio Haïti-Inter audio 1957–2003, incl. Vodou programmes | Streaming | Unknown | – | Not usable/unverified: Anubis bot check; nearly all post-1970 | blocked |
| 23 | **Archive of Haitian Religion and Culture / Vodou Archive** (UF/Duke via dLOC) | Vodou ceremonies and interviews, 2010s | dLOC app | – | api.dloc.com blocked | **Not usable**: recent and sacred | search-only |
| 24 | **dLOC: Digital Library of the Caribbean** (UF) | Multi-island digital library. One known audio set: the folk-music record that came with *Jamaica Journal* 10:1 (1976) | JS app. API host blocked | Per item | api.dloc.com (blocked) | Unverified. Recheck when unblocked | blocked |
| 25 | **Indiana University Archives of Traditional Music / Media Collections Online** | Herskovits Suriname 1928–29, Haiti 1934, Trinidad 1939. Courlander Haiti. Laura Boulton etc. | Mostly reading-room listening. MCO behind a Cloudflare bot check | Some community restrictions | – | Not usable for streaming (on-site). Ask only for specific collections | blocked |
| 26 | **Florida Memory: Florida Folklife Collection** (State Archives of Florida) | Includes Bahamian, Cuban and other Caribbean-diaspora folk recordings from the 1930s on (background knowledge) | Unknown from sandbox | Florida state records are generally public (unverified) | – | Recheck: 403 from sandbox. Likely shared with the North America sweep | blocked |

---

## 2. Country checklist

| Country / territory | Result |
|---|---|
| **Mexico** (federal) | Fonoteca Nacional (blocked, ask), Fonoteca INAH (blocked, ask), INPI Fonoteca Yurchenco (search-only, sensitive). Memórica (memoricamexico.gob.mx) is reachable but has no confirmed audio. No IA collection of note: `meridamexico` is not usable. |
| Mexico (states / peoples) | State fonotecas not searched (budget). Maya, Nahua, Cora/Huichol, Zapotec etc. are represented in INAH *Testimonio Musical* and the INPI/Stanford collections. All of these are ask-permission with community review. Diaspora: Lummis cylinders and Padua Hills tapes on IA are **good fits**. |
| Guatemala | CIRMA (cirma.org.gt) reachable but a JS app; fonoteca not checked. Otherwise NOT SEARCHED (budget). |
| Belize | Only UWI OP41 Garifuna (1988, out of range). NOT SEARCHED further. |
| Honduras, El Salvador, Nicaragua, Costa Rica, Panama | NOT SEARCHED (budget). MUPI El Salvador host unreachable. |
| **Colombia** | Señal Memoria (ask, verified MP3s). BNC Centro de Documentación Musical (ask, blocked). Banco de la República virtual library (TLS error, unverified). |
| Venezuela | Biblioteca Nacional and Fundación Bigott hosts unreachable. NOT SEARCHED (budget). FUNDEF/Isabel Aretz material is partly mirrored at INMCV, Argentina. |
| Ecuador | NOT SEARCHED (budget). Nothing found. |
| **Peru** | IDE PUCP (blocked, ask). Biblioteca Nacional digital host unreachable. |
| Bolivia | MUSEF host unreachable. NOT SEARCHED (budget). |
| **Chile** (beyond Memoria Chilena) | BN Archivo de Música, U. de Chile AMTCh, PUCV Margot Loyola: all ask permission, search-only. |
| **Argentina** | INMCV Carlos Vega (ask). BN Mariano Moreno / Trapalanda (blocked, no audio confirmed). Archivo RTA (not usable). |
| **Uruguay** | CDM Lauro Ayestarán (ask; TLS error). Swedish Museum of Music holds digitised copies. |
| Paraguay | Biblioteca Nacional reachable (301), not explored. NOT SEARCHED (budget). |
| **Brazil** (beyond IMS / BN Digital) | Discoteca Oneyda Alvarenga (ask). Museu do Índio (ask, sensitive). IMMuB is a metadata catalogue, no audio. CNFCP: the *Documentário Sonoro* LPs are marked "restreint" at CREM (known source). Museu Nacional (TLS error; most Roquette-Pinto material lost or unclear after the 2018 fire). Funarte, EBC and Museu Goeldi hosts unreachable. IEB-USP and Arquivo Nacional SIAN reachable but not explored. |
| Guyana | National Archives host unreachable. NOT SEARCHED. |
| Suriname | Nationaal Archief Suriname reachable, not explored. Herskovits 1928–29 recordings at Indiana ATM (on-site). |
| French Guiana | Archives territoriales host unreachable. Manioc has no relevant audio. |
| Cuba | Biblioteca Nacional José Martí and Museo Nacional de la Música hosts unreachable. NOT SEARCHED further (FIU Díaz-Ayala is already known). |
| Haiti | Duke Radio Haiti (bot check, post-1970). Vodou Archive (sacred, recent). Lomax Haiti is already known (LoC/ACE). |
| Dominican Republic | AGN and Biblioteca Nacional PHU return 403. NOT SEARCHED further. |
| **Puerto Rico** | ICP Archivo Virtual (ask, verified MP3s incl. 1909 cylinders). AGPR Archivo de Música y Sonido holds the cylinders. FNCP (prpop.org) is news and biographies, with no archive audio seen. UPR repository returns 403. |
| Jamaica | NLJ Digital: images only, not usable. ACIJ/Jamaica Memory Bank not reached. |
| **Trinidad and Tobago** | UWISpace OP61 Herskovits 1939 (ask). |
| Martinique, Guadeloupe | Manioc not usable. Archives départementales/territoriales hosts unreachable. Lameca host unreachable. |
| Bahamas, Barbados, other Lesser Antilles | NOT SEARCHED (budget). Lomax Bahamas 1935 is already known (LoC). |
| Dutch Caribbean (Curaçao, Aruba, Bonaire) | NOT SEARCHED. Beeld en Geluid is reachable, but openbeelden.nl is unreachable. |
| US/European holders | Indiana ATM (blocked). AILLA (login). Duke (bot check). Florida Memory (403). Autry and Claremont via IA (**good**). UCLA MEAP (Montejano: unverified). Berlin Phonogramm-Archiv, Världskulturmuseerna (Nordenskiöld), quai Branly: NOT SEARCHED. |

---

## 3. Per-archive notes

### Autry Museum: Charles F. Lummis cylinders (IA `autrynationalcenter`), good fit, verified
- Example: https://archive.org/details/WC.C.21, "Este Beso al Despedirme, sung by Manuela Garcia", 1904. File `WC.C.21.mp3` streams (HTTP 206, audio/mpeg).
- Rights field: "Public domain. No restrictions on use." with licenseurl PDM 1.0. Exception: WC.N.2 is CC BY-NC-ND 4.0, which still allows non-commercial use.
- Query: `collection:autrynationalcenter AND mediatype:audio` gives 362 items. About 346 are wax cylinders (`identifier:WC.*`).
- Lummis also recorded Native American songs (Hopi, Navajo, Pueblo etc.) in this same series. Flag or exclude those under the sacred/sensitive rule.

### Claremont Heritage: Padua Hills Theatre Tapes (IA `claremontheritage`), good fit with a date check, verified
- Example: https://archive.org/details/caclah_000124, "Tape 33 – La Bamba", 1968, PDM. File `caclah_000124_access.mp3` streams.
- The collection has 44 Padua Hills tapes. Only 000093 (1954), 000110 (1962) and 000124 (1968) carry dates.
- Description: "rehearsal and performance recordings of the Mexican Players … based on Mexican folk songs and dances or other ancient Native Central American songs … some tapes also feature field recordings."

### IA `emilios78popular`, minor good fit, verified
- https://archive.org/details/emilios78popular
- Examples: "En aquel cuartito" (1928) and "Criolo Viejo" (1928), both PDM.
- Mostly German popular music, so filter for Latin items.

### Instituto de Cultura Puertorriqueña: Archivo Virtual, ask permission, verified
- Cylinder page: https://www.archivoicp.com/primeras-grabaciones-coleccion-de-cilindros. It has 8 direct MP3s, e.g. "BA 22516 Seis Chorreao (Gracia López & Jorge H. Santoni, Nueva York 1909)" and "BA 22522 Gira Campestre, seis bombeao".
- Folk series: https://www.archivoicp.com/escuchar-serie-de-musica-folklorica (one MP3 sample, plus a Spotify playlist).
- Footer: "La reproducción sin autorización del contenido queda totalmente prohibida … Derechos Reservados © 2021".
- Contact: grabaciones@icp.pr.gov (from https://www.archivoicp.com/contacto).
- Background: the cylinders were found in the Archivo General de Puerto Rico. More than 50 celluloid cylinders were digitised with Ramón Vázquez Lamboy (El Nuevo Día / Primera Hora reports).

### Señal Memoria (RTVC), Colombia, ask permission, verified
- Site: https://www.senalmemoria.co. Its audio pieces embed direct MP3s from `s3.amazonaws.com/rtvc-assets-senalmemoria.gov.co/...` (tested 206).
- The site advertises a "Licenciamiento" service and an external catalogue. Contact: senalmemoria@rtvc.gov.co.
- Background: over 250,000 sound items (MinTIC: https://www.mintic.gov.co/portal/inicio/Sala-de-prensa/Noticias/13842:Mas-de-250-mil-archivos-sonoros-y-radiales-se-custodian-desde-Senal-Memoria) and more than 15,000 HJCK registers (https://www.radionacional.co/cultura/la-hjck-vive-en-los-archivos-de-senal-memoria-con-mas-de-15000-registros-sonoros).

### Fonoteca Nacional (Mexico), ask permission, blocked
- Consultation guide for the Beno Lieberman, Enrique Ramírez de Arellano and Eduardo Llerenas fondos: https://fonotecanacional.gob.mx/publicaciones-digitales/Guia_BenoLlerenasLieberman_2022.pdf
- Montejano collection with UCLA MEAP: https://meap.library.ucla.edu/projects/the-ricardo-montejano-collection
- Press on putting the Montejano archive online: https://www.cronica.com.mx/cultura/fonoteca-nacional-pondra-linea-archivos-fondo-ricardo-montejano.html
- Stanford collection (>5,000 Indigenous field recordings, MoW 2010): https://www.elfinanciero.com.mx/culturas/fallece-el-etnomusicologo-thomas-stanford/

### Fonoteca INAH / Mediateca INAH, ask permission, blocked
- Mediateca booklet example: https://mediateca.inah.gob.mx/islandora_74/islandora/object/disco:31/datastream/PDF/view
- Rights ownership by the Fonoteca INAH appears in an INAH request letter: https://datos.inah.gob.mx/sites/default/files/solicitudes/num-de-oficio-dg-mh-0308-2024-asunto-solicitud-de-autorizacion-a-mediateca-inah-para-la-utilizacion-de-recursos-digitales-relativos-a-raul-hellmer.pdf
- INAH bulletin on the series: https://inah.gob.mx/images/boletines/2023/617/Boletin%20617.pdf

### INPI Fonoteca Henrietta Yurchenco, sensitive, search-only
- https://mexiconewsdaily.com/culture/digitizing-mexicos-indigenous-history/
- Article on the archive's history: https://biblat.unam.mx/en/revista/iztapalapa/articulo/del-registro-al-archivo-el-devenir-de-la-imagen-etnografica-en-el-instituto-nacional-indigenista

### UWISpace (UWI St Augustine), ask permission, verified
- OP61 record: https://uwispace.sta.uwi.edu/items/916158dc-2549-4b07-bd2c-205f80ecb8f5. Metadata reads: "These tapes relate to the work done by anthropologist Melville Herskovits in Toco during the late 1930s…"
- `dc.date.copyright`: "Please contact the Main Library … for permission to use the digitized images."
- Bitstream `op61-1_of_15.mp3` is 384,939 bytes and opens without login.
- API: `https://uwispace.sta.uwi.edu/server/api/discover/search/objects?query=dc.type:"Recording, oral"`. This returns 2,736 items, most of them "Caribbean Report" BBC broadcasts from 1988–2002.

### INMCV Carlos Vega (Argentina), ask permission, search-only
- UBA digitisation seminar (2025): https://artes-hml.filo.uba.ar/sites/artes.filo.uba.ar/files/%5BPST%5D%20Preservacio%CC%81n%20de%20archivos%20sonoros%20de%20corte%20etnogra%CC%81fico.%20Digitalizacio%CC%81n%20y%20catalogacio%CC%81n%20de%20los%20registros%20sonoros%20de%20campo%20del%20Instituo%20Nacional%20de%20Musicologi%CC%81a%20Carlos%20Vega%20-%202025.pdf
- Clang article (CC licensed): https://papelcosido.fba.unlp.edu.ar/ojs/index.php/clang/article/download/1910/1808/4471

### CDM Lauro Ayestarán (Uruguay), ask permission, search-only
- Article on the Swedish digitisations and field recordings: https://ojs.fhce.edu.uy/index.php/cont/article/download/1046/1185/3514
- CDM invitation: https://cdf.montevideo.gub.uy/system/files/descargas_exposiciones/invitacionelectronica_cdm.pdf

### Chile: BN Archivo de Música / U. de Chile / PUCV, ask permission, search-only
- IAML Chile directory: https://www.iaml.info/chile
- Revista Musical Chilena on the restored 78s: https://actascoloquiogiannini.uchile.cl/index.php/RMCH/article/viewFile/12743/13032
- BND (reachable; footer "Algunos derechos reservados"): https://www.bibliotecanacionaldigital.gob.cl

### IDE PUCP (Peru), ask permission, blocked
- https://archivoide.pucp.edu.pe/index.php/coleccion-josafat-roel-pineda
- Sonidos Andinos booklet: https://repositorio.pucp.edu.pe/index/handle/123456789/136395

### BNC Centro de Documentación Musical (Colombia), search-only
- https://cambiocolombia.com/cultura/articulo/2026/5/el-centro-de-documentacion-musical-50-anos-guardando-el-alma-sonora-de-colombia

### Museu do Índio / Museu Nacional dos Povos Indígenas, search-only
- https://www.gov.br/museudoindio/pt-br/assuntos/acervos/arquivistico. Contact: arquivo@museudoindio.gov.br

### Discoteca Oneyda Alvarenga / Missão 1938, search-only
- SESC article: https://portal.sescsp.org.br/online/artigo/compartilhar/13117_DISCOTECA+ONEYDA+ALVARENGA
- CREM (known source) entry for the 6-CD box set, marked as an edited, restricted release: https://archives.crem-cnrs.fr/archives/collections/CNRSMH_E_2009_001_001/

### AILLA, login required, search-only
- Access levels are explained here: https://podcasts.la.utexas.edu/the-benson-at-100/tag/ailla/ and https://islandora-ailla.lib.utexas.edu

### Archivo RTA (Argentina), not usable, verified
- https://www.archivorta.com.ar. Footer: "© Copyright 2021 Radio y Televisión Argentina S.E. – Todos los derechos reservados". The music category is almost entirely post-1975 video.

### NLJ Digital (Jamaica), not usable, verified
- https://nljdigital.nlj.gov.jm. The `GET /api/item_types` call shows Sound = 0, Oral History = 0 and Still Image = 3,462. Footer: "© Copyright NLJ. All Rights Reserved".

### Manioc, not usable, verified
- https://www.manioc.org/sons: "les émissions phares de Campus FM", meaning recent campus radio programmes.

### Duke Radio Haiti and the Vodou Archive, not usable, blocked or search-only
- Example Radio Haiti item: https://repository.duke.edu/dc/radiohaiti/RL10059-RR-0079_01 (Anubis bot check)
- Vodou Archive press release: https://www.latam.ufl.edu/media/latamufledu/press-releases/ArchiveofHaitianReligionandCulture_001d.pdf

### dLOC / Indiana ATM / Florida Memory, blocked
- dLOC search is a JS app and api.dloc.com is denied by the proxy.
- MCO (media.dlib.indiana.edu) shows a Cloudflare challenge.
- floridamemory.com returns 403.
- ATM background: https://en.wikipedia.org/wiki/Archives_of_Traditional_Music

---

## 4. Suggested next steps
1. Add the IA collections `autrynationalcenter` (Hispanic cylinders only), `claremontheritage` (Padua Hills, dated tapes ≤1970) and the Latin items in `emilios78popular`.
2. Send outreach emails, in priority order:
   - ICP Puerto Rico (grabaciones@icp.pr.gov): 1909 cylinders and folk LPs.
   - Señal Memoria (senalmemoria@rtvc.gov.co).
   - INMCV Argentina.
   - CDM Uruguay.
   - Fonoteca Nacional and Fonoteca INAH (Mexico).
   - BN Chile Archivo de Música.
   - IDE PUCP.
   - UWI Alma Jordan Library (OP61).
3. Rerun with a fresh search budget, and with the blocked hosts allowed, for the countries marked "NOT SEARCHED". Also cover the European holders: Berlin Phonogramm-Archiv (Koch-Grünberg, Venezuela/Brazil 1911), Världskulturmuseerna (Nordenskiöld, Chaco), Scenkonstmuseet (Ayestarán copies) and Beeld en Geluid (Suriname/Antilles).

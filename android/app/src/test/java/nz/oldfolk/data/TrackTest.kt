package nz.oldfolk.data

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class TrackTest {
    private val locJson = """{"i":"loc:flwpa000003","f":"loc.afc.afcflwpa.3135a1.mp3","t":"Gonna See My Long-Haired Babe",
        "a":"Zora Neale Hurston","y":1939,"r":"USA","g":"field",
        "u":"https://tile.loc.gov/storage-services/media/ingested/api/loc.afc.afcflwpa.3135a1.mp3",
        "w":"https://www.loc.gov/item/flwpa000003/"}"""

    @Test
    fun recordingsFromOtherArchivesUseTheirOwnUrls() {
        val t = Track.fromJson(JSONObject(locJson))
        assertFalse(t.onArchiveOrg)
        assertEquals("https://tile.loc.gov/storage-services/media/ingested/api/loc.afc.afcflwpa.3135a1.mp3", t.streamUrl)
        assertEquals("https://www.loc.gov/item/flwpa000003/", t.detailsUrl)
        assertEquals("Library of Congress", t.sourceName)
        assertNull(t.artUrl)     // never the Internet Archive's thumbnail service
    }

    @Test
    fun creditedInstitutionIsShown() {
        val t = Track.fromJson(JSONObject("""{"i":"finna:sks.1","f":"a.mp3","t":"Itkuvirsi","r":"Nordic","g":"european",
            "u":"https://yksa.disec.fi/Yksa4/download/1/file/a","w":"https://www.finna.fi/Record/sks.1",
            "s":"Finnish Literature Society"}"""))
        assertEquals("Finnish Literature Society", t.sourceName)
        assertEquals(t, Track.fromJson(t.toJson()))
    }

    @Test
    fun externalUrlsSurviveSavingAndLoading() {
        val t = Track.fromJson(JSONObject(locJson))
        val back = Track.fromJson(t.toJson())
        assertEquals(t, back)
        assertEquals(t.key, back.key)
    }

    @Test
    fun indexIncludesExtShards() {
        val index = TrackIndex.parse("""{"v":1,"genres":[{"k":"field","name":"Field","n":2}],
            "shards":[{"f":"s-field-1.json","g":"field","n":1,"sha256":"aa"}],
            "ext":[{"f":"x-field-1.json","g":"field","n":1,"sha256":"bb"}]}""")
        assertEquals(listOf("s-field-1.json", "x-field-1.json"), index.shards.map { it.file })
        // Older lists without "ext" still parse.
        val old = TrackIndex.parse("""{"v":1,"genres":[],"shards":[]}""")
        assertEquals(0, old.shards.size)
    }
}

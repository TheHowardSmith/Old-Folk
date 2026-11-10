package nz.oldfolk.ui

import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import nz.oldfolk.R
import org.json.JSONObject

/** One region of the map: its key and outline, in map units. */
class AtlasRegion(val key: String, val path: Path)

/**
 * The world map on the Genres screen (res/raw/region_map.json, made by
 * builder/scripts/make_map.py): an equirectangular outline of each map region,
 * plus the land outside every region.
 */
class Atlas(val width: Float, val height: Float, val regions: List<AtlasRegion>, val land: Path) {
    /** The region under a point in map units, or null. */
    fun regionAt(x: Float, y: Float): String? {
        for (r in regions) {
            val p = r.path.asAndroidPath()
            val bounds = RectF().also { p.computeBounds(it, true) }
            if (!bounds.contains(x, y)) continue
            val clip = android.graphics.Region(bounds.left.toInt() - 1, bounds.top.toInt() - 1,
                bounds.right.toInt() + 1, bounds.bottom.toInt() + 1)
            val hit = android.graphics.Region().apply { setPath(p, clip) }
            if (hit.contains(x.toInt(), y.toInt())) return r.key
        }
        return null
    }

    companion object {
        fun parse(json: String): Atlas {
            val o = JSONObject(json)
            val parser = PathParser()
            fun path(d: String): Path = if (d.isBlank()) Path() else parser.parsePathString(d).toPath()
            val rg = o.getJSONArray("regions")
            return Atlas(
                width = o.getDouble("w").toFloat(),
                height = o.getDouble("h").toFloat(),
                regions = List(rg.length()) { rg.getJSONObject(it).let { r -> AtlasRegion(r.getString("k"), path(r.getString("d"))) } },
                land = path(o.optString("land")),
            )
        }
    }
}

@Composable
fun rememberAtlas(): Atlas? {
    val context = LocalContext.current
    return remember {
        runCatching {
            context.resources.openRawResource(R.raw.region_map).bufferedReader().use { Atlas.parse(it.readText()) }
        }.getOrNull()
    }
}

/**
 * An old school atlas in the radio's palette. Chosen regions are tinted gold; when some
 * are chosen the rest fade back a little. Tapping a region toggles it.
 */
@Composable
fun RegionMap(atlas: Atlas, selected: Set<String>, available: Set<String>, onToggle: (String) -> Unit,
              modifier: Modifier = Modifier) {
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val sea = if (dark) Color(0xFF2A1E15) else Color(0xFFEFE3CB)
    val land = if (dark) Color(0xFF3E2E21) else Color(0xFFE3D2B1)
    val ink = if (dark) Color(0xFFCDAE6E) else Color(0xFF6A4529)
    val on = if (dark) Color(0xFFD9A94F) else Color(0xFFC9A050)
    val grat = if (dark) Color(0x24CDAE6E) else Color(0x40B08A45)
    val brass = LocalRadioColors.current.brass
    val toggle by rememberUpdatedState(onToggle)
    val shape = RoundedCornerShape(10.dp)
    Canvas(
        modifier
            .fillMaxWidth()
            .aspectRatio(atlas.width / atlas.height)
            .clip(shape)
            .border(2.dp, brass, shape)
            .semantics { contentDescription = "World map. Tap a region to play music from there; the list below does the same." }
            .pointerInput(atlas) {
                detectTapGestures { pos: Offset ->
                    val k = atlas.regionAt(pos.x / size.width * atlas.width, pos.y / size.height * atlas.height)
                    if (k != null && k in available) toggle(k)
                }
            },
    ) {
        drawRect(sea)
        val sx = size.width / atlas.width
        val sy = size.height / atlas.height
        val hair = 1f / sx                                   // about one pixel, in map units
        scale(sx, sy, pivot = Offset.Zero) {
            // Graticule every 30 degrees (the map runs 80N to 56S).
            for (lat in listOf(60, 30, 0, -30)) {
                val y = (80 - lat) / 136f * atlas.height
                drawLine(grat, Offset(0f, y), Offset(atlas.width, y), strokeWidth = hair)
            }
            for (lon in -150..150 step 30) {
                val x = (lon + 180) / 360f * atlas.width
                drawLine(grat, Offset(x, 0f), Offset(x, atlas.height), strokeWidth = hair)
            }
            drawPath(atlas.land, land)
            drawPath(atlas.land, ink.copy(alpha = 0.3f), style = Stroke(hair * 0.8f))
            val any = selected.isNotEmpty()
            for (r in atlas.regions) {
                val chosen = r.key in selected
                when {
                    chosen -> { drawPath(r.path, land); drawPath(r.path, on.copy(alpha = 0.6f)) }
                    any -> drawPath(r.path, land.copy(alpha = 0.6f))
                    else -> drawPath(r.path, land)
                }
                drawPath(r.path, ink.copy(alpha = if (chosen) 0.9f else 0.45f), style = Stroke(hair * (if (chosen) 1.4f else 0.9f)))
            }
        }
    }
}

package nz.oldfolk.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.min

/**
 * Header and bottom-bar band: oxblood bakelite with an art-deco scallop
 * pattern (two offset rows of fan arcs), as on the website.
 */
fun Modifier.decoBand(top: Color, bottom: Color, line: Color): Modifier = drawWithCache {
    val tileW = 32.dp.toPx()
    val tileH = 16.dp.toPx()
    val r1 = 9.5.dp.toPx()
    val r2 = 14.5.dp.toPx()
    val stroke = Stroke(1.dp.toPx())
    val base = Brush.verticalGradient(listOf(top, bottom))
    onDrawBehind {
        drawRect(base)
        clipRect {
        var row = 0
        var y = 0f
        while (y < size.height + tileH) {
            val shift = if (row % 2 == 0) 0f else tileW / 2
            var x = -tileW + shift
            while (x < size.width + tileW) {
                val c = Offset(x + tileW / 2, y + tileH)
                for (r in floatArrayOf(r1, r2)) {
                    drawArc(line, 180f, 180f, false, topLeft = Offset(c.x - r, c.y - r), size = Size(2 * r, 2 * r), style = stroke)
                }
                x += tileW
            }
            y += tileH / 2
            row++
        }
        }
    }
}

/**
 * Vox-style diamond grille cloth: a checkerboard of diamonds (every other one
 * patterned) outlined by fawn threads, with a fine basket weave over it.
 */
fun Modifier.voxCloth(base: Color, fawn: Color, mid: Color, hi: Color, tile: Dp = 26.dp): Modifier = drawWithCache {
    val t = tile.toPx()
    val w = size.width
    val h = size.height
    fun diamond(cx: Float, cy: Float, r: Float) = Path().apply {
        moveTo(cx, cy - r); lineTo(cx + r, cy); lineTo(cx, cy + r); lineTo(cx - r, cy); close()
    }
    val outer = Path()
    val inner = Path()
    val core = Path()
    var cy = t / 2
    while (cy < h + t) {
        var cx = t / 2
        while (cx < w + t) {
            outer.addPath(diamond(cx, cy, t / 2))
            inner.addPath(diamond(cx, cy, t / 4))
            core.addPath(diamond(cx, cy, t * 3 / 28))
            cx += t
        }
        cy += t
    }
    // The fawn threads: two families of diagonals through the diamond corners.
    val lattice = Path()
    var k = -h
    while (k < w + h) {
        lattice.moveTo(k + t / 2, 0f); lattice.lineTo(k + t / 2 - h, h)
        lattice.moveTo(k + t / 2, 0f); lattice.lineTo(k + t / 2 + h, h)
        k += t
    }
    val weave = Path()
    val ws = 2f * t / 28f
    var y = 0f
    while (y < h) { weave.moveTo(0f, y); weave.lineTo(w, y); y += ws }
    var x = ws / 2
    while (x < w) { weave.moveTo(x, 0f); weave.lineTo(x, h); x += ws }
    val thread = Stroke(t * 2.4f / 28f)
    val fine = Stroke(t * 0.8f / 28f)
    onDrawBehind {
        drawRect(base)
        drawPath(outer, mid)
        drawPath(inner, hi)
        drawPath(core, base)
        drawPath(lattice, fawn, style = thread)
        drawPath(weave, Color.Black.copy(alpha = 0.16f), style = fine)
    }
}

/** Fluted bakelite knob face: ridged rim, domed cap, soft highlight; ivory with a brass ring for the main knob. */
fun DrawScope.flutedKnob(primary: Boolean, rc: RadioColors) {
    val r = size.minDimension / 2
    val c = center
    val flutes = 44
    val (lo, hi) = if (primary) Color(0xFFD6C5A3) to Color(0xFFEFE4CC) else rc.knobFluteLo to rc.knobFluteHi
    for (i in 0 until flutes) {
        drawArc(if (i % 2 == 0) lo else hi, i * 360f / flutes, 360f / flutes, true)
    }
    drawCircle(Color.Black.copy(alpha = 0.5f), r, c, style = Stroke(1.5.dp.toPx()))
    if (primary) {
        drawCircle(Color.Black.copy(alpha = 0.4f), r * 0.63f, c)
        drawCircle(Color(0xFFB08A45), r * 0.61f, c)
        drawCircle(Color(0xFFF4EBD8), r * 0.56f, c)
    } else {
        drawCircle(Color.Black.copy(alpha = 0.55f), r * 0.63f, c)
        drawCircle(rc.knobCap, r * 0.6f, c)
    }
    drawCircle(
        Brush.radialGradient(
            listOf(Color.White.copy(alpha = if (primary) 0.55f else 0.3f), Color.Transparent),
            center = Offset(c.x - r * 0.16f, c.y - r * 0.4f), radius = r * 0.84f,
        ),
        r, c,
    )
}

/**
 * A 78 rpm record drawn in full (for tracks without a picture): grooves with
 * gaps between the tracks, a red label with the name lettered around it, and
 * a fixed light sheen.
 */
@Composable
fun Record78(modifier: Modifier = Modifier) {
    val measurer = rememberTextMeasurer()
    Canvas(modifier) {
        val r = min(size.width, size.height) / 2 * 0.98f
        val c = center
        // soft shadow under the disc
        drawCircle(Brush.radialGradient(listOf(Color(0x59281405), Color.Transparent), center = c + Offset(0f, r * 0.08f), radius = r * 1.08f),
            r * 1.08f, c + Offset(0f, r * 0.08f))
        drawCircle(Color(0xFF16100C), r, c)
        drawCircle(Color(0xFF3A2C22), r * 0.99f, c, style = Stroke(r * 0.012f))
        var gr = r * 0.97f
        var i = 0
        val groove = Stroke(r * 0.0056f)
        while (gr > r * 0.45f) {
            drawCircle(Color.White.copy(alpha = if (i % 2 == 0) 0.035f else 0.05f), gr, c, style = groove)
            gr -= r * 0.0138f; i++
        }
        for (g in floatArrayOf(0.84f, 0.71f, 0.58f)) drawCircle(Color.Black.copy(alpha = 0.55f), r * g, c, style = Stroke(r * 0.016f))
        // sheen: two opposite wedges of light that don't turn with the disc
        val sheen = Brush.radialGradient(
            0.45f to Color.Transparent, 0.62f to Color.White.copy(alpha = 0.06f), 0.97f to Color.White.copy(alpha = 0.02f), 1f to Color.Transparent,
            center = c, radius = r,
        )
        drawArc(sheen, -150f, 35f, true, topLeft = c - Offset(r, r), size = Size(2 * r, 2 * r))
        drawArc(sheen, 30f, 35f, true, topLeft = c - Offset(r, r), size = Size(2 * r, 2 * r))
        // label
        val lr = r * 0.43f
        drawCircle(Brush.radialGradient(0.55f to Color(0xFFB23A1E), 1f to Color(0xFF7E2412), center = c, radius = lr), lr, c)
        drawCircle(Color(0xFFE3C27A), lr * 0.94f, c, style = Stroke(r * 0.009f))
        drawCircle(Color(0xFFE3C27A), lr * 0.52f, c, style = Stroke(r * 0.006f))
        val ink = Color(0xFFF4E3BF)
        val px = r / 100f   // one unit of the 200-unit SVG record on the website
        arcText(measurer, "OLD FOLK AIR", TextStyle(color = ink, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
            fontSize = (8.6f * px).toSp(), letterSpacing = (2f * px).toSp()), c, 29f * px, -90f, outward = true)
        arcText(measurer, "· 78 R.P.M. ·", TextStyle(color = ink, fontFamily = FontFamily.Serif,
            fontSize = (6.6f * px).toSp(), letterSpacing = (2.2f * px).toSp()), c, 34f * px, 90f, outward = false)
        val small = TextStyle(color = ink, fontFamily = FontFamily.Serif, fontSize = (5f * px).toSp(), letterSpacing = (1f * px).toSp())
        centredText(measurer, "ENDLESS", small, Offset(c.x, c.y - 8f * px))
        centredText(measurer, "RADIO", small, Offset(c.x, c.y + 13f * px))
        drawCircle(Color(0xFF0B0806), 3.4f * px, c)
    }
}

private fun DrawScope.centredText(m: TextMeasurer, text: String, style: TextStyle, baseline: Offset) {
    val l = m.measure(text, style)
    drawText(l, topLeft = Offset(baseline.x - l.size.width / 2f, baseline.y - l.firstBaseline))
}

/**
 * Letters around a circle, centred on [centreDeg] (0 = right, 90 = bottom).
 * outward = true: tops face out, reading clockwise (top of a label);
 * false: tops face the centre, reading left to right along the bottom.
 */
private fun DrawScope.arcText(
    m: TextMeasurer, text: String, style: TextStyle, c: Offset, radius: Float, centreDeg: Float, outward: Boolean,
) {
    val spacing = with(this) { if (style.letterSpacing == TextUnit.Unspecified) 0f else style.letterSpacing.toPx() }
    val glyphs = text.map { m.measure(it.toString(), style.copy(letterSpacing = 0.sp)) }
    val widths = glyphs.map { it.size.width.toFloat() }
    val total = widths.sum() + spacing * (glyphs.size - 1)
    val sweep = Math.toDegrees((total / radius).toDouble()).toFloat()
    var pos = 0f
    glyphs.forEachIndexed { i, g ->
        val mid = pos + widths[i] / 2
        val a = Math.toDegrees((mid / radius).toDouble()).toFloat()
        val angle = if (outward) centreDeg - sweep / 2 + a else centreDeg + sweep / 2 - a
        rotate(if (outward) angle + 90f else angle - 90f, pivot = c) {
            val y = if (outward) c.y - radius - g.firstBaseline else c.y + radius - g.firstBaseline
            drawText(g, topLeft = Offset(c.x - widths[i] / 2, y))
        }
        pos += widths[i] + spacing
    }
}

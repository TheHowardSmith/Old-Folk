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
 * Vox-style grille cloth, kept faded: a plain brown weave crossed by large
 * diamonds of fine triple threads (gold, teal, maroon).
 */
fun Modifier.voxCloth(base: Color, gold: Color, teal: Color, maroon: Color, tileW: Dp = 40.dp, tileH: Dp = 33.dp): Modifier = drawWithCache {
    val tw = tileW.toPx()
    val th = tileH.toPx()
    val w = size.width
    val h = size.height
    val gap = 1.3.dp.toPx()
    // Diagonals through the diamond corners: y = th * (x / tw - k) and y = th * (k - x / tw), k a half-integer.
    fun threads(dy: Float) = Path().apply {
        val kMin = -(h / th).toInt() - 2
        val kMax = (w / tw).toInt() + (h / th).toInt() + 3
        for (n in kMin..kMax) {
            val k = n + 0.5f
            moveTo(0f, th * (0f - k) + dy); lineTo(w, th * (w / tw - k) + dy)
            moveTo(0f, th * k + dy); lineTo(w, th * (k - w / tw) + dy)
        }
    }
    val lines = listOf(gold to threads(-gap), teal to threads(0f), maroon to threads(gap))
    val weave = Path()
    val ws = 2.dp.toPx()
    var y = 0f
    while (y < h) { weave.moveTo(0f, y); weave.lineTo(w, y); y += ws }
    var x = ws / 2
    while (x < w) { weave.moveTo(x, 0f); weave.lineTo(x, h); x += ws }
    val fine = Stroke(0.6.dp.toPx())
    val thread = Stroke(0.75.dp.toPx())
    onDrawBehind {
        drawRect(base)
        drawPath(weave, Color.Black.copy(alpha = 0.1f), style = fine)
        clipRect { for ((c, path) in lines) drawPath(path, c, style = thread) }
    }
}

/** Flat-faced bakelite knob with a slight bevel (caramel); the main knob in ivory with a brass ring. */
fun DrawScope.flutedKnob(primary: Boolean, rc: RadioColors) {
    val r = size.minDimension / 2
    val c = center
    val (hi, mid, lo) = if (primary) Triple(Color(0xFFF5EDDC), Color(0xFFEADFC7), Color(0xFFDDCDAC))
        else Triple(rc.knobFluteHi, rc.knobCap, rc.knobFluteLo)
    drawCircle(Brush.verticalGradient(0f to hi, 0.55f to mid, 1f to lo, startY = c.y - r, endY = c.y + r), r, c)
    if (primary) {
        drawCircle(Color(0xFFB08A45), r - 1.5.dp.toPx(), c, style = Stroke(3.dp.toPx()))
        drawCircle(Color.Black.copy(alpha = 0.15f), r - 3.5.dp.toPx(), c, style = Stroke(1.dp.toPx()))
    } else {
        // thin light edge along the top, like a bevel
        drawArc(Color(0x40FFF0D7), 200f, 140f, false, topLeft = c - Offset(r - 1.dp.toPx(), r - 1.dp.toPx()),
            size = Size(2 * r - 2.dp.toPx(), 2 * r - 2.dp.toPx()), style = Stroke(1.dp.toPx()))
    }
    // dark outer edge so the knob stands clear of the cloth
    drawCircle(Color(0x99190E06), r - 0.75.dp.toPx(), c, style = Stroke(1.5.dp.toPx()))
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

/**
 * Screen background: faint paper (light theme) — fine fibres and speckle on
 * cream — or faint wood grain (dark theme) on the dark walnut colour.
 */
fun Modifier.screenTexture(base: Color, dark: Boolean): Modifier = drawWithCache {
    val w = size.width
    val h = size.height
    val rnd = kotlin.random.Random(11)
    val strokes = mutableListOf<Triple<Path, Color, Float>>()
    if (dark) {
        // long, slightly wavy grain lines across the screen
        val count = (h / 12.dp.toPx()).toInt().coerceIn(10, 90)
        repeat(count) {
            val y0 = rnd.nextFloat() * h
            val amp = (1f + rnd.nextFloat() * 4f).dp.toPx()
            val freq = (2 * Math.PI / (220 + rnd.nextFloat() * 380).dp.toPx()).toFloat()
            val phase = rnd.nextFloat() * 6.28f
            val path = Path()
            path.moveTo(0f, y0 + amp * kotlin.math.sin(phase))
            var x = 0f
            val step = 14.dp.toPx()
            while (x < w) { x += step; path.lineTo(x, y0 + amp * kotlin.math.sin(x * freq + phase)) }
            val light = rnd.nextFloat() < 0.35f
            strokes += Triple(path,
                if (light) Color(0xFFE8C9A0).copy(alpha = 0.006f + rnd.nextFloat() * 0.012f)
                else Color.Black.copy(alpha = 0.01f + rnd.nextFloat() * 0.02f),
                (0.6f + rnd.nextFloat() * 1.8f).dp.toPx())
        }
    } else {
        // short paper fibres in every direction
        val count = ((w * h) / (18.dp.toPx() * 18.dp.toPx())).toInt().coerceIn(200, 3000)
        repeat(count) {
            val x = rnd.nextFloat() * w
            val y = rnd.nextFloat() * h
            val len = (3f + rnd.nextFloat() * 9f).dp.toPx()
            val a = rnd.nextFloat() * 6.28f
            val path = Path()
            path.moveTo(x, y)
            path.quadraticTo(x + len / 2 * kotlin.math.cos(a) + rnd.nextFloat() * 4f, y + len / 2 * kotlin.math.sin(a),
                x + len * kotlin.math.cos(a), y + len * kotlin.math.sin(a))
            strokes += Triple(path,
                if (rnd.nextFloat() < 0.6f) Color(0xFF8A6A40).copy(alpha = 0.035f + rnd.nextFloat() * 0.04f)
                else Color.White.copy(alpha = 0.18f + rnd.nextFloat() * 0.2f),
                (0.5f + rnd.nextFloat() * 0.6f).dp.toPx())
        }
    }
    // speckle
    val specks = List(((w * h) / (9.dp.toPx() * 9.dp.toPx())).toInt().coerceIn(300, 9000)) {
        Offset(rnd.nextFloat() * w, rnd.nextFloat() * h)
    }
    val speckColor = if (dark) Color.Black.copy(alpha = 0.1f) else Color(0xFF6B4E2C).copy(alpha = 0.07f)
    val shade = Brush.radialGradient(
        listOf(Color.Transparent, Color.Black.copy(alpha = if (dark) 0.14f else 0.04f)),
        center = Offset(w / 2, h * 0.4f), radius = kotlin.math.max(w, h) * 0.85f,
    )
    onDrawBehind {
        drawRect(base)
        for ((p, c, sw) in strokes) drawPath(p, c, style = Stroke(sw))
        drawPoints(specks, androidx.compose.ui.graphics.PointMode.Points, speckColor, strokeWidth = 1.dp.toPx())
        drawRect(shade)
    }
}

package nz.oldfolk.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

private const val DIAL_START = 1895f
private const val DIAL_SPAN = 80f   // 1895..1975

/**
 * A radio tuning dial marked in decades, with the station plate and valve
 * lamp set into its bezel. The needle sweeps to the recording's year (shown
 * riding above it); undated 78s show a soft band across the 78 rpm era.
 */
@Composable
fun TuningDial(year: Int?, station: String, playing: Boolean, modifier: Modifier = Modifier) {
    val rc = LocalRadioColors.current
    val target = year?.let { ((it.coerceIn(1895, 1975) - DIAL_START) / DIAL_SPAN) }
    val pos by animateFloatAsState(target ?: 0.5f, tween(900, easing = FastOutSlowInEasing), label = "needle")
    val measurer = rememberTextMeasurer()
    val labelStyle = TextStyle(fontSize = 11.sp, color = rc.dialInk.copy(alpha = 0.92f), fontFamily = androidx.compose.ui.text.font.FontFamily.Serif)
    val yearStyle = TextStyle(fontSize = 13.sp, color = rc.needle, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
        fontFamily = androidx.compose.ui.text.font.FontFamily.Serif)
    val lit by animateFloatAsState(if (playing) 1f else 0f, tween(800), label = "backlight")
    val shape = RoundedCornerShape(8.dp)
    WoodCabinet(modifier.fillMaxWidth()) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Brush.verticalGradient(listOf(rc.dialFace, rc.dialFaceBottom)))
            .glassSetIn()
            .border(1.5.dp, Brush.horizontalGradient(listOf(Color(0xFF8C6B33), Color(0xFFD9B872), Color(0xFF8C6B33))), shape)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .semantics { contentDescription = (year?.let { "Recorded around $it" } ?: "78 rpm era, date unknown") + ". $station" },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ValveLamp(on = playing)
            Spacer(Modifier.width(6.dp))
            Text(station.uppercase(), style = PlateLabel, color = rc.dialInk, maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
        }
        Canvas(Modifier.fillMaxWidth().height(66.dp)) {
            val w = size.width
            val h = size.height
            val cordY = 19.dp.toPx()
            val baseY = h * 0.72f
            // Backlight glowing up through the glass, brighter while playing.
            drawRect(Brush.radialGradient(
                listOf(Color(0xFFFFA848).copy(alpha = 0.12f + 0.16f * lit), Color.Transparent),
                center = Offset(w / 2, h * 1.25f), radius = w * 0.6f,
            ))
            if (target == null) {
                // 78 rpm era band (roughly 1900s-1950s) for undated discs, behind the scale.
                val x0 = (1902 - DIAL_START) / DIAL_SPAN * w
                val x1 = (1958 - DIAL_START) / DIAL_SPAN * w
                drawRoundRect(
                    rc.glow.copy(alpha = 0.14f),
                    topLeft = Offset(x0, cordY + 4f),
                    size = androidx.compose.ui.geometry.Size(x1 - x0, baseY - cordY - 4f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f),
                )
            }
            // Dial cord the pointer rides on, and the printed double baseline.
            drawLine(rc.dialInk.copy(alpha = 0.28f), Offset(0f, cordY), Offset(w, cordY), 1.dp.toPx())
            drawLine(rc.dialInk.copy(alpha = 0.55f), Offset(0f, baseY), Offset(w, baseY), 1.dp.toPx())
            drawLine(rc.dialInk.copy(alpha = 0.25f), Offset(0f, baseY + 2.5.dp.toPx()), Offset(w, baseY + 2.5.dp.toPx()), 1.dp.toPx())
            for (y in 1898..1972) {
                val x = (y - DIAL_START) / DIAL_SPAN * w
                val len = when { y % 10 == 0 -> 14.dp; y % 5 == 0 -> 8.dp; else -> 4.dp }.toPx()
                drawLine(
                    rc.dialInk.copy(alpha = if (y % 10 == 0) 0.9f else 0.5f),
                    Offset(x, baseY), Offset(x, baseY - len),
                    strokeWidth = if (y % 10 == 0) 1.5.dp.toPx() else 1.dp.toPx(),
                )
                if (y % 10 == 0 && y in 1900..1970) {
                    val t = measurer.measure("$y", labelStyle)
                    drawText(t, topLeft = Offset(x - t.size.width / 2f, baseY + 5.dp.toPx()))
                }
            }
            if (target != null) {
                val x = pos * w
                val gw = 14.dp.toPx()
                drawRect(
                    Brush.horizontalGradient(listOf(Color.Transparent, rc.glow.copy(alpha = 0.14f), Color.Transparent), startX = x - gw, endX = x + gw),
                    topLeft = Offset(x - gw, cordY), size = androidx.compose.ui.geometry.Size(gw * 2, h - cordY),
                )
                drawLine(rc.needle.copy(alpha = 0.25f), Offset(x, cordY), Offset(x, baseY + 7.dp.toPx()), strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)
                drawLine(rc.needle, Offset(x, cordY), Offset(x, baseY + 7.dp.toPx()), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                // the carriage clipped to the cord
                val cw = 9.dp.toPx(); val ch = 7.dp.toPx()
                drawRoundRect(
                    Brush.verticalGradient(listOf(Color(0xFF5A4A3A), Color(0xFF241A12)), startY = cordY - ch / 2, endY = cordY + ch / 2),
                    topLeft = Offset(x - cw / 2, cordY - ch / 2), size = androidx.compose.ui.geometry.Size(cw, ch),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()),
                )
                val t = measurer.measure("${year}", yearStyle)
                val lx = (x - t.size.width / 2f).coerceIn(0f, w - t.size.width)
                drawText(t, topLeft = Offset(lx, cordY - ch / 2 - t.size.height))
            } else {
                val t = measurer.measure("78 rpm era", yearStyle)
                drawText(t, topLeft = Offset((w - t.size.width) / 2f, cordY - t.size.height))
            }
        }
    }
    }
}

/**
 * A small green "magic eye" tuning indicator, like the one on old valve sets:
 * the glowing fan opens while playing and closes to a dark sliver when paused.
 */
@Composable
fun ValveLamp(on: Boolean, modifier: Modifier = Modifier) {
    val rc = LocalRadioColors.current
    val open by animateFloatAsState(if (on) 1f else 0f, tween(700, easing = FastOutSlowInEasing), label = "eye")
    val flicker by rememberInfiniteTransition(label = "flicker").animateFloat(
        0.86f, 1f, infiniteRepeatable(tween(2400), RepeatMode.Reverse), label = "flicker",
    )
    Box(
        modifier.size(16.dp).drawBehind {
            val r = size.minDimension / 2
            if (open > 0f) drawCircle(Brush.radialGradient(listOf(rc.eye.copy(alpha = 0.3f * open), Color.Transparent)), radius = r)
            drawCircle(Color(0xFF1F2A1A), radius = r * 0.78f)
            val sweep = 40f + 280f * open
            val a = if (on) flicker else 1f
            drawArc(
                rc.eye.copy(alpha = (0.35f + 0.65f * open) * a),
                startAngle = -90f + (360f - sweep) / 2f + 180f, sweepAngle = sweep, useCenter = true,
                topLeft = Offset(r - r * 0.7f, r - r * 0.7f), size = androidx.compose.ui.geometry.Size(r * 1.4f, r * 1.4f),
            )
            drawCircle(Color(0xFF1F2A1A), radius = r * 0.22f)
        },
    )
}

/**
 * A walnut radio cabinet: grained wood with a bevelled edge, holding the dial
 * glass or the grille cloth set into it.
 */
@Composable
fun WoodCabinet(modifier: Modifier = Modifier, frame: Dp = 11.dp, content: @Composable () -> Unit) {
    val rc = LocalRadioColors.current
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier
            .shadow(6.dp, shape, ambientColor = Color(0xFF3C200A), spotColor = Color(0xFF3C200A))
            .clip(shape)
            .woodGrain(rc.woodHi, rc.wood, rc.woodLo)
            .padding(frame),
    ) { content() }
}

private class Grain(val path: Path, val color: Color, val width: Float)

/** Procedural wood grain: long wavy streaks, mostly dark with a few pale ones, on a lit-from-above gradient. */
fun Modifier.woodGrain(hi: Color, mid: Color, lo: Color): Modifier = drawWithCache {
    val w = size.width
    val h = size.height
    val rnd = Random(7)
    val step = 10.dp.toPx()
    val streaks = (h / 7.dp.toPx()).toInt().coerceIn(6, 50)
    val grain = List(streaks) {
        val y0 = rnd.nextFloat() * h
        val amp = (0.4f + rnd.nextFloat() * 2.4f).dp.toPx()
        val freq = (2 * PI / (150 + rnd.nextFloat() * 300).dp.toPx()).toFloat()
        val phase = rnd.nextFloat() * 6.28f
        val drift = (rnd.nextFloat() - 0.5f) * h * 0.08f
        val path = Path()
        path.moveTo(0f, y0 + amp * sin(phase))
        var x = 0f
        while (x < w) {
            x += step
            path.lineTo(x, y0 + amp * sin(x * freq + phase) + drift * x / w)
        }
        val dark = true
        Grain(
            path,
            if (dark) Color(0xFF1E0E04).copy(alpha = 0.02f + rnd.nextFloat() * 0.04f)
            else Color(0xFFF2C68E).copy(alpha = 0.02f + rnd.nextFloat() * 0.04f),
            (0.5f + rnd.nextFloat() * 1.7f).dp.toPx(),
        )
    }
    val base = Brush.verticalGradient(listOf(hi, mid, lo))
    val sheen = Brush.linearGradient(
        listOf(Color.Transparent, Color(0x0AFFDCAA), Color.Transparent),
        start = Offset(w * 0.2f, 0f), end = Offset(w * 0.75f, h),
    )
    onDrawBehind {
        drawRect(base)
        for (g in grain) drawPath(g.path, g.color, style = Stroke(g.width))
        drawRect(sheen)
        // Bevel: light along the top edge, shadow along the bottom.
        drawLine(Color(0x47FFE2B4), Offset(0f, 0.75.dp.toPx()), Offset(w, 0.75.dp.toPx()), 1.5.dp.toPx())
        drawLine(Color(0x59000000), Offset(0f, h - 1.dp.toPx()), Offset(w, h - 1.dp.toPx()), 2.dp.toPx())
    }
}

/** Dial glass set into the cabinet: shaded at the top edge, with a faint reflection over it. */
fun Modifier.glassSetIn(): Modifier = drawWithContent {
    drawContent()
    val edge = 7.dp.toPx()
    drawRect(Brush.verticalGradient(listOf(Color(0x8C000000), Color.Transparent), endY = edge),
        size = androidx.compose.ui.geometry.Size(size.width, edge))
    drawRect(Brush.linearGradient(
        0f to Color(0x1AFFFFFF), 0.38f to Color(0x05FFFFFF), 0.4f to Color.Transparent,
        start = Offset(0f, 0f), end = Offset(size.width * 0.25f, size.height * 1.2f),
    ))
}

/** The grille cloth set into the cabinet: shaded where it meets the wood (drawn under the knobs). */
fun Modifier.clothSetIn(): Modifier = drawBehind {
    val edge = 7.dp.toPx()
    drawRect(Brush.verticalGradient(listOf(Color(0x66000000), Color.Transparent), endY = edge),
        size = androidx.compose.ui.geometry.Size(size.width, edge))
}

/** Woven cloth grille: a fine diamond lattice behind content. */
fun Modifier.grille(color: Color, spacing: Dp = 12.dp): Modifier = drawBehind {
    val step = spacing.toPx()
    val w = size.width
    val h = size.height
    val stroke = 1.dp.toPx()
    var x = -h
    while (x < w + h) {
        drawLine(color, Offset(x, 0f), Offset(x + h, h), stroke)
        drawLine(color, Offset(x + h, 0f), Offset(x, h), stroke)
        x += step
    }
}

/**
 * Round control, like the knobs on an old set: fluted bakelite with a domed
 * cap; the main one in ivory with a brass ring.
 */
@Composable
fun Knob(
    onClick: () -> Unit,
    contentDescription: String,
    size: Dp,
    primary: Boolean = false,
    content: @Composable () -> Unit,
) {
    val rc = LocalRadioColors.current
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.Transparent,
        contentColor = if (primary) Color(0xFF3A2414) else rc.knobIcon,
        shadowElevation = 2.dp,
        modifier = Modifier.size(size).semantics { this.contentDescription = contentDescription },
    ) {
        Box(
            Modifier.fillMaxSize().drawBehind { flutedKnob(primary, rc) },
            contentAlignment = Alignment.Center,
        ) { content() }
    }
}

/** A small 78 rpm record: grooves and a coloured label (used for playlists). */
@Composable
fun RecordIcon(label: Color, modifier: Modifier = Modifier.size(40.dp)) {
    Box(
        modifier.drawBehind {
            val r = size.minDimension / 2
            drawCircle(Color(0xFF17120E), radius = r)
            for (i in 1..4) {
                drawCircle(Color.White.copy(alpha = 0.07f), radius = r * (0.55f + i * 0.1f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(0.8.dp.toPx()))
            }
            drawCircle(label, radius = r * 0.36f)
            drawCircle(Color(0xFF17120E), radius = r * 0.06f)
        },
    )
}

/** Dark-glass summary strip in the style of the tuning dial. */
@Composable
fun DialStrip(text: String, modifier: Modifier = Modifier) {
    val rc = LocalRadioColors.current
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier.fillMaxWidth().clip(shape)
            .background(Brush.verticalGradient(listOf(rc.dialFace, rc.dialFaceBottom)))
            .border(1.5.dp, Brush.horizontalGradient(listOf(Color(0xFF8C6B33), Color(0xFFD9B872), Color(0xFF8C6B33))), shape)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(text.uppercase(), style = PlateLabel, color = rc.dialInk, maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
    }
}


/**
 * Disc label or album cover for the current track, in a brass-edged frame.
 * A drawn 78 shows while it loads or when there is no image.
 */
@Composable
fun AlbumArt(url: String?, modifier: Modifier = Modifier) {
    val rc = LocalRadioColors.current
    val shape = RoundedCornerShape(14.dp)
    var loaded by androidx.compose.runtime.remember(url) { androidx.compose.runtime.mutableStateOf(false) }
    Box(modifier, contentAlignment = Alignment.Center) {
        // The drawn 78 until (or unless) the label or cover loads.
        if (!loaded) Record78(Modifier.fillMaxSize())
        if (url != null) {
            coil.compose.AsyncImage(
                model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                    .data(url).crossfade(300).build(),
                contentDescription = "Disc label or cover",
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                onSuccess = { loaded = true },
                onError = { loaded = false },
                modifier = Modifier.fillMaxSize()
                    .then(if (loaded) Modifier.shadow(6.dp, shape) else Modifier)
                    .clip(shape).background(if (loaded) rc.cloth else Color.Transparent)
                    .border(1.dp, if (loaded) rc.brass.copy(alpha = 0.5f) else Color.Transparent, shape),
            )
        }
    }
}

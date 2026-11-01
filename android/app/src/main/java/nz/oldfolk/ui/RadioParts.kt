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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
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
    val labelStyle = TextStyle(fontSize = 12.sp, color = rc.dialInk, fontFeatureSettings = "tnum")
    val yearStyle = TextStyle(fontSize = 13.sp, color = rc.needle, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
        fontFeatureSettings = "tnum")
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Brush.verticalGradient(listOf(rc.dialFace, rc.dialFaceBottom)))
            // glass sheen
            .background(Brush.verticalGradient(0f to Color.White.copy(alpha = 0.07f), 0.5f to Color.Transparent))
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
        Canvas(Modifier.fillMaxWidth().height(62.dp)) {
            val w = size.width
            val h = size.height
            val baseY = h * 0.66f
            val topY = h * 0.26f
            if (target == null) {
                // 78 rpm era band (roughly 1900s-1950s) for undated discs, behind the scale.
                val x0 = (1902 - DIAL_START) / DIAL_SPAN * w
                val x1 = (1958 - DIAL_START) / DIAL_SPAN * w
                drawRoundRect(
                    rc.glow.copy(alpha = 0.14f),
                    topLeft = Offset(x0, topY - 4f),
                    size = androidx.compose.ui.geometry.Size(x1 - x0, baseY - topY + 4f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f),
                )
            }
            drawLine(rc.dialInk.copy(alpha = 0.5f), Offset(0f, baseY), Offset(w, baseY), 1.dp.toPx())
            for (y in 1900..1970 step 5) {
                val x = (y - DIAL_START) / DIAL_SPAN * w
                val major = y % 10 == 0
                drawLine(
                    rc.dialInk.copy(alpha = if (major) 0.9f else 0.45f),
                    Offset(x, baseY), Offset(x, baseY - if (major) 14.dp.toPx() else 7.dp.toPx()),
                    strokeWidth = if (major) 1.5.dp.toPx() else 1.dp.toPx(),
                )
                if (y % 20 == 0) {
                    val t = measurer.measure("$y", labelStyle)
                    drawText(t, topLeft = Offset(x - t.size.width / 2f, baseY + 3.dp.toPx()))
                }
            }
            if (target != null) {
                val x = pos * w
                val gw = 16.dp.toPx()
                drawRect(
                    Brush.horizontalGradient(listOf(Color.Transparent, rc.glow.copy(alpha = 0.16f), Color.Transparent), startX = x - gw, endX = x + gw),
                    topLeft = Offset(x - gw, 0f), size = androidx.compose.ui.geometry.Size(gw * 2, h),
                )
                drawLine(rc.needle.copy(alpha = 0.18f), Offset(x, topY), Offset(x, h), strokeWidth = 8.dp.toPx(), cap = StrokeCap.Round)
                drawLine(rc.needle.copy(alpha = 0.3f), Offset(x, topY), Offset(x, h), strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round)
                drawLine(rc.needle, Offset(x, topY), Offset(x, h), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                val t = measurer.measure("${year}", yearStyle)
                val lx = (x - t.size.width / 2f).coerceIn(0f, w - t.size.width)
                drawText(t, topLeft = Offset(lx, topY - t.size.height - 1.dp.toPx()))
            } else {
                val t = measurer.measure("78 rpm era", yearStyle)
                drawText(t, topLeft = Offset((w - t.size.width) / 2f, topY - t.size.height - 1.dp.toPx()))
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
 * Round control. Secondary: dark bakelite knob with a brass ring and centre.
 * Primary: ivory push-key with a brass ring.
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
        color = if (primary) rc.ivory else rc.knob,
        contentColor = if (primary) Color(0xFF2B1D12) else rc.knobIcon,
        border = BorderStroke(if (primary) 3.dp else 2.dp,
            Brush.linearGradient(listOf(Color(0xFFD9B872), Color(0xFF8C6B33)))),
        shadowElevation = if (primary) 6.dp else 3.dp,
        modifier = Modifier.size(size).semantics { this.contentDescription = contentDescription },
    ) {
        Box(
            Modifier.fillMaxSize().drawBehind {
                if (!primary) {
                    // brass centre ring, like the knobs on the old set
                    drawCircle(Color(0xFFC9A867).copy(alpha = 0.55f), radius = this.size.minDimension * 0.34f,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(1.dp.toPx()))
                }
                drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = if (primary) 0.5f else 0.08f), Color.Transparent),
                    center = Offset(this.size.width * 0.35f, this.size.height * 0.3f), radius = this.size.minDimension * 0.6f))
            },
            contentAlignment = Alignment.Center,
        ) { content() }
    }
}


/** A small 78 rpm record: grooves and a coloured label (used for playlists). */
@Composable
fun RecordIcon(label: Color, modifier: Modifier = Modifier) {
    Box(
        modifier.size(40.dp).drawBehind {
            val r = size.minDimension / 2
            drawCircle(Color(0xFF17120E), radius = r)
            for (i in 1..4) {
                drawCircle(Color.White.copy(alpha = 0.07f), radius = r * (0.55f + i * 0.1f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(0.8.dp.toPx()))
            }
            drawCircle(label, radius = r * 0.36f)
            drawCircle(Color(0xFF17120E), radius = r * 0.06f)
            drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.12f), Color.Transparent),
                center = Offset(r * 0.6f, r * 0.5f), radius = r))
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

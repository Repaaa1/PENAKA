package com.example.tesprojek.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.tesprojek.ui.theme.GrassEdge
import com.example.tesprojek.ui.theme.GrassGreen
import com.example.tesprojek.ui.theme.GrassTuft
import com.example.tesprojek.ui.theme.SkyBlue
import kotlin.math.PI
import kotlin.math.sin

// Posisi rumput kecil (fraksi lebar, fraksi tinggi) - semuanya berada di bawah garis bukit.
private val GrassTufts = listOf(
    0.08f to 0.92f, 0.18f to 0.84f, 0.27f to 0.95f, 0.38f to 0.88f,
    0.47f to 0.97f, 0.55f to 0.83f, 0.64f to 0.93f, 0.72f to 0.86f,
    0.80f to 0.96f, 0.90f to 0.90f, 0.95f to 0.82f
)

/** Langit + bukit sekaligus (dipakai GameScreen). */
@Composable
fun GameBackground(modifier: Modifier = Modifier) {
    Box(modifier) {
        SkyLayer(Modifier.fillMaxSize())
        HillLayer(Modifier.fillMaxSize())
    }
}

/** Layer langit. Dipisah supaya SplashScreen bisa menaruh pelangi di antara langit dan bukit. */
@Composable
fun SkyLayer(modifier: Modifier = Modifier) {
    Canvas(modifier) { drawRect(SkyBlue) }
}

/** Layer bukit rumput asimetris. */
@Composable
fun HillLayer(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height

        val edge = Path().apply {
            moveTo(0f, h * 0.80f)
            cubicTo(w * 0.20f, h * 0.66f, w * 0.42f, h * 0.60f, w * 0.60f, h * 0.62f)
            cubicTo(w * 0.80f, h * 0.64f, w * 0.92f, h * 0.70f, w, h * 0.74f)
        }
        val hill = Path().apply {
            addPath(edge)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(hill, GrassGreen)
        drawPath(edge, GrassEdge, style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round))
    }
}

/** Rumput kecil yang bergoyang pelan tertiup angin. */
@Composable
fun SwayingGrass(modifier: Modifier = Modifier) {
    val phase by rememberInfiniteTransition(label = "grass").animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(durationMillis = 3600, easing = LinearEasing)),
        label = "grassPhase"
    )

    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val tuftSize = 7.dp.toPx()
        val tuftStroke = 2.5.dp.toPx()
        GrassTufts.forEachIndexed { i, (fx, fy) ->
            // Fase berbeda tiap rumput -> goyangan tidak serempak (lebih natural)
            val sway = sin(phase + i * 0.9f) * tuftSize * 0.18f
            drawGrassTuft(Offset(w * fx, h * fy), tuftSize, GrassTuft, tuftStroke, sway)
        }
    }
}

private fun DrawScope.drawGrassTuft(
    base: Offset,
    size: Float,
    color: Color,
    strokeWidth: Float,
    sway: Float
) {
    drawLine(color, base, base + Offset(-size * 0.6f + sway, -size), strokeWidth, StrokeCap.Round)
    drawLine(color, base, base + Offset(sway * 1.3f, -size * 1.15f), strokeWidth, StrokeCap.Round)
    drawLine(color, base, base + Offset(size * 0.6f + sway, -size), strokeWidth, StrokeCap.Round)
}
package com.example.tesprojek.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.tesprojek.ui.theme.OutlineBrown

// Ikon digambar dengan Canvas supaya tidak perlu dependency material-icons-extended.

@Composable
fun ExclamationIcon(modifier: Modifier = Modifier, color: Color = OutlineBrown) {
    Canvas(modifier) {
        val stroke = size.width * 0.18f
        val cx = size.width / 2f
        drawLine(
            color = color,
            start = Offset(cx, size.height * 0.10f),
            end = Offset(cx, size.height * 0.58f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawCircle(
            color = color,
            radius = stroke * 0.62f,
            center = Offset(cx, size.height * 0.86f)
        )
    }
}

@Composable
fun PlayIcon(modifier: Modifier = Modifier, color: Color = OutlineBrown) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.18f, h * 0.08f)
            lineTo(w * 0.92f, h * 0.50f)
            lineTo(w * 0.18f, h * 0.92f)
            close()
        }
        drawPath(path, color)
        // Stroke dengan join round -> sudut segitiga membulat
        drawPath(path, color, style = Stroke(width = w * 0.14f, join = StrokeJoin.Round))
    }
}

@Composable
fun SpeakerIcon(
    isOn: Boolean,
    modifier: Modifier = Modifier,
    color: Color = OutlineBrown
) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.10f

        val body = Path().apply {
            moveTo(w * 0.08f, h * 0.38f)
            lineTo(w * 0.27f, h * 0.38f)
            lineTo(w * 0.50f, h * 0.17f)
            lineTo(w * 0.50f, h * 0.83f)
            lineTo(w * 0.27f, h * 0.62f)
            lineTo(w * 0.08f, h * 0.62f)
            close()
        }
        drawPath(body, color)
        drawPath(body, color, style = Stroke(width = stroke * 0.8f, join = StrokeJoin.Round))

        if (isOn) {
            listOf(0.22f, 0.40f).forEach { r ->
                val radius = w * r
                drawArc(
                    color = color,
                    startAngle = -45f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(w * 0.50f - radius, h * 0.50f - radius),
                    size = Size(radius * 2f, radius * 2f),
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
            }
        } else {
            drawLine(color, Offset(w * 0.64f, h * 0.36f), Offset(w * 0.92f, h * 0.64f), stroke, StrokeCap.Round)
            drawLine(color, Offset(w * 0.92f, h * 0.36f), Offset(w * 0.64f, h * 0.64f), stroke, StrokeCap.Round)
        }
    }
}
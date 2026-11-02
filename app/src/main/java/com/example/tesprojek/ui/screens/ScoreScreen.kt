package com.example.tesprojek.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tesprojek.ui.components.BouncyButton
import com.example.tesprojek.ui.components.GameBackground
import com.example.tesprojek.ui.components.PenakaTopBar
import com.example.tesprojek.ui.components.SwayingGrass
import com.example.tesprojek.ui.theme.SkyBlue
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val ScoreBrown = Color(0xFF8A5A2B)
private val PanelFill = Color(0xFFFFF8E1)
private val StarOn = Color(0xFFF6B93B)
private val StarOff = Color(0xFFE3DAC3)
private val StarOffLine = Color(0xFFB9A98A)

/** Bintang berdasarkan persentase salah: <=15% -> 3, <=40% -> 2, selain itu 1. */
internal fun starsFor(mistakes: Int, total: Int): Int = when {
    mistakes * 100 <= total * 15 -> 3
    mistakes * 100 <= total * 40 -> 2
    else -> 1
}

private fun formatTime(totalSeconds: Int): String {
    val h = totalSeconds / 3600
    val m = totalSeconds % 3600 / 60
    val s = totalSeconds % 60
    return "%02d:%02d:%02d".format(h, m, s)
}

/**
 * Halaman skor bintang. [stars] 1..3, [seconds] lama bermain.
 * Tombol bawah: menu utama, ulangi, dan lanjut.
 */
@Composable
internal fun ScoreScreen(
    stars: Int,
    seconds: Int,
    modifier: Modifier = Modifier,
    soundOn: Boolean = true,
    onBackClick: () -> Unit = {},
    onSoundToggle: (Boolean) -> Unit = {},
    onMenuClick: () -> Unit = {},
    onReplayClick: () -> Unit = {},
    onNextClick: () -> Unit = {}
) {
    val (title, praise) = when (stars) {
        3 -> "Menakjubkan" to "Hebat!"
        2 -> "Bagus Sekali" to "Terus Berlatih!"
        else -> "Ayo Coba Lagi" to "Semangat!"
    }

    BoxWithConstraints(
        modifier
            .fillMaxSize()
            .background(SkyBlue)
    ) {
        val w = maxWidth
        val h = maxHeight
        val u = h / 412f

        GameBackground(Modifier.fillMaxSize())
        SwayingGrass(Modifier.fillMaxSize())
        Clouds.forEach { spec ->
            DriftingCloud(spec, w, h, Modifier.align(Alignment.TopStart))
        }
        MenuDecorations(u, LearnScene)

        PenakaTopBar(
            h = h,
            soundOn = soundOn,
            onStartClick = onBackClick,
            startContentDescription = "Kembali",
            onSoundClick = { onSoundToggle(!soundOn) },
            modifier = Modifier.align(Alignment.TopCenter),
            title = {},
            startIcon = { buttonSize -> BackArrowIcon(Modifier.size(buttonSize * 0.5f)) }
        )

        // Panel skor (kiri-kanan mengikuti desain 353 x 353)
        val panelIn = rememberEntrance(delayMs = 100, durationMs = 700, easing = OutBack)
        val panelShape = RoundedCornerShape(u * 4)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = u * 20)
                .enter(panelIn, rise = 14.dp, startScale = 0.92f)
                .size(u * 353, u * 353)
                .background(PanelFill, panelShape)
                .border(2.dp, ScoreBrown, panelShape)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = u * 40),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ScoreText(title, u, 22f)
                Spacer(Modifier.height(u * 28))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(u * 2),
                    verticalAlignment = Alignment.Bottom
                ) {
                    ScoreStar(on = stars >= 1, size = u * 63, delayMs = 400, u = u)
                    ScoreStar(on = stars >= 2, size = u * 82, delayMs = 650, u = u)
                    ScoreStar(on = stars >= 3, size = u * 63, delayMs = 900, u = u)
                }
                Spacer(Modifier.height(u * 24))
                ScoreText(praise, u, 20f)
                Spacer(Modifier.height(u * 14))
                ScoreText("Waktu : ${formatTime(seconds)}", u, 20f)
            }

            // Tiga tombol, setengahnya menjorok keluar dari tepi bawah panel
            val btn = u * 46
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = btn / 2),
                horizontalArrangement = Arrangement.spacedBy(u * 34)
            ) {
                RoundAction("Menu", btn, Color(0xFFE8967A), onMenuClick) { MenuLinesIcon(Modifier.size(btn * 0.5f)) }
                RoundAction("Ulangi", btn, StarOn, onReplayClick) { ReplayIcon(Modifier.size(btn * 0.55f)) }
                RoundAction("Lanjut", btn, Color(0xFFFFD66B), onNextClick) { PlayTriangle(Modifier.size(btn * 0.45f)) }
            }
        }
    }
}

@Composable
private fun ScoreText(text: String, u: Dp, size: Float) {
    Text(
        text = text,
        color = ScoreBrown,
        fontSize = (u.value * size).sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1
    )
}

/** Bintang muncul membal satu per satu; bintang yang tidak diraih berwarna abu-abu. */
@Composable
private fun ScoreStar(on: Boolean, size: Dp, delayMs: Int, u: Dp) {
    val progress = rememberEntrance(delayMs = delayMs, durationMs = 600, easing = OutBack)
    Canvas(Modifier.size(size).enter(progress, rise = 0.dp, startScale = 0.2f)) {
        drawStar(if (on) StarOn else StarOff, if (on) ScoreBrown else StarOffLine, u.toPx() * 2.5f)
    }
}

private fun DrawScope.drawStar(fill: Color, line: Color, strokeWidth: Float) {
    val cx = size.width / 2f
    val cy = size.height * 0.53f
    val outer = size.minDimension / 2f - strokeWidth
    val inner = outer * 0.5f
    val path = Path()
    for (i in 0 until 10) {
        val r = if (i % 2 == 0) outer else inner
        val a = -PI / 2 + i * PI / 5
        val x = cx + (r * cos(a)).toFloat()
        val y = cy + (r * sin(a)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, fill)
    drawPath(path, line, style = Stroke(width = strokeWidth, join = StrokeJoin.Round, cap = StrokeCap.Round))
}

@Composable
private fun RoundAction(
    description: String,
    size: Dp,
    color: Color,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    BouncyButton(
        onClick = onClick,
        modifier = Modifier.size(size),
        shape = CircleShape,
        containerColor = color,
        borderColor = ScoreBrown,
        borderWidth = 2.dp,
        contentDescription = description
    ) { icon() }
}

@Composable
private fun MenuLinesIcon(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val stroke = size.height * 0.12f
        for (i in 0..2) {
            val y = size.height * (0.2f + 0.3f * i)
            drawLine(ScoreBrown, Offset(size.width * 0.05f, y), Offset(size.width * 0.95f, y), stroke, StrokeCap.Round)
        }
    }
}

@Composable
private fun ReplayIcon(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val stroke = size.width * 0.14f
        val pad = stroke
        val d = size.width - 2 * pad
        drawArc(
            color = ScoreBrown,
            startAngle = -60f,
            sweepAngle = 290f,
            useCenter = false,
            topLeft = Offset(pad, pad),
            size = Size(d, d),
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
        // Kepala panah di ujung busur (sudut 230 derajat), mengikuti arah putaran
        val r = d / 2f
        val c = size.width / 2f
        val a = Math.toRadians(230.0)
        val p = Offset(c + r * cos(a).toFloat(), c + r * sin(a).toFloat())
        val n = Offset(cos(a).toFloat(), sin(a).toFloat())            // radial
        val t = Offset(-sin(a).toFloat(), cos(a).toFloat())           // tangen
        val head = Path().apply {
            moveTo(p.x + n.x * stroke * 1.6f, p.y + n.y * stroke * 1.6f)
            lineTo(p.x - n.x * stroke * 1.6f, p.y - n.y * stroke * 1.6f)
            lineTo(p.x + t.x * stroke * 2.2f, p.y + t.y * stroke * 2.2f)
            close()
        }
        drawPath(head, ScoreBrown)
    }
}

@Composable
private fun PlayTriangle(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val path = Path().apply {
            moveTo(size.width * 0.1f, 0f)
            lineTo(size.width, size.height / 2f)
            lineTo(size.width * 0.1f, size.height)
            close()
        }
        drawPath(path, ScoreBrown)
        drawPath(path, ScoreBrown, style = Stroke(width = size.width * 0.12f, join = StrokeJoin.Round))
    }
}

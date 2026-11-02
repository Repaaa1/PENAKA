package com.example.tesprojek.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tesprojek.R
import kotlinx.coroutines.delay
import kotlin.math.PI

// Helper yang dipakai bersama oleh GameScreen, SplashScreen, dan MenuScreen.

internal val TWO_PI = (2 * PI).toFloat()

// Easing lembut: OutCubic (berhenti halus) dan OutBack (sedikit melampaui lalu kembali = "pop" halus).
internal val OutCubic: Easing = CubicBezierEasing(0.215f, 0.61f, 0.355f, 1f)
internal val OutBack: Easing = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)

/**
 * Animasi berulang. Nilainya sebaiknya dibaca di dalam graphicsLayer { } / Canvas { }
 * supaya animasi berjalan tanpa recomposition (ringan).
 */
@Composable
internal fun loopState(
    from: Float,
    to: Float,
    durationMs: Int,
    easing: Easing = FastOutSlowInEasing,
    mode: RepeatMode = RepeatMode.Reverse,
    startOffsetMs: Int = 0,
    label: String = "loop"
): State<Float> = rememberInfiniteTransition(label = label).animateFloat(
    initialValue = from,
    targetValue = to,
    animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = durationMs, easing = easing),
        repeatMode = mode,
        initialStartOffset = StartOffset(startOffsetMs)
    ),
    label = label
)

/** Progress 0..1 sekali jalan yang mulai setelah [delayMs]. Baca nilainya di dalam graphicsLayer { }. */
@Composable
internal fun rememberEntrance(
    delayMs: Int,
    durationMs: Int = 700,
    easing: Easing = FastOutSlowInEasing
): State<Float> {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(delayMs.toLong())
        progress.animateTo(1f, tween(durationMillis = durationMs, easing = easing))
    }
    return progress.asState()
}

/** Fade-in + naik sedikit + membesar sedikit sesuai [progress]. */
internal fun Modifier.enter(
    progress: State<Float>,
    rise: Dp = 0.dp,
    startScale: Float = 1f,
    origin: TransformOrigin = TransformOrigin.Center
): Modifier = graphicsLayer {
    val p = progress.value
    alpha = p.coerceIn(0f, 1f)
    translationY = (1f - p) * rise.toPx()
    val s = startScale + (1f - startScale) * p
    scaleX = s
    scaleY = s
    transformOrigin = origin
}

@Composable
internal fun DecoImage(
    @DrawableRes res: Int,
    aspect: Float,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(res),
        contentDescription = null,
        modifier = modifier.aspectRatio(aspect),
        contentScale = ContentScale.Fit
    )
}

// ---------------------------------------------------------------------------
// Awan: melayang pelan ke kanan dengan kecepatan berbeda, lalu muncul lagi dari kiri.
// ---------------------------------------------------------------------------

internal data class CloudSpec(
    @DrawableRes val res: Int,
    val x0: Float, val y0: Float,   // posisi awal pada strip awan desain (869x110)
    val cw: Float, val ch: Float,   // ukuran awan pada strip
    val durationMs: Int,            // waktu melintasi layar (makin lama makin lambat)
    val bobMs: Int                  // periode naik-turun kecil
)

internal val Clouds = listOf(
    CloudSpec(R.drawable.awan_1, 0f, 1f, 79f, 43f, 130_000, 6200),
    CloudSpec(R.drawable.awan_2, 135f, 41f, 115f, 60f, 95_000, 5400),
    CloudSpec(R.drawable.awan_3, 628f, 58f, 102f, 52f, 160_000, 7000),
    CloudSpec(R.drawable.awan_4, 754f, 0f, 115f, 64f, 110_000, 5800)
)

@Composable
internal fun DriftingCloud(
    spec: CloudSpec,
    w: Dp,
    h: Dp,
    modifier: Modifier = Modifier
) {
    val u = w * 0.948f / 869f                 // dp per satuan pada strip awan desain
    val cloudW = u * spec.cw
    val cloudH = u * spec.ch
    val startX = w * 0.0175f + u * spec.x0    // posisi awal = posisi di desain
    val y = h * 0.024f + u * spec.y0
    val span = w + cloudW
    val startFraction = (startX + cloudW) / span

    val phase by loopState(0f, 1f, spec.durationMs, LinearEasing, RepeatMode.Restart, label = "cloudDrift")
    val bob by loopState(-1f, 1f, spec.bobMs, label = "cloudBob")

    Image(
        painter = painterResource(spec.res),
        contentDescription = null,
        modifier = modifier
            .offset(y = y)
            .size(cloudW, cloudH)
            .graphicsLayer {
                val f = (startFraction + phase) % 1f
                translationX = (span * f - cloudW).toPx()
                translationY = bob * 3.dp.toPx()
            },
        contentScale = ContentScale.Fit
    )
}
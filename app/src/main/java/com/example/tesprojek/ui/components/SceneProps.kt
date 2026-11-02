package com.example.tesprojek.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.example.tesprojek.R
import com.example.tesprojek.ui.screens.TWO_PI
import com.example.tesprojek.ui.screens.loopState
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// Properti dekorasi beranimasi yang dipakai bersama oleh GameScreen dan MenuScreen.

@Composable
internal fun HoneyJar(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier.aspectRatio(101f / 150f)) {
        Image(
            painter = painterResource(R.drawable.madu_di_jar),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
        Sparkle(
            modifier = Modifier
                .offset(x = maxWidth * 0.60f, y = maxHeight * 0.30f)
                .size(maxWidth * 0.20f),
            delayMs = 0
        )
        Sparkle(
            modifier = Modifier
                .offset(x = maxWidth * 0.26f, y = maxHeight * 0.52f)
                .size(maxWidth * 0.14f),
            delayMs = 1500
        )
    }
}

/** Bintang kecil yang muncul-membesar-menghilang, lalu jeda sebentar. */
@Composable
private fun Sparkle(modifier: Modifier = Modifier, delayMs: Int) {
    val t by rememberInfiniteTransition(label = "sparkle").animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes<Float> {
                durationMillis = 3200
                0f at 0
                1f at 500 using FastOutSlowInEasing
                0f at 1100 using FastOutSlowInEasing
            },
            initialStartOffset = StartOffset(delayMs)
        ),
        label = "sparkleT"
    )

    Canvas(
        modifier.graphicsLayer {
            alpha = t
            val s = 0.4f + 0.6f * t
            scaleX = s
            scaleY = s
            rotationZ = t * 40f
        }
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val outer = size.width / 2f
        val inner = outer * 0.25f
        val star = Path()
        for (i in 0 until 8) {
            val angle = (PI / 4 * i - PI / 2).toFloat()
            val r = if (i % 2 == 0) outer else inner
            val x = cx + cos(angle) * r
            val y = cy + sin(angle) * r
            if (i == 0) star.moveTo(x, y) else star.lineTo(x, y)
        }
        star.close()
        drawPath(star, Color(0xFFFFFBE0))
    }
}

@Composable
internal fun Butterfly(modifier: Modifier = Modifier, driftAmplitude: Dp) {
    val flap by loopState(1f, 0.3f, 200, label = "butterflyFlap")
    val angle by loopState(0f, TWO_PI, 6000, LinearEasing, RepeatMode.Restart, label = "butterflyPath")

    Image(
        painter = painterResource(R.drawable.kupu_kupu),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier.graphicsLayer {
            val amp = driftAmplitude.toPx()
            translationX = sin(angle) * amp
            translationY = sin(angle * 2f + 0.5f) * amp * 0.7f
            rotationZ = sin(angle) * 12f
            scaleX = flap
        }
    )
}
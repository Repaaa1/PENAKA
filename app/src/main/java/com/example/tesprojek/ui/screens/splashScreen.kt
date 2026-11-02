package com.example.tesprojek.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tesprojek.R
import com.example.tesprojek.ui.theme.SkyBlue
import com.example.tesprojek.ui.theme.TitleBlue
import com.example.tesprojek.ui.theme.TitleOrange
import com.example.tesprojek.ui.theme.TitlePink
import com.example.tesprojek.ui.theme.TitlePurple
import com.example.tesprojek.ui.theme.TitleRedOrange
import com.example.tesprojek.ui.theme.TitleYellow
import com.example.tesprojek.ui.components.HillLayer
import com.example.tesprojek.ui.components.SkyLayer
import com.example.tesprojek.ui.components.SwayingGrass
import kotlinx.coroutines.delay
import kotlin.math.sin

/** Lama splash tampil sebelum pindah ke GameScreen (animasi masuk selesai di sekitar 2,3 detik). */
private const val SPLASH_DURATION_MS = 3400L

/**
 * Splash screen. Semua ukuran/posisi mengikuti desain 917x412 (1 satuan desain = [u] dp),
 * grup tengah (pelangi, beruang, sarang, lebah, judul) berpatokan ke tengah layar,
 * grup kiri/kanan berpatokan ke tepi masing-masing, jadi tetap rapi di berbagai rasio layar.
 *
 * Urutan animasi masuk: langit -> pelangi -> pohon/bunga/sarang -> beruang -> lebah & kupu-kupu -> judul.
 * Sesudahnya semua elemen bergerak pelan (idle) sampai [onFinished] dipanggil.
 */
@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onFinished: () -> Unit = {}
) {
    val currentOnFinished by rememberUpdatedState(onFinished)
    LaunchedEffect(Unit) {
        delay(SPLASH_DURATION_MS)
        currentOnFinished()
    }

    BoxWithConstraints(
        modifier
            .fillMaxSize()
            .background(SkyBlue)
    ) {
        val w = maxWidth
        val h = maxHeight
        val u = h / 412f // 1 satuan desain

        val sceneIn = rememberEntrance(delayMs = 0, durationMs = 600)

        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = sceneIn.value }
        ) {
            // --- Langit, awan, pelangi, bukit ---------------------------------------------
            SkyLayer(Modifier.fillMaxSize())
            Clouds.forEach { spec ->
                DriftingCloud(spec, w, h, Modifier.align(Alignment.TopStart))
            }

            val rainbowIn = rememberEntrance(delayMs = 250, durationMs = 1200, easing = OutCubic)
            val shimmer by loopState(0.94f, 1f, 3200, label = "rainbowShimmer")
            DecoImage(
                res = R.drawable.pelangi,
                aspect = 441f / 232f,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = u * 124)
                    .height(u * 232)
                    .graphicsLayer {
                        val p = rainbowIn.value
                        val s = 0.9f + 0.1f * p
                        scaleX = s
                        scaleY = s
                        alpha = p.coerceIn(0f, 1f) * shimmer
                        transformOrigin = TransformOrigin(0.5f, 1f)
                    }
            )

            HillLayer(Modifier.fillMaxSize())
            SwayingGrass(Modifier.fillMaxSize())

            // --- Kiri: pohon, lebah di tanah, bunga, kupu-kupu ----------------------------
            val treeIn = rememberEntrance(delayMs = 350, durationMs = 900, easing = OutCubic)
            val treeSway by loopState(-1.2f, 0f, 4200, label = "treeSway")
            DecoImage(
                res = R.drawable.pohon,
                aspect = 145f / 304f,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = u * 43)
                    .height(u * 304)
                    .enter(treeIn, rise = 12.dp)
                    .graphicsLayer {
                        rotationZ = treeSway
                        transformOrigin = TransformOrigin(0.3f, 1f)
                    }
            )

            val groundBeeIn = rememberEntrance(delayMs = 1000, durationMs = 700, easing = OutCubic)
            val groundBee by loopState(0f, TWO_PI, 3400, LinearEasing, RepeatMode.Restart, label = "groundBee")
            DecoImage(
                res = R.drawable.lebah,
                aspect = 110f / 50f,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = u * 40, bottom = u * 57)
                    .height(u * 50)
                    .enter(groundBeeIn, rise = 8.dp)
                    .graphicsLayer {
                        val amp = (u * 5).toPx()
                        translationX = sin(groundBee) * amp
                        translationY = sin(groundBee * 2f) * amp * 0.6f
                        rotationZ = sin(groundBee + 0.8f) * 6f
                    }
            )

            val flowerIn = rememberEntrance(delayMs = 550, durationMs = 800, easing = OutCubic)
            val flowerLeft by loopState(-4f, 4f, 2800, startOffsetMs = 900, label = "flowerLeft")
            DecoImage(
                res = R.drawable.bunga,
                aspect = 52f / 76f,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = u * 180, bottom = u * 100)
                    .height(u * 76)
                    .enter(flowerIn, rise = 10.dp, startScale = 0.9f, origin = TransformOrigin(0.45f, 1f))
                    .graphicsLayer {
                        rotationZ = flowerLeft
                        transformOrigin = TransformOrigin(0.45f, 1f)
                    }
            )

            val butterflyIn = rememberEntrance(delayMs = 1100, durationMs = 700, easing = OutCubic)
            val flap by loopState(1f, 0.4f, 230, label = "butterflyFlap")
            val flight by loopState(0f, TWO_PI, 6000, LinearEasing, RepeatMode.Restart, label = "butterflyFlight")
            DecoImage(
                res = R.drawable.kupu_kupu,
                aspect = 33f / 28f,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = u * 173, bottom = u * 174)
                    .height(u * 28)
                    .enter(butterflyIn, rise = 6.dp)
                    .graphicsLayer {
                        val amp = (u * 9).toPx()
                        translationX = sin(flight) * amp
                        translationY = sin(flight * 2f + 0.5f) * amp * 0.7f
                        rotationZ = sin(flight) * 10f
                        scaleX = flap // sayap mengepak
                    }
            )

            // --- Kanan: bunga ---------------------------------------------------------------
            val flowerRight by loopState(-4f, 4f, 3100, startOffsetMs = 300, label = "flowerRight")
            DecoImage(
                res = R.drawable.bunga,
                aspect = 52f / 76f,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = u * 5, bottom = u * 97)
                    .height(u * 76)
                    .enter(flowerIn, rise = 10.dp, startScale = 0.9f, origin = TransformOrigin(0.45f, 1f))
                    .graphicsLayer {
                        rotationZ = flowerRight
                        transformOrigin = TransformOrigin(0.45f, 1f)
                    }
            )

            // --- Tengah: sarang, beruang, lebah terbang ------------------------------------
            val hiveIn = rememberEntrance(delayMs = 650, durationMs = 800, easing = OutCubic)
            val hiveSway by loopState(-1f, 1f, 3300, startOffsetMs = 600, label = "hiveSway")
            DecoImage(
                res = R.drawable.sarang,
                aspect = 77f / 79f,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(x = u * 118)
                    .padding(bottom = u * 134)
                    .height(u * 76)
                    .enter(hiveIn, rise = 10.dp, startScale = 0.92f, origin = TransformOrigin(0.5f, 1f))
                    .graphicsLayer {
                        rotationZ = hiveSway
                        transformOrigin = TransformOrigin(0.5f, 1f)
                    }
            )

            val bearIn = rememberEntrance(delayMs = 600, durationMs = 1000, easing = OutBack)
            val breathe by loopState(1f, 1.018f, 2600, label = "bearBreathe")
            val bearTilt by loopState(-0.8f, 0.8f, 3800, startOffsetMs = 400, label = "bearTilt")
            DecoImage(
                res = R.drawable.maskot_splash,
                aspect = 143f / 202f,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = u * 110)
                    .height(u * 202)
                    .enter(bearIn, rise = 28.dp, startScale = 0.9f, origin = TransformOrigin(0.5f, 1f))
                    .graphicsLayer {
                        scaleY = breathe
                        scaleX = 1f + (breathe - 1f) * 0.4f
                        rotationZ = bearTilt
                        transformOrigin = TransformOrigin(0.5f, 1f)
                    }
            )

            val flyingBeeIn = rememberEntrance(delayMs = 1000, durationMs = 700, easing = OutCubic)
            val flyingBee by loopState(0f, TWO_PI, 2900, LinearEasing, RepeatMode.Restart, label = "flyingBee")
            DecoImage(
                res = R.drawable.lebah_splash,
                aspect = 54f / 46f,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(x = -(u * 114.5f))
                    .padding(bottom = u * 157)
                    .height(u * 46)
                    .enter(flyingBeeIn, rise = 10.dp)
                    .graphicsLayer {
                        val amp = (u * 6).toPx()
                        translationX = sin(flyingBee) * amp
                        translationY = sin(flyingBee * 2f) * amp * 0.7f
                        rotationZ = sin(flyingBee + 0.8f) * 7f
                    }
            )

            // --- Judul ---------------------------------------------------------------------
            SplashTitle(
                fontSize = (u.value * 72f).sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = u * 26)
            )
        }
    }
}

/** Judul: tiap huruf muncul berurutan (naik + fade), lalu bergelombang sangat tipis. */
@Composable
private fun SplashTitle(fontSize: TextUnit, modifier: Modifier = Modifier) {
    val letterColors = listOf(
        TitlePink, TitleRedOrange, TitleYellow, TitleBlue, TitlePurple, TitleOrange
    )
    val wave by loopState(0f, TWO_PI, 2800, LinearEasing, RepeatMode.Restart, label = "titleWave")
    val density = LocalDensity.current
    val amplitude = with(density) { (fontSize * 0.025f).toDp() }
    val spacing = with(density) { (fontSize * 0.06f).toDp() }
    val rise = with(density) { (fontSize * 0.45f).toDp() }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        "PENAKA".forEachIndexed { index, char ->
            val enter = rememberEntrance(delayMs = 1100 + index * 90, durationMs = 700, easing = OutBack)
            Text(
                text = char.toString(),
                color = letterColors[index],
                fontSize = fontSize,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                modifier = Modifier.graphicsLayer {
                    val p = enter.value
                    val visible = p.coerceIn(0f, 1f)
                    alpha = visible
                    translationY = (1f - p) * rise.toPx() +
                            sin(wave - index * 0.7f) * amplitude.toPx() * visible
                }
            )
        }
    }
}
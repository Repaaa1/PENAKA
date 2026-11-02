package com.example.tesprojek.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tesprojek.R
import com.example.tesprojek.ui.components.ScreenVoice
import com.example.tesprojek.ui.components.BouncyButton
import com.example.tesprojek.ui.components.Butterfly
import com.example.tesprojek.ui.components.ExclamationIcon
import com.example.tesprojek.ui.components.GameBackground
import com.example.tesprojek.ui.components.HexagonShape
import com.example.tesprojek.ui.components.HoneyJar
import com.example.tesprojek.ui.components.PenakaTopBar
import com.example.tesprojek.ui.components.PlayIcon
import com.example.tesprojek.ui.components.SpeakerIcon
import com.example.tesprojek.ui.components.SwayingGrass
import com.example.tesprojek.ui.theme.OutlineBrown
import com.example.tesprojek.ui.theme.PlayYellow
import com.example.tesprojek.ui.theme.SkyBlue
import com.example.tesprojek.ui.theme.TitleBlue
import com.example.tesprojek.ui.theme.TitleOrange
import com.example.tesprojek.ui.theme.TitlePink
import com.example.tesprojek.ui.theme.TitlePurple
import com.example.tesprojek.ui.theme.TitleRedOrange
import com.example.tesprojek.ui.theme.TitleYellow
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    soundOn: Boolean = true,
    onPlayClick: () -> Unit = {},
    onInfoClick: () -> Unit = {},
    onSoundToggle: (Boolean) -> Unit = {}
) {

    BoxWithConstraints(
        modifier
            .fillMaxSize()
            .background(SkyBlue)
    ) {
        val w = maxWidth
        val h = maxHeight

        // Suara halaman utama: res/raw/suara_halo
        ScreenVoice(name = "suara_halo", enabled = soundOn)

        // Layer 1: langit + bukit (statis), lalu rumput yang bergoyang
        GameBackground(Modifier.fillMaxSize())
        SwayingGrass(Modifier.fillMaxSize())

        // Layer 2: awan + dekorasi (semua beranimasi)
        Decorations(w, h)

        // Layer 3: top bar
        PenakaTopBar(
            h = h,
            soundOn = soundOn,
            onStartClick = onInfoClick,
            startContentDescription = "Informasi",
            onSoundClick = { onSoundToggle(!soundOn) },
            modifier = Modifier.align(Alignment.TopCenter),
            startIcon = { buttonSize -> ExclamationIcon(Modifier.size(buttonSize * 0.5f)) }
        )

        // Layer 4: tombol Play hexagon (agak ke kanan)
        PlayButton(
            width = h * 0.40f,
            onClick = onPlayClick,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = w * 0.17f, y = h * 0.11f)
        )
    }
}

// ---------------------------------------------------------------------------
// Tombol Play hexagon: berdenyut + miring sangat halus
// ---------------------------------------------------------------------------

@Composable
private fun PlayButton(
    width: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hexagon = remember { HexagonShape(cornerRadius = 14.dp) }
    val pulse by loopState(1f, 1.05f, 900, label = "playPulse")
    val tilt by loopState(-2f, 2f, 1900, label = "playTilt")

    BouncyButton(
        onClick = onClick,
        modifier = modifier
            .size(width = width, height = width * 0.866f)
            .graphicsLayer {
                scaleX = pulse
                scaleY = pulse
                rotationZ = tilt
            },
        shape = hexagon,
        containerColor = PlayYellow,
        borderWidth = 6.dp,
        pressedScale = 0.88f,
        contentDescription = "Main"
    ) {
        PlayIcon(Modifier.size(width * 0.34f))
    }
}

// ---------------------------------------------------------------------------
// Awan + dekorasi. Ukuran & posisi mengikuti desain 917x412 (proporsional ke tinggi layar).
// ---------------------------------------------------------------------------

@Composable
private fun BoxScope.Decorations(w: Dp, h: Dp) {
    // Awan: melayang pelan ke kanan dengan kecepatan berbeda, lalu muncul lagi dari kiri
    Clouds.forEach { spec ->
        DriftingCloud(spec, w, h, Modifier.align(Alignment.TopStart))
    }

    // Pohon + sarang: condong pelan tertiup angin (hanya ke kiri supaya tepi kiri tetap tertutup)
    val treeSway by loopState(-1.2f, 0f, 4200, label = "treeSway")
    DecoImage(
        res = R.drawable.pohon_sarang,
        aspect = 145f / 304f,
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(bottom = h * 0.104f)
            .height(h * 0.738f)
            .graphicsLayer {
                rotationZ = treeSway
                transformOrigin = TransformOrigin(0.3f, 1f)
            }
    )

    // Lebah: melayang berputar kecil (gerakan angka 8) di dekat sarang.
    // Koordinat lebah pada gambar pohon: x 40..150, y 240..290 dari tinggi 304.
    val unit = h * 0.738f / 304f
    val beeAngle by loopState(0f, TWO_PI, 3400, LinearEasing, RepeatMode.Restart, label = "beeHover")
    DecoImage(
        res = R.drawable.lebah,
        aspect = 110f / 50f,
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(start = unit * 40f, bottom = h * 0.104f + unit * 14f)
            .height(unit * 50f)
            .graphicsLayer {
                val amp = (h * 0.012f).toPx()
                translationX = sin(beeAngle) * amp
                translationY = sin(beeAngle * 2f) * amp * 0.6f
                rotationZ = sin(beeAngle + 0.8f) * 6f
            }
    )

    // Beruang: bernapas pelan + sedikit bergoyang
    val breathe by loopState(1f, 1.018f, 2600, label = "bearBreathe")
    val tilt by loopState(-1f, 1f, 3800, startOffsetMs = 400, label = "bearTilt")
    DecoImage(
        res = R.drawable.maskot_beruang,
        aspect = 156f / 195f,
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(start = w * 0.18f, bottom = h * 0.121f)
            .height(h * 0.473f)
            .graphicsLayer {
                scaleY = breathe
                scaleX = 1f + (breathe - 1f) * 0.4f
                rotationZ = tilt
                transformOrigin = TransformOrigin(0.5f, 1f)
            }
    )

    // Kupu-kupu: mengepak sambil terbang melingkar kecil di dekat tangan beruang
    Butterfly(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(start = w * 0.32f, bottom = h * 0.52f)
            .size(h * 0.08f),
        driftAmplitude = h * 0.025f
    )

    // Toples madu: berkilau sesekali
    HoneyJar(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = w * 0.072f, bottom = h * 0.04f)
            .height(h * 0.364f)
    )

    // Bunga: bergoyang dari batang
    val flowerSway by loopState(-4f, 4f, 2800, startOffsetMs = 900, label = "flowerSway")
    DecoImage(
        res = R.drawable.bunga,
        aspect = 52f / 76f,
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = w * 0.005f, bottom = h * 0.233f)
            .height(h * 0.184f)
            .graphicsLayer {
                rotationZ = flowerSway
                transformOrigin = TransformOrigin(0.45f, 1f)
            }
    )
}
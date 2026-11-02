package com.example.tesprojek.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tesprojek.R
import com.example.tesprojek.ui.components.ScreenVoice
import com.example.tesprojek.ui.components.BouncyButton
import com.example.tesprojek.ui.components.ClickSound
import com.example.tesprojek.ui.components.SpeakerIcon
import com.example.tesprojek.ui.components.PenakaTopBar
import com.example.tesprojek.ui.theme.OutlineBrown
import com.example.tesprojek.ui.theme.SkyBlue
import com.example.tesprojek.ui.components.Butterfly
import com.example.tesprojek.ui.components.GameBackground
import com.example.tesprojek.ui.components.HoneyJar
import com.example.tesprojek.ui.components.SwayingGrass
import com.example.tesprojek.ui.screens.Clouds
import com.example.tesprojek.ui.screens.DecoImage
import com.example.tesprojek.ui.screens.DriftingCloud
import com.example.tesprojek.ui.screens.OutBack
import com.example.tesprojek.ui.screens.TWO_PI
import com.example.tesprojek.ui.screens.enter
import com.example.tesprojek.ui.screens.loopState
import com.example.tesprojek.ui.screens.rememberEntrance
import kotlin.math.sin

private val CardOutline = Color(0xFF8A5A2B)
private val PillFill = Color(0xFFFFF8D6)
private val PillOutline = Color(0xFF4A3426)

/** Posisi elemen yang berbeda antar halaman menu (satuan = piksel desain 917x412). */
internal data class SceneLayout(
    val bearStart: Float = 97f,
    val bearBottom: Float = 19f,
    val bearHeight: Float = 171f,
    val butterflyStart: Float = 212f,
    val butterflyBottom: Float = 161f,
    val groundBee: Boolean = false,
    val tree: Boolean = true,
    val hive: Boolean = true,
    val leftFlower: Boolean = false
)

/**
 * Menu pilihan (Huruf / Angka). Ukuran & posisi mengikuti desain 917x412
 * (1 satuan desain = u dp, u = tinggi layar / 412), sama seperti SplashScreen.
 */
@Composable
fun MenuScreen(
    modifier: Modifier = Modifier,
    soundOn: Boolean = true,
    onBackClick: () -> Unit = {},
    onSoundToggle: (Boolean) -> Unit = {},
    onLettersClick: () -> Unit = {},
    onNumbersClick: () -> Unit = {}
) {
    BoxWithConstraints(
        modifier
            .fillMaxSize()
            .background(SkyBlue)
    ) {
        val w = maxWidth
        val h = maxHeight
        val u = h / 412f

        // Suara halaman Menu: res/raw/suara_menu
        ScreenVoice(name = "suara_menu", enabled = soundOn)

        // Langit + bukit + rumput bergoyang
        GameBackground(Modifier.fillMaxSize())
        SwayingGrass(Modifier.fillMaxSize())

        // Awan melayang
        Clouds.forEach { spec ->
            DriftingCloud(spec, w, h, Modifier.align(Alignment.TopStart))
        }

        // Pohon, sarang, beruang, kupu-kupu, bunga, toples madu
        MenuDecorations(u)

        PenakaTopBar(
            h = h,
            soundOn = soundOn,
            onStartClick = onBackClick,
            startContentDescription = "Kembali",
            onSoundClick = { onSoundToggle(!soundOn) },
            modifier = Modifier.align(Alignment.TopCenter),
            startIcon = { buttonSize -> BackArrowIcon(Modifier.size(buttonSize * 0.5f)) }
        )

        // Dua kartu pilihan
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = u * 156),
            horizontalArrangement = Arrangement.spacedBy(u * 76)
        ) {
            MenuCard(label = "Huruf", index = 0, u = u, onClick = onLettersClick) { BlocksIllustration(R.drawable.balok_huruf, u, phaseMs = 0) }
            MenuCard(label = "Angka", index = 1, u = u, onClick = onNumbersClick) { BlocksIllustration(R.drawable.balok_angka, u, phaseMs = 700) }
        }
    }
}

@Composable
internal fun BackArrowIcon(modifier: Modifier = Modifier, color: Color = OutlineBrown) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.14f
        drawLine(color, Offset(w * 0.08f, h * 0.5f), Offset(w * 0.95f, h * 0.5f), stroke, StrokeCap.Round)
        drawLine(color, Offset(w * 0.08f, h * 0.5f), Offset(w * 0.42f, h * 0.16f), stroke, StrokeCap.Round)
        drawLine(color, Offset(w * 0.08f, h * 0.5f), Offset(w * 0.42f, h * 0.84f), stroke, StrokeCap.Round)
    }
}

// ---------------------------------------------------------------------------
// Kartu pilihan: muncul bergantian (pop halus), lalu melayang tipis. Ditekan -> memantul.
// ---------------------------------------------------------------------------

@Composable
internal fun MenuCard(
    label: String,
    index: Int,
    u: Dp,
    onClick: () -> Unit,
    cardWidth: Float = 171f,
    cardHeight: Float = 100f,
    pillMinWidth: Float = 79f,
    illustration: @Composable BoxScope.() -> Unit
) {
    val cardIn = rememberEntrance(delayMs = 150 + index * 150, durationMs = 800, easing = OutBack)
    val float by loopState(
        from = -1f,
        to = 1f,
        durationMs = 2800 + index * 500,
        startOffsetMs = index * 600,
        label = "cardFloat"
    )
    val cardShape = RoundedCornerShape(u * 8)

    // BouncyButton dipakai sebagai pembungkus (tanpa latar/garis) supaya kartu + label memantul bersama.
    BouncyButton(
        onClick = { ClickSound.play(); onClick() },
        modifier = Modifier
            .enter(cardIn, rise = 16.dp, startScale = 0.9f)
            .graphicsLayer { translationY = float * 2.dp.toPx() },
        shape = RectangleShape,
        containerColor = Color.Transparent,
        borderColor = Color.Transparent,
        borderWidth = 0.dp,
        pressedScale = 0.93f,
        contentDescription = label
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(u * cardWidth, u * cardHeight)
                    .clip(cardShape)
                    .background(Color.White, cardShape)
                    .border(2.dp, CardOutline, cardShape),
                content = illustration
            )
            Spacer(Modifier.height(u * 5))
            LabelPill(label, u, pillMinWidth)
        }
    }
}

@Composable
private fun LabelPill(text: String, u: Dp, minWidth: Float = 79f) {
    val shape = RoundedCornerShape(percent = 50)
    Box(
        modifier = Modifier
            .defaultMinSize(minWidth = u * minWidth, minHeight = u * 26)
            .clip(shape)
            .background(PillFill, shape)
            .border(1.dp, PillOutline, shape)
            .padding(horizontal = u * 12),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = PillOutline,
            fontSize = (u.value * 14f).sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

// ---------------------------------------------------------------------------
// Gambar di dalam kartu (aset: res/drawable/balok_huruf.xml & balok_angka.xml)
// ---------------------------------------------------------------------------

/** Gambar balok di dalam kartu; bergoyang dan "bernapas" sangat tipis. */
@Composable
internal fun BoxScope.BlocksIllustration(
    @DrawableRes res: Int,
    u: Dp,
    phaseMs: Int,
    padding: Float = 6f,
    swayDeg: Float = 1.5f,
    breatheBy: Float = 0.03f
) {
    val sway by loopState(-swayDeg, swayDeg, 2600, startOffsetMs = phaseMs, label = "blocksSway")
    val breathe by loopState(1f, 1f + breatheBy, 2200, startOffsetMs = phaseMs, label = "blocksBreathe")
    Image(
        painter = painterResource(res),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxSize()
            .padding(u * padding)
            .graphicsLayer {
                rotationZ = sway
                scaleX = breathe
                scaleY = breathe
            }
    )
}

// ---------------------------------------------------------------------------
// Dekorasi (posisi diukur dari desain 917x412)
// ---------------------------------------------------------------------------

@Composable
internal fun BoxScope.MenuDecorations(u: Dp, layout: SceneLayout = SceneLayout()) {
    // Pohon: kanopi lebih lebar & tidak terpotong (pohon_menu.xml), condong tipis tertiup angin
    val treeSway by loopState(-1.2f, 0f, 4200, label = "treeSway")
    if (layout.tree) DecoImage(
        res = R.drawable.pohon_menu,
        aspect = 221f / 304f,
        modifier = Modifier
            .align(Alignment.BottomStart)
            .offset(x = -(u * 62))
            .padding(bottom = u * 52)
            .height(u * 304)
            .graphicsLayer {
                rotationZ = treeSway
                transformOrigin = TransformOrigin(0.43f, 1f)
            }
    )

    // Sarang berdiri, sebagian tertutup beruang
    val hiveSway by loopState(-1f, 1f, 3300, startOffsetMs = 600, label = "hiveSway")
    if (layout.hive) DecoImage(
        res = R.drawable.sarang,
        aspect = 77f / 79f,
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(start = u * 78, bottom = u * 108)
            .height(u * 76)
            .graphicsLayer {
                rotationZ = hiveSway
                transformOrigin = TransformOrigin(0.5f, 1f)
            }
    )

    // Bunga kiri (dipakai halaman Mengenal)
    val leftFlowerSway by loopState(-4f, 4f, 3100, startOffsetMs = 300, label = "leftFlowerSway")
    if (layout.leftFlower) {
        // Garis bukit bergantung pada lebar layar, jadi tinggi bunga dihitung dari kurva bukit (HillLayer)
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val sw = maxWidth.value
            val sh = maxHeight.value
            val flowerX = u.value * 36f
            val hillY = hillEdgeY(flowerX, sw, sh)
            val flowerBottom = (sh - hillY).dp - u * 4
            DecoImage(
                res = R.drawable.bunga,
                aspect = 52f / 76f,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = u * 14, bottom = flowerBottom)
                    .height(u * 72)
                    .graphicsLayer {
                        rotationZ = leftFlowerSway
                        transformOrigin = TransformOrigin(0.45f, 1f)
                    }
            )
        }
    }

    // Beruang melambai: bernapas pelan + sedikit bergoyang
    val breathe by loopState(1f, 1.018f, 2600, label = "bearBreathe")
    val bearTilt by loopState(-1f, 1f, 3800, startOffsetMs = 400, label = "bearTilt")
    DecoImage(
        res = R.drawable.maskot_beruang,
        aspect = 156f / 195f,
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(start = u * layout.bearStart, bottom = u * layout.bearBottom)
            .height(u * layout.bearHeight)
            .graphicsLayer {
                scaleY = breathe
                scaleX = 1f + (breathe - 1f) * 0.4f
                rotationZ = bearTilt
                transformOrigin = TransformOrigin(0.5f, 1f)
            }
    )

    // Lebah di depan beruang (hanya di halaman sub menu), melayang kecil
    if (layout.groundBee) {
        val groundBee by loopState(0f, TWO_PI, 3400, LinearEasing, RepeatMode.Restart, label = "groundBee")
        DecoImage(
            res = R.drawable.lebah,
            aspect = 110f / 50f,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = u * 40, bottom = u * 57)
                .height(u * 50)
                .graphicsLayer {
                    val amp = (u * 5).toPx()
                    translationX = sin(groundBee) * amp
                    translationY = sin(groundBee * 2f) * amp * 0.6f
                    rotationZ = sin(groundBee + 0.8f) * 6f
                }
        )
    }

    // Kupu-kupu di dekat tangan beruang
    Butterfly(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(start = u * layout.butterflyStart, bottom = u * layout.butterflyBottom)
            .size(u * 30),
        driftAmplitude = u * 10
    )

    // Bunga kanan
    val flowerSway by loopState(-4f, 4f, 2800, startOffsetMs = 900, label = "flowerSway")
    DecoImage(
        res = R.drawable.bunga,
        aspect = 52f / 76f,
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = u * 29, bottom = u * 84)
            .height(u * 76)
            .graphicsLayer {
                rotationZ = flowerSway
                transformOrigin = TransformOrigin(0.45f, 1f)
            }
    )

    // Toples madu berkilau
    HoneyJar(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = u * 99, bottom = u * 30)
            .height(u * 146)
    )
}

/** Tinggi (dari atas) garis bukit pada posisi x; sama dengan kurva pertama di HillLayer. */
private fun hillEdgeY(x: Float, w: Float, h: Float): Float {
    var px = 0f; var py = h * 0.80f
    val x1 = w * 0.20f; val y1 = h * 0.66f
    val x2 = w * 0.42f; val y2 = h * 0.60f
    val x3 = w * 0.60f; val y3 = h * 0.62f
    for (i in 1..64) {
        val t = i / 64f; val m = 1 - t
        val cx = m * m * m * 0f + 3 * m * m * t * x1 + 3 * m * t * t * x2 + t * t * t * x3
        val cy = m * m * m * h * 0.80f + 3 * m * m * t * y1 + 3 * m * t * t * y2 + t * t * t * y3
        if (cx >= x) {
            val f = if (cx == px) 0f else (x - px) / (cx - px)
            return py + (cy - py) * f
        }
        px = cx; py = cy
    }
    return py
}
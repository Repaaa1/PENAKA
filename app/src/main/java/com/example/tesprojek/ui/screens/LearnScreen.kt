package com.example.tesprojek.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tesprojek.ui.components.BouncyButton
import com.example.tesprojek.ui.components.GameBackground
import com.example.tesprojek.ui.components.LearnItem
import com.example.tesprojek.ui.components.LearnKind
import com.example.tesprojek.ui.components.PenakaTopBar
import com.example.tesprojek.ui.components.SwayingGrass
import com.example.tesprojek.ui.theme.SkyBlue

internal val LearnCardOutline = Color(0xFF8A5A2B)
internal val LearnPillFill = Color(0xFFFFF8D6)
internal val LearnPillOutline = Color(0xFF4A3426)
private val LearnArrow = Color(0xFF7A4A28)

/** Dekorasi halaman Mengenal: tanpa pohon & sarang, ada bunga di kiri. */
internal val LearnScene = SceneLayout(
    bearStart = 77f, bearBottom = 30f, bearHeight = 171f,
    butterflyStart = 195f, butterflyBottom = 178f,
    tree = false, hive = false, leftFlower = true
)

/**
 * Halaman "Mengenal Huruf / Angka": kartu glyph di kiri, kartu benda di kanan, label di bawah,
 * panah kiri/kanan untuk ganti item. Ukuran mengikuti desain 917x412 (u = tinggi layar / 412).
 */
@Composable
internal fun LearnScreen(
    kind: LearnKind,
    modifier: Modifier = Modifier,
    soundOn: Boolean = true,
    onBackClick: () -> Unit = {},
    onSoundToggle: (Boolean) -> Unit = {}
) {
    var index by rememberSaveable(kind) { mutableIntStateOf(0) }
    val items = kind.items

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
            title = {
                Text(
                    text = kind.title,
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Black,
                    fontSize = (u.value * 22f).sp,
                    fontWeight = FontWeight.Normal
                )
            },
            startIcon = { buttonSize -> BackArrowIcon(Modifier.size(buttonSize * 0.5f)) }
        )

        Crossfade(
            targetState = index,
            animationSpec = tween(250),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = u * 97),
            label = "learnItem"
        ) { i ->
            val item = items[i]
            Row(horizontalArrangement = Arrangement.spacedBy(u * 106)) {
                LearnCard(u, kind.fixedLabel, 123f) {
                    LearnImage(item.glyphName, item.glyphText, u, 70f)
                }
                LearnCard(u, item.phrase, 170f) {
                    LearnImage(item.objectName, item.label, u, 24f)
                }
            }
        }

        // Panah ganti item (polos, tersembunyi di ujung daftar)
        NavArrow(
            forward = false,
            visible = index > 0,
            u = u,
            onClick = { index-- },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Start))
                .padding(start = u * 10)
        )
        NavArrow(
            forward = true,
            visible = index < items.lastIndex,
            u = u,
            onClick = { index++ },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.End))
                .padding(end = u * 10)
        )
    }
}

@Composable
private fun LearnCard(
    u: Dp,
    label: String,
    pillMinWidth: Float,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(u * 27)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(u * 175, u * 180)
                .clip(shape)
                .background(Color.White, shape)
                .border(2.dp, LearnCardOutline, shape)
                .padding(u * 12),
            contentAlignment = Alignment.Center
        ) { content() }
        Spacer(Modifier.height(u * 7))
        val pill = RoundedCornerShape(percent = 50)
        Box(
            modifier = Modifier
                .defaultMinSize(minWidth = u * pillMinWidth)
                .clip(pill)
                .background(LearnPillFill, pill)
                .border(1.dp, LearnPillOutline, pill)
                .padding(horizontal = u * 12, vertical = u * 4),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = LearnPillOutline,
                fontSize = (u.value * 14f).sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

/** Gambar dari res/drawable berdasarkan nama; kalau belum ada, tampilkan teks pengganti. */
@Composable
internal fun LearnImage(name: String, fallbackText: String, u: Dp, fallbackSize: Float) {
    val context = LocalContext.current
    val resId = remember(name) { context.resources.getIdentifier(name, "drawable", context.packageName) }
    if (resId != 0) {
        Image(
            painter = painterResource(resId),
            contentDescription = fallbackText,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        Text(
            text = fallbackText,
            color = LearnPillOutline,
            fontSize = (u.value * fallbackSize).sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun NavArrow(
    forward: Boolean,
    visible: Boolean,
    u: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val boxW = u * 44
    val boxH = u * 80
    Box(modifier.size(boxW, boxH)) {
        if (visible) {
            BouncyButton(
                onClick = onClick,
                modifier = Modifier.size(boxW, boxH),
                shape = RectangleShape,
                containerColor = Color.Transparent,
                borderColor = Color.Transparent,
                borderWidth = 0.dp,
                contentDescription = if (forward) "Berikutnya" else "Sebelumnya"
            ) {
                Canvas(Modifier.size(u * 34, u * 62)) {
                    val w = size.width
                    val hh = size.height
                    val stroke = u.toPx() * 5f
                    val x0 = if (forward) w * 0.15f else w * 0.85f
                    val x1 = if (forward) w * 0.85f else w * 0.15f
                    drawLine(LearnArrow, Offset(x0, hh * 0.02f), Offset(x1, hh * 0.5f), stroke, StrokeCap.Round)
                    drawLine(LearnArrow, Offset(x1, hh * 0.5f), Offset(x0, hh * 0.98f), stroke, StrokeCap.Round)
                }
            }
        }
    }
}

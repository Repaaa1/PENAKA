package com.example.tesprojek.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tesprojek.ui.components.BouncyButton
import com.example.tesprojek.ui.components.SpeakerIcon
import com.example.tesprojek.ui.theme.TitleBlue
import com.example.tesprojek.ui.theme.TitleOrange
import com.example.tesprojek.ui.theme.TitlePink
import com.example.tesprojek.ui.theme.TitlePurple
import com.example.tesprojek.ui.theme.TitleRedOrange
import com.example.tesprojek.ui.theme.TitleYellow
import com.example.tesprojek.ui.screens.loopState
import com.example.tesprojek.ui.screens.TWO_PI
import kotlin.math.sin


/**
 * Top bar yang SAMA untuk semua layar: tombol di ujung kiri, judul PENAKA di tengah,
 * speaker di ujung kanan. Layar hanya berbeda pada tombol kiri:
 *  - layar utama  -> ikon tanda seru (info)
 *  - layar menu   -> ikon panah kembali
 * [startIcon] menerima ukuran tombol supaya ikon bisa diskalakan proporsional.
 */
@Composable
internal fun PenakaTopBar(
    h: Dp,
    soundOn: Boolean,
    onStartClick: () -> Unit,
    startContentDescription: String,
    onSoundClick: () -> Unit,
    modifier: Modifier = Modifier,
    // Bagian tengah; default = judul PENAKA. Halaman sub menu mengganti dengan judul halaman.
    title: @Composable BoxScope.() -> Unit = {
        PenakaTitle(
            fontSize = (h.value * 0.13f).sp,
            modifier = Modifier.align(Alignment.Center)
        )

    },
    startIcon: @Composable (buttonSize: Dp) -> Unit
) {
    val buttonSize = (h * 0.14f).coerceAtLeast(44.dp)


    Box(
        modifier = modifier
            .fillMaxWidth()
            // Inset cutout HORIZONTAL sengaja tidak dipakai: lubang kamera pada mode landscape ada di
            // tengah sisi kiri, tidak bertabrakan dengan tombol di pojok atas. Kalau dipakai, tombol kiri
            // terdorong menjauhi tepi dan judul jadi tidak di tengah.
            .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Top))
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        BouncyButton(
            onClick = { ClickSound.play(); onStartClick() },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(buttonSize),
            borderWidth = 3.dp,
            contentDescription = startContentDescription
        ) {
            startIcon(buttonSize)
        }

        title()

        BouncyButton(
            onClick = { ClickSound.play(); onSoundClick() },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(buttonSize),
            borderWidth = 3.dp,
            contentDescription = if (soundOn) "Matikan suara" else "Nyalakan suara"
        ) {
            SpeakerIcon(isOn = soundOn, modifier = Modifier.size(buttonSize * 0.55f))
        }
    }
}

/** Judul: tiap huruf naik-turun pelan membentuk gelombang. */
@Composable
internal fun PenakaTitle(fontSize: TextUnit, modifier: Modifier = Modifier) {
    val letterColors = listOf(
        TitlePink, TitleRedOrange, TitleYellow, TitleBlue, TitlePurple, TitleOrange
    )
    val phase by loopState(0f, TWO_PI, 2600, LinearEasing, RepeatMode.Restart, label = "titleWave")
    val density = LocalDensity.current
    val amplitude = with(density) { (fontSize * 0.05f).toDp() }
    val spacing = with(density) { (fontSize * 0.2f).toDp() }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        "PENAKA".forEachIndexed { index, char ->
            Text(
                text = char.toString(),
                color = letterColors[index],
                fontSize = fontSize,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                modifier = Modifier.graphicsLayer {
                    translationY = sin(phase - index * 0.7f) * amplitude.toPx()
                }
            )
        }
    }
}
package com.example.tesprojek.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.example.tesprojek.R
import com.example.tesprojek.ui.components.ScreenVoice
import com.example.tesprojek.ui.components.GameBackground
import com.example.tesprojek.ui.components.PenakaTopBar
import com.example.tesprojek.ui.components.SwayingGrass
import com.example.tesprojek.ui.theme.SkyBlue

/**
 * Isi dua halaman sub menu. Strukturnya sama; yang beda hanya judul, label, gambar kartu,
 * aset papan, dan posisi beruang/kupu-kupu (mengikuti desain masing-masing halaman).
 */
internal enum class SubMenuKind(
    val title: String,
    val learnLabel: String,
    val matchLabel: String,
    @DrawableRes val learnImage: Int,
    @DrawableRes val boardImage: Int,
    val layout: SceneLayout
) {
    Letters(
        title = "Huruf",
        learnLabel = "Huruf",
        matchLabel = "Mencocokan Huruf",
        learnImage = R.drawable.balok_huruf,
        boardImage = R.drawable.papan_huruf,
        layout = SceneLayout(
            bearStart = 54f, bearBottom = 15f, bearHeight = 158f,
            butterflyStart = 152f, butterflyBottom = 144f,
            groundBee = true
        )
    ),
    Numbers(
        title = "Angka",
        learnLabel = "Angka",
        matchLabel = "Mencocokan Angka",
        learnImage = R.drawable.balok_angka,
        boardImage = R.drawable.papan_angka,
        layout = SceneLayout(
            bearStart = 121f, bearBottom = 18f, bearHeight = 171f,
            butterflyStart = 234f, butterflyBottom = 193f,
            groundBee = true
        )
    )
}

/**
 * Halaman sub menu (Huruf / Angka): kartu belajar (balok) di kiri dan kartu mencocokkan (papan) di kanan.
 * Ukuran & posisi mengikuti desain 917x412 (1 satuan desain = u dp, u = tinggi layar / 412).
 */
@Composable
internal fun SubMenuScreen(
    kind: SubMenuKind,
    modifier: Modifier = Modifier,
    soundOn: Boolean = true,
    onBackClick: () -> Unit = {},
    onSoundToggle: (Boolean) -> Unit = {},
    onLearnClick: () -> Unit = {},
    onMatchClick: () -> Unit = {}
) {
    BoxWithConstraints(
        modifier
            .fillMaxSize()
            .background(SkyBlue)
    ) {
        val w = maxWidth
        val h = maxHeight
        val u = h / 412f

        // Suara sub menu: res/raw/suara_huruf atau suara_angka
        ScreenVoice(
            name = if (kind == SubMenuKind.Letters) "suara_huruf" else "suara_angka",
            enabled = soundOn
        )

        // Langit + bukit + rumput bergoyang, awan melayang
        GameBackground(Modifier.fillMaxSize())
        SwayingGrass(Modifier.fillMaxSize())
        Clouds.forEach { spec ->
            DriftingCloud(spec, w, h, Modifier.align(Alignment.TopStart))
        }

        // Pohon, sarang, beruang, lebah, kupu-kupu, bunga, toples madu
        MenuDecorations(u, kind.layout)

        // Top bar sama dengan layar lain; tombol kiri = kembali, tengah = judul halaman
        PenakaTopBar(
            h = h,
            soundOn = soundOn,
            onStartClick = onBackClick,
            startContentDescription = "Kembali",
            onSoundClick = { onSoundToggle(!soundOn) },
            modifier = Modifier.align(Alignment.TopCenter),
            title = { PageTitle(kind.title, u, Modifier.align(Alignment.Center)) },
            startIcon = { buttonSize -> BackArrowIcon(Modifier.size(buttonSize * 0.5f)) }
        )

        // Dua kartu pilihan
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = u * 129),
            horizontalArrangement = Arrangement.spacedBy(u * 82)
        ) {
            MenuCard(
                label = kind.learnLabel,
                index = 0,
                u = u,
                onClick = onLearnClick,
                cardWidth = 131f,
                cardHeight = 102f,
                pillMinWidth = 118f
            ) {
                BlocksIllustration(kind.learnImage, u, phaseMs = 0)
            }
            MenuCard(
                label = kind.matchLabel,
                index = 1,
                u = u,
                onClick = onMatchClick,
                cardWidth = 173f,
                cardHeight = 102f,
                pillMinWidth = 160f
            ) {
                // Papan hampir memenuhi kartu, jadi geraknya lebih tipis
                BlocksIllustration(
                    kind.boardImage, u, phaseMs = 700,
                    padding = 3f, swayDeg = 0.6f, breatheBy = 0.015f
                )
            }
        }
    }
}

/** Judul halaman: putih tebal dengan bayangan lembut supaya tetap terbaca di atas langit pucat. */
@Composable
private fun PageTitle(text: String, u: Dp, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = TextStyle(
            color = Color.White,
            fontSize = (u.value * 22f).sp,
            fontWeight = FontWeight.Bold,
            shadow = Shadow(
                color = Color(0x66124A7A),
                offset = Offset(0f, 3f),
                blurRadius = 6f
            )
        )
    )
}
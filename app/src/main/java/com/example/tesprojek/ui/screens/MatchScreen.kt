package com.example.tesprojek.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.tesprojek.ui.theme.OutlineBrown
import com.example.tesprojek.ui.theme.SkyBlue
import kotlinx.coroutines.delay

private val MatchCardOutline = Color(0xFF8A5A2B)
private val MatchPillFill = Color(0xFFFFF8D6)
private val MatchPillOutline = Color(0xFF4A3426)
private val ChipFill = Color(0xFFFFECC8)

@Composable
internal fun MatchScreen(
    kind: LearnKind,
    modifier: Modifier = Modifier,
    soundOn: Boolean = true,
    onBackClick: () -> Unit = {},
    onSoundToggle: (Boolean) -> Unit = {},
    onFinished: (seconds: Int, mistakes: Int) -> Unit = { _, _ -> }
) {
    var currentIndex by rememberSaveable(kind) { mutableIntStateOf(0) }
    var mistakes by rememberSaveable(kind) { mutableIntStateOf(0) }
    var seconds by rememberSaveable(kind) { mutableIntStateOf(0) }

    val currentOnFinished by rememberUpdatedState(onFinished)

    // Timer penghitung waktu
    LaunchedEffect(kind) {
        while (true) {
            delay(1000L)
            seconds++
        }
    }

    val items = kind.items
    val currentItem = items.getOrElse(currentIndex) { items.first() }

    // Buat 3 pilihan jawaban (1 benar + 2 pengacau)
    val options = remember(currentIndex, kind) {
        val wrongOptions = items.filter { it != currentItem }.shuffled().take(2)
        (listOf(currentItem) + wrongOptions).shuffled()
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
            title = {
                Text(
                    text = kind.matchTitle,
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Black,
                    fontSize = (u.value * 22f).sp,
                    fontWeight = FontWeight.Normal
                )
            },
            startIcon = { buttonSize -> BackArrowIcon(Modifier.size(buttonSize * 0.5f)) }
        )

        Crossfade(
            targetState = currentIndex,
            animationSpec = tween(250),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = u * 90),
            label = "matchContent"
        ) { targetIdx ->
            val item = items.getOrElse(targetIdx) { items.first() }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(u * 14)
            ) {
                // Kartu Target
                val cardShape = RoundedCornerShape(u * 20)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(u * 160, u * 150)
                            .clip(cardShape)
                            .background(Color.White, cardShape)
                            .border(2.dp, MatchCardOutline, cardShape)
                            .padding(u * 10),
                        contentAlignment = Alignment.Center
                    ) {
                        LearnImage(item.objectName, item.label, u, 24f)
                    }
                    Spacer(Modifier.height(u * 5))
                    val pill = RoundedCornerShape(percent = 50)
                    Box(
                        modifier = Modifier
                            .defaultMinSize(minWidth = u * 120)
                            .clip(pill)
                            .background(MatchPillFill, pill)
                            .border(1.dp, MatchPillOutline, pill)
                            .padding(horizontal = u * 12, vertical = u * 4),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.label,
                            color = MatchPillOutline,
                            fontSize = (u.value * 14f).sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }

                // Pilihan Kartu
                Row(
                    horizontalArrangement = Arrangement.spacedBy(u * 16),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    options.forEach { choice ->
                        val chipShape = RoundedCornerShape(u * 16)
                        BouncyButton(
                            onClick = {
                                if (choice == item) {
                                    if (targetIdx + 1 >= items.size) {
                                        currentOnFinished(seconds, mistakes)
                                    } else {
                                        currentIndex = targetIdx + 1
                                    }
                                } else {
                                    mistakes++
                                }
                            },
                            modifier = Modifier.size(u * 75, u * 75),
                            shape = chipShape,
                            containerColor = ChipFill,
                            borderColor = MatchCardOutline,
                            borderWidth = 2.dp,
                            contentDescription = choice.chipText
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                LearnImage(choice.chipName, choice.chipText, u, 32f)
                            }
                        }
                    }
                }
            }
        }
    }
}

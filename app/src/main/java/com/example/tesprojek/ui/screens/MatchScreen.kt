package com.example.tesprojek.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.tesprojek.ui.components.ClickSound
import com.example.tesprojek.ui.components.LearnItem
import com.example.tesprojek.ui.components.LearnKind
import com.example.tesprojek.ui.components.PenakaTopBar
import com.example.tesprojek.ui.components.ScreenVoice
import com.example.tesprojek.ui.components.VoiceOver
import com.example.tesprojek.ui.theme.SkyBlue
import com.example.tesprojek.ui.components.GameBackground
import com.example.tesprojek.ui.components.SwayingGrass
import com.example.tesprojek.ui.screens.Clouds
import com.example.tesprojek.ui.screens.DriftingCloud
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val MatchCorrect = Color(0xFF6BB84A)
private val MatchWrong = Color(0xFFE8402F)
private val NumberWords = listOf("satu", "dua", "tiga", "empat", "lima", "enam", "tujuh", "delapan", "sembilan", "sepuluh")
private val ChipFallbackColors = listOf(Color(0xFFE8402F), Color(0xFF2EA8E0), Color(0xFFF27BB0))

/** Satu ronde = satu item; setelah ronde terakhir halaman skor dibuka. Pilihan: jawaban benar + 2 pengecoh, posisi jawaban bergilir. */
private fun matchOptions(n: Int, round: Int): List<Int> {
    val (a, b) = if (n <= 10) 3 to 6 else 9 to 17
    val list = mutableListOf((round + a) % n, (round + b) % n)
    list.add(round % 3, round)
    return list
}

/**
 * Halaman "Cocokan Huruf / Angka": kartu benda di kanan, tarik huruf/angka yang benar
 * dari rak di bawah ke kartu kosong di kiri. Benar -> kartu terisi, lanjut ronde berikutnya.
 * Salah -> kartu berkedip merah dan pilihan kembali ke rak.
 */
@Composable
internal fun MatchScreen(
    kind: LearnKind,
    modifier: Modifier = Modifier,
    soundOn: Boolean = true,
    onBackClick: () -> Unit = {},
    onSoundToggle: (Boolean) -> Unit = {},
    /** Dipanggil setelah soal terakhir terjawab: (detik berlalu, jumlah salah). */
    onFinished: (seconds: Int, mistakes: Int) -> Unit = { _, _ -> }
) {
    val items = kind.items
    var round by rememberSaveable(kind) { mutableIntStateOf(0) }
    val item = items[round]
    val options = remember(kind, round) { matchOptions(items.size, round) }
    var solved by remember(kind, round) { mutableStateOf(false) }
    var flashWrong by remember(kind, round) { mutableStateOf(false) }
    var dropRect by remember { mutableStateOf(Rect.Zero) }
    var elapsed by rememberSaveable(kind) { mutableIntStateOf(0) }
    var mistakes by rememberSaveable(kind) { mutableIntStateOf(0) }
    var timerRunning by rememberSaveable(kind) { mutableStateOf(true) }

    // Suara soal tiap ronde: kuis_huruf_a .. kuis_huruf_z / kuis_angka_1 .. kuis_angka_10 (res/raw)
    val context = LocalContext.current
    val questionVoice = "kuis_${item.glyphName}"
    ScreenVoice(name = questionVoice, enabled = soundOn)
    val replayQuestion = { if (soundOn) VoiceOver.play(context, questionVoice) }
    val reactCorrect = { if (soundOn) VoiceOver.play(context, "reaksi_benar") }
    val reactWrong = { if (soundOn) VoiceOver.play(context, "reaksi_salah") }

    LaunchedEffect(kind, timerRunning) {
        while (timerRunning) {
            delay(1000)
            elapsed++
        }
    }

    LaunchedEffect(kind, round, solved) {
        if (solved) {
            delay(1400)
            if (round == items.lastIndex) onFinished(elapsed, mistakes) else round++
        }
    }
    LaunchedEffect(flashWrong) {
        if (flashWrong) {
            delay(450)
            flashWrong = false
        }
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

        if (kind == LearnKind.Numbers) {
            // Cocokan Angka: tiga kartu angka berjajar, ketuk angka yang sesuai pertanyaan di pil
            var wrongIdx by remember(kind, round) { mutableIntStateOf(-1) }
            val pop by animateFloatAsState(
                targetValue = if (solved) 1.08f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "pickPop"
            )
            val cardShape = RoundedCornerShape(u * 27)
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = u * 97),
                horizontalArrangement = Arrangement.spacedBy(u * 35.5f)
            ) {
                options.forEach { idx ->
                    val isAnswer = idx == round
                    val borderColor = when {
                        solved && isAnswer -> MatchCorrect
                        flashWrong && wrongIdx == idx -> MatchWrong
                        else -> LearnCardOutline
                    }
                    Box(
                        modifier = Modifier
                            .size(u * 175, u * 180)
                            .graphicsLayer {
                                val sc = if (solved && isAnswer) pop else 1f
                                scaleX = sc
                                scaleY = sc
                            }
                            .clip(cardShape)
                            .background(Color.White, cardShape)
                            .border(if (borderColor == LearnCardOutline) 2.dp else 3.dp, borderColor, cardShape)
                            .pointerInput(round, solved) {
                                detectTapGestures {
                                    if (solved) return@detectTapGestures
                                    ClickSound.play()
                                    if (isAnswer) {
                                        solved = true
                                        if (round == items.lastIndex) timerRunning = false
                                        reactCorrect()
                                    } else {
                                        wrongIdx = idx
                                        flashWrong = true
                                        mistakes++
                                        reactWrong()
                                    }
                                }
                            }
                            .padding(u * 12),
                        contentAlignment = Alignment.Center
                    ) {
                        LearnImage(items[idx].glyphName, items[idx].glyphText, u, 70f)
                    }
                }
            }
            LearnPill(
                u = u,
                label = "Mana angka ${NumberWords[round]}?",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = u * 286)
                    .pointerInput(round) { detectTapGestures { replayQuestion() } },
                width = 150f
            )
        } else {
            // Dua kartu: kiri = tempat menjatuhkan, kanan = benda
            val cardShape = RoundedCornerShape(u * 27)
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = u * 97),
                horizontalArrangement = Arrangement.spacedBy(u * 109)
            ) {
                val borderColor = when {
                    solved -> MatchCorrect
                    flashWrong -> MatchWrong
                    else -> LearnCardOutline
                }
                Box(
                    modifier = Modifier
                        .size(u * 175, u * 180)
                        .onGloballyPositioned { dropRect = it.boundsInRoot() }
                        .clip(cardShape)
                        .background(Color.White, cardShape)
                        .border(if (solved || flashWrong) 3.dp else 2.dp, borderColor, cardShape)
                        .padding(u * 12),
                    contentAlignment = Alignment.Center
                ) {
                    val pop by animateFloatAsState(
                        targetValue = if (solved) 1f else 0.6f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "dropPop"
                    )
                    if (solved) {
                        Box(Modifier.graphicsLayer { scaleX = pop; scaleY = pop }) {
                            LearnImage(item.glyphName, item.glyphText, u, 70f)
                        }
                    } else {
                        Text(
                            text = kind.dragHint,
                            color = Color.Black,
                            fontSize = (u.value * 14f).sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(u * 175, u * 180)
                        .clip(cardShape)
                        .background(Color.White, cardShape)
                        .border(2.dp, LearnCardOutline, cardShape)
                        .pointerInput(round) { detectTapGestures { replayQuestion() } }
                        .padding(u * 12),
                    contentAlignment = Alignment.Center
                ) {
                    Crossfade(targetState = round, animationSpec = tween(250), label = "matchObject") { r ->
                        LearnImage(items[r].objectName, items[r].label, u, 24f)
                    }
                }
            }

            // Rak pilihan (digambar terakhir supaya kartu yang diseret tampil di atas kartu lain)
            val pill = RoundedCornerShape(percent = 50)
            key(kind, round) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = u * 313.5f)
                        .background(LearnPillFill, pill)
                        .border(1.dp, LearnPillOutline, pill)
                        .padding(horizontal = u * 37, vertical = u * 2.8f),
                    horizontalArrangement = Arrangement.spacedBy(u * 15.4f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    options.forEachIndexed { pos, idx ->
                        val isAnswer = idx == round
                        DragChip(
                            item = items[idx],
                            fallbackColor = ChipFallbackColors[pos % ChipFallbackColors.size],
                            u = u,
                            hidden = solved && isAnswer,
                            enabled = !solved,
                            onDrop = { center ->
                                when {
                                    solved || !dropRect.contains(center) -> false
                                    isAnswer -> {
                                        solved = true
                                        if (round == items.lastIndex) timerRunning = false
                                        reactCorrect()
                                        true
                                    }
                                    else -> { flashWrong = true; mistakes++; reactWrong(); false }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/** Satu huruf/angka yang bisa diseret. onDrop mengembalikan true bila diterima (kartu tidak kembali). */
@Composable
private fun DragChip(
    item: LearnItem,
    fallbackColor: Color,
    u: Dp,
    hidden: Boolean,
    enabled: Boolean,
    onDrop: (center: Offset) -> Boolean
) {
    val scope = rememberCoroutineScope()
    val ox = remember { Animatable(0f) }
    val oy = remember { Animatable(0f) }
    var origin by remember { mutableStateOf(Offset.Zero) }
    var dragging by remember { mutableStateOf(false) }

    fun finish() {
        dragging = false
        val accepted = onDrop(origin + Offset(ox.value, oy.value))
        if (!accepted) {
            scope.launch { ox.animateTo(0f, spring(dampingRatio = 0.6f)) }
            scope.launch { oy.animateTo(0f, spring(dampingRatio = 0.6f)) }
        }
    }

    val context = LocalContext.current
    val resId = remember(item.chipName) {
        context.resources.getIdentifier(item.chipName, "drawable", context.packageName)
    }
    val size = u * 40

    Box(
        modifier = Modifier
            .size(size)
            // dibaca sebelum offset, jadi origin = posisi diam
            .onGloballyPositioned { origin = it.boundsInRoot().center }
            .zIndex(if (dragging) 1f else 0f)
            .graphicsLayer {
                translationX = ox.value
                translationY = oy.value
                val s = if (dragging) 1.15f else 1f
                scaleX = s
                scaleY = s
                alpha = if (hidden) 0f else 1f
            }
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                detectDragGestures(
                    onDragStart = { dragging = true },
                    onDrag = { change, amount ->
                        change.consume()
                        scope.launch {
                            ox.snapTo(ox.value + amount.x)
                            oy.snapTo(oy.value + amount.y)
                        }
                    },
                    onDragEnd = { finish() },
                    onDragCancel = { finish() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (resId != 0) {
            Image(
                painter = painterResource(resId),
                contentDescription = item.chipText,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = item.chipText,
                color = fallbackColor,
                fontSize = (u.value * 26f).sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}
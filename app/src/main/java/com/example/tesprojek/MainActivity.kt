package com.example.tesprojek

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.tesprojek.data.KuisViewModel
import com.example.tesprojek.data.keLearnItems
import com.example.tesprojek.ui.components.ClickSound
import com.example.tesprojek.ui.components.LearnKind
import com.example.tesprojek.ui.screens.BackgroundMusic
import com.example.tesprojek.ui.screens.GameScreen
import com.example.tesprojek.ui.screens.LearnScreen
import com.example.tesprojek.ui.screens.MatchScreen
import com.example.tesprojek.ui.screens.MenuScreen
import com.example.tesprojek.ui.screens.ScoreScreen
import com.example.tesprojek.ui.screens.SplashScreen
import com.example.tesprojek.ui.screens.SubMenuKind
import com.example.tesprojek.ui.screens.SubMenuScreen
import com.example.tesprojek.ui.screens.starsFor
import com.example.tesprojek.ui.theme.PenakaTheme

/**
 * Setiap layar punya "kedalaman" supaya arah transisi bisa ditentukan:
 * maju (kedalaman naik) -> geser dari kanan, mundur -> geser dari kiri,
 * kedalaman sama -> memudar sambil sedikit membesar.
 */
private enum class Screen(val depth: Int) {
    Splash(0),
    Home(1),
    Menu(2),
    Letters(3),
    Numbers(3),
    LearnLetters(4),
    LearnNumbers(4),
    MatchLetters(4),
    MatchNumbers(4),
    ScoreLetters(4),
    ScoreNumbers(4)
}

// Warna latar di belakang transisi, supaya tidak ada kilatan putih/hitam antar layar.
// Ganti dengan warna langit yang sama seperti di splash / tema kamu.
private val WarnaLatar = Color(0xFF8ED8F8)

// Di AndroidManifest.xml, pada <activity> tambahkan:
//   android:screenOrientation="sensorLandscape"
//   android:theme="@style/Theme.Penaka.Starting"
class MainActivity : ComponentActivity() {

    private val kuisViewModel: KuisViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash sistem harus dipanggil sebelum super.onCreate()
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Paksa landscape (bisa terbalik 180 derajat mengikuti sensor)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        // Layar tidak mati saat anak sedang belajar
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        ClickSound.init(this)
        actionBar?.hide()
        hideSystemBars()

        setContent {
            PenakaTheme {
                // rememberSaveable: rotasi layar tidak mengulang splash / kembali ke awal
                var screen by rememberSaveable { mutableStateOf(Screen.Splash) }
                var soundOn by rememberSaveable { mutableStateOf(true) }
                var tampilInfo by rememberSaveable { mutableStateOf(false) }

                // Hasil kuis terakhir untuk halaman skor
                var scoreStars by rememberSaveable { mutableIntStateOf(3) }
                var scoreSeconds by rememberSaveable { mutableIntStateOf(0) }

                BackgroundMusic(enabled = soundOn)

                // Memuat 10 soal acak dari database, lalu pindah ke layar kuis
                val bukaKuisHuruf = {
                    kuisViewModel.muatSoal("huruf")
                    screen = Screen.MatchLetters
                }
                val bukaKuisAngka = {
                    kuisViewModel.muatSoal("angka")
                    screen = Screen.MatchNumbers
                }
                // Memuat materi dari database, lalu pindah ke halaman belajar
                val bukaBelajarHuruf = {
                    kuisViewModel.muatMateri("huruf")
                    screen = Screen.LearnLetters
                }
                val bukaBelajarAngka = {
                    kuisViewModel.muatMateri("angka")
                    screen = Screen.LearnNumbers
                }

                // Tombol kembali sistem: Huruf/Angka -> Menu -> Home
                BackHandler(enabled = screen != Screen.Splash && screen != Screen.Home) {
                    screen = when (screen) {
                        Screen.Menu -> Screen.Home
                        Screen.LearnLetters,
                        Screen.MatchLetters,
                        Screen.ScoreLetters -> Screen.Letters

                        Screen.LearnNumbers,
                        Screen.MatchNumbers,
                        Screen.ScoreNumbers -> Screen.Numbers

                        else -> Screen.Menu
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(WarnaLatar)
                ) {
                    AnimatedContent(
                        targetState = screen,
                        transitionSpec = {
                            val dariSplash = initialState == Screen.Splash
                            val sama = targetState.depth == initialState.depth
                            val maju = targetState.depth > initialState.depth

                            when {
                                // Splash -> Home, dan pindah antar layar sekelas: memudar + membesar halus
                                dariSplash || sama ->
                                    (fadeIn(tween(500)) +
                                            scaleIn(initialScale = 0.94f, animationSpec = tween(500)))
                                        .togetherWith(fadeOut(tween(300)))

                                // Maju: layar baru masuk dari kanan
                                maju ->
                                    (slideInHorizontally(tween(450)) { it / 3 } + fadeIn(tween(450)))
                                        .togetherWith(
                                            slideOutHorizontally(tween(450)) { -it / 3 } + fadeOut(tween(300))
                                        )

                                // Mundur: layar sebelumnya masuk dari kiri
                                else ->
                                    (slideInHorizontally(tween(450)) { -it / 3 } + fadeIn(tween(450)))
                                        .togetherWith(
                                            slideOutHorizontally(tween(450)) { it / 3 } + fadeOut(tween(300))
                                        )
                            }
                        },
                        label = "screenTransition"
                    ) { current ->
                        when (current) {
                            Screen.Splash -> SplashScreen(
                                onFinished = { screen = Screen.Home }
                            )

                            Screen.Home -> GameScreen(
                                soundOn = soundOn,
                                onSoundToggle = { soundOn = it },
                                onPlayClick = { screen = Screen.Menu },
                                onInfoClick = { tampilInfo = true }
                            )

                            Screen.Menu -> MenuScreen(
                                soundOn = soundOn,
                                onBackClick = { screen = Screen.Home },
                                onSoundToggle = { soundOn = it },
                                onLettersClick = { screen = Screen.Letters },
                                onNumbersClick = { screen = Screen.Numbers }
                            )

                            Screen.Letters -> SubMenuScreen(
                                kind = SubMenuKind.Letters,
                                soundOn = soundOn,
                                onBackClick = { screen = Screen.Menu },
                                onSoundToggle = { soundOn = it },
                                onLearnClick = bukaBelajarHuruf,
                                onMatchClick = bukaKuisHuruf
                            )

                            Screen.Numbers -> SubMenuScreen(
                                kind = SubMenuKind.Numbers,
                                soundOn = soundOn,
                                onBackClick = { screen = Screen.Menu },
                                onSoundToggle = { soundOn = it },
                                onLearnClick = bukaBelajarAngka,
                                onMatchClick = bukaKuisAngka
                            )

                            Screen.LearnLetters -> LearnScreen(
                                kind = LearnKind.Letters,
                                soundOn = soundOn,
                                onBackClick = { screen = Screen.Letters },
                                onSoundToggle = { soundOn = it },
                                items = kuisViewModel.materiHuruf.keLearnItems(LearnKind.Letters)
                            )

                            Screen.LearnNumbers -> LearnScreen(
                                kind = LearnKind.Numbers,
                                soundOn = soundOn,
                                onBackClick = { screen = Screen.Numbers },
                                onSoundToggle = { soundOn = it },
                                items = kuisViewModel.materiAngka.keLearnItems(LearnKind.Numbers)
                            )

                            Screen.MatchLetters -> MatchScreen(
                                kind = LearnKind.Letters,
                                soal = kuisViewModel.soal,
                                soundOn = soundOn,
                                onBackClick = { screen = Screen.Letters },
                                onSoundToggle = { soundOn = it },
                                onFinished = { seconds, mistakes ->
                                    val total = kuisViewModel.soal.size
                                    val bintang = starsFor(mistakes, total)
                                    scoreStars = bintang
                                    scoreSeconds = seconds
                                    kuisViewModel.simpanHasil("huruf", total, mistakes, bintang, seconds)
                                    screen = Screen.ScoreLetters
                                }
                            )

                            Screen.ScoreLetters -> ScoreScreen(
                                stars = scoreStars,
                                seconds = scoreSeconds,
                                soundOn = soundOn,
                                onBackClick = { screen = Screen.Letters },
                                onSoundToggle = { soundOn = it },
                                onMenuClick = { screen = Screen.Menu },
                                onReplayClick = bukaKuisHuruf,
                                onNextClick = bukaKuisAngka,
                                rekorBintang = kuisViewModel.rekor?.bintangDidapat,
                                rekorDetik = kuisViewModel.rekor?.durasiDetik ?: 0
                            )

                            Screen.MatchNumbers -> MatchScreen(
                                kind = LearnKind.Numbers,
                                soal = kuisViewModel.soal,
                                soundOn = soundOn,
                                onBackClick = { screen = Screen.Numbers },
                                onSoundToggle = { soundOn = it },
                                onFinished = { seconds, mistakes ->
                                    val total = kuisViewModel.soal.size
                                    val bintang = starsFor(mistakes, total)
                                    scoreStars = bintang
                                    scoreSeconds = seconds
                                    kuisViewModel.simpanHasil("angka", total, mistakes, bintang, seconds)
                                    screen = Screen.ScoreNumbers
                                }
                            )

                            Screen.ScoreNumbers -> ScoreScreen(
                                stars = scoreStars,
                                seconds = scoreSeconds,
                                soundOn = soundOn,
                                onBackClick = { screen = Screen.Numbers },
                                onSoundToggle = { soundOn = it },
                                onMenuClick = { screen = Screen.Menu },
                                onReplayClick = bukaKuisAngka,
                                onNextClick = bukaKuisHuruf,
                                rekorBintang = kuisViewModel.rekor?.bintangDidapat,
                                rekorDetik = kuisViewModel.rekor?.durasiDetik ?: 0
                            )
                        }
                    }
                }

                // Dialog info, muncul di atas layar saat tombol "!" ditekan
                if (tampilInfo) {
                    InfoDialog(onDismiss = { tampilInfo = false })
                }
            }
        }
    }

    // Sembunyikan ulang bar sistem tiap jendela mendapat fokus
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }
    }
}
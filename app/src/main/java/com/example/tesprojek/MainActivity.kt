package com.example.tesprojek

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.tesprojek.data.KuisViewModel
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
import com.example.tesprojek.data.keLearnItems

private enum class Screen { Splash, Home, Menu, Letters, Numbers, LearnLetters, LearnNumbers, MatchLetters, MatchNumbers, ScoreLetters, ScoreNumbers }

// Di AndroidManifest.xml, pada <activity> tambahkan:
//   android:screenOrientation="sensorLandscape"
//   android:theme="@style/Theme.Penaka.Starting"
class MainActivity : ComponentActivity() {

    private val kuisViewModel: KuisViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash sistem (satu warna langit, tanpa ikon) supaya tidak ada kilatan putih sebelum
        // SplashScreen Compose tampil. Harus dipanggil sebelum super.onCreate().
        installSplashScreen()
        super.onCreate(savedInstanceState)
        ClickSound.init(this)
        // Jaga-jaga jika tema masih membawa ActionBar (judul proyek menutupi atas layar)
        actionBar?.hide()
        hideSystemBars()

        setContent {
            PenakaTheme {
                // rememberSaveable: rotasi layar tidak mengulang splash / kembali ke awal
                var screen by rememberSaveable { mutableStateOf(Screen.Splash) }
                // Status suara dipegang di sini supaya konsisten antar layar
                var soundOn by rememberSaveable { mutableStateOf(true) }
                BackgroundMusic(enabled = soundOn)
                // Hasil kuis terakhir untuk halaman skor
                var scoreStars by rememberSaveable { mutableIntStateOf(3) }
                var scoreSeconds by rememberSaveable { mutableIntStateOf(0) }

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
                        Screen.LearnLetters -> Screen.Letters
                        Screen.LearnNumbers -> Screen.Numbers
                        Screen.MatchLetters -> Screen.Letters
                        Screen.MatchNumbers -> Screen.Numbers
                        Screen.ScoreLetters -> Screen.Letters
                        Screen.ScoreNumbers -> Screen.Numbers
                        else -> Screen.Menu
                    }
                }

                Crossfade(
                    targetState = screen,
                    animationSpec = tween(durationMillis = 700),
                    label = "screenFade"
                ) { current ->
                    when (current) {
                        Screen.Splash -> SplashScreen(
                            onFinished = { screen = Screen.Home }
                        )

                        Screen.Home -> GameScreen(
                            soundOn = soundOn,
                            onSoundToggle = { soundOn = it },
                            onPlayClick = { screen = Screen.Menu },
                            onInfoClick = { toast("Info") }
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
        }
    }

    // Sembunyikan ulang bar sistem tiap jendela mendapat fokus (sekali di onCreate sering tidak cukup)
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

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
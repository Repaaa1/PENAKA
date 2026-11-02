package com.example.tesprojek.ui.screens

import android.content.Context
import android.media.MediaPlayer
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.delay

/** Nama file di res/raw (tanpa ekstensi): res/raw/chill_song.mp3 */
private const val MUSIC_NAME = "chill_song"
private const val MUSIC_VOLUME = 0.45f

/** Lama saling-silang di sambungan akhir -> awal lagu (ms). */
private const val CROSSFADE_MS = 2500

/**
 * Pemutar berulang tanpa celah: dua MediaPlayer memutar lagu yang sama. Menjelang akhir lagu,
 * player kedua mulai dari detik 0 sambil volume player pertama turun, jadi sambungannya mulus
 * (MediaPlayer.isLooping pada MP3 biasanya meninggalkan jeda kecil).
 */
private class SeamlessLooper(context: Context, resId: Int, private val volume: Float) {
    private val a: MediaPlayer? = MediaPlayer.create(context, resId)
    private val b: MediaPlayer? = MediaPlayer.create(context, resId)
    private var active: MediaPlayer? = a
    private var idle: MediaPlayer? = b

    val ready get() = a != null && b != null

    private fun MediaPlayer.vol(v: Float) = setVolume(v, v)

    /** Berjalan sampai dibatalkan (jeda / ke latar belakang); lanjut dari posisi terakhir saat dipanggil lagi. */
    suspend fun run() {
        val cur0 = active ?: return
        val fadeMs = minOf(CROSSFADE_MS, cur0.duration / 3).coerceAtLeast(300)
        try {
            active?.vol(volume)
            idle?.vol(0f)
            if (active?.isPlaying == false) active?.start()
            while (true) {
                // tunggu sampai sisa lagu tinggal selama crossfade
                while (true) {
                    val p = active ?: return
                    if (p.duration - p.currentPosition <= fadeMs) break
                    delay(100)
                }
                val prev = active ?: return
                val next = idle ?: return
                next.seekTo(0)
                next.vol(0f)
                next.start()
                val steps = fadeMs / 50
                for (i in 1..steps) {
                    delay(50)
                    val f = i / steps.toFloat()
                    prev.vol(volume * (1f - f))
                    next.vol(volume * f)
                }
                prev.pause()
                prev.seekTo(0)
                active = next
                idle = prev
            }
        } finally {
            runCatching { a?.pause() }
            runCatching { b?.pause() }
        }
    }

    fun release() {
        runCatching { a?.release() }
        runCatching { b?.release() }
    }
}

/**
 * Musik latar chill yang berputar terus (sambungan mulus) selama aplikasi dibuka.
 * - mengikuti tombol speaker ([enabled]): mati -> jeda, nyala -> lanjut
 * - berhenti saat aplikasi ke latar belakang, lanjut saat dibuka lagi
 * - kalau file res/raw/chill_song belum ada, tidak melakukan apa-apa (aplikasi tetap jalan)
 */
@Composable
fun BackgroundMusic(enabled: Boolean) {
    val context = LocalContext.current
    val inForeground = remember { mutableStateOf(true) }

    val looper = remember {
        val resId = context.resources.getIdentifier(MUSIC_NAME, "raw", context.packageName)
        if (resId == 0) null
        else SeamlessLooper(context, resId, MUSIC_VOLUME).takeIf { it.ready }
    }

    DisposableEffect(context) {
        val owner = context as? ComponentActivity
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> inForeground.value = true
                Lifecycle.Event.ON_STOP -> inForeground.value = false
                else -> Unit
            }
        }
        owner?.lifecycle?.addObserver(observer)
        onDispose { owner?.lifecycle?.removeObserver(observer) }
    }

    LaunchedEffect(enabled, inForeground.value) {
        if (enabled && inForeground.value) looper?.run()
    }

    DisposableEffect(looper) {
        onDispose { looper?.release() }
    }
}
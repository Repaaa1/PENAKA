package com.example.tesprojek.ui.components

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

/**
 * Efek suara "tut" saat tombol diketuk (res/raw/tut.wav).
 * [enabled] mengikuti tombol speaker. Kalau file belum ada, tidak melakukan apa-apa.
 */
object ClickSound {
    @Volatile var enabled: Boolean = true

    private var pool: SoundPool? = null
    private var soundId = 0
    private var loaded = false

    fun init(context: Context) {
        if (pool != null) return
        val app = context.applicationContext
        val resId = app.resources.getIdentifier("tut", "raw", app.packageName)
        if (resId == 0) return
        val sp = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()
        sp.setOnLoadCompleteListener { _, _, status -> loaded = status == 0 }
        soundId = sp.load(app, resId, 1)
        pool = sp
    }

    fun play() {
        if (!enabled || !loaded) return
        pool?.play(soundId, 0.8f, 0.8f, 1, 0, 1f)
    }
}
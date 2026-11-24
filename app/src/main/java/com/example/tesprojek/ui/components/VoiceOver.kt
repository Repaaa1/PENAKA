package com.example.tesprojek.ui.components

import android.content.Context
import android.media.MediaPlayer
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Suara pengantar (rekaman sendiri) di res/raw. Satu suara pada satu waktu; suara baru
 * menggantikan yang lama. File yang belum ada dilewati tanpa error.
 */
object VoiceOver {
    private var player: MediaPlayer? = null
    private var currentName: String? = null
    private var onDone: (() -> Unit)? = null

    fun play(
        context: Context,
        name: String,
        fallback: String? = null,
        onComplete: (() -> Unit)? = null
    ) {
        stop()
        val app = context.applicationContext
        var resId = app.resources.getIdentifier(name, "raw", app.packageName)
        var played = name
        if (resId == 0 && fallback != null) {
            resId = app.resources.getIdentifier(fallback, "raw", app.packageName)
            played = fallback
        }
        if (resId == 0) { onComplete?.invoke(); return }

        val mp = MediaPlayer.create(app, resId)
        if (mp == null) { onComplete?.invoke(); return }

        currentName = played
        onDone = onComplete
        player = mp.apply {
            setOnCompletionListener { done ->
                if (player === done) player = null
                runCatching { done.release() }
                val cb = onDone
                onDone = null
                cb?.invoke()
            }
            setOnErrorListener { done, _, _ ->
                if (player === done) player = null
                runCatching { done.release() }
                val cb = onDone
                onDone = null
                cb?.invoke()
                true
            }
            start()
        }
    }

    fun stop() {
        onDone = null
        player?.let { runCatching { it.stop() }; runCatching { it.release() } }
        player = null
        currentName = null
    }

    fun stopIf(name: String?) {
        if (name != null && name == currentName) stop()
    }
}

suspend fun VoiceOver.playAndAwait(
    context: Context,
    name: String,
    fallback: String? = null
) = suspendCancellableCoroutine<Unit> { cont ->
    play(context, name, fallback) { if (cont.isActive) cont.resume(Unit) }
    cont.invokeOnCancellation {
        stopIf(name)
        stopIf(fallback)
    }
}

/**
 * Memutar suara untuk layar yang sedang tampil. [name] null = tidak ada suara (suara lama dihentikan).
 * Berhenti saat [enabled] false (speaker dimatikan) atau aplikasi ke latar belakang.
 * [passive] = true: tidak mengubah suara (layar itu mengurus suaranya sendiri).
 */
@Composable
fun ScreenVoice(name: String?, enabled: Boolean, passive: Boolean = false, fallback: String? = null) {
    val context = LocalContext.current
    val latestName = rememberUpdatedState(name)
    val latestFallback = rememberUpdatedState(fallback)

    LaunchedEffect(name, enabled, passive) {
        if (passive) return@LaunchedEffect
        if (name != null && enabled) VoiceOver.play(context, name, fallback) else VoiceOver.stop()
    }

    DisposableEffect(context) {
        val owner = context as? ComponentActivity
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) VoiceOver.stop()
        }
        owner?.lifecycle?.addObserver(observer)
        onDispose {
            owner?.lifecycle?.removeObserver(observer)
            VoiceOver.stopIf(latestName.value)
            VoiceOver.stopIf(latestFallback.value)
        }
    }
}
package com.impalo.features.editor.playback

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.media3.common.Effect
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.impalo.media.IdentityGlEffect

@OptIn(UnstableApi::class)
@Composable
fun rememberChromiaPlayer(
    uri: Uri
) : ExoPlayer {

    val applicationContext = LocalContext.current.applicationContext
    val player = remember(applicationContext) {
        ExoPlayer.Builder(applicationContext).build().also { exoPlayer ->
            exoPlayer.setVideoEffects(
                listOf<Effect>(IdentityGlEffect())
            )
        }
    }

    var resumeAfterForeground by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(player, uri) {
        if(uri == Uri.EMPTY) {
            player.clearMediaItems()
            return@LaunchedEffect
        }

        player.setMediaItem(MediaItem.fromUri(uri))

        player.prepare()
    }

    LifecycleResumeEffect(player) {
        if (
            uri != Uri.EMPTY &&
            resumeAfterForeground
        ) {
            player.play()
        }

        onPauseOrDispose {
            resumeAfterForeground = player.playWhenReady
            player.pause()
        }
    }

    DisposableEffect(player) {
        onDispose {
            player.release()
        }
    }

    return player
}
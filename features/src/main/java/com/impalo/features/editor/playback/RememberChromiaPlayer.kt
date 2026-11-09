package com.impalo.features.editor.playback

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.media3.common.Effect
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaLibraryInfo
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.impalo.features.editor.model.SettingsState
import com.impalo.media.GradingSettings
import com.impalo.media.GradingSettingsHolder
import com.impalo.media.IdentityGlEffect
import com.impalo.media.VideoMetadata
import androidx.media3.common.C
import androidx.media3.common.Format

@OptIn(UnstableApi::class)
@Composable
fun rememberChromiaPlayer(
    uri: Uri,
    gradingSettings: SettingsState,
    onVideoMetadataLoaded: (VideoMetadata) -> Unit
) : ExoPlayer {

    val settingsHolder = remember {
        GradingSettingsHolder()
    }

    val sdrEffect = remember(settingsHolder) {
        IdentityGlEffect(settingsHolder)
    }
    val samsungLogEffect = remember(settingsHolder) {
        IdentityGlEffect(settingsHolder, samsungLog = true)
    }

    SideEffect{
        settingsHolder.update(
            gradingSettings.toGradingSettings()
        )
    }

    val currentOnVideoMetadataLoaded by rememberUpdatedState(
        onVideoMetadataLoaded
    )

    val applicationContext = LocalContext.current.applicationContext
    val player = remember(applicationContext) {
        ExoPlayer.Builder(applicationContext).build().also { exoPlayer ->
            exoPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState != Player.STATE_READY) return

                    val format = exoPlayer.videoFormat ?: return
                    val metadata = format.toVideoMetadata()

                    val effect = if (metadata.supportsSamsungLogV1()) {
                        samsungLogEffect
                    } else {
                        sdrEffect
                    }

                    exoPlayer.setVideoEffects(listOf(effect))
                    currentOnVideoMetadataLoaded(metadata)
                }
            })
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
        player.setVideoEffects(listOf(sdrEffect))
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


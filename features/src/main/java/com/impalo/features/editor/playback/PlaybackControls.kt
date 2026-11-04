package com.impalo.features.editor.playback

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.state.rememberPlayPauseButtonState
import androidx.media3.ui.compose.state.rememberProgressStateWithTickInterval
import com.impalo.core.ui.theme.InterFontFamily
import com.impalo.features.R
import com.impalo.features.editor.controls.VideoSeekBar
import java.util.Locale

@OptIn(UnstableApi::class)
@Composable
fun BoxScope.PlaybackControls(
    player: Player,
    modifier: Modifier = Modifier
) {

    val playPauseState = rememberPlayPauseButtonState(player)
    val progressState = rememberProgressStateWithTickInterval(
        player = player,
        tickIntervalMs = 35L
    )
    var wasPlayingBeforeDrag by remember(player) { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .padding(start = 24.dp, end = 18.dp)
            .align(Alignment.BottomStart),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = playPauseState::onClick,
            modifier = modifier,
        ) {
            Icon(
                modifier = Modifier.size(26.dp),
                painter = painterResource(
                    if (playPauseState.showPlay) R.drawable.play_ic
                    else R.drawable.pause_ic
                ),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "${formatVideoTime(progressState.currentPositionMs)} / ${formatVideoTime(progressState.durationMs)}" ,
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = InterFontFamily,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.width(20.dp))

        VideoSeekBar(
            modifier = Modifier.weight(1f),
            positionMs = progressState.currentPositionMs,
            durationMs = progressState.durationMs,
            seekTo = { newPositionMs ->
                if(player.isCommandAvailable(Player.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM)) {
                    player.seekTo(newPositionMs)
                }
            },
            onDragStart = {
                wasPlayingBeforeDrag = player.isPlaying
                player.pause()
            },
            onDragEnd  = {
                if(wasPlayingBeforeDrag) player.play()
            },
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

private fun formatVideoTime(
    timeMs: Long
) : String {
    val safeTimeMs = timeMs.coerceAtLeast(0L)

    val totalSeconds = safeTimeMs / 1_000L

    val hours = totalSeconds / 3_600L
    val minutes = (totalSeconds % 3_600L) / 60
    val seconds = totalSeconds % 60L

    return if(hours > 0) {
        String.format(
            Locale.ROOT,
            "%d:%02d:%02d",
            hours,
            minutes,
            seconds,
        )
    } else {
        String.format(
            Locale.ROOT,
            "%02d:%02d",
            minutes,
            seconds
        )
    }
}
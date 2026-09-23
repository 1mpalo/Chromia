package com.impalo.features.editor.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.impalo.core.ui.theme.InterFontFamily
import com.impalo.features.R
import java.util.Locale

@Composable
fun BoxScope.PlaybackControls(
    currentPositionMs: Long,
    durationMs: Long,
    showPlayButton: Boolean,
    modifier: Modifier = Modifier,
    onPlayPauseClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .padding(start = 26.dp, end = 18.dp)
            .align(Alignment.BottomStart),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPlayPauseClicked,
            modifier = modifier,
        ) {
            Icon(
                modifier = Modifier.size(26.dp),
                painter = painterResource(
                    if (showPlayButton) R.drawable.play_ic
                    else R.drawable.pause_ic
                ),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        Text(
            text = "${formatVideoTime(currentPositionMs)} / ${formatVideoTime(durationMs)}" ,
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = InterFontFamily,
            fontSize = 14.sp
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
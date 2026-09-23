package com.impalo.features.editor

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.state.rememberPlayPauseButtonState
import androidx.media3.ui.compose.state.rememberProgressStateWithTickInterval
import com.impalo.features.editor.components.ChromiaVideoPlayer
import com.impalo.features.editor.components.EditingPanel
import com.impalo.features.editor.components.EditorTopBar
import com.impalo.features.editor.components.PlaybackControls
import com.impalo.features.editor.components.rememberChromiaPlayer

@OptIn(UnstableApi::class)
@Composable
fun EditorScreen(
    uri: Uri
) {
    val context = LocalContext.current
    val fileName = remember(uri) {
        context.contentResolver.getFileName(uri)
            ?: "Unknown video"
    }

    val player = rememberChromiaPlayer(uri)
    val playPauseState = rememberPlayPauseButtonState(player)
    val progressState = rememberProgressStateWithTickInterval(
        player = player,
        tickIntervalMs = 1_000L
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(0.66f)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.TopCenter,
            ) {

                ChromiaVideoPlayer(
                    player = player,
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.BottomCenter)
                )

                EditorTopBar(
                    fileName = fileName,
                    onAutoGradeClicked = {/* TODO Добавить */ },
                    onExportClicked = {/* TODO Добавить */ },
                )

                PlaybackControls(
                    showPlayButton = playPauseState.showPlay,
                    onPlayPauseClicked = playPauseState::onClick,
                    currentPositionMs = progressState.currentPositionMs,
                    durationMs = progressState.durationMs
                )
            }

            VerticalDivider(
                color = MaterialTheme.colorScheme.inversePrimary,
                thickness = 1.dp
            )

            EditingPanel(modifier = Modifier.weight(0.34f))

        }
    }
}


// TODO по хорошему надо перенести в viewModel, так как работаем с системой
fun ContentResolver.getFileName(uri: Uri): String? {
    val projection = arrayOf(
        OpenableColumns.DISPLAY_NAME
    )

    query(
        uri,
        projection,
        null,
        null,
        null
    )?.use { cursor ->
        val nameColumnIndex = cursor.getColumnIndex(
            OpenableColumns.DISPLAY_NAME
        )

        if (nameColumnIndex >= 0 && cursor.moveToFirst()) {
            return cursor.getString(nameColumnIndex)
        }
    }

    return uri.lastPathSegment
}
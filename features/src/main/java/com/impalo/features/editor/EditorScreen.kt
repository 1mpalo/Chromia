package com.impalo.features.editor

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.state.rememberPlayPauseButtonState
import androidx.media3.ui.compose.state.rememberProgressStateWithTickInterval
import com.impalo.features.editor.playback.ChromiaVideoPlayer
import com.impalo.features.editor.controls.EditingPanel
import com.impalo.features.editor.controls.EditorTopBar
import com.impalo.features.editor.playback.PlaybackControls
import com.impalo.features.editor.model.SettingsState
import com.impalo.features.editor.playback.rememberChromiaPlayer

@OptIn(UnstableApi::class)
@Composable
fun EditorScreen(
    uri: Uri,
    fileName: String,
    onEvent: (EditorEvents) -> Unit,
    settings: SettingsState
) {

    val player = rememberChromiaPlayer(
        uri = uri,
        gradingSettings = settings
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
                    player = player
                )
            }

            VerticalDivider(
                color = MaterialTheme.colorScheme.inversePrimary,
                thickness = 1.dp
            )

            EditingPanel(
                modifier = Modifier.weight(0.34f),
                onSliderValueChange = { tool, newValue ->
                    onEvent(EditorEvents.OnSliderValueChange(tool, newValue))
                },
                settings = settings
            )
        }
    }
}
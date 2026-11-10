package com.impalo.features.editor

import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.Format
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.state.rememberPlayPauseButtonState
import androidx.media3.ui.compose.state.rememberProgressStateWithTickInterval
import com.impalo.features.editor.playback.ChromiaVideoPlayer
import com.impalo.features.editor.controls.EditingPanel
import com.impalo.features.editor.controls.EditorTopBar
import com.impalo.features.editor.controls.ExportOverlay
import com.impalo.features.editor.model.ExportState
import com.impalo.features.editor.playback.PlaybackControls
import com.impalo.features.editor.model.SettingsState
import com.impalo.features.editor.model.UiEffect
import com.impalo.features.editor.playback.rememberChromiaPlayer
import com.impalo.features.editor.playback.toVideoInfoLine
import com.impalo.media.VideoExporter
import com.impalo.media.VideoMetadata
import kotlinx.coroutines.flow.Flow

@OptIn(UnstableApi::class)
@Composable
fun EditorScreen(
    uri: Uri,
    onEvent: (EditorEvents) -> Unit,
    settings: SettingsState,
    videoMetadata: VideoMetadata?,
    effectFlow: Flow<UiEffect>,
    exportState: ExportState
) {

    val player = rememberChromiaPlayer(
        uri = uri,
        gradingSettings = settings,
        onVideoMetadataLoaded = { metadata ->
            onEvent(EditorEvents.OnVideoMetadataLoaded(metadata, uri))
        }
    )

    val context = LocalContext.current
    val videoExporter = VideoExporter(context)

    LaunchedEffect(player, exportState is ExportState.Exporting) {
        if(exportState is ExportState.Exporting) player.pause()
    }

    val contentResolver = context.contentResolver
    val extension = contentResolver.getType(uri)?.let { mimeType ->
        MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
    }
    val info = videoMetadata?.toVideoInfoLine(extension)

    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is UiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

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
                    info = info,
                    onAutoGradeClicked = {/* TODO Добавить */ },
                    onExportClicked = {
                        onEvent(EditorEvents.OnExportClicked(uri, videoExporter))
                    },
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

        if(exportState is ExportState.Exporting) {
            ExportOverlay(
                exportState = exportState
            )
        }
    }
}
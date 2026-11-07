package com.impalo.features.editor

import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.impalo.features.editor.model.ControlSpec
import com.impalo.features.editor.model.ExportState
import com.impalo.features.editor.model.SettingsState
import com.impalo.features.editor.model.Tool
import com.impalo.features.editor.model.UiEffect
import com.impalo.media.VideoExporter
import com.impalo.media.VideoMetadata
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditorViewModel : ViewModel() {

    private val _settings = MutableStateFlow<SettingsState>(SettingsState())
    val settings = _settings.asStateFlow()

    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState = _exportState.asStateFlow()

    private val _videoMetadata = MutableStateFlow<VideoMetadata?>(null)
    val videMetadata = _videoMetadata.asStateFlow()

    private val _effectFlow = MutableSharedFlow<UiEffect>()
    val effectFlow = _effectFlow.asSharedFlow()

    private var exportJob: Job? = null

    fun onEvent(event: EditorEvents) {
        when(event) {
            is EditorEvents.OnSliderValueChange -> {
                onToolValueChange(tool = event.tool, newValue = event.newValue)
            }

            is EditorEvents.OnExportClicked -> {
                onExportClicked(
                    videoExporter = event.videoExporter,
                    uri = event.uri
                )
            }

            is EditorEvents.OnVideoMetadataLoaded -> {
                _videoMetadata.value = event.metadata
            }
        }
    }

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

    private fun onToolValueChange(tool: Tool, newValue: Float) {
        val slider = tool.control as? ControlSpec.Slider ?: return

        val value = newValue.coerceIn(
            slider.range.start,
            slider.range.endInclusive
        )

        Log.d("value", "$value")

        _settings.update { state ->
            when (tool) {
                Tool.EXPOSURE ->
                    state.copy(exposure = value)

                Tool.CONTRAST ->
                    state.copy(contrast = value)

                Tool.HIGHLIGHTS ->
                    state.copy(highlights = value)

                Tool.SHADOWS ->
                    state.copy(shadows = value)

                Tool.WHITES ->
                    state.copy(whites = value)

                Tool.BLACKS ->
                    state.copy(blacks = value)

                Tool.TEMP ->
                    state.copy(temp = value)

                Tool.TINT ->
                    state.copy(tint = value)

                Tool.SATURATION ->
                    state.copy(saturation = value)

                Tool.VIBRANCE ->
                    state.copy(vibrance = value)

                else -> { state }
            }
        }
    }

    private fun onExportClicked(
        videoExporter: VideoExporter,
        uri: Uri
    ) {
        if(exportJob?.isCompleted != false) {
            val settingsSnapshot = _settings.value.toGradingSettings()

            _exportState.value = ExportState.Exporting(progress = null)

            exportJob = viewModelScope.launch {
                try {
                    val savedUri = videoExporter.export(
                        uri = uri,
                        grading = settingsSnapshot,
                        onProgress = { progress ->
                            _exportState.value = ExportState.Exporting(progress = progress)
                        },
                        onSaving = {
                            _exportState.value = ExportState.Saving
                        }
                    )

                    _exportState.value = ExportState.Success(uri = savedUri)
                    _effectFlow.emit(UiEffect.ShowToast("Video saved"))
                } catch (exception: CancellationException) {
                    _exportState.value = ExportState.Idle
                    throw exception
                } catch (exception: Exception) {
                    _exportState.value = ExportState.Error(
                        message = exception.message
                            ?: "Failed to export the video"
                    )
                } finally {
                    exportJob = null
                }
            }
        }
    }
}
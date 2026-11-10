package com.impalo.features.editor

import android.net.Uri
import com.impalo.features.editor.model.Tool
import com.impalo.media.VideoExporter
import com.impalo.media.VideoMetadata

sealed interface EditorEvents {

    data class OnSliderValueChange(
        val tool: Tool,
        val newValue: Float
    ) : EditorEvents

    data class OnExportClicked(
        val uri: Uri,
        val videoExporter: VideoExporter
    ) : EditorEvents

    data class OnVideoMetadataLoaded(
        val metadata: VideoMetadata,
        val uri: Uri
    ) : EditorEvents
}
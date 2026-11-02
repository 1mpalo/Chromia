package com.impalo.features.editor.model

import android.net.Uri

sealed interface ExportState {
    data object Idle: ExportState

    data class Exporting(
        val progress: Int?
    ) : ExportState

    data object Saving : ExportState

    data class Success(
        val uri: Uri
    ) : ExportState

    data class Error(
        val message: String
    ) : ExportState
}
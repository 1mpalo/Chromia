package com.impalo.features.editor

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.ViewModel
import com.impalo.features.editor.model.ControlSpec
import com.impalo.features.editor.model.SettingsState
import com.impalo.features.editor.model.Tool
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EditorViewModel : ViewModel() {

    private val _settings = MutableStateFlow<SettingsState>(SettingsState())
    val settings = _settings.asStateFlow()

    fun onEvent(event: EditorEvents) {
        when(event) {
            is EditorEvents.OnSliderValueChange -> {
                onToolValueChange(tool = event.tool, newValue = event.newValue)
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
}
package com.impalo.features.editor

import com.impalo.features.editor.model.Tool

interface EditorEvents {

    data class OnSliderValueChange(
        val tool: Tool,
        val newValue: Float
    ) : EditorEvents
}
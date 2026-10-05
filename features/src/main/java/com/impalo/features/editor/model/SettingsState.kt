package com.impalo.features.editor.model

data class SettingsState (
    val exposure: Float = 0f,
    val contrast: Float = 0f,
    val highlights: Float = 0f,
    val shadows: Float = 0f,
    val whites: Float = 0f,
    val blacks: Float = 0f,
    val temp: Float = 0f,
    val tint: Float = 0f,
    val saturation: Float = 100f,
    val vibrance: Float = 0f,
) {
    fun valueFor(tool: Tool): Float = when (tool) {
        Tool.EXPOSURE -> exposure
        Tool.CONTRAST -> contrast
        Tool.HIGHLIGHTS -> highlights
        Tool.SHADOWS -> shadows
        Tool.WHITES -> whites
        Tool.BLACKS -> blacks
        Tool.TEMP -> temp
        Tool.TINT -> tint
        Tool.SATURATION -> saturation
        Tool.VIBRANCE -> vibrance

        else -> { 0.0f }
    }
}
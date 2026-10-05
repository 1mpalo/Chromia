package com.impalo.features.editor.model

enum class Tool(
    val title: String,
    val category: Category,
    val control: ControlSpec
) {
    EXPOSURE(
        title = "Exposure",
        category = Category.LIGHT,
        control = ControlSpec.Slider(
            range = -4f..4f,
            defaultValue = 0f,
            suffix = "EV",
            decimalPlaces = 1
        )
    ),
    CONTRAST(
        title = "Contrast",
        category = Category.LIGHT,
        control = ControlSpec.Slider(
            range = -100f..100f,
            defaultValue = 0f
        )
    ),
    HIGHLIGHTS(
        title = "Highlights",
        category = Category.LIGHT,
        control = ControlSpec.Slider(
            range = -100f..100f,
            defaultValue = 0f
        )
    ),
    SHADOWS(
        title = "Shadows",
        category = Category.LIGHT,
        control = ControlSpec.Slider(
            range = -100f..100f,
            defaultValue = 0f
        )
    ),
    WHITES(
        title = "White",
        category = Category.LIGHT,
        control = ControlSpec.Slider(
            range = -100f..100f,
            defaultValue = 0f
        )
    ),
    BLACKS(
        title = "Blacks",
        category = Category.LIGHT,
        control = ControlSpec.Slider(
            range = -100f..100f,
            defaultValue = 0f
        )
    ),
    TEMP(
        title = "Temp",
        category = Category.COLOR,
        control = ControlSpec.Slider(
            range = -100f..100f,
            defaultValue = 0f
        )
    ),
    TINT(
        title = "Tint",
        category = Category.COLOR,
        control = ControlSpec.Slider(
            range = -100f..100f,
            defaultValue = 0f
        )
    ),
    SATURATION(
        title = "Saturation",
        category = Category.COLOR,
        control = ControlSpec.Slider(
            range = 0f..200f,
            defaultValue = 100f,
            suffix = "%"
        )
    ),
    VIBRANCE(
        title = "Vibrance",
        category = Category.COLOR,
        control = ControlSpec.Slider(
            range = -100f..100f,
            defaultValue = 0f
        )
    ),
    RGB(
        title = "RGB",
        category = Category.CURVES,
        control = ControlSpec.Curve
    ),
    R(
        title = "R",
        category = Category.CURVES,
        control = ControlSpec.Curve
    ),
    G(
        title = "G",
        category = Category.CURVES,
        control = ControlSpec.Curve
    ),
    B(
        title = "B",
        category = Category.CURVES,
        control = ControlSpec.Curve
    ),
}

sealed interface ControlSpec {
    data class Slider(
        val range: ClosedFloatingPointRange<Float>,
        val defaultValue: Float,
        val fillFrom: Float = defaultValue,
        val suffix: String = "",
        val decimalPlaces: Int = 0
    ) : ControlSpec

    data object Curve: ControlSpec
    data object LutPicker: ControlSpec
}
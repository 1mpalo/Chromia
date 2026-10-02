package com.impalo.features.editor.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun CustomSlider(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: (Float) -> Unit,
    color: Color,
    range: ClosedFloatingPointRange<Float>,
    defaultValue: Float,
    fillFrom: Float = defaultValue
) {
    require(range.start < range.endInclusive) {
        "Slider range must have a positive length"
    }

    val currentOnValueChange by rememberUpdatedState(onValueChange)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp)
            .pointerInput(range.start, range.endInclusive) {
                val thumbRadius = 6.dp.toPx()

                fun updateFromTouch(x: Float) {
                    val left = thumbRadius
                    val right = size.width - thumbRadius
                    if (right <= left) return

                    val fraction =
                        ((x - left) / (right - left)).coerceIn(0f, 1f)

                    // Переводим долю в настоящий диапазон слайдера.
                    val newValue =
                        range.start +
                                fraction * (range.endInclusive - range.start)

                    currentOnValueChange(newValue)
                }

                detectDragGestures(
                    onDragStart = { offset ->
                        updateFromTouch(offset.x)
                    },
                    onDrag = { change, _ ->
                        updateFromTouch(change.position.x)
                        change.consume()
                    }
                )
            }
    ) {
        val thumbRadius = 6.dp.toPx()
        val left = thumbRadius
        val right = size.width - thumbRadius
        val centerY = size.height / 2f
        val trackWidth = right - left

        if (trackWidth <= 0f) return@Canvas

        fun valueToX(number: Float): Float {
            val fraction =
                ((number - range.start) /
                        (range.endInclusive - range.start))
                    .coerceIn(0f, 1f)

            return left + trackWidth * fraction
        }

        val thumbX = valueToX(value)
        val fillFromX = valueToX(fillFrom)

        // Вся дорожка.
        drawLine(
            color = color.copy(alpha = 0.3f),
            start = Offset(left, centerY),
            end = Offset(right, centerY),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Отметка нейтрального значения, если оно внутри дорожки.
        if (fillFromX > left && fillFromX < right) {
            val tickHalfHeight = 5.dp.toPx()

            drawLine(
                color = color.copy(alpha = 0.5f),
                start = Offset(fillFromX, centerY - tickHalfHeight),
                end = Offset(fillFromX, centerY + tickHalfHeight),
                strokeWidth = 1.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Заполнение от fillFrom до текущего значения.
        if (thumbX != fillFromX) {
            drawLine(
                color = color,
                start = Offset(fillFromX, centerY),
                end = Offset(thumbX, centerY),
                strokeWidth = 2.5.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        drawCircle(
            color = color,
            radius = thumbRadius,
            center = Offset(thumbX, centerY)
        )
    }
}
package com.impalo.features.editor.controls

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
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
fun VideoSeekBar (
    modifier: Modifier = Modifier,
    positionMs: Long,
    durationMs: Long,
    seekTo: (positionMs: Long) -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    color: Color
) {

    val currentOnValueChange by rememberUpdatedState(seekTo)

    Canvas(
        modifier = modifier
            .height(26.dp)
            .pointerInput(durationMs) {

                val thumbRadius = 6.dp.toPx()

                fun updateFromTouch(x: Float) {
                    if (durationMs <= 0L) return

                    val left = thumbRadius
                    val right = size.width - thumbRadius
                    if (right <= left) return

                    val fraction = ((x - left) / (right - left)).coerceIn(0f, 1f)
                    val newPositionMs = (fraction * durationMs).toLong()

                    currentOnValueChange(newPositionMs)
                }

                detectDragGestures(
                    onDragStart = { offset ->
                        onDragStart()
                        updateFromTouch(x = offset.x)
                    },
                    onDrag = { change, _ ->
                        updateFromTouch(x = change.position.x)
                        change.consume()
                    },
                    onDragEnd = onDragEnd
                )
            }
    ) {
        val thumbRadius = 6.dp.toPx()
        val left = thumbRadius
        val right = size.width - thumbRadius
        val centerY = size.height / 2

        val fraction = (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)

        val thumbX = left + (right - left) * fraction

        drawLine(
            color = color.copy(alpha = 0.3f),
            start = Offset(x = left, y = centerY),
            end = Offset(x = right, y = centerY),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )

        drawLine(
            color = color,
            start = Offset(x = left, y = centerY),
            end = Offset(x = thumbX , y = centerY),
            strokeWidth = 3.5.dp.toPx(),
            cap = StrokeCap.Round
        )

        drawCircle(
            color = color,
            radius = thumbRadius,
            center = Offset(x = thumbX, y = centerY),
        )
    }

}
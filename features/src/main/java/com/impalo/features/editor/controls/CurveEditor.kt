package com.impalo.features.editor.controls

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.impalo.media.CurvePoint

@Composable
fun CurveEditor(
    modifier: Modifier = Modifier,
    points: List<CurvePoint>,
    onPositionChange: (index: Int, point: CurvePoint) -> Unit,
    onDragEnd: () -> Unit,
    curveColor: Color,
) {
    val latestPoints by rememberUpdatedState(points)
    val notifyPositionChange by rememberUpdatedState(onPositionChange)
    val notifyDragEnd by rememberUpdatedState(onDragEnd)
    val haptics = LocalHapticFeedback.current
    val inset = 8.dp

    Canvas(
        modifier = modifier
            .aspectRatio(1.35f)
            .pointerInput(haptics) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    val plot = graphBounds(size.width.toFloat(), size.height.toFloat(), inset.toPx())
                    if (plot.width <= 0f || plot.height <= 0f) return@awaitEachGesture

                    val gesturePoints = latestPoints
                    val pointIndex = (1 until gesturePoints.lastIndex).minByOrNull { index ->
                        (down.position - plot.positionOf(gesturePoints[index])).getDistance()
                    } ?: return@awaitEachGesture

                    var draggedPoint = gesturePoints[pointIndex]
                    if ((down.position - plot.positionOf(draggedPoint)).getDistance() > 20.dp.toPx()) {
                        return@awaitEachGesture
                    }

                    val previousX = gesturePoints[pointIndex - 1].x
                    val nextX = gesturePoints[pointIndex + 1].x
                    val minX = minOf(draggedPoint.x, previousX + 0.02f)
                    val maxX = maxOf(draggedPoint.x, nextX - 0.02f)
                    var pointChanged = false

                    fun movePoint(delta: Offset, allowHorizontal: Boolean) {
                        val newX = if (allowHorizontal) {
                            (draggedPoint.x + delta.x / plot.width).coerceIn(minX, maxX)
                        } else {
                            draggedPoint.x
                        }
                        val newY = (draggedPoint.y - delta.y / plot.height).coerceIn(0f, 1f)
                        val updatedPoint = CurvePoint(x = newX, y = newY)
                        if (updatedPoint == draggedPoint) return

                        draggedPoint = updatedPoint
                        pointChanged = true
                        notifyPositionChange(pointIndex, updatedPoint)
                    }

                    try {
                        var pointerId = down.id
                        val movedBeforeHold = withTimeoutOrNull(
                            viewConfiguration.longPressTimeoutMillis
                        ) {
                            val dragStart = awaitTouchSlopOrCancellation(down.id) { change, overSlop ->
                                movePoint(overSlop, allowHorizontal = false)
                                change.consume()
                            }
                            if (dragStart != null) pointerId = dragStart.id
                            dragStart != null
                        }

                        if (movedBeforeHold == false) return@awaitEachGesture

                        val allowHorizontal = movedBeforeHold == null
                        if (allowHorizontal) {
                            val stillPressed = currentEvent.changes.any {
                                it.id == pointerId && it.pressed
                            }
                            if (!stillPressed) return@awaitEachGesture

                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        }

                        drag(pointerId) { change ->
                            movePoint(change.positionChange(), allowHorizontal)
                            change.consume()
                        }
                    } finally {
                        if (pointChanged) notifyDragEnd()
                    }
                }
            },
    ) {
        val plot = graphBounds(size.width, size.height, inset.toPx())
        if (plot.width <= 0f || plot.height <= 0f) return@Canvas

        val gridColor = Color.White.copy(0.3f)
        val gridStroke = 1.dp.toPx()
        drawRect(
            color = gridColor,
            topLeft = plot.topLeft,
            size = plot.size,
            style = Stroke(width = gridStroke),
        )

        for (division in 1..3) {
            val fraction = division / 4f
            val x = plot.left + fraction * plot.width
            val y = plot.top + fraction * plot.height
            drawLine(gridColor, Offset(x, plot.top), Offset(x, plot.bottom), gridStroke)
            drawLine(gridColor, Offset(plot.left, y), Offset(plot.right, y), gridStroke)
        }

        drawLine(
            color = gridColor.copy(alpha = 0.25f),
            start = Offset(plot.left, plot.bottom),
            end = Offset(plot.right, plot.top),
            strokeWidth = gridStroke,
        )

        val curveStroke = 2.dp.toPx()
        for (index in 0 until points.lastIndex) {
            drawLine(
                color = curveColor,
                start = plot.positionOf(points[index]),
                end = plot.positionOf(points[index + 1]),
                strokeWidth = curveStroke,
            )
        }

        val pointRadius = 5.dp.toPx()
        for (point in points) {
            drawCircle(curveColor, pointRadius, plot.positionOf(point))
        }
    }
}

private fun graphBounds(width: Float, height: Float, inset: Float): Rect =
    Rect(inset, inset, width - inset, height - inset)

private fun Rect.positionOf(point: CurvePoint): Offset =
    Offset(
        x = left + point.x * width,
        y = bottom - point.y * height,
    )

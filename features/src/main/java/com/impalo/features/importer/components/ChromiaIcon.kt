package com.impalo.features.importer.components

import android.graphics.DashPathEffect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.impalo.core.ui.theme.ChromiaTheme
import kotlin.math.PI

@Composable
fun ChromiaIcon(
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier.size(44.dp)
    ) {
        val outerRadius = size.minDimension / 2f
        val dashRadius = outerRadius * 0.55f
        val innerRadius = outerRadius * 0.15f

        val length = 2f * PI.toFloat() * dashRadius
        val segmentLength = length / 8

        val dashLength = segmentLength * 0.5f
        val gapLength = segmentLength * 0.5f

        drawCircle(
            color = Color.Gray,
            radius = outerRadius,
            style = Stroke(width = 1.5.dp.toPx())
        )

        drawCircle(
            color = Color.Gray,
            radius = dashRadius,
            style = Stroke(
                width = 1.3 .dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(
                    intervals = floatArrayOf(dashLength, gapLength),
                    phase = 0f
                )
            )
        )

        drawCircle(
            color = Color.Gray,
            radius = innerRadius
        )

    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0A0A0C
)
@Composable
private fun PreviewChromiaIcon() {
    ChromiaTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ChromiaIcon()
        }
    }
}

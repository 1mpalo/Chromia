package com.impalo.features.editor.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.impalo.core.ui.theme.ChromiaTheme
import com.impalo.core.ui.theme.CurveBlue
import com.impalo.core.ui.theme.CurveGreen
import com.impalo.core.ui.theme.CurveRed
import com.impalo.core.ui.theme.CurveRgb
import com.impalo.core.ui.theme.InterFontFamily
import com.impalo.core.ui.theme.Neutral100
import com.impalo.features.editor.model.ControlSpec
import com.impalo.features.editor.model.SettingsState
import com.impalo.features.editor.model.Tool
import com.impalo.media.CurveChannel
import com.impalo.media.CurvePoint

@Composable
fun ToolsList(
    tools: List<Tool>,
    onSliderValueChange: (tool: Tool, newValue: Float) -> Unit,
    onCurvePointMoved: (channel: CurveChannel, index: Int, value: CurvePoint) -> Unit,
    settings: SettingsState,
    onDragEnd: () -> Unit,
    onCurveDragEnd: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(36.dp)
    ) {
        items(tools) { tool ->
            if (tool.control is ControlSpec.Slider) {

                val currentValue = settings.valueFor(tool)

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = tool.title,
                            fontFamily = InterFontFamily,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "${"%.${tool.control.decimalPlaces}f".format(currentValue)} ${tool.control.suffix}",
                            fontFamily = InterFontFamily,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    CustomSlider(
                        value = currentValue,
                        onValueChange = { newValue ->
                            onSliderValueChange(tool, newValue)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        range = tool.control.range,
                        fillFrom = tool.control.fillFrom,
                        defaultValue = tool.control.defaultValue,
                        onDragEnd = onDragEnd
                    )
                }
            }

            if (tool.control is ControlSpec.Curve) {
                val channel = tool.control.channel
                val curveColor = when (channel) {
                    CurveChannel.RGB -> CurveRgb
                    CurveChannel.R -> CurveRed
                    CurveChannel.G -> CurveGreen
                    CurveChannel.B -> CurveBlue
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(start = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(color = curveColor, shape = CircleShape),
                        )

                        Text(
                            text = channel.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = InterFontFamily,
                            color = Neutral100,
                        )
                    }

                    CurveEditor(
                        modifier = Modifier.fillMaxWidth(),
                        points = settings.curves.getValue(channel),
                        onPositionChange = { index, point ->
                            onCurvePointMoved(channel, index, point)
                        },
                        onDragEnd = onCurveDragEnd,
                        curveColor = curveColor,
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

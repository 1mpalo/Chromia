package com.impalo.features.editor.controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.impalo.core.ui.theme.InterFontFamily
import com.impalo.features.editor.model.ControlSpec
import com.impalo.features.editor.model.SettingsState
import com.impalo.features.editor.model.Tool

@Composable
fun ToolsList(
    tools: List<Tool>,
    onSliderValueChange: (tool: Tool, newValue: Float) -> Unit,
    settings: SettingsState
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(36.dp)
    ) {
        items(tools) { tool ->
            if(tool.control is ControlSpec.Slider) {

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
                        defaultValue = tool.control.defaultValue
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

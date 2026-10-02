package com.impalo.features.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.impalo.core.ui.theme.InterFontFamily

@Composable
fun EditingPanel(
    modifier: Modifier = Modifier
) {

    var selectedCategory by remember { mutableStateOf(Category.LIGHT) }
    val tools = toolsFor(selectedCategory)

    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surfaceContainer),
    ) {

        CategoryBar(
            selectedCategory = selectedCategory,
            onCategoryClicked = { selectedCategory = it }
        )

        HorizontalDivider(
            color = MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.7f),
            thickness = 0.3.dp
        )

        Spacer(modifier = Modifier.height(12.dp))

        ToolsList(tools = tools)

    }
}

private fun toolsFor(selectedCategory: Category): List<Tool> {
    return when (selectedCategory) {
        Category.LIGHT -> listOf(
            Tool.EXPOSURE,
            Tool.CONTRAST,
            Tool.HIGHLIGHTS,
            Tool.SHADOWS,
            Tool.WHITES,
            Tool.BLACKS
        )

        Category.COLOR -> listOf(
            Tool.TEMP,
            Tool.TINT,
            Tool.SATURATION,
            Tool.VIBRANCE
        )

        Category.CURVES -> listOf(
            Tool.RGB,
            Tool.R,
            Tool.B,
            Tool.G
        )

        Category.LUT -> emptyList()
    }
}

@Composable
fun ToolsList(
    modifier: Modifier = Modifier,
    tools: List<Tool>
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(36.dp)
    ) {
        items(tools) { tool ->
            if(tool.control is ControlSpec.Slider) {
                var value by remember {
                    mutableFloatStateOf(tool.control.defaultValue)
                }

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
                            text = "$value ${tool.control.suffix}",
                            fontFamily = InterFontFamily,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    CustomSlider(
                        value = value,
                        onValueChange = { value = it },
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        range = tool.control.range,
                        fillFrom = tool.control.fillFrom,
                        defaultValue = tool.control.defaultValue
                    )
                }
            }
        }
    }
}

//TODO может выкинуть в другой файл
@Composable
fun RowScope.CategoryItem(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Text(
        modifier = modifier
            .drawBehind {
                if (isSelected) {
                    drawLine(
                        color = Color.Gray,
                        start = Offset(0f, size.height + 8.dp.toPx()),
                        end = Offset(size.width, size.height + 8.dp.toPx()),
                        strokeWidth = 3.5f
                    )
                }
            }
            .clickable { onClick() },
        text = text,
        fontFamily = InterFontFamily,
        color = if (isSelected) MaterialTheme.colorScheme.onBackground
        else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
    )
}

//TODO решить что с этим делать, тоже может выкинуть
@Composable
fun CategoryBar(
    selectedCategory: Category,
    onCategoryClicked: (Category) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(end = 18.dp)
    ) {
        for (item in Category.entries) {
            CategoryItem(
                text = item.categoryName,
                modifier = Modifier.weight(1f),
                isSelected = selectedCategory == item,
                onClick = { onCategoryClicked(item) }
            )
        }
    }
}
package com.impalo.features.editor.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.impalo.features.editor.model.Category
import com.impalo.features.editor.model.SettingsState
import com.impalo.features.editor.model.Tool

@Composable
fun EditingPanel(
    modifier: Modifier = Modifier,
    onSliderValueChange: (tool: Tool, newValue: Float) -> Unit,
    settings: SettingsState
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

        ToolsList(
            tools = tools,
            onSliderValueChange = onSliderValueChange,
            settings = settings
        )
    }
}

private fun toolsFor(selectedCategory: Category): List<Tool> =
    Tool.entries.filter { tool ->
        tool.category == selectedCategory
    }
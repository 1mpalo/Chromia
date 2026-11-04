package com.impalo.features.editor.controls

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.impalo.features.editor.model.Category

@Composable
fun CategoryBar(
    selectedCategory: Category,
    onCategoryClicked: (Category) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(end = 8.dp)
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
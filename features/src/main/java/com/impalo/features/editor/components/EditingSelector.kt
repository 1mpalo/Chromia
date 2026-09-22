package com.impalo.features.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.impalo.core.ui.theme.InterFontFamily
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip

@Composable
fun EditingSelector(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceContainer),
    ) {

        HorizontalDivider(
            color = MaterialTheme.colorScheme.inversePrimary
        )

        CategorySelector()

        HorizontalDivider(
            color = MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.5f),
            thickness = 0.3.dp
        )

        Spacer(modifier = Modifier.height(12.dp))

        SubCategoryItem(
            text = "Exposure",
            isSelected = true,
            onClick = {}
        )

        Slider(
            modifier = Modifier.fillMaxWidth(),
            value = 0.4f,
            onValueChange = {}
        )

    }
}

@Composable
fun SubCategoryItem(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(
                if(isSelected) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp),
            text = text,
            fontFamily = InterFontFamily,
            color = if(isSelected) MaterialTheme.colorScheme.onBackground
            else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
        )
    }
}

//TODO может выкинуть в другой файл
@Composable
fun RowScope.CategoryItem(
    modifier: Modifier = Modifier,
    text: String,
    weight: Float,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Text(
        modifier = modifier
            .weight(weight)
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
            .clickable{onClick()}
        ,
        text = text,
        fontFamily = InterFontFamily,
        color = if(isSelected) MaterialTheme.colorScheme.onBackground
            else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
    )
}

//TODO решить что с этим делать, тоже может выкинуть
@Composable
fun CategorySelector() {


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(end = 18.dp)
    ) {
        CategoryItem(
            text = "LIGHT",
            weight = 0.12f,
            isSelected = false,
            onClick = {}
        )

        CategoryItem(
            text = "COLOR",
            weight = 0.12f,
            isSelected = false,
            onClick = {}
        )

        CategoryItem(
            text = "CURVES",
            weight = 0.12f,
            isSelected = true,
            onClick = {}
        )

        CategoryItem(
            text = "LUT",
            weight = 0.12f,
            isSelected = false,
            onClick = {}
        )

        Spacer(modifier = Modifier.weight(0.52f))

        Text(

            text = "Reset",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            fontFamily = InterFontFamily,
            fontSize = 12.sp
        )
    }

}
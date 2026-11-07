package com.impalo.features.editor.controls

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.impalo.core.ui.common.ChromiaIcon
import com.impalo.core.ui.theme.InterFontFamily
import com.impalo.features.R

@Composable
fun EditorTopBar(
    info: String?,
    onAutoGradeClicked: () -> Unit,
    onExportClicked: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .padding(start = 26.dp, end = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChromiaIcon(
            modifier = Modifier.size(20.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = info ?: "Unknown format",
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = InterFontFamily,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            autoSize = TextAutoSize.StepBased(
                minFontSize = 10.sp,
                maxFontSize = 14.sp,
                stepSize = 0.5.sp
            )
        )

        CustomEditorButton(
            onClick = onAutoGradeClicked
        ) {
            Icon(
                painter = painterResource(R.drawable.star_ic),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "Auto Grade",
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = InterFontFamily,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        CustomEditorButton(
            onClick = onExportClicked,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
        ) {
            Text(
                text = "Export",
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = InterFontFamily,
                fontSize = 14.sp
            )
        }

    }
}

@Composable
private fun CustomEditorButton(
    modifier: Modifier = Modifier,
    border: BorderStroke = BorderStroke(0.dp, Color.Transparent),
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .border(border, RoundedCornerShape(6.dp)),
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}
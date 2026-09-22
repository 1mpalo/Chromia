package com.impalo.features.editor

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.impalo.core.ui.common.ChromiaIcon
import com.impalo.core.ui.theme.ChromiaTheme
import com.impalo.core.ui.theme.InterFontFamily
import com.impalo.features.R
import com.impalo.features.editor.components.EditingSelector

@Composable
fun EditorScreen(
    uri: Uri
) {
    val context = LocalContext.current
    val fileName = remember(uri) {
        context.contentResolver.getFileName(uri)
            ?: "Unknown video"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.weight(0.62f).fillMaxWidth(),
                contentAlignment = Alignment.TopCenter,
            ) {

                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    painter = painterResource(R.drawable.placeholder1),
                    contentDescription = null,
                    alignment = Alignment.TopCenter
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.82f),
                                    Color.Black.copy(alpha = 0.40f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ChromiaIcon(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = fileName,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        fontFamily = InterFontFamily,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    CustomButton(
                        onClick = {/* TODO Добавить */ }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.star_ic),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "Auto Grade",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            fontFamily = InterFontFamily,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    CustomButton(
                        onClick = {/* TODO Добавить */ },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                    ) {
                        Text(
                            text = "Export",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            fontFamily = InterFontFamily,
                            fontSize = 14.sp
                        )
                    }

                }


            }

            EditingSelector(modifier = Modifier.weight(0.38f))

        }
    }
}



// TODO вынести в другой файл и доработать
@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    border: BorderStroke = BorderStroke(0.dp, Color.Transparent),
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .border(border),
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


// TODO по хорошему надо перенести в viewModel, так как работаем с системой
fun ContentResolver.getFileName(uri: Uri): String? {
    val projection = arrayOf(
        OpenableColumns.DISPLAY_NAME
    )

    query(
        uri,
        projection,
        null,
        null,
        null
    )?.use { cursor ->
        val nameColumnIndex = cursor.getColumnIndex(
            OpenableColumns.DISPLAY_NAME
        )

        if (nameColumnIndex >= 0 && cursor.moveToFirst()) {
            return cursor.getString(nameColumnIndex)
        }
    }

    return uri.lastPathSegment
}


@Preview(device = "spec:width=411dp,height=891dp,orientation=landscape")
@Composable
private fun EditorScreenPreview() {
    MaterialTheme {
        ChromiaTheme() {
            EditorScreen(uri = Uri.EMPTY)
        }
    }
}
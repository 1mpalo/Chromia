package com.impalo.features.importer

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.impalo.core.ui.theme.InterFontFamily
import com.impalo.core.ui.theme.Neutral850
import com.impalo.features.projects.SavedProject
import com.impalo.features.projects.loadProjectThumbnail

@Composable
fun RecentProjectCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    project: SavedProject,
) {

    val context = LocalContext.current

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Neutral850,
        border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.surfaceBright),
    ) {
        val thumbnail by produceState<Bitmap?>(
            initialValue = null,
            key1 = project.id,
            key2 = project.videoUri
        ) {
            value = null
            value = loadProjectThumbnail(context, project)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 96.dp, height = 54.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                val bitmap = thumbnail

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop,
                        contentDescription = null
                    )
                } else {
                    Text("Video")
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.displayName,
                    fontFamily = InterFontFamily,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = project.updatedAtMs.toString(),
                    fontFamily = InterFontFamily,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
            }
        }
    }
}
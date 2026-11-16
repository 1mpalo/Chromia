package com.impalo.features.importer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.impalo.core.ui.theme.InterDisplayFontFamily
import com.impalo.core.ui.theme.InterFontFamily
import com.impalo.core.ui.theme.Neutral900
import com.impalo.core.ui.common.ChromiaIcon
import com.impalo.core.ui.theme.Neutral850
import com.impalo.features.R
import com.impalo.features.projects.SavedProject

@Composable
fun ImportScreen(
    recentProjects: List<SavedProject>,
    onOpenFootageClicked: () -> Unit,
    onRecentProjectClicked: (SavedProject) -> Unit
) {
     Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,

        ) {
            Row(
                modifier = Modifier.padding(end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ChromiaIcon(modifier = Modifier.size(28.dp))

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "CHROMIA",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = InterDisplayFontFamily,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                onClick = onOpenFootageClicked,
                shape = RoundedCornerShape(16.dp),
                color = Neutral850,
                border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.surfaceBright),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 48.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add_ic),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Open Footage",
                        fontFamily = InterFontFamily,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(2.5.dp))

                    Text(
                        text = "MP4 · MOV · LOG · R3D · BRAW",
                        color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            //TODO(add recent projects)
            Text(
                text = "Recent projects",
                fontFamily = InterFontFamily,
                color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(0.8f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recentProjects, key = { it.id }) { project ->
                    RecentProjectCard(
                        modifier = Modifier.width(280.dp),
                        onClick = { onRecentProjectClicked(project) },
                        project = project
                    )
                }
            }
        }
    }
}
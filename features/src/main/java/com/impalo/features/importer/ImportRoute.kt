package com.impalo.features.importer

import android.net.Uri
import androidx.activity.compose.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.compose.runtime.Composable
import com.impalo.features.projects.SavedProject

@Composable
fun ImportRoute(
    recentProjects: List<SavedProject>,
    onVideoSelected: (Uri) -> Unit,
    onRecentProjectClicked: (SavedProject) -> Unit
) {
    val videoPicker = rememberLauncherForActivityResult(
        contract = PickVisualMedia()
    ) { uri ->
        if(uri != null) {
            onVideoSelected(uri)
        }
    }

    ImportScreen(
        recentProjects = recentProjects,
        onOpenFootageClicked = {
            videoPicker.launch(
                PickVisualMediaRequest(
                    mediaType = PickVisualMedia.VideoOnly
                )
            )
        },
        onRecentProjectClicked = onRecentProjectClicked
    )
}
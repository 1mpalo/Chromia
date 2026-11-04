package com.impalo.features.importer

import android.net.Uri
import androidx.activity.compose.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.compose.runtime.Composable

@Composable
fun ImportRoute(
    onVideoSelected: (Uri) -> Unit
) {
    val videoPicker = rememberLauncherForActivityResult(
        contract = PickVisualMedia()
    ) { uri ->
        if(uri != null) {
            onVideoSelected(uri)
        }
    }

    ImportScreen(
        onOpenFootageClicked = {
            videoPicker.launch(
                PickVisualMediaRequest(
                    mediaType = PickVisualMedia.VideoOnly
                )
            )
        }
    )
}
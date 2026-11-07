package com.impalo.media

import androidx.annotation.OptIn
import androidx.media3.common.ColorInfo
import androidx.media3.common.util.UnstableApi

@OptIn(UnstableApi::class)
data class VideoMetadata(
    val containerMimeType: String?,
    val sampleMimeType: String?,
    val width: Int?,
    val height: Int?,
    val frameRate: Float?,
    val colorInfo: ColorInfo?,
    val textTags: Map<String, String>
)
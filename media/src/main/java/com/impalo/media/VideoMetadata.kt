package com.impalo.media

import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.ColorInfo
import androidx.media3.common.Format
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
) {
    fun supportsSamsungLogV1(): Boolean {
        val logTag = textTags["com.samsung.android.logvideo"] ?: return false

        val isVersion1 = logTag.split(',').any { field ->
            field.substringBefore('=').trim() == "version" &&
                    field.substringAfter('=').trim() == "V1.0"
        }

        return isVersion1 && colorInfo?.colorSpace == C.COLOR_SPACE_BT2020
    }
}
package com.impalo.features.editor.playback

import androidx.annotation.OptIn
import androidx.media3.common.Format
import androidx.media3.common.util.UnstableApi
import androidx.media3.container.MdtaMetadataEntry
import com.impalo.media.VideoMetadata

@OptIn(UnstableApi::class)
fun Format.toVideoMetadata() : VideoMetadata {
    val textTags = buildMap<String, String> {
        val sourceMetadata = metadata

        if(sourceMetadata != null) {
            for(index in 0 until sourceMetadata.length()) {
                val entry = sourceMetadata[index]

                if(
                    entry is MdtaMetadataEntry &&
                    entry.typeIndicator == MdtaMetadataEntry.TYPE_INDICATOR_STRING
                ) {
                    put (
                        entry.key,
                        entry.value.toString(Charsets.UTF_8)
                    )
                }
            }
        }
    }

    return VideoMetadata(
        containerMimeType = containerMimeType,
        sampleMimeType = sampleMimeType,
        width = width.takeIf { it > 0 },
        height = height.takeIf { it > 0 },
        frameRate =frameRate.takeIf {
            it.isFinite() && it > 0f
        },
        colorInfo = colorInfo,
        textTags = textTags
    )
}
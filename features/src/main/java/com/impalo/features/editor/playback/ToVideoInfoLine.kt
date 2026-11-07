package com.impalo.features.editor.playback

import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import com.impalo.media.VideoMetadata
import java.util.Locale
import kotlin.math.abs

@OptIn(UnstableApi::class)
fun VideoMetadata.toVideoInfoLine(
    extension: String?
): String {

    val samsungLogTag = textTags[
        "com.samsung.android.logvideo"
    ]

    val dynamicRange = when {

        samsungLogTag != null -> "Samsung Log"

        else -> when (colorInfo?.colorTransfer) {
            C.COLOR_TRANSFER_SDR,
            C.COLOR_TRANSFER_SRGB,
            C.COLOR_TRANSFER_GAMMA_2_2 -> "SDR"

            C.COLOR_TRANSFER_HLG -> "HDR (HLG)"
            C.COLOR_TRANSFER_ST2084 -> "HDR (PQ)"

            else -> null
        }
    }

    Log.d("infoExt", "$dynamicRange")

    val container = extension
        ?.takeIf { it.isNotBlank() }
        ?.uppercase(Locale.ROOT)

    Log.d("infoExt", "$container")

    val codec = when (sampleMimeType) {
        MimeTypes.VIDEO_H264 -> "H.264"
        MimeTypes.VIDEO_H265 -> "H.265"
        MimeTypes.VIDEO_AV1 -> "AV1"
        MimeTypes.VIDEO_VP9 -> "VP9"

        else -> sampleMimeType
    }

    val resolution = if(width != null && height != null) {
        "${width}×${height}"
    } else null

    val baseFpsList = listOf(24 ,30, 60, 90, 120, 144, 169, 240)
    val fps = frameRate?.let { value ->
        val closestFps = baseFpsList.minByOrNull { abs(it - value) } ?: value.toInt()
        "$closestFps fps"
    }


    val lumaDepth = colorInfo?.lumaBitdepth
    val chromaDepth = colorInfo?.chromaBitdepth

    val bitDepth = when {
        lumaDepth == null || chromaDepth == null -> null
        lumaDepth <= 0 || chromaDepth <= 0 -> null
        lumaDepth == chromaDepth -> "$lumaDepth bit"
        else -> "Y $lumaDepth bit / C $chromaDepth bit"
    }

    return listOfNotNull(
        dynamicRange,
        container,
        codec,
        resolution,
        fps,
        bitDepth
    ).joinToString(" · ")
}
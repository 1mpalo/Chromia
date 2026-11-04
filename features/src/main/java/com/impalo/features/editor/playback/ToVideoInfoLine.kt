package com.impalo.features.editor.playback

import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import java.util.Locale

@OptIn(UnstableApi::class)
fun Format.toVideoInfoLine(
    extension: String?
): String {

    val dynamicRange = when(colorInfo?.colorTransfer) {
        C.COLOR_TRANSFER_SDR,
        C.COLOR_TRANSFER_SRGB,
        C.COLOR_TRANSFER_GAMMA_2_2 -> "SDR"

        C.COLOR_TRANSFER_HLG -> "HDR (HLG)"
        C.COLOR_TRANSFER_ST2084 -> "HDR (PQ)"

        else -> null
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

    Log.d("infoExt", "$codec")

    val resolution = if(width > 0 && height > 0) {
        "${width}×${height}"
    } else null

    Log.d("infoExt", "$resolution")

    val fps = frameRate
        .takeIf { it > 0f }
        ?.let { value ->
            String.format(Locale.ROOT, "%.2f fps", value)
        }

    Log.d("infoExt", "$fps")

    val lumaDepth = colorInfo?.lumaBitdepth
    val chromaDepth = colorInfo?.chromaBitdepth

    val bitDepth = when {
        lumaDepth == null || chromaDepth == null -> null
        lumaDepth <= 0 || chromaDepth <= 0 -> null
        lumaDepth == chromaDepth -> "$lumaDepth bit"
        else -> "Y $lumaDepth bit / C $chromaDepth bit"
    }

    return listOf(
        dynamicRange,
        container,
        codec,
        resolution,
        fps,
        bitDepth
    ).joinToString(" · ")
}
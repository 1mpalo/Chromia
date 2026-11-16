package com.impalo.features.projects

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

suspend fun loadProjectThumbnail(
    context: Context,
    project: SavedProject
): Bitmap? = withContext(Dispatchers.IO) {
    val directory = File(context.cacheDir, "project_thumbnails")
    val cachedFile = File(directory, "${project.id}.jpg")

    if (cachedFile.isFile) {
        BitmapFactory.decodeFile(cachedFile.path)?.let { bitmap ->
            return@withContext bitmap
        }
    }

    val bitmap = try {
        MediaMetadataRetriever().use { retriever ->
            retriever.setDataSource(context, Uri.parse(project.videoUri))
            retriever.getScaledFrameAtTime(
                0L,
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC,
                320,
                180
            )
        }
    } catch (_: RuntimeException) {
        null
    } catch (_: IOException) {
        null
    } ?: return@withContext null

    try {
        directory.mkdirs()
        val tempFile = File.createTempFile("thumbnail-", ".jpg", directory)
        try {
            val encoded = tempFile.outputStream().use { output ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, output)
            }
            if (encoded) {
                tempFile.renameTo(cachedFile)
            }
        } finally {
            tempFile.delete()
        }
    } catch (_: IOException) {
        // The decoded frame is still available for this card.
    }

    bitmap
}
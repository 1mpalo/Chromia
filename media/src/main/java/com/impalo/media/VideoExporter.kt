package com.impalo.media

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.Effects
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.ProgressHolder
import androidx.media3.transformer.Transformer
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class VideoExporter(context: Context) {

    private val appContext = context.applicationContext

    suspend fun export(
        uri: Uri,
        grading: GradingSettings,
        samsungLog: Boolean,
        onProgress: (Int?) -> Unit = {},
        onSaving: () -> Unit
    ): Uri = withContext(Dispatchers.Main.immediate) {

        require(uri != Uri.EMPTY) {
            "A video is not selected"
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            error("This method is only available on Android 10+")
        }

        val outputFile = File(
            appContext.cacheDir,
            "chromia_${UUID.randomUUID()}.mp4"
        )

        try {
            renderToFile(
                uri = uri,
                grading = grading,
                samsungLog = samsungLog,
                outputFile = outputFile,
                onProgress = onProgress
            )

            currentCoroutineContext().ensureActive()

            onSaving()

            withContext(NonCancellable + Dispatchers.IO) {
                saveToGallery(outputFile)
            }
        } finally {
            withContext(NonCancellable) {
                withContext(Dispatchers.IO) {
                    outputFile.delete()
                }
            }
        }
    }

    @OptIn(UnstableApi::class)
    private suspend fun renderToFile(
        uri: Uri,
        grading: GradingSettings,
        samsungLog: Boolean,
        outputFile: File,
        onProgress: (Int?) -> Unit
    ) = coroutineScope {

        Log.d(
            "ChromiaExport",
            "START file=${outputFile.name}, input=$uri, grading=$grading"
        )

        val exportSettingsHolder = GradingSettingsHolder().apply {
            update(grading)
        }

        val editedMediaItem = EditedMediaItem.Builder(
            MediaItem.fromUri(uri)
        ).setEffects(
            Effects(
                emptyList(),
                listOf(
                    IdentityGlEffect(exportSettingsHolder, samsungLog)
                )
            )
        ).build()

        val completion = CompletableDeferred<Unit>()

        val transformer = Transformer.Builder(appContext)
            .setVideoMimeType(MimeTypes.VIDEO_H264)
            .setAudioMimeType(MimeTypes.AUDIO_AAC)
            .addListener(
                object : Transformer.Listener {
                    override fun onCompleted(
                        composition: Composition,
                        exportResult: ExportResult
                    ) {
                        completion.complete(Unit)

                        Log.d(
                            "ChromiaExport",
                            "DONE file=${outputFile.name}, " +
                                    "encoder=${exportResult.videoEncoderName}, " +
                                    "size=${exportResult.width}x${exportResult.height}, " +
                                    "bitrate=${exportResult.averageVideoBitrate}, " +
                                    "conversion=${exportResult.videoConversionProcess}"
                        )
                    }

                    override fun onError(
                        composition: Composition,
                        exportResult: ExportResult,
                        exportException: ExportException
                    ) {
                        completion.completeExceptionally(exportException)
                    }
                }
            ).build()

        val progressJob = launch {
            val progressHolder = ProgressHolder()

            while (isActive) {
                val progressState = transformer.getProgress(
                    progressHolder
                )

                val progress = if (
                    progressState ==
                    Transformer.PROGRESS_STATE_AVAILABLE
                ) {
                    progressHolder.progress
                } else {
                    null
                }

                onProgress(progress)

                delay(300)
            }
        }

        try {
            transformer.start(
                editedMediaItem,
                outputFile.absolutePath
            )

            completion.await()
        } finally {
            progressJob.cancel()

            transformer.cancel()
        }
    }

    private fun saveToGallery(sourceFile: File): Uri {
        val resolver = appContext.contentResolver

        val values = ContentValues().apply {
            put(
                MediaStore.Video.Media.DISPLAY_NAME,
                sourceFile.name
            )
            put(
                MediaStore.Video.Media.MIME_TYPE,
                "video/mp4"
            )
            put(
                MediaStore.Video.Media.RELATIVE_PATH,
                "${Environment.DIRECTORY_MOVIES}/Chromia"
            )
            put(
                MediaStore.Video.Media.IS_PENDING,
                1
            )
        }

        val collection = MediaStore.Video.Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL_PRIMARY
        )

        val outputUri = resolver.insert(collection, values)
            ?: error("Failed to save the video to the gallery")

        try {
            val outputStream = resolver.openOutputStream(
                outputUri,
                "w"
            ) ?: error("Failed to open the video for write")

            outputStream.use { output ->
                sourceFile.inputStream().use { input ->
                    input.copyTo(output)
                }
            }

            val completedValues = ContentValues().apply {
                put(MediaStore.Video.Media.IS_PENDING, 0)
            }

            check(
                resolver.update(
                    outputUri,
                    completedValues,
                    null,
                    null,
                ) > 0
            ) {
                "Failed to finish saving the video"
            }

            return outputUri
        } catch (exception: Exception) {
            runCatching {
                resolver.delete(outputUri, null, null)
            }

            throw exception
        }
    }

}
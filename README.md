# Chromia

An Android app for manual video color grading. Import a video, adjust its colors with a live preview, and export the result to your gallery.

[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack_Compose-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/compose)
[![Media3](https://img.shields.io/badge/Video-Media3-3DDC84?logo=android&logoColor=white)](https://developer.android.com/media/media3)
![Status](https://img.shields.io/badge/Status-In_development-orange)

I'm building Chromia to learn video processing on Android: color math, OpenGL shaders, playback, and export.

## Features

- Import a video from the system picker.
- Preview adjustments during playback.
- Adjust exposure, contrast, highlights, shadows, whites, and blacks.
- Change temperature, tint, saturation, and vibrance.
- Export the graded video to the device gallery.

## Current limitations

The current color-processing pipeline targets **SDR Rec.709 video**. HDR and camera LOG profiles are not supported yet. Support for individual codecs and containers also depends on the device; not every video format has been tested.

Editing sessions are not saved. Leaving the editor with the Back button discards the adjustments, and sessions cannot be restored after closing the app. Export the result before leaving the editor.

Intermittent artifacts have been observed in exported video. Recent exports have been clean, but the cause has not been identified.

## Tech stack

- **Kotlin**
- **Jetpack Compose / Material 3** — interface
- **Media3 ExoPlayer** — video playback
- **Media3 Transformer** — video export
- **OpenGL ES / GLSL** — color adjustments
- **ViewModel, Coroutines, and Flow** — UI state and export coordination
- **MediaStore** — saving exported videos to the gallery
- **Gradle Kotlin DSL** — build configuration

## Video processing

Color adjustments run in a custom GL effect during playback. Export uses a separate snapshot of the grading settings, so changes in the editor do not affect an export already in progress.

Media3 Transformer writes the processed video to a temporary file. Once rendering finishes, the file is copied into MediaStore and becomes available in the gallery.

## Build and run

Clone the repository:

```bash
git clone https://github.com/1mpalo/Chromia.git
cd Chromia
```

Open the project in Android Studio, let Gradle sync, then run the `app` configuration on a compatible Android device. Use an SDR Rec.709 clip to try the current grading pipeline.

## Roadmap

- [ ] Save editing projects and restore previous sessions
- [ ] Tone curves
- [ ] LUT import and application
- [ ] Support for camera LOG profiles
- [ ] Broader format testing across devices

## Bug reports

If playback or export fails, open an [issue](https://github.com/1mpalo/Chromia/issues) with your device model, Android version, video format, and the adjustments you used. A short sample clip helps reproduce video-related problems.

package com.impalo.features.editor.playback

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.media3.common.Player
import androidx.media3.ui.compose.ContentFrame


//TODO: Когда видео доходит до конца и при выкл/вкл экрана не показывает последний кадр, а тупо черный экран
@Composable
fun ChromiaVideoPlayer(
    player: Player,
    modifier: Modifier = Modifier
) {
    ContentFrame(
        player = player,
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.Fit,
        keepContentOnReset = true
    )
}
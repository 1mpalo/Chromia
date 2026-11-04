package com.impalo.features.editor.controls

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.impalo.core.ui.theme.InterFontFamily
import com.impalo.core.ui.theme.Neutral850
import com.impalo.features.editor.model.ExportState

@Composable
fun ExportOverlay(
    exportState: ExportState
) {

    var progress by remember { mutableIntStateOf(0) }

    LaunchedEffect(key1 = exportState) {
        if (exportState is ExportState.Exporting) {
            progress = exportState.progress ?: 0
        }
    }

    val animatedProgress by animateIntAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.85f))
            .clickable {},
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(
            visible = exportState is ExportState.Exporting,
            enter = scaleIn(animationSpec = tween()) + fadeIn(animationSpec = tween()),
            exit = scaleOut(animationSpec = tween()) + fadeOut(animationSpec = tween())
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .fillMaxHeight(0.25f),
                color = Neutral850,
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 22.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Exporting",
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = InterFontFamily,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LinearProgressIndicator(
                            progress = { progress.toFloat() / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .weight(0.85f),
                            color = Color.White,
                            trackColor = Color.Gray,
                            strokeCap = StrokeCap.Round,
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "${animatedProgress}%",
                            fontFamily = InterFontFamily,
                            color = Color.White,
                            fontSize = 18.sp,
                            modifier = Modifier.weight(0.15f)
                        )
                    }
                }
            }
        }
    }
}

package com.impalo.chromia.navigation

import android.app.AlertDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.impalo.core.ui.theme.InterFontFamily
import com.impalo.features.editor.model.ExportState

@Composable
fun AlertDialog(
    show: Boolean,
    title: String,
    text: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.85f))
            .clickable {},
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(
            visible = show,
            enter = scaleIn(animationSpec = tween()) + fadeIn(animationSpec = tween()),
            exit = scaleOut(animationSpec = tween()) + fadeOut(animationSpec = tween())
        ) {
            AlertDialog(
                onDismissRequest = onDismiss,
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                title = {
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontFamily = InterFontFamily
                    )
                },
                text = {
                    Text(
                        text = text,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onConfirm()
                            onDismiss()
                        }
                    ) {
                        Text(
                            text = "Confirm",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    }
}
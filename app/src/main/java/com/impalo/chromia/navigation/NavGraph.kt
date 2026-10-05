package com.impalo.chromia.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.impalo.features.editor.EditorScreen
import com.impalo.features.importer.ImportRoute
import com.impalo.features.editor.EditorViewModel

@Composable
fun NavGraph() {

    val backStack = remember { mutableStateListOf<Screen>(Screen.ImportScreen) }

    NavDisplay(
        backStack = backStack,
        onBack = {
            if(backStack.size > 1) {
                backStack.removeLastOrNull()
            }
        },
        entryProvider = entryProvider {
            entry<Screen.ImportScreen> {
                ImportRoute { selectedUri ->
                    Log.d("Chrr", "${selectedUri}")
                    backStack.add(Screen.EditorScreen(selectedUri.toString()))
                }
            }

            entry<Screen.EditorScreen> { screen ->
                val viewModel: EditorViewModel = viewModel()
                val settings by viewModel.settings.collectAsStateWithLifecycle()

                val context = LocalContext.current
                val fileName = with(viewModel) {context.contentResolver.getFileName(screen.uri.toUri()) ?: "Unknown video"}

                EditorScreen(
                    uri = screen.uri.toUri(),
                    fileName = fileName,
                    onEvent = viewModel::onEvent,
                    settings = settings
                )
            }

        }
    )
}
package com.impalo.chromia.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.core.net.toUri
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.impalo.features.editor.EditorScreen
import com.impalo.features.importer.ImportRoute

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
                EditorScreen(screen.uri.toUri())
            }

        }
    )
}
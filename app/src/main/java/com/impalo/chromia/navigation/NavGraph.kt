package com.impalo.chromia.navigation

import android.content.Intent
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.impalo.features.editor.EditorScreen
import com.impalo.features.importer.ImportRoute
import com.impalo.features.editor.EditorViewModel
import com.impalo.features.editor.model.ExportState
import com.impalo.features.projects.DatastoreProjectsRepositoryImpl
import com.impalo.features.projects.SavedProject
import com.impalo.media.GradingSettings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun NavGraph() {

    val context = LocalContext.current
    val backStack = remember { mutableStateListOf<Screen>(Screen.ImportScreen) }

    val projectsRepository = DatastoreProjectsRepositoryImpl(context)

    val scope = rememberCoroutineScope()

    var showExitDialog by remember { mutableStateOf(false) }

    AlertDialog(
        show = showExitDialog,
        title = "To import screen.",
        text = "Are you sure you want to go back to import screen? Your progress will be saved",
        onDismiss = { showExitDialog = false },
        onConfirm = { backStack.removeLastOrNull() }
    )

    NavDisplay(
        backStack = backStack,
        onBack = {
            showExitDialog = true
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Screen.ImportScreen> {

                val recentProjects by projectsRepository.savedProjects
                    .collectAsStateWithLifecycle(initialValue = emptyList())

                ImportRoute (
                    recentProjects = recentProjects,
                    onVideoSelected = { uri ->

                        context.contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )

                        scope.launch {
                            val project = SavedProject(
                                id = UUID.randomUUID().toString(),
                                videoUri = uri.toString(),
                                displayName = uri.fragment ?: "no name",
                                updatedAtMs = System.currentTimeMillis(),
                                grading = GradingSettings()
                            )

                            projectsRepository.saveProject(project)
                            backStack.add(Screen.EditorScreen(projectId = project.id))
                        }
                    },
                    onRecentProjectClicked = { project ->
                        backStack.add(Screen.EditorScreen(projectId = project.id))
                    }
                )
            }

            entry<Screen.EditorScreen> { screen ->
                val viewModel: EditorViewModel = viewModel<EditorViewModel> {
                    EditorViewModel(projectsRepository = projectsRepository, projectId = screen.projectId)
                }

                val project by viewModel.project.collectAsStateWithLifecycle()
                val settings by viewModel.settings.collectAsStateWithLifecycle()

                val exportState by viewModel.exportState.collectAsStateWithLifecycle()
                val videoMetadata by viewModel.videMetadata.collectAsStateWithLifecycle()

                project?.videoUri?.let {
                    EditorScreen(
                        uri = it.toUri(), // TODO не делай так
                        onEvent = viewModel::onEvent,
                        settings = settings,
                        exportState = exportState,
                        videoMetadata = videoMetadata,
                        effectFlow = viewModel.effectFlow
                    )
                }
            }

        }
    )
}
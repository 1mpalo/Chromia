package com.impalo.chromia.navigation

import android.net.Uri

sealed interface Screen {
    data object ImportScreen : Screen
    data class EditorScreen(val uri: String) : Screen
}
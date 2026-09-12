package com.impalo.chromia

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.impalo.chromia.navigation.NavGraph
import com.impalo.core.ui.theme.ChromiaTheme
import com.impalo.features.importer.ImportRoute

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChromiaTheme {
                NavGraph()
            }
        }
    }
}
package com.example.hotelapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.example.hotelapp.navigation.SetupNavGraph
import com.example.hotelapp.ui.theme.AppTheme


/**
 * Единственная Activity приложения. Включает edge-to-edge, применяет тему и
 * запускает корневой навигационный граф Compose.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                // Корневой Surface задаёт всем экранам фон из темы и выставляет
                // LocalContentColor в onSurface, чтобы текст без явного цвета
                // (например, заголовки авторизации) оставался читаемым в тёмной теме.
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    SetupNavGraph(navController)
                }
            }
        }

    }
}





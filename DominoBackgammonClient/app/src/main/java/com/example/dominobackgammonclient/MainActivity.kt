package com.example.dominobackgammonclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.example.dominobackgammonclient.ui.BGScreenLandscape
import com.example.dominobackgammonclient.ui.BGScreenPortrait
import com.example.dominobackgammonclient.ui.theme.DominoBackgammonClientTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            DominoBackgammonClientTheme {
                when(windowSizeClass.widthSizeClass) {
                    WindowWidthSizeClass.Compact -> {
                        BGScreenPortrait()
                    }
                    WindowWidthSizeClass.Expanded -> {
                        BGScreenLandscape()
                    }
                }
            }
        }
    }
}
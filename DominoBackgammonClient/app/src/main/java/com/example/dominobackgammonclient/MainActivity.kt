package com.example.dominobackgammonclient

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.dominobackgammonclient.ui.BGScreenLandscape
import com.example.dominobackgammonclient.ui.BGScreenPortrait
import com.example.dominobackgammonclient.ui.theme.DominoBackgammonClientTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            DominoBackgammonClientTheme {
                when(windowSizeClass.widthSizeClass) {
                    WindowWidthSizeClass.Compact -> {
                        BGScreenPortrait()
                    }
                    else -> {
                        BGScreenLandscape()
                    }
                }
            }
        }

        hideSystemUI()
    }


    private fun hideSystemUI() {
        // hide navigation bar & show on swipe
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.navigationBars())

        // extend into cutout insets
        if (VERSION.SDK_INT >= VERSION_CODES.R)
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
    }
}
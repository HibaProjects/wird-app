package com.hiba.wird

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hiba.wird.navigation.AppNavigation
import com.hiba.wird.ui.theme.WirdTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            WirdTheme {

                AppNavigation()
            }
        }
    }
}


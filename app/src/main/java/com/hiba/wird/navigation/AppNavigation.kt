package com.hiba.wird.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.hiba.wird.ui.components.BottomNavBar
import com.hiba.wird.ui.screens.*

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    Scaffold(

        bottomBar = {
            BottomNavBar(navController)
        }

    ) { padding ->

        NavHost(
            navController = navController,

            startDestination = Screen.Home.route,

            modifier = Modifier.padding(padding)
        ) {

            composable(Screen.Home.route) {
                HomeScreen()
            }

            composable(Screen.Adkar.route) {
                AdkarScreen()
            }

            composable(Screen.Prayers.route) {
                PrayersScreen()
            }

            composable(Screen.Sadaqa.route) {
                SadaqaScreen()
            }

        }
    }
}


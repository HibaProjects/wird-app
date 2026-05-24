package com.hiba.wird.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hiba.wird.navigation.Screen
import com.hiba.wird.ui.theme.*

@Composable
fun BottomNavBar(
    navController: NavController
) {

    val items = listOf(
        Screen.Home,
        Screen.Adkar,
        Screen.Prayers,
        Screen.Sadaqa
    )

    NavigationBar(
        containerColor = Night2
    ) {

        val currentRoute =
            navController.currentBackStackEntryAsState()
                .value?.destination?.route

        items.forEach { screen ->

            NavigationBarItem(

                selected = currentRoute == screen.route,

                onClick = {
                    navController.navigate(screen.route)
                },

                icon = {

                    when(screen) {

                        Screen.Home -> {
                            Icon(Icons.Default.Home, null)
                        }

                        Screen.Adkar -> {
                            Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null)
                        }

                        Screen.Prayers -> {
                            Icon(Icons.Default.AccessTime, null)
                        }

                        Screen.Sadaqa -> {
                            Icon(Icons.Default.DateRange, null)
                        }

                        


                    }
                },

                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Gold,
                    unselectedIconColor = TextMuted,
                    indicatorColor = Night3
                )
            )
        }
    }
}
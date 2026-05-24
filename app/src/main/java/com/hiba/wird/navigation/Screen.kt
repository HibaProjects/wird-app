package com.hiba.wird.navigation

sealed class Screen(
    val route: String
) {

    object Home : Screen("home")

    object Adkar : Screen("adkar")

    object Prayers : Screen("prayers")

    object Sadaqa : Screen("sadaqa")

}
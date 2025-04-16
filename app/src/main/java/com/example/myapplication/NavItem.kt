package com.example.myapplication

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Home : NavItem("home", "Home", Icons.Default.Home)
    object Run : NavItem("run", "run", Icons.Default.DirectionsRun)
    object Profile : NavItem("profile", "Profile", Icons.Default.Person)

    companion object {
        val items = listOf(Home, Run, Profile)
    }
}
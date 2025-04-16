package com.example.myapplication

import androidx.compose.material.icons.Icons
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
    object Other : NavItem("other", "Other", Icons.Default.List)
    object Profile : NavItem("profile", "Profile", Icons.Default.Person)
    object Login : NavItem("login", "Login", Icons.Default.Lock)

    companion object {
        val items = listOf(Home, Other, Profile, Login)
    }
}
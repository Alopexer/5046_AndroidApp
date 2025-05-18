package com.example.myapplication

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.RunningPlanViewModel
import com.example.myapplication.viewmodel.UserViewModel


@Composable
fun MainScreen(
    userViewModel: UserViewModel,
    runningPlanViewModel: RunningPlanViewModel,
    onGoogleLoginClick: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentUser = userViewModel.currentUser
    if (currentUser == null) {
        LoginScreen(
            navController = navController,
            goLogin = true,
            onLoginSuccess = { },
            userViewModel = userViewModel,
            runningPlanViewModel = runningPlanViewModel,
            onGoogleLoginClick = onGoogleLoginClick

        )

        return
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavItem.items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute?.startsWith(item.route) == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavItem.Home.route) {
                HomeScreen()
            }

            composable(NavItem.Run.route) {
                RunningScreen(navController)
            }

            composable(NavItem.Profile.route) {
                ProfileScreen(navController, userViewModel, runningPlanViewModel)
            }

            composable("run/run-plan") {
                RunningPlanCreateScreen(navController, userViewModel, runningPlanViewModel)
            }
        }
    }
}

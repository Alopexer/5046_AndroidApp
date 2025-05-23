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
import com.example.myapplication.screens.NewsDetailScreen
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
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = false
                                }
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
                HomeScreen(
                    navController = navController,
                    runningPlanViewModel = runningPlanViewModel,
                    userEmail = userViewModel.currentUser?.email ?: "",
                    userName = userViewModel.currentUser?.username ?: ""
                )

            }


            composable(NavItem.Run.route) {
                RunningScreen(navController, userViewModel, runningPlanViewModel)
            }
            composable(NavItem.Profile.route) {
                ProfileScreen(navController, userViewModel, runningPlanViewModel)
            }
            composable("run/run-plan") {
                RunningPlanCreateScreen(navController, userViewModel, runningPlanViewModel)
            }
            composable("run/map") {
                MapScreen(
                    userViewModel = userViewModel,
                    runningPlanViewModel = runningPlanViewModel,
                    navController = navController
                )
            }


            composable(
                "newsDetail/{newsId}"
            ) { backStackEntry ->
                val newsId = backStackEntry.arguments?.getString("newsId")
                NewsDetailScreen(newsId, navController)
            }


        }
    }
}
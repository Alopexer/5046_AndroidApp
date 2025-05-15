package com.example.myapplication

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.myapplication.data.RunningPlanViewModel
import com.example.myapplication.screens.*
import androidx.compose.runtime.saveable.rememberSaveable


@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var userEmail by rememberSaveable { mutableStateOf("") }
    val runningPlanViewModel: RunningPlanViewModel = viewModel()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavItem.items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute?.startsWith(item.route) == true,
                        onClick = {
                            val destination = if (item.route == "profile") {
                                if (userEmail.isNotEmpty()) {
                                    "profile/$userEmail"
                                } else {
                                    "profile/login"
                                }
                            } else {
                                item.route
                            }

                            navController.navigate(destination) {
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
                HomeScreen(navController)
            }

            composable(NavItem.Run.route) {
                RunningScreen(navController)
            }

            composable("profile/{email}") { backStackEntry ->
                val email = backStackEntry.arguments?.getString("email") ?: ""
                userEmail = email
                ProfileScreen(navController, email = email, isLogin = true)
            }

            composable("profile/login") {
                LoginScreen(
                    navController = navController,
                    goLogin = true,
                    onLoginSuccess = { email ->
                        userEmail = email
                        navController.navigate("profile/$email") {
                            popUpTo("profile/login") { inclusive = true }
                        }
                    }
                )
            }

            composable("run/run-plan") {
                RunningPlanCreateScreen(navController, runningPlanViewModel)
            }

            composable("run/map") {
                OtherScreen()
            }

            // ✅ 添加 News Detail 路由
            composable("newsDetail/{newsId}") { backStackEntry ->
                val newsId = backStackEntry.arguments?.getString("newsId")
                NewsDetailScreen(newsId = newsId, navController = navController)
            }
        }
    }
}

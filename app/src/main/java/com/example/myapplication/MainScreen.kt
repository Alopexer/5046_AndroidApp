package com.example.myapplication

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.RunningPlanViewModel
import com.example.myapplication.viewmodel.UserViewModel

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var userEmail by rememberSaveable { mutableStateOf("") }
    val userViewModel: UserViewModel = viewModel ()
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
                HomeScreen()
            }

            composable(NavItem.Run.route) {
                RunningScreen(navController)
            }

            composable("profile/{email}") { backStackEntry ->
                val email = backStackEntry.arguments?.getString("email") ?: ""
                userEmail = email // Save for later use
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
                    },
                    userViewModel,
                    runningPlanViewModel
                )
            }

            composable("run/run-plan") {
                RunningPlanCreateScreen(navController, userViewModel, runningPlanViewModel)
            }

//            composable("run/map") {
//                OtherScreen()
//            }
        }
    }
}

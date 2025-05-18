package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.RunningPlanViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.UserViewModel
import androidx.navigation.compose.rememberNavController


//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            MyApplicationTheme {
//                Surface {
//                    val navController = rememberNavController()
//                    val userViewModel: UserViewModel = viewModel()
//                    val runningPlanViewModel: RunningPlanViewModel = viewModel()
//
//                    NavHost(navController = navController, startDestination = "login") {
//                        composable("login") {
//                            LoginScreen(
//                                navController = navController,
//                                goLogin = true,
//                                onLoginSuccess = { email ->
//                                    navController.navigate("home") {
//                                        popUpTo("login") { inclusive = true }
//                                    }
//                                },
//                                userViewModel = userViewModel,
//                                runningPlanViewModel = runningPlanViewModel
//                            )
//                        }
//                        composable("home") {
//                            if (userViewModel.isLoggedIn()) {
//                                HomeScreen()
//                            } else {
//                                navController.navigate("login")
//                            }
//                        }
//                        composable("run") {
//                            if (userViewModel.isLoggedIn()) {
//                                RunningScreen(navController)
//                            } else {
//                                navController.navigate("login")
//                            }
//                        }
//                        composable("profile") {
//                            ProfileScreen(
//                                navController = navController,
//                                email = userViewModel.currentUser?.email ?: "",
//                                isLogin = userViewModel.isLoggedIn()
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val userViewModel: UserViewModel = viewModel()
                val runningPlanViewModel: RunningPlanViewModel = viewModel()
                var isLoggedIn by remember { mutableStateOf(userViewModel.isLoggedIn()) }
                var userEmail by remember { mutableStateOf(userViewModel.currentUser?.email ?: "") }
//                Surface {
//                    val userViewModel: UserViewModel = viewModel()
//                    val runningPlanViewModel: RunningPlanViewModel = viewModel()
//                    val navController = rememberNavController()
//
//                    // 🔍 直接加载 Map 页面进行测试
//                    OtherScreen(
//                        userViewModel = userViewModel,
//                        runningPlanViewModel = runningPlanViewModel,
//                        navController = navController
//                    )
//                }
                Surface {
                    if (!isLoggedIn) {
                        LoginScreen(
                            navController = null,
                            goLogin = true,
                            onLoginSuccess = { email ->
                                userEmail = email
                                isLoggedIn = true
                            },
                            userViewModel = userViewModel,
                            runningPlanViewModel = runningPlanViewModel
                        )
                    } else {
                        MainScreen(userEmail = userEmail, userViewModel = userViewModel, runningPlanViewModel = runningPlanViewModel)
                    }
                }
            }
        }
    }
}
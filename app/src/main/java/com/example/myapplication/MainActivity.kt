package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.RunningPlanViewModel
import com.example.myapplication.data.UserEntity
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

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

//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            MyApplicationTheme {
//                val userViewModel: UserViewModel = viewModel()
//                val runningPlanViewModel: RunningPlanViewModel = viewModel()
//                var isLoggedIn by remember { mutableStateOf(userViewModel.isLoggedIn()) }
//                var userEmail by remember { mutableStateOf(userViewModel.currentUser?.email ?: "") }
//
//                Surface {
//                    if (!isLoggedIn) {
//                        LoginScreen(
//                            navController = null,
//                            goLogin = true,
//                            onLoginSuccess = { email ->
//                                userEmail = email
//                                isLoggedIn = true
//                            },
//                            userViewModel = userViewModel,
//                            runningPlanViewModel = runningPlanViewModel
//                        )
//                    } else {
//                        MainScreen(userEmail = userEmail, userViewModel = userViewModel, runningPlanViewModel = runningPlanViewModel)
//                    }
//                }
//            }
//        }
//    }
//}

//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            MyApplicationTheme {
//                val userViewModel: UserViewModel = viewModel()
//                val runningPlanViewModel: RunningPlanViewModel = viewModel()
//
//                // ✅ 只渲染 MainScreen，它负责判断是否显示 LoginScreen
//                MainScreen(
//                    userViewModel = userViewModel,
//                    runningPlanViewModel = runningPlanViewModel
//                )
//            }
//        }
//    }
//}

class MainActivity : ComponentActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var userViewModel: UserViewModel
    private lateinit var runningPlanViewModel: RunningPlanViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            MyApplicationTheme {
                userViewModel = viewModel()
                runningPlanViewModel = viewModel()

                val user by userViewModel::currentUser

                if (user == null) {
                    LoginScreen(
                        navController = null,
                        goLogin = true,
                        onLoginSuccess = { },
                        userViewModel = userViewModel,
                        runningPlanViewModel = runningPlanViewModel,
                        onGoogleLoginClick = {
                            launcher.launch(googleSignInClient.signInIntent)
                        }
                    )
                } else {
                    MainScreen(
                        userViewModel = userViewModel,
                        runningPlanViewModel = runningPlanViewModel,
                        onGoogleLoginClick = {
                            launcher.launch(googleSignInClient.signInIntent)
                        }
                    )
                }
            }
        }
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.let {
                val email = it.email ?: return@let
                val username = it.displayName ?: email
                val newUser = UserEntity(
                    username = username,
                    email = email,
                    phone = "N/A",
                    password = "GOOGLE_AUTH"
                )
                userViewModel.loginOrRegisterByGoogle(newUser) { success ->
                    Log.d("GOOGLE_LOGIN", "Google login success = $success")
                    // 页面会自动更新
                }
            }
        } catch (e: ApiException) {
            Log.e("GOOGLE_LOGIN", "Login failed: ${e.statusCode}")
        }
    }
}

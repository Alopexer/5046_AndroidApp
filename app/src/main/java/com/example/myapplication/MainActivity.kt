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
import androidx.navigation.compose.rememberNavController
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

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.let { acc ->
                val email = acc.email ?: return@let
                val username = acc.displayName ?: email
                val newUser = UserEntity(
                    username = username,
                    email = email,
                    phone = "N/A",
                    password = "GOOGLE_AUTH"
                )
                // ⚠️ 此处不能用外部 userViewModel，传给下面的 lambda
                signInCallback?.invoke(newUser)
            }
        } catch (e: ApiException) {
            Log.e("GOOGLE_LOGIN", "Login failed: ${e.statusCode}")
        }
    }

    private var signInCallback: ((UserEntity) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            MyApplicationTheme {
                val userViewModel: UserViewModel = viewModel()
                val runningPlanViewModel: RunningPlanViewModel = viewModel()

                // ✅ 观察 currentUser 是否为 null
                val currentUser = userViewModel.currentUser
                if (currentUser == null) {
                    // ⚠️ 将回调设定给外部变量
                    signInCallback = { userEntity ->
                        userViewModel.loginOrRegisterByGoogle(userEntity) { success ->
                            Log.d("GOOGLE_LOGIN", "User login complete: $success")
                            // Compose 会自动刷新 UI
                        }
                    }

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
}

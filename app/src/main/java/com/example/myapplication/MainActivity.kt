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

                val currentUser = userViewModel.currentUser
                if (currentUser == null) {
                    signInCallback = { userEntity ->
                        userViewModel.loginOrRegisterByGoogle(userEntity) { success ->
                            Log.d("GOOGLE_LOGIN", "User login complete: $success")
                        }
                    }

                    LoginScreen(
                        navController = null,
                        goLogin = true,
                        onLoginSuccess = { },
                        userViewModel = userViewModel,
                        runningPlanViewModel = runningPlanViewModel,
                        onGoogleLoginClick = {
                            googleSignInClient.signOut().addOnCompleteListener {
                                launcher.launch(googleSignInClient.signInIntent)
                            }
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

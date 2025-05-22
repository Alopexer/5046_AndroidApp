package com.example.myapplication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.RunningPlanViewModel
import com.example.myapplication.data.UserEntity
import com.example.myapplication.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun LoginScreen(
    navController: NavController?,
    goLogin: Boolean,
    onLoginSuccess: (String) -> Unit,
    userViewModel: UserViewModel,
    runningPlanViewModel: RunningPlanViewModel,
    onGoogleLoginClick: () -> Unit
) {
    LoginRegisterScreen(
        navController = navController,
        goLogin = goLogin,
        onLoginSuccess = onLoginSuccess,
        userViewModel = userViewModel,
        runningPlanViewModel = runningPlanViewModel,
        onGoogleLoginClick = onGoogleLoginClick
    )
}

@Composable
fun LoginRegisterScreen(
    navController: NavController?,
    goLogin: Boolean,
    onLoginSuccess: (String) -> Unit,
    userViewModel: UserViewModel,
    runningPlanViewModel: RunningPlanViewModel,
    onGoogleLoginClick: () -> Unit
) {
    var isLogin by remember { mutableStateOf(goLogin) }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var successMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 64.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column {
            Text(
                text = if (isLogin) "Welcome Back" else "Create Account",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (!isLogin) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone (e.g. 0412345678)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    shape = MaterialTheme.shapes.medium
                )
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                shape = MaterialTheme.shapes.medium
            )

            if (!isLogin) {
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    singleLine = true,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = MaterialTheme.shapes.medium
                )
            }

            if (errorText.isNotEmpty()) {
                Text(
                    text = errorText,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (successMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = successMessage, color = Color(0xFF4CAF50)) // 绿色
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    errorText = ""
                    scope.launch {
                        if (isLogin) {
                            userViewModel.login(email, password) { user ->
                                if (user != null) {
                                    onLoginSuccess(user.email)
                                } else {
                                    errorText = "Invalid email or password"
                                }
                            }
                        } else {
                            when {
                                username.isBlank() -> errorText = "Username is required"
                                !isValidPhone(phone) -> errorText = "Invalid phone number"
                                !isValidEmail(email) -> errorText = "Invalid email format"
                                !isStrongPassword(password) -> errorText = "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, and one number."
                                password != confirmPassword -> errorText = "Passwords do not match"
                                email.lowercase().endsWith("@gmail.com") -> errorText = "Please login with Google!"
                                else -> {
                                    val newUser = UserEntity(username = username, phone = phone, email = email, password = password)
                                    userViewModel.register(newUser) { success ->
                                        if (success) {
                                            isLogin = true
                                            successMessage = "Registered successfully. Please log in."
                                            scope.launch {
                                                delay(5000)
                                                successMessage = ""
                                            }
                                        } else {
                                            errorText = "Email already exists"
                                        }
                                    }



                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = if (isLogin) "Sign In" else "Sign Up")
            }

            Text(
                text = "     ",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(vertical = 6.dp)
            )

            Button(
                onClick = { onGoogleLoginClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Sign in with Google")
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (isLogin) "Don't have an account?" else "Already have one?")
            Spacer(modifier = Modifier.width(6.dp))
            TextButton(onClick = {
                isLogin = !isLogin
                password = ""
                confirmPassword = ""
                errorText = ""
            }) {
                Text(text = if (isLogin) "Sign Up" else "Sign In")
            }
        }
    }
}

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isValidPhone(phone: String): Boolean {
    return phone.matches(Regex("^0[0-9]{9}$"))
}

fun isStrongPassword(password: String): Boolean {
    val pattern = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
    return pattern.containsMatchIn(password)
}

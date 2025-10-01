package com.example.quickgram.ui.auth.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickgram.R
import com.example.quickgram.ui.auth.AuthViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "QuickGram",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                isLoading = true
                errorMessage = null

                coroutineScope.launch {
                    Log.d("LoginScreen", "Attempting login for email: $email")

                    val result = viewModel.login(email, password)
                    isLoading = false
                    result.fold(
                        onSuccess = {
                            Log.d("LoginScreen", "Login successful for $email")
                            navController.navigate("feed")
                        },
                        onFailure = { e ->
                            val msg = e.message ?: "Login failed"
                            Log.e("LoginScreen", "Login error: $msg", e)
                            errorMessage = msg
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Login")
            }
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { navController.navigate("signup") }
        ) {
            Text("Don't have an account? Sign up")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Google Login Button
        OutlinedButton(
            onClick = {
                coroutineScope.launch {
                    try {
                        Log.d("LoginScreen", "Attempting Google login")
                        val result = viewModel.loginWithGoogle()
                        result.fold(
                            onSuccess = {
                                Log.d("LoginScreen", "Google login successful")
                                navController.navigate("feed")
                            },
                            onFailure = { e ->
                                val msg = e.message ?: "Google login failed"
                                Log.e("LoginScreen", "Google login error: $msg", e)
                                errorMessage = msg
                            }
                        )
                    } catch (e: Exception) {
                        Log.e("LoginScreen", "Unexpected Google login error", e)
                        errorMessage = e.message
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_google), // 👈 your google logo drawable
                contentDescription = "Google Logo",
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = 8.dp)
            )
            Text("Continue with Google")
        }
    }
}

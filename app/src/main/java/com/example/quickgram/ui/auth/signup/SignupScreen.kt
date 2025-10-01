package com.example.quickgram.ui.auth.signup

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.quickgram.ui.auth.AuthViewModel
import kotlinx.coroutines.launch
@Composable
fun SignupScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf(TextFieldValue()) }
    var password by remember { mutableStateOf(TextFieldValue()) }
    var confirmPassword by remember { mutableStateOf(TextFieldValue()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sign Up",
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

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))
            val coroutineScope = rememberCoroutineScope()
            Button(
                onClick = {
                    if (password.text != confirmPassword.text) {
                        errorMessage = "Passwords do not match"
                        Log.e("SignupScreen", "Passwords do not match")
                    } else {
                        isLoading = true
                        coroutineScope.launch {
                            val result = viewModel.signUp(
                                email.text,
                                password.text
                            ) { success, message ->
                                // This handles NeonDB backend response
                                if (success) {
                                    Log.d("SignupScreen", "Backend signup success: $message")
                                } else {
                                    Log.e("SignupScreen", "Backend signup failed: $message")
                                }
                            }

                            isLoading = false

                            result.onSuccess {
                                Log.d("SignupScreen", "Signup successful!")
                                navController.popBackStack()
                            }.onFailure { error ->
                                errorMessage = error.message
                                Log.e("SignupScreen", "Signup failed: $error")
                            }
                        }
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
                    Text("Sign Up")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { navController.popBackStack() }
            ) {
                Text("Already have an account? Sign In")
            }

            // Show Snackbar if there's an error
            LaunchedEffect(errorMessage) {
                errorMessage?.let {
                    snackbarHostState.showSnackbar(it)
                    errorMessage = null
                }
            }
        }
    }
}

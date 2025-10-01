package com.example.quickgram.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.quickgram.data.remote.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.user.UserInfo
import io.github.jan.supabase.postgrest.postgrest
import at.favre.lib.crypto.bcrypt.BCrypt
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    companion object {
        private const val TAG = "AuthViewModel"
    }

    // ---------------- EMAIL LOGIN ----------------
    suspend fun login(email: String, password: String): Result<Unit> {
        Log.d(TAG, "login: Attempting login for email: $email")
        return try {
            SupabaseClient.client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Log.i(TAG, "login: Successfully logged in")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "login: Failed to login", e)
            Result.failure(e)
        }
    }

    // ---------------- EMAIL SIGNUP ----------------
    suspend fun signUp(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ): Result<UserInfo?> {
        Log.d(TAG, "signUp: Starting signup process for email: $email")
        return try {
            Log.d(TAG, "signUp: Creating Supabase auth account")
            val authResult = SupabaseClient.client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            Log.i(TAG, "signUp: Supabase auth account created successfully")

            // hash password before storing
            Log.d(TAG, "signUp: Hashing password")
            val hashedPassword = BCrypt.withDefaults()
                .hashToString(12, password.toCharArray())
            Log.d(TAG, "signUp: Password hashed successfully")

            // insert into Supabase table
            Log.d(TAG, "signUp: Inserting user into Supabase quickgram_users table")
            SupabaseClient.client.postgrest["quickgram_users"].insert(
                mapOf(
                    "email" to email,
                    "password" to hashedPassword
                )
            )
            Log.i(TAG, "signUp: User inserted into Supabase table successfully")

            // also insert into Neon DB via Flask API
            Log.d(TAG, "signUp: Initiating Neon DB insertion via Flask API")
            insertUserToNeonDB(email, hashedPassword, callback)

            Log.i(TAG, "signUp: Signup process completed successfully")
            Result.success(authResult)
        } catch (e: Exception) {
            Log.e(TAG, "signUp: Signup failed", e)
            callback(false, e.message ?: "Signup failed")
            Result.failure(e)
        }
    }

    private fun insertUserToNeonDB(
        email: String,
        hashedPassword: String,
        callback: (Boolean, String) -> Unit
    ) {
        Log.d(TAG, "insertUserToNeonDB: Starting Neon DB insertion")
        val client = OkHttpClient()
        val baseUrl = "http://10.113.235.19:5000"

        val json = JSONObject().apply {
            put("email", email)
            put("password", hashedPassword)
        }
        Log.d(TAG, "insertUserToNeonDB: Request body prepared")

        val body = json.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url("$baseUrl/auth/signup")
            .post(body)
            .build()

        Log.d(TAG, "insertUserToNeonDB: Sending request to $baseUrl/auth/signup")
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "insertUserToNeonDB: Network request failed", e)
                callback(false, e.message ?: "Network error")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        val errorBody = it.body?.string() ?: "Signup failed"
                        Log.e(TAG, "insertUserToNeonDB: Request unsuccessful. Code: ${it.code}, Body: $errorBody")
                        callback(false, errorBody)
                    } else {
                        val responseBody = it.body?.string() ?: "Signup success"
                        Log.i(TAG, "insertUserToNeonDB: Successfully inserted into Neon DB. Response: $responseBody")
                        callback(true, responseBody)
                    }
                }
            }
        })
    }

    // ---------------- GOOGLE LOGIN ----------------
    suspend fun loginWithGoogle(): Result<Unit> {
        Log.d(TAG, "loginWithGoogle: Attempting Google OAuth login")
        return try {
            // Sign in with Google
            // Redirect URL (quickgram://login-callback) is configured in AndroidManifest.xml
            Log.d(TAG, "loginWithGoogle: Initiating Google sign-in flow")
            SupabaseClient.client.auth.signInWith(Google){
//                redirectTo = "quickgram://login-callback"
            }
            Log.i(TAG, "loginWithGoogle: Google sign-in initiated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "loginWithGoogle: Google login failed", e)
            Result.failure(e)
        }
    }
}
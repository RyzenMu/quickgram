package com.example.quickgram.ui.auth

import com.example.quickgram.data.remote.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.UserSession
import javax.inject.Inject
import io.github.jan.supabase.gotrue.user.UserInfo


class AuthRepository @Inject constructor() {

    // LOGIN
    suspend fun login(email: String, password: String): Boolean {
        return try {
            SupabaseClient.client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    // SIGNUP

    suspend fun signUp(email: String, password: String): Result<UserInfo?> {
        return try {
            val userInfo = SupabaseClient.client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(userInfo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}

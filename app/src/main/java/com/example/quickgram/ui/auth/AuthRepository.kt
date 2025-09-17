package com.example.quickgram.ui.auth

import kotlinx.coroutines.delay
import javax.inject.Inject

class AuthRepository @Inject constructor() {

    suspend fun login(email: String, password: String): Boolean {
        delay(1000) // simulate network delay
        return (email == "test@test.com" && password == "123456")
    }
}

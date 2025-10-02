package com.example.quickgramJWTbackend

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import java.util.*

object JWTConfig {
    // Move this to environment variables or secure storage in production
    const val SECRET_KEY = "your-secret-key-change-in-production"
    const val ISSUER = "QuickGram"
    const val VALIDITY_MS = 24 * 60 * 60 * 1000L // 24 hours

    val algorithm: Algorithm = Algorithm.HMAC256(SECRET_KEY)
}

/**
 * Generates a JWT token for authenticated user
 */
fun generateToken(userId: String, email: String): String {
    return JWT.create()
        .withIssuer(JWTConfig.ISSUER)
        .withClaim("userId", userId)
        .withClaim("email", email)
        .withIssuedAt(Date())
        .withExpiresAt(Date(System.currentTimeMillis() + JWTConfig.VALIDITY_MS))
        .sign(JWTConfig.algorithm)
}

/**
 * Verifies and decodes a JWT token
 */
fun verifyToken(token: String): DecodedJWT {
    val verifier = JWT.require(JWTConfig.algorithm)
        .withIssuer(JWTConfig.ISSUER)
        .build()

    return verifier.verify(token)
}

/**
 * Extracts user ID from token
 */
fun getUserIdFromToken(token: String): String? {
    return try {
        val decoded = verifyToken(token)
        decoded.getClaim("userId").asString()
    } catch (e: Exception) {
        null
    }
}

/**
 * Checks if token is valid and not expired
 */
fun isTokenValid(token: String): Boolean {
    return try {
        verifyToken(token)
        true
    } catch (e: Exception) {
        false
    }
}
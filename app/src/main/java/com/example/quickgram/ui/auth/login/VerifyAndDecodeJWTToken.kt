package com.example.quickgram.ui.auth.login

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT

fun verifyToken(token: String): DecodedJWT {
    val algorithm = Algorithm.HMAC256("secret123")
    val verifier = JWT.require(algorithm)
        .withIssuer("myApp")
        .build()
    return verifier.verify(token) // throws if invalid
}

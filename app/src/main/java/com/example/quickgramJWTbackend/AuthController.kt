package com.example.quickgramJWTbackend

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.mindrot.jbcrypt.BCrypt

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class SignupRequest(
    val email: String,
    val password: String,
    val username: String
)

@Serializable
data class AuthResponse(
    val success: Boolean,
    val token: String? = null,
    val userId: String? = null,
    val email: String? = null,
    val message: String? = null
)

// Mock database - Replace with real database
object UserDatabase {
    private val users = mutableMapOf<String, User>()

    data class User(
        val id: String,
        val email: String,
        val passwordHash: String,
        val username: String
    )

    fun findByEmail(email: String): User? = users[email]

    fun createUser(email: String, password: String, username: String): User {
        val id = java.util.UUID.randomUUID().toString()
        val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())
        val user = User(id, email, passwordHash, username)
        users[email] = user
        return user
    }

    fun verifyPassword(user: User, password: String): Boolean {
        return BCrypt.checkpw(password, user.passwordHash)
    }
}

fun Application.configureAuthRoutes() {
    routing {
        route("/auth") {

            // Login endpoint
            post("/login") {
                try {
                    val request = call.receive<LoginRequest>()

                    // Validate input
                    if (request.email.isBlank() || request.password.isBlank()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            AuthResponse(
                                success = false,
                                message = "Email and password are required"
                            )
                        )
                        return@post
                    }

                    // Find user
                    val user = UserDatabase.findByEmail(request.email)
                    if (user == null) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            AuthResponse(
                                success = false,
                                message = "Invalid email or password"
                            )
                        )
                        return@post
                    }

                    // Verify password
                    if (!UserDatabase.verifyPassword(user, request.password)) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            AuthResponse(
                                success = false,
                                message = "Invalid email or password"
                            )
                        )
                        return@post
                    }

                    // Generate token
                    val token = generateToken(user.id, user.email)

                    call.respond(
                        HttpStatusCode.OK,
                        AuthResponse(
                            success = true,
                            token = token,
                            userId = user.id,
                            email = user.email,
                            message = "Login successful"
                        )
                    )

                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        AuthResponse(
                            success = false,
                            message = "An error occurred: ${e.message}"
                        )
                    )
                }
            }

            // Signup endpoint
            post("/signup") {
                try {
                    val request = call.receive<SignupRequest>()

                    // Validate input
                    if (request.email.isBlank() || request.password.isBlank() || request.username.isBlank()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            AuthResponse(
                                success = false,
                                message = "All fields are required"
                            )
                        )
                        return@post
                    }

                    // Check if user exists
                    if (UserDatabase.findByEmail(request.email) != null) {
                        call.respond(
                            HttpStatusCode.Conflict,
                            AuthResponse(
                                success = false,
                                message = "User with this email already exists"
                            )
                        )
                        return@post
                    }

                    // Create user
                    val user = UserDatabase.createUser(
                        request.email,
                        request.password,
                        request.username
                    )

                    // Generate token
                    val token = generateToken(user.id, user.email)

                    call.respond(
                        HttpStatusCode.Created,
                        AuthResponse(
                            success = true,
                            token = token,
                            userId = user.id,
                            email = user.email,
                            message = "Signup successful"
                        )
                    )

                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        AuthResponse(
                            success = false,
                            message = "An error occurred: ${e.message}"
                        )
                    )
                }
            }

            // Logout endpoint
            post("/logout") {
                // In a real app, you might want to blacklist the token
                call.respond(HttpStatusCode.OK)
            }

            // Token refresh endpoint
            post("/refresh") {
                try {
                    val authHeader = call.request.header("Authorization")
                    val token = authHeader?.removePrefix("Bearer ")

                    if (token == null) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            AuthResponse(success = false, message = "No token provided")
                        )
                        return@post
                    }

                    // Verify old token
                    val decoded = verifyToken(token)
                    val userId = decoded.getClaim("userId").asString()
                    val email = decoded.getClaim("email").asString()

                    // Generate new token
                    val newToken = generateToken(userId, email)

                    call.respond(
                        HttpStatusCode.OK,
                        AuthResponse(
                            success = true,
                            token = newToken,
                            userId = userId,
                            email = email
                        )
                    )

                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        AuthResponse(success = false, message = "Invalid token")
                    )
                }
            }
        }
    }
}
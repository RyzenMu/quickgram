// In RemoteDataSource:
package com.example.quickgram.data.remote

import com.example.quickgram.data.model.Post
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import javax.inject.Inject

// API Service Interface
interface ApiService {
    @GET("posts")
    suspend fun getPosts(): Response<List<Post>>

    @GET("posts/{userId}")
    suspend fun getUserPosts(@Path("userId") userId: String): Response<List<Post>>

    @POST("posts")
    suspend fun createPost(@Body post: Post): Response<Post>

    @GET("users/{userId}")
    suspend fun getUser(@Path("userId") userId: String): Response<User>
}

// User data class for API
data class User(
    val id: String,
    val username: String,
    val email: String,
    val avatar: String,
    val bio: String? = null
)

// API Response wrapper
data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val message: String?
)

class RemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun fetchPosts(): List<Post> {
        return try {
            val response = apiService.getPosts()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            // Return mock data for demo purposes
            listOf(
                Post(
                    id = "remote1",
                    userId = "api_user1",
                    username = "remote_user",
                    userAvatar = "https://i.pravatar.cc/150?img=8",
                    imageUrl = "https://picsum.photos/500/600?random=10",
                    caption = "From remote API! 🌐",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun fetchUserPosts(userId: String): List<Post> {
        return try {
            val response = apiService.getUserPosts(userId)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun uploadPost(post: Post): Post? {
        return try {
            val response = apiService.createPost(post)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
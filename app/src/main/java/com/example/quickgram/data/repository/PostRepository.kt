package com.example.quickgram.data.repository

import com.example.quickgram.data.local.LocalDataSource
import com.example.quickgram.data.remote.RemoteDataSource
import com.example.quickgram.data.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) {

    // Fallback hardcoded data
    private val fallbackPosts = listOf(
        Post(
            id = "1",
            userId = "user1",
            username = "john_doe",
            userAvatar = "https://i.pravatar.cc/150?img=3",
            imageUrl = "https://picsum.photos/500/600?random=1",
            caption = "Beautiful sunset at the beach! 🌅",
            timestamp = System.currentTimeMillis()
        ),
        Post(
            id = "2",
            userId = "user2",
            username = "jane_smith",
            userAvatar = "https://i.pravatar.cc/150?img=4",
            imageUrl = "https://picsum.photos/500/600?random=2",
            caption = "Coffee and code ☕️ Perfect morning!",
            timestamp = System.currentTimeMillis() - 3600000
        ),
        Post(
            id = "3",
            userId = "user3",
            username = "travel_lover",
            userAvatar = "https://i.pravatar.cc/150?img=5",
            imageUrl = "https://picsum.photos/500/600?random=3",
            caption = "Mountain hiking adventure 🏔️ #nature #hiking",
            timestamp = System.currentTimeMillis() - 7200000
        ),
        Post(
            id = "4",
            userId = "user4",
            username = "foodie_life",
            userAvatar = "https://i.pravatar.cc/150?img=6",
            imageUrl = "https://picsum.photos/500/600?random=4",
            caption = "Homemade pasta night! 🍝 Recipe in bio",
            timestamp = System.currentTimeMillis() - 10800000
        ),
        Post(
            id = "5",
            userId = "user5",
            username = "art_enthusiast",
            userAvatar = "https://i.pravatar.cc/150?img=7",
            imageUrl = "https://picsum.photos/500/600?random=5",
            caption = "Work in progress... 🎨 #art #painting",
            timestamp = System.currentTimeMillis() - 14400000
        )
    )

    // Expose cached posts as a Flow with fallback
    fun getPosts(): Flow<List<Post>> {
        return localDataSource.getAllPosts()
            .map { cachedPosts ->
                if (cachedPosts.isEmpty()) {
                    fallbackPosts
                } else {
                    cachedPosts
                }
            }
            .catch {
                // If there's an error accessing local data, emit fallback data
                emit(fallbackPosts)
            }
    }

    // Refresh from API and update cache
    suspend fun refreshPosts() {
        try {
            val remotePosts = remoteDataSource.fetchPosts()
            if (remotePosts.isNotEmpty()) {
                localDataSource.cachePosts(remotePosts)
            }
        } catch (e: Exception) {
            // Log error or handle it as needed
            // The UI will still show cached data or fallback data
        }
    }

    fun getUserPosts(userId: String): Flow<List<Post>> {
        return localDataSource.getUserPosts(userId)
            .map { userPosts ->
                if (userPosts.isEmpty()) {
                    // Filter fallback posts for this user
                    fallbackPosts.filter { it.userId == userId }
                } else {
                    userPosts
                }
            }
            .catch {
                // If there's an error, emit filtered fallback data for this user
                emit(fallbackPosts.filter { it.userId == userId })
            }
    }

    suspend fun refreshUserPosts(userId: String) {
        try {
            val remotePosts = remoteDataSource.fetchUserPosts(userId)
            if (remotePosts.isNotEmpty()) {
                localDataSource.cachePosts(remotePosts)
            }
        } catch (e: Exception) {
            // Log error or handle it as needed
        }
    }

    suspend fun createPost(post: Post) {
        try {
            val uploaded = remoteDataSource.uploadPost(post)
            uploaded?.let {
                localDataSource.insertPost(it) // cache it locally
            }
        } catch (e: Exception) {
            // Handle upload error - maybe queue for retry later
        }
    }

    // Alternative approach: Get posts with automatic refresh attempt
    fun getPostsWithAutoRefresh(): Flow<List<Post>> = flow {
        // First emit cached data (or fallback if cache is empty)
        val cachedPosts = try {
            localDataSource.getAllPosts()
        } catch (e: Exception) {
            flow { emit(fallbackPosts) }
        }

        cachedPosts.collect { posts ->
            emit(if (posts.isEmpty()) fallbackPosts else posts)
        }

        // Then try to refresh in background
        try {
            refreshPosts()
        } catch (e: Exception) {
            // Refresh failed, but we've already emitted cached/fallback data
        }
    }
}
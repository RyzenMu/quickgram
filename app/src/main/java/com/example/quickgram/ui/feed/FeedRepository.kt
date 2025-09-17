package com.example.quickgram.ui.feed

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.example.quickgram.data.model.Post // Use this import

class FeedRepository @Inject constructor() {
    fun getFeed(): Flow<List<Post>> = flow {
        emit(
            listOf(
                Post(
                    id = "1",
                    userId = "user1", // Add userId
                    username = "Alice",
                    userAvatar = "https://i.pravatar.cc/150?img=1",
                    imageUrl = "https://picsum.photos/400",
                    caption = "Morning vibes!",
                    timestamp = System.currentTimeMillis() // Add timestamp
                ),
                Post(
                    id = "2",
                    userId = "user2", // Add userId
                    username = "Bob",
                    userAvatar = "https://i.pravatar.cc/150?img=2",
                    imageUrl = "https://picsum.photos/401",
                    caption = "Weekend trip 🚀",
                    timestamp = System.currentTimeMillis() - 3600000 // Add timestamp
                )
            )
        )
    }
}
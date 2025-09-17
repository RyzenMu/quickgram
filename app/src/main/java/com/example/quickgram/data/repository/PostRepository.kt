package com.example.quickgram.data.repository
import com.example.quickgram.data.local.LocalDataSource
import com.example.quickgram.data.remote.RemoteDataSource
import com.example.quickgram.data.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class PostRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) {

    fun getPosts(): Flow<List<Post>> = flow {
        emit(
            listOf(
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
        )
    }
}

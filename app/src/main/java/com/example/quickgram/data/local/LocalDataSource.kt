// In LocalDataSource:
package com.example.quickgram.data.local

import androidx.room.*
import com.example.quickgram.data.model.Post
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlinx.coroutines.flow.*

// Room Entity
@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val username: String,
    val userAvatar: String,
    val imageUrl: String,
    val caption: String,
    val timestamp: Long
)

// DAO Interface
@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE userId = :userId ORDER BY timestamp DESC")
    fun getUserPosts(userId: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :postId")
    suspend fun getPostById(postId: String): PostEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Delete
    suspend fun deletePost(post: PostEntity)

    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()
}

// Room Database
@Database(
    entities = [PostEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
}

// Extension functions for conversion
fun Post.toEntity(): PostEntity {
    return PostEntity(
        id = id,
        userId = userId,
        username = username,
        userAvatar = userAvatar,
        imageUrl = imageUrl,
        caption = caption,
        timestamp = timestamp
    )
}

fun PostEntity.toPost(): Post {
    return Post(
        id = id,
        userId = userId,
        username = username,
        userAvatar = userAvatar,
        imageUrl = imageUrl,
        caption = caption,
        timestamp = timestamp
    )
}

class LocalDataSource @Inject constructor(
    private val database: AppDatabase
) {
    private val postDao = database.postDao()

    fun getAllPosts(): Flow<List<Post>> {
        return postDao.getAllPosts().map { entities ->
            entities.map { it.toPost() }
        }
    }

    fun getUserPosts(userId: String): Flow<List<Post>> {
        return postDao.getUserPosts(userId).map { entities ->
            entities.map { it.toPost() }
        }
    }

    suspend fun getPostById(postId: String): Post? {
        return postDao.getPostById(postId)?.toPost()
    }

    suspend fun insertPost(post: Post) {
        postDao.insertPost(post.toEntity())
    }

    suspend fun insertPosts(posts: List<Post>) {
        postDao.insertPosts(posts.map { it.toEntity() })
    }

    suspend fun deletePost(post: Post) {
        postDao.deletePost(post.toEntity())
    }

    suspend fun clearAllPosts() {
        postDao.deleteAllPosts()
    }

    // Cache management
    suspend fun cachePosts(posts: List<Post>) {
        postDao.insertPosts(posts.map { it.toEntity() })
    }
}
package com.example.quickgram.data.model


data class Post(
    val id: String,
    val userId: String,
    val username: String,
    val userAvatar: String,
    val imageUrl: String,
    val caption: String,
    val timestamp: Long
)

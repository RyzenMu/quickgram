package com.example.quickgram.ui.feed
import androidx.compose.foundation.Image

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items


@Composable
fun FeedScreen(
    navController: NavController,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val posts by viewModel.posts.collectAsState(initial = emptyList())

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(posts) { post ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column {
                        Row(Modifier.padding(8.dp)) {
                            Image(
                                painter = rememberAsyncImagePainter(post.userAvatar),
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = post.username,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Image(
                            painter = rememberAsyncImagePainter(post.imageUrl),
                            contentDescription = "Post Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = post.caption,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate("upload") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Upload")
        }
    }
}

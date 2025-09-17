package com.example.quickgram.ui.components

import androidx.compose.material3.Button
import androidx.compose.runtime.Composable

@Composable
fun CustomButton(onClick: () -> Unit, text: String) {
    Button(onClick = onClick) {
        androidx.compose.material3.Text(text)
    }
}

package com.a602.commonproject.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.a602.commonproject.designsystem.theme.LMTheme

@Composable
fun MemoryEmptyScreen() {
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = com.a602.commonproject.designsystem.R.drawable.empty_planet),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Text and overlay removed as per user request. 
        // Only showing the empty planet background.
    }
}

@Preview(showBackground = true, name = "Memory - Empty", widthDp = 411)
@Composable
fun Preview_Memory_Empty() {
    LMTheme {
        MaterialTheme {
            MemoryEmptyScreen()
        }
    }
}


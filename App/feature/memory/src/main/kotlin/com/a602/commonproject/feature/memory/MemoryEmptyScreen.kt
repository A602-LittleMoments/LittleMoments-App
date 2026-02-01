package com.a602.commonproject.feature.memory

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .offset(y = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(180.dp))
            Image(
                painter = painterResource(id = com.a602.commonproject.designsystem.R.drawable.moon2),
                contentDescription = null,
                modifier = Modifier.size(150.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.height(30.dp))
            Text(
                text = "AI가 추억을 모으고 있어요",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "사진이 많아질수록 빨리 추억이 만들어져요.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
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


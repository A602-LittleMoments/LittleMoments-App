package com.a602.commonproject.feature.gallery

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey

object HighlightLoadingNavKey : NavKey

@Composable
fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = com.a602.commonproject.designsystem.R.drawable.empty_planet),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-52).dp)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Image(
                painter = painterResource(id = com.a602.commonproject.designsystem.R.drawable.moon),
                contentDescription = null,
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.height(8.dp))

            Text(
                text = "AI가 추억을 모으고 있어요",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "잠시만 기다려주세요...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun LoadingContentPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            LoadingContent()
        }
    }
}

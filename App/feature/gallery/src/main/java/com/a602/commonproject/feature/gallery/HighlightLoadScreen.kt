
package com.a602.commonproject.feature.gallery

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.a602.commonproject.designsystem.R
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.feature.gallery.viewmodel.HighlightLoadingViewModel

@Composable
fun HighlightLoadingRoute(
    startMillis: Long,
    endMillis: Long,
    onSuccess: (String) -> Unit, // COMPLETED 상태의 slideshowId 전달
    onFailure: (Throwable) -> Unit,
    viewModel: HighlightLoadingViewModel = hiltViewModel()
) {
    LaunchedEffect(startMillis, endMillis) {
        viewModel.createAndWaitSlideshow(
            startMillis = startMillis,
            endMillis = endMillis,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    HighlightLoadingScreen()
}
@Composable
fun HighlightLoadingScreen(
    modifier: Modifier = Modifier
) {
    LoadingContent(modifier = modifier)
}

@Composable
fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.empty_planet),
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
                painter = painterResource(id = R.drawable.moon),
                contentDescription = null,
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.height(8.dp))

            Text(
                text = "AI가 추억을 모으고 있어요",
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "잠시만 기다려주세요...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            ////            LinearProgressIndicator(
////                progress = { progress.value },
////                modifier = Modifier
////                    .fillMaxWidth(0.8f)
////                    .height(10.dp),
////            )

            Spacer(Modifier.height(18.dp))
            LoadingDots()
        }
    }
}

@Composable
private fun LoadingDots() {
    val infinite = rememberInfiniteTransition(label = "dots")
    val s1 by infinite.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
        label = "s1"
    )
    val s2 by infinite.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(500, delayMillis = 150), RepeatMode.Reverse),
        label = "s2"
    )
    val s3 by infinite.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(500, delayMillis = 300), RepeatMode.Reverse),
        label = "s3"
    )

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Dot(s1)
        Dot(s2)
        Dot(s3)
    }
}

@Composable
private fun Dot(scale: Float) {
    Surface(
        modifier = Modifier
            .size(10.dp)
            .scale(scale),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primary
    ) {}
}

@Preview(showBackground = true)
@Composable
fun LoadingContentPreview() {
    LMTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            LoadingContent()
        }
    }
}

//package com.a602.commonproject.feature.home
//
//import androidx.compose.animation.core.*
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.LinearProgressIndicator
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.scale
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.tooling.preview.Preview
//import com.a602.commonproject.designsystem.theme.LMTheme
//
//@Composable
//fun MemoryMakeScreen(
//    totalMillis: Int = 1_000,
//) {
//    val progress = remember { Animatable(0f) }
//    var started by rememberSaveable { mutableStateOf(false) }
//
//    LaunchedEffect(totalMillis) {
//        if (!started) {
//            started = true
//            progress.snapTo(0f)
//            progress.animateTo(
//                targetValue = 1f,
//                animationSpec = tween(durationMillis = totalMillis, easing = LinearEasing)
//            )
//        }
//    }
//
//    Box(Modifier.fillMaxSize()) {
//        Image(
//            painter = painterResource(id = com.a602.commonproject.designsystem.R.drawable.empty_planet),
//            contentDescription = null,
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.Crop
//        )
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(horizontal = 24.dp)
//                .offset(y = 30.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Spacer(Modifier.height(180.dp))
//
//            Image(
//                painter = painterResource(id = com.a602.commonproject.designsystem.R.drawable.rocket2),
//                contentDescription = null,
//                modifier = Modifier.size(150.dp),
//                contentScale = ContentScale.Fit
//            )
//
//            Spacer(Modifier.height(30.dp))
//
//            Text(
//                text = "AI가 새로운 행성을 생성했어요",
//                style = MaterialTheme.typography.headlineLarge,
//                color = MaterialTheme.colorScheme.primary
//            )
//            Spacer(Modifier.height(8.dp))
//            Text(
//                text = "우리 아이의 모험을 함께 해봐요",
//                style = MaterialTheme.typography.bodyMedium,
//                color = MaterialTheme.colorScheme.onSurfaceVariant
//            )
//
//            Spacer(Modifier.height(22.dp))
//
//            LinearProgressIndicator(
//                progress = { progress.value },
//                modifier = Modifier
//                    .fillMaxWidth(0.8f)
//                    .height(10.dp),
//            )
//
//            Spacer(Modifier.height(18.dp))
//            LoadingDots()
//        }
//    }
//}
//
//@Composable
//private fun LoadingDots() {
//    val infinite = rememberInfiniteTransition(label = "dots")
//    val s1 by infinite.animateFloat(0.6f, 1.0f, infiniteRepeatable(tween(500), RepeatMode.Reverse), label = "s1")
//    val s2 by infinite.animateFloat(0.6f, 1.0f, infiniteRepeatable(tween(500, delayMillis = 150), RepeatMode.Reverse), label = "s2")
//    val s3 by infinite.animateFloat(0.6f, 1.0f, infiniteRepeatable(tween(500, delayMillis = 300), RepeatMode.Reverse), label = "s3")
//
//    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
//        Dot(s1); Dot(s2); Dot(s3)
//    }
//}
//
//@Composable
//private fun Dot(scale: Float) {
//    Surface(
//        modifier = Modifier.size(10.dp).scale(scale),
//        shape = MaterialTheme.shapes.small,
//        color = MaterialTheme.colorScheme.primary
//    ) {}
//}
//
//@Preview(showBackground = true, name = "Memory - Loading", widthDp = 411)
//@Composable
//private fun Preview_Memory_Loading() {
//    LMTheme {
//        MemoryMakeScreen(totalMillis = 1_000)
//    }
//}
//

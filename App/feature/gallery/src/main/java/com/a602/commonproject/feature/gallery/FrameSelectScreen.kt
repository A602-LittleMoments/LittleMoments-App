//package com.a602.commonproject.feature.gallery
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.layout.ContentScale
//import coil.compose.AsyncImage
//import com.a602.commonproject.designsystem.theme.background
//
//data class MediaFrame(
//    val frameId: String,
//    val imageUrl: String
//)
//@Composable
//fun MediaDetailScreen(
//    frames: List<MediaFrame>,
//    modifier: Modifier = Modifier
//) {
//    var selectedFrame by rememberSaveable {
//        mutableStateOf(frames.first())
//    }
//
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .background(background)
//    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(1f),
//            contentAlignment = Alignment.Center
//        ) {
//            AsyncImage(
//                model = selectedFrame.imageUrl,
//                contentDescription = "selected frame",
//                modifier = Modifier.fillMaxSize(),
//                contentScale = ContentScale.Fit
//            )
//        }
//
//        LazyRow(
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(Color(0xFF111111))
//                .padding(vertical = 12.dp),
//            horizontalArrangement = Arrangement.spacedBy(8.dp),
//            contentPadding = PaddingValues(horizontal = 16.dp)
//        ) {
//            items(frames, key = { it.frameId }) { frame ->
//                FrameThumbnail(
//                    frame = frame,
//                    isSelected = frame.frameId == selectedFrame.frameId,
//                    onClick = { selectedFrame = frame }
//                )
//            }
//        }
//    }
//}

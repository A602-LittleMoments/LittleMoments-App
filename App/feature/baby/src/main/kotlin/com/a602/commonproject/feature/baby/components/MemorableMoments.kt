package com.a602.commonproject.feature.baby.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.a602.commonproject.model.data.Collection

@Composable
fun MemorableMoments(
    collections: List<Collection>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "기억하고 싶은 순간",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(collections) { collection ->
                MomentCard(collection = collection)
            }
        }
    }
}

@Composable
fun MomentCard(collection: Collection) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(100.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            // Collection에는 현재 Representative Image Url이 없음.
            // 실제 구현 시에는 Collection에 썸네일 URL을 추가하거나
            // SharedMediaRepository에서 첫 번째 이미지를 가져오는 로직이 필요.
            // 여기서는 플레이스홀더로 대체.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
            ) {
                // TODO: 썸네일 이미지 로드
                /*
                AsyncImage(
                    model = ...,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                 */
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = collection.keywordValue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}

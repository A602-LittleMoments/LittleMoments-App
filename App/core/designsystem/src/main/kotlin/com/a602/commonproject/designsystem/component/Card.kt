package com.a602.commonproject.designsystem.component

import com.a602.commonproject.designsystem.theme.background
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.designsystem.theme.color3
import com.a602.commonproject.designsystem.theme.color4


@Composable
fun LMCard(
    title: String,
    description: String,
    time: String,
    type: String,   // 아이콘 대신 타입 받기
    imageUrl: String? = null, //썸네일 url 나중에 가져오나요?
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(0.9f)
            .widthIn(min= 280.dp, max = 350.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = LMCardDefaults.containerColor()
        ),
        elevation = LMCardDefaults.elevation()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 아이콘 영역

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(6.dp, CircleShape)
                    .clip(CircleShape)
                    .background(LMCardDefaults.iconBackgroundColor()),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = LMicons.notification(type),
                    contentDescription = null,
                    tint = LMCardDefaults.iconTintColor(type) ,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = LMCardDefaults.titleColor(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.size(4.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.labelMedium,
                    color = LMCardDefaults.descriptionColor(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.size(4.dp))

                Text(
                    text = time,
                    style = MaterialTheme.typography.labelSmall,
                    color = LMCardDefaults.timeColor()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))



                if (!imageUrl.isNullOrBlank()) {
                    Spacer(modifier = Modifier.width(12.dp))

                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }


        }
    }
}

object LMCardDefaults {
    @Composable
    fun containerColor(): Color = background

    @Composable
    fun iconBackgroundColor(): Color = background

    @Composable
    fun titleColor(): Color = color3

    @Composable
    fun descriptionColor(): Color = color3
    @Composable
    fun timeColor(): Color = color4

    @Composable
    fun elevation() = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)

    @Composable
    fun titleTextStyle(): TextStyle = MaterialTheme.typography.bodyLarge

    @Composable
    fun descriptionTextStyle(): TextStyle = MaterialTheme.typography.labelMedium

    @Composable
    fun timeTextStyle(): TextStyle = MaterialTheme.typography.labelMedium

    @Composable
    fun iconTintColor(type: String): Color {
        return when (type) {
            "FAMILY" -> Color(0xFFFFC800)   // 노랑
            "LOVE" -> Color(0xFFFF383C)
            "HIGHLIGHT" -> Color(0xFFFFC800) // 노랑
            "Date" -> Color.Unspecified
            else -> Color(0xFFFFC800)
        }
    }


}

@Preview(showBackground = true, widthDp = 411)
@Composable
fun LMCardPreview() {
    LMTheme {
        Column(
            modifier = Modifier
                .background(background)
                .padding(vertical = 16.dp)
        ) {

            LMCard(
                title = "가족 앨범이 업데이트 되었어요",
                description = "엄마가 새로운 사진을 추가했어요",
                time = "10분 전",
                type = "HIGHLIGHT"
            )

            LMCard(
                title = "오늘의 추억이 도착했어요",
                description = "1년 전 오늘을 기억하시나요?",
                time = "1시간 전",
                type = "HIGHLIGHT",
                imageUrl = "https://via.placeholder.com/150/E0E0E0/777777"


            )
            LMCard(
                title = "아이와 함께한 지 300일이 되었어요",
                description = "우리 아이의 300일 어떻게 보내셨어요?",
                time = "1시간 전",
                type = "Date",
            )

            LMCard(
                title = "사랑이 가득 담긴 순간",
                description = "하트 반응이 추가되었어요",
                time = "2시간 전",
                type = "LOVE"
            )
        }
    }
}

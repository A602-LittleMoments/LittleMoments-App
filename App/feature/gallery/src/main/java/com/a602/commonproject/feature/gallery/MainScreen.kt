package com.a602.commonproject.feature.gallery

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.a602.commonproject.designsystem.component.AstronautPhotoPicker
import com.a602.commonproject.designsystem.component.DdayProgressBar
import com.a602.commonproject.designsystem.component.Polaroid
import com.a602.commonproject.designsystem.component.PolaroidMeta
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color1
import com.a602.commonproject.designsystem.theme.color3
import com.a602.commonproject.designsystem.theme.gray2
import com.a602.commonproject.designsystem.theme.lightbackground
import com.a602.commonproject.model.data.Collection

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    babyImageUri: Uri? = null,
    babyName: String ,
    dDay: Int,
    collections: List<Collection> = emptyList(),
    memoryPhotos: List<MemoryPhoto> = emptyList(),
    onCollectionClick: (Collection) -> Unit = {},
    onMemoryPhotoClick: (MemoryPhoto) -> Unit = {},
) {
    val scrollState = rememberScrollState()
    Spacer(modifier = Modifier.height(40.dp))

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(background)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {

        // Header Section
        MainScreenHeader(
            babyImageUri = babyImageUri,
            babyName = babyName,
            dDay = dDay,
            modifier = Modifier.fillMaxWidth()
        )

        // CollectionsSection (기억하고 싶은 순간)
        if (collections.isNotEmpty()) {
            CollectionSection(
                collections = collections,
                onCollectionClick = onCollectionClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // MemoryTimelineSection (추억 타임라인)
        MemoryTimelineSection(
            memoryPhotos = memoryPhotos,
            onMemoryPhotoClick = onMemoryPhotoClick,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun MainScreenHeader(
    babyImageUri: Uri?,
    babyName: String,
    dDay: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(top = 40.dp)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Baby Profile with Astronaut Body
        AstronautPhotoPicker(
            imageUri = babyImageUri,
            onClick = { /* 사진 등록 로직 필요 없음 */ },
            modifier = Modifier.padding(bottom = 16.dp),
            headSize = 140.dp,
            bodyWidth = 120.dp,
            bodyOffsetY = 110.dp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 아이이름
        Text(
            text = babyName,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(4.dp))

        // D-day Bar
        Text(
            text = "D+$dDay",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // D-day Bar
        // 기준을 모르겠어요 ㅠ
        val totalDays = 365
        val progress = if (dDay <= totalDays) {
            (dDay.toFloat() / totalDays).coerceIn(0f, 1f)
        } else {
            1f
        }

        DdayProgressBar(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            height = 18.dp
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}


// Data class memory photos
data class MemoryPhoto(
    val id: String,
    val rearImage: ImageBitmap,
    val frontImage: ImageBitmap,
    val date: String,
    val uploaderRole: String,
    val comment: String?,
    val dDay: Int,
    val isLiked: Boolean = false
)

@Composable
private fun CollectionSection(
    collections: List<Collection>,
    onCollectionClick: (Collection) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp)
    ) {
        Text(
            text = "기억하고 싶은 순간",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(collections) { collection ->
                CollectionCard(
                    collection = collection,
                    onClick = { onCollectionClick(collection) }
                )
            }
        }
    }
}

@Composable
private fun CollectionCard(
    collection: Collection,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val previewRear = previewBitmap(color = Color(0xFF1B1B1F).toArgb())
    val previewFront = previewBitmap(width = 600, height = 600, color = Color(0xFF9BB7D4).toArgb())

    Polaroid(
        rearImage = previewRear,
        frontImage = previewFront,
        meta = PolaroidMeta(
            date = collection.categoryValue,
            role = " ",
            comment = null
        ),
        modifier = modifier.width(130.dp)
    )
}

@Composable
private fun MemoryTimelineSection(
    memoryPhotos: List<MemoryPhoto>,
    onMemoryPhotoClick: (MemoryPhoto) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp)

    ) {

        if (memoryPhotos.isEmpty()) {
            // Empty state
            EmptyMemoryState()
        } else {
            Text(
                text = "추억 타임라인",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(memoryPhotos) { photo ->
                    MemoryPhotoCard(
                        photo = photo,
                        onClick = { onMemoryPhotoClick(photo) }
                    )
                }
            }
        }
    }
}



@Composable
private fun EmptyMemoryState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "사진 속에 자라는 우리 아이,\nAI가 성장의 기록을\n앨범으로 담아드려요.",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 16.sp,
                lineHeight = 24.sp
            ),
            color = color3,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MemoryPhotoCard(
    photo: MemoryPhoto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        // Date and D-day row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = photo.date,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "D+${photo.dDay}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.weight(1f))

            // Heart icon placeholder
            Text(
                text = if (photo.isLiked) "❤️" else "🤍",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Polaroid photo
        Polaroid(
            rearImage = photo.rearImage,
            frontImage = photo.frontImage,
            meta = PolaroidMeta(
                date = photo.date,
                role = photo.uploaderRole,
                comment = photo.comment
            ),
            modifier = Modifier.width(280.dp)
        )
    }
}

// Preview helper function
private fun previewBitmap(
    width: Int = 1080,
    height: Int = 1440,
    color: Int
): ImageBitmap {
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bmp.eraseColor(color)
    return bmp.asImageBitmap()
}

@Preview(showBackground = true, backgroundColor = 0xFFF6F1E8)
@Composable
private fun PreviewMainScreenWithContent() {
    MaterialTheme {
        val sampleCollections = listOf(
            Collection(
                categoryId = "1",
                categoryValue = "처음",
                keywordId = "1",
                keywordValue = "처음",
                collectionSize = 5
            ),
            Collection(
                categoryId = "2",
                categoryValue = "생육",
                keywordId = "2",
                keywordValue = "생육",
                collectionSize = 3
            ),
            Collection(
                categoryId = "3",
                categoryValue = "감성",
                keywordId = "3",
                keywordValue = "감성",
                collectionSize = 8
            )
        )

        val samplePhotos = listOf(
            MemoryPhoto(
                id = "1",
                rearImage = previewBitmap(color = Color(0xFF1B1B1F).toArgb()),
                frontImage = previewBitmap(width = 600, height = 600, color = Color(0xFF9BB7D4).toArgb()),
                date = "2026.01.02 (화)",
                uploaderRole = "엄마",
                comment = "우리 아기 첫 사진!",
                dDay = 60,
                isLiked = true
            )
        )

        MainScreen(
            babyImageUri = null,
            babyName = "하람이",
            dDay = 60,
            collections = sampleCollections,
            memoryPhotos = samplePhotos
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF6F1E8)
@Composable
private fun PreviewMainScreenEmpty() {
    MaterialTheme {
        MainScreen(
            babyImageUri = null,
            babyName = "하람이",
            dDay = 60,
            collections = emptyList(),
            memoryPhotos = emptyList()
        )
    }
}

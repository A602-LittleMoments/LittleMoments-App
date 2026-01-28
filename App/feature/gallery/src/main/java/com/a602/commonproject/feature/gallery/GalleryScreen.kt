/*
package com.a602.commonproject.feature.gallery

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.time.Instant
import java.time.ZoneId
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.a602.commonproject.designsystem.component.ButtonSize
import com.a602.commonproject.designsystem.component.FillWrapButton
import com.a602.commonproject.designsystem.component.FilledButton
import com.a602.commonproject.designsystem.component.PolaroidMeta
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color3
import com.a602.commonproject.designsystem.theme.lightbackground
import com.a602.commonproject.designsystem.theme.lightblue
import com.a602.commonproject.designsystem.theme.main
import com.a602.coommonproject.ui.GalleryGridPolaroid
import com.a602.coommonproject.ui.MediaDetailAction
import com.a602.coommonproject.ui.MediaDetailScreen
import com.a602.coommonproject.ui.MediaDetailUiState
import com.a602.coommonproject.ui.PolaroidData
import com.a602.coommonproject.ui.SelectableGalleryGrid


data class CalendarDay(
    val day: Int,
    val imageUrl: String? = null, // 사진이 없으면 null
    val isCurrentMonth: Boolean = true
)

@Composable
fun CalendarPhotoView(days: List<CalendarDay>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // 요일 헤더 (일, 월, 화...)
        DayOfWeekHeader()

        Spacer(modifier = Modifier.height(8.dp))

        // 날짜 그리드
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
             items(days) { day ->
                CalendarDayItem(
                    day = day,
                    onClick = { */
/* 나중에 Action 연결 *//*
 }
                )
            }
        }
    }
}

@Composable
fun DayOfWeekHeader() {
    val days = listOf("일", "월", "화", "수", "목", "금", "토")

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        userScrollEnabled = false
    ) {
        items(days) { day ->
            Text(
                text = day,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun CalendarDayItem(
    day: CalendarDay,
    onClick: (CalendarDay) -> Unit) {
    val isPreview = LocalInspectionMode.current // 프리뷰 확인용
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth()
            .clickable(enabled = day.day != null) {
                onClick(day)
            },
        contentAlignment = Alignment.Center
    ) {
        if (day.imageUrl != null) {
        if (isPreview) { // 프리뷰용
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
        } else {
            // 이미지가 있는 경우: 원형 이미지 + 중앙 텍스트
            AsyncImage(
                model = day.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .graphicsLayer(alpha = 0.8f),
                contentScale = ContentScale.Crop,
            )
        }
        }



        // 날짜 텍스트
        Text(
            text = if (day.day > 0) day.day.toString() else "",
            style = MaterialTheme.typography.bodyMedium,
            color = if (day.imageUrl != null) Color.White else color3,
            fontWeight = FontWeight.Bold
        )
    }
}


@Preview()
@Composable
fun CalendarJanuary2026Preview() {
    val january2026Days = remember {
        val days = mutableListOf<CalendarDay>()

        // 1. 2026년 1월 1일은 목요일 -> 앞의 4칸(일~수)을 빈 데이터로 채움
        repeat(4) { days.add(CalendarDay(day = 0)) }

        // 2. 1일부터 31일까지 생성
        for (i in 1..31) {
            // 특정 날짜(예: 1, 5, 12, 19, 26일)에만 사진이 있는 것으로 설정
            val hasImage = i in listOf(1, 5, 12, 19, 26, 29)
            days.add(
                CalendarDay(
                    day = i,
                    imageUrl = if (hasImage) "https://via.placeholder.com/150" else null

                )
            )
        }
        days
    }

        Surface(modifier = Modifier.wrapContentSize(),color=background) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // 연도 및 월 헤더
                Text(
                    text = "1월",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(top = 32.dp, bottom = 16.dp),
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center
                )

                // 캘린더 그리드 출력
                CalendarPhotoView(days = january2026Days)
            }

    }
}

// 격자 보기
@Composable
fun GridGallery(
    polaroids: List<PolaroidData>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. 상단 헤더 영역
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = " Recent",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
                FillWrapButton(
                    text = "캘린더 보기",
                    onClick= {*/
/*이동 구현 해야함*//*
 },
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                GalleryGridPolaroid(
                    polaroids = polaroids,
                    onClick = { clickedItem ->} // 상세보기하나요 ?
                        )
            }
        }
    }
}
private fun previewBitmap(
    width: Int = 1080,
    height: Int = 1440,
    color: Int
): ImageBitmap {
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bmp.eraseColor(color)
    return bmp.asImageBitmap()
}
private val samplePolaroids = List(9) { i ->
    PolaroidData(
        rearImage = previewBitmap(color = 0xFF1B1B1F.toInt() + i * 0x00101010),
        frontImage = previewBitmap(width = 200, height = 200, color = 0xFF9BB7D4.toInt() + i * 0x00080808),
        meta = PolaroidMeta(
            date = "2026.01.0${i + 1}",
            role = "엄마",
            comment =  "오늘 사진"
        )
    )
}

@Preview(showBackground = true)
@Composable
fun GridGalleryPreview() {
        GridGallery(
            polaroids = samplePolaroids
        )
}

//임시 앨범
@Composable
fun TempGridGallery(
    polaroids: List<PolaroidData>,
    modifier: Modifier = Modifier
) {
    var isSelectMode by remember { mutableStateOf(false) }
    val selectedItems = remember { mutableStateListOf<Int>() }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = background // 상단에 정의된 배경색
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            // 1. 상단 헤더
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                FillWrapButton(
                    onClick = { */
/* 전체 비우기 로직 *//*
 },
                    text = "전체비우기",
                    modifier = Modifier.align(Alignment.CenterStart)
                )

                FillWrapButton(
                    onClick = {
                        isSelectMode = !isSelectMode
                        if (!isSelectMode) selectedItems.clear()
                    },
                    text = if (isSelectMode) "취소" else "선택",
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            // 2. 그리드 영역 (상태에 따라 스위칭)
            Box(modifier = Modifier.weight(1f)) {
                if (isSelectMode) {
                    // 선택 모드일 때
                    SelectableGalleryGrid(
                        polaroids = polaroids,
                        isSelectMode = true,
                        selectedIds = selectedItems.toList(),
                        onItemClick = { item ->
                            val id = item.rearImage.hashCode()
                            if (selectedItems.contains(id)) selectedItems.remove(id)
                            else selectedItems.add(id)
                        }
                    )
                } else {
                    // 일반 모드일 때 (기존꺼 재활용)
                    GalleryGridPolaroid(
                        polaroids = polaroids,
                        onClick = { */
/* 상세 화면 이동 등 *//*
 }
                    )
                }
            }

            AnimatedVisibility(
                visible = isSelectMode && selectedItems.isNotEmpty(),
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Surface(
                    tonalElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth(),
                    color = lightbackground
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 40.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 삭제 버튼
                        FooterActionItem(
                            icon = LMicons.Delete,
                            text = "삭제",
                            color = Color.Red,
                            onClick = { */
/*ConfirmDeleteDialog-> 삭제*//*
 }
                        )

                        // 저장 버튼
                        FooterActionItem(
                            icon = Icons.Default.Share,
                            text = "저장",
                            color = Color(0xFF89A1F7),
                            onClick = { */
/* 저장 로직 *//*
 }
                        )
                    }
                }
            }

            // 반복되는 푸터 아이템 컴포저블
            @Composable
            fun FooterActionItem(
                icon: ImageVector,
                text: String,
                color: Color,
                onClick: () -> Unit
            ) {
                Column(
                    modifier = Modifier.clickable(onClick = onClick).padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(imageVector = icon, contentDescription = text, tint = color)
                    Text(text = text, style = MaterialTheme.typography.labelSmall, color = color)
                }
            }
                }
            }
        }

@Composable
fun FooterActionItem(icon: ImageVector, text: String, color: Color, onClick: () -> Unit) {
    TODO("Not yet implemented")
}


@Preview(showBackground = true, name = "임시 앨범 - 일반 상태")
@Composable
fun TempGridGalleryNormalPreview() {
    MaterialTheme {
        // 배경색 변수(background)가 정의되어 있지 않다면 Color.White 또는 테마 색상을 사용하세요.
        Surface(color = Color(0xFFFDF7F2)) {
            TempGridGallery(polaroids = samplePolaroids)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760, name = "임시 앨범 - 선택 모드 활성화")
@Composable
fun TempGridGallerySelectModePreview() {
    // 프리뷰에서 강제로 선택 모드를 보여주기 위해
    // 실제 컴포저블의 내부 상태(isSelectMode)를 조절하는 방식 대신,
    // Interactive Mode를 켜서 '선택' 버튼을 눌러보시는 것이 가장 정확합니다.
    MaterialTheme {
        Surface(color = Color(0xFFFDF7F2)) {
            TempGridGallery(polaroids = samplePolaroids)
        }
    }
}

// 하이라이트 날짜 선택 -> 아오 화살표로 이동 왜.
fun Long?.toDay(): String {
    if (this == null) return "-"
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .dayOfMonth
        .toString()
}




@Composable
fun DateRangePickerModal(
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberDateRangePickerState()


    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.98f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            color = Color.White
        ) {
            val calendarTypography = MaterialTheme.typography.copy(
                labelLarge = TextStyle(fontSize = 12.sp), // 요일
                bodyLarge = TextStyle(fontSize = 14.sp)   // 날짜 숫자
            )

            MaterialTheme(typography = calendarTypography) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 상단 타이틀
                    Text(
                        text = "추억의 날짜를 선택해주세요",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    // 2. 캘린더 영역
                    Box(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                        DateRangePicker(
                            state = state,
                            title = {
                                Spacer(modifier = Modifier.height(45.dp))
                            },
                            headline = null,
                            showModeToggle = false,
                            colors = DatePickerDefaults.colors(
                                containerColor = Color.White,
                                selectedDayContainerColor = main,
                                dayInSelectionRangeContainerColor = main.copy(alpha = 0.15f),
                                navigationContentColor = main,
                                weekdayContentColor = Color.Gray
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // 3. 날짜 정보 카드 영역 (시작일 ~ 종료일)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = lightblue
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            DateInfoCard(
                                title = "시작일",
                                date = state.selectedStartDateMillis,
                                modifier = Modifier.weight(1f)
                            )

                            Text(
                                text = "~",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )

                            DateInfoCard(
                                title = "종료일",
                                date = state.selectedEndDateMillis,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // 4. 생성 버튼
                    FilledButton(
                        text = "하이라이트 생성하기",
                        onClick = {
                            onDateRangeSelected(
                                state.selectedStartDateMillis to state.selectedEndDateMillis
                            )
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = state.selectedStartDateMillis != null &&
                            state.selectedEndDateMillis != null,
                        size = ButtonSize.Full
                    )
                }
            }
        }
    }
}

@Composable
fun DateInfoCard(
    title: String,
    date: Long?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = date.toDay(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

// 프리뷰 함수
@Preview(showBackground = true)
@Composable
fun DateRangePickerModalPreview() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            DateRangePickerModal(
                onDateRangeSelected = { },
                onDismiss = { }
            )
        }
    }
}

// 생성 대기 화면
@Composable
private fun LoadingContent(
 ) {
    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = 1),
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


// 하이라이트 완료창
@Composable
fun Highlight(
    uiState: MediaDetailUiState,
    onAction: (MediaDetailAction) -> Unit,
    modifier: Modifier = Modifier,
){
    MediaDetailScreen(
        uiState = uiState,
        onAction = onAction,
        modifier = modifier
    )
}

@Preview(showBackground = true, name = "Video - Paused State", widthDp = 360, heightDp = 760)
@Composable
fun VideoPausedPreview() {
    MaterialTheme {
        val videoFrame = previewBitmap(color = Color.Black.toArgb())
        val frontFrame = previewBitmap(width = 600, height = 600, color = Color(0xFF9BB7D4).toArgb())

        Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
            Highlight(
                uiState = MediaDetailUiState(
                    title = "하이라이트",
                    isVideo = true,
                    isPlaying = false,
                    rearImage = videoFrame,
                    frontImage = frontFrame,
                    meta = PolaroidMeta(
                        date = "2026.01.02",
                        role = "아빠",
                        comment = "아이의 첫 걸음마 순간입니다! 🎥"
                    )
                ),
                onAction = {}
            )
        }
    }
}


*/

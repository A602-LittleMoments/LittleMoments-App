package com.a602.commonproject.feature.home

import android.icu.util.Calendar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.a602.commonproject.designsystem.theme.main
import com.a602.commonproject.feature.home.viewmodel.SlideshowRequest
import com.a602.commonproject.model.data.Collection
import com.a602.commonproject.designsystem.R as DsR
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlideshowCreationDialog(
    collections: List<Collection>, // Available keywords for planet mode
    onConfirm: (SlideshowRequest) -> Unit,
    onDismiss: () -> Unit
) {
    var mode by remember { mutableStateOf(SlideshowMode.KEYWORD) }
    var selectedKeyword by remember { mutableStateOf<String?>(null) }

    // [Fix] Calculate today in UTC to match DatePicker's timestamps
    val maxDateMillis = remember {
        LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }

    val selectableDates = remember(maxDateMillis) {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= maxDateMillis
            }

            override fun isSelectableYear(year: Int): Boolean {
                return true
            }
        }
    }

    // Date Range State
    val dateRangePickerState = rememberDateRangePickerState(
        initialDisplayedMonthMillis = maxDateMillis,
        selectableDates = selectableDates
    )

    // Unique keywords for selection
    val uniqueKeywords = remember(collections) {
        collections.distinctBy { it.keywordId }
    }

    SlideshowCreationDialogContent(
        mode = mode,
        onModeChange = { mode = it },
        selectedKeyword = selectedKeyword,
        onKeywordSelect = { selectedKeyword = it },
        dateRangePickerState = dateRangePickerState,
        uniqueKeywords = uniqueKeywords,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SlideshowCreationDialogContent(
    mode: SlideshowMode,
    onModeChange: (SlideshowMode) -> Unit,
    selectedKeyword: String?,
    onKeywordSelect: (String) -> Unit,
    dateRangePickerState: DateRangePickerState,
    uniqueKeywords: List<Collection>,
    onConfirm: (SlideshowRequest) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .width(340.dp) // Slightly narrower
                    .height(500.dp) // Reduced height
                    .clip(RoundedCornerShape(28.dp)) // Softer corners
                    .background(Color.White)
                    .clickable(enabled = false) {}
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Title
                Text(
                    text = "하이라이트 만들기",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = com.a602.commonproject.designsystem.theme.main
                )
                Spacer(modifier = Modifier.height(4.dp))

                // 2. Description (Fixed Height)
                Box(
                    modifier = Modifier.height(44.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (mode == SlideshowMode.KEYWORD)
                               "어떤 추억 행성으로\n영상을 만들어볼까요?"
                               else "언제부터 언제까지의\n추억을 담아볼까요?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Toggle Button (Compact & Cute)
                Row(
                    modifier = Modifier
                        .width(180.dp)
                        .height(34.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFF0F0F0))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    ToggleOption(
                        text = "행성",
                        isSelected = mode == SlideshowMode.KEYWORD,
                        onClick = { onModeChange(SlideshowMode.KEYWORD) }
                    )
                    ToggleOption(
                        text = "달력",
                        isSelected = mode == SlideshowMode.DATE,
                        onClick = { onModeChange(SlideshowMode.DATE) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Content Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (mode == SlideshowMode.DATE) Color.Transparent else Color(0xFFFAFAFA)) // Subtle bg for planets
                        .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (mode == SlideshowMode.KEYWORD) {
                            // Keyword Selection
                            if (uniqueKeywords.isEmpty()) {
                                Text("생성된 추억 행성이 없습니다.", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                            } else {
                                LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(20.dp),
                                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                    items(uniqueKeywords) { item ->
                                        // [Fix] Use keywordId for logic, keywordValue for display
                                        val isSelected = selectedKeyword == item.keywordId
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .width(110.dp) // 80 -> 110
                                                .clickable { onKeywordSelect(item.keywordId) }
                                                .scaleEffect(isSelected)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(100.dp) // 76 -> 100
                                                    .border(
                                                        width = if (isSelected) 3.dp else 0.dp,
                                                        color = if (isSelected) com.a602.commonproject.designsystem.theme.main else Color.Transparent,
                                                        shape = CircleShape
                                                    )
                                                    .padding(8.dp) // 6 -> 8
                                            ) {
                                                // [Fix] Use global helper to ensure consistency with Main Screen
                                                val planetRes = getPlanetIcon(item)
                                                Image(
                                                    painter = painterResource(id = planetRes),
                                                    contentDescription = null,
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = ContentScale.Fit
                                                )
                                            }
                                        Spacer(modifier = Modifier.height(12.dp)) // 8 -> 12
                                        Text(
                                            text = item.keywordValue,
                                            style = MaterialTheme.typography.bodyMedium.copy( // labelMedium -> bodyMedium
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) com.a602.commonproject.designsystem.theme.main else Color.DarkGray
                                            ),
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Date Picker (Custom Style with Box Scaling)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                // Clip to avoid any overflow drawing over buttons
                                .clip(RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.TopCenter
                        ) {
                             DateRangePicker(
                                 state = dateRangePickerState,
                                 title = null,
                                 headline = null,
                                 showModeToggle = false,
                                 colors = DatePickerDefaults.colors(
                                     containerColor = Color.Transparent,
                                     todayContentColor = main,
                                     selectedDayContainerColor = main,
                                     selectedDayContentColor = Color.White,
                                     dayContentColor = Color.DarkGray,
                                     disabledDayContentColor = Color.LightGray
                                 ),
                                 modifier = Modifier
                                     .requiredWidth(360.dp) // Force width to prevent squashing
                                     .requiredHeight(480.dp) // Force height
                                     .graphicsLayer {
                                         scaleX = 0.9f
                                         scaleY = 0.9f
                                         transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 0f) // Pivot Top
                                         translationY = 200f // [Fix] Less aggressive offset to show current month title
                                     }
                             )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 5. Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                     val isEnabled = if (mode == SlideshowMode.KEYWORD) {
                        selectedKeyword != null
                    } else {
                        dateRangePickerState.selectedStartDateMillis != null &&
                        dateRangePickerState.selectedEndDateMillis != null
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, main),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = main)
                    ) {
                        Text("취소", fontWeight = FontWeight.Medium, color = main)
                    }

                    Button(
                        onClick = {
                            if (mode == SlideshowMode.KEYWORD) {
                                selectedKeyword?.let { id ->
                                    val label = uniqueKeywords.find { it.keywordId == id }?.keywordValue ?: "추억 영상"
                                    onConfirm(SlideshowRequest.ByKeyword(id, label))
                                }
                            } else {
                                val start = dateRangePickerState.selectedStartDateMillis
                                val end = dateRangePickerState.selectedEndDateMillis
                                if (start != null && end != null) {
                                    val formatter = SimpleDateFormat("yyyy.MM.dd") // Locale default is fine or US
                                    val startStr = formatter.format(java.util.Date(start))
                                    val endStr = formatter.format(java.util.Date(end))
                                    val label = "$startStr ~ $endStr"
                                    onConfirm(SlideshowRequest.ByDateRange(start, end, label))
                                }
                            }
                        },
                        enabled = isEnabled,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                         colors = ButtonDefaults.buttonColors(
                            containerColor = main,
                            disabledContainerColor = Color(0xFFEEEEEE),
                            contentColor = Color.White,
                            disabledContentColor = Color.Gray
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text("만들기", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@androidx.compose.ui.tooling.preview.Preview(name = "Slideshow - Keyword Mode")
@Composable
fun Preview_Slideshow_Keyword() {
    val items = listOf(
        Collection("c1", "물건", "k1", "인형", 12),
        Collection("c2", "음식", "k2", "밥", 20),
        Collection("c3", "인물", "k3", "엄마", 30)
    )
    val dateRangePickerState = rememberDateRangePickerState()

    com.a602.commonproject.designsystem.theme.LMTheme {
        SlideshowCreationDialogContent(
            mode = SlideshowMode.KEYWORD,
            onModeChange = {},
            selectedKeyword = "인형",
            onKeywordSelect = {},
            dateRangePickerState = dateRangePickerState,
            uniqueKeywords = items,
            onConfirm = {},
            onDismiss = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@androidx.compose.ui.tooling.preview.Preview(name = "Slideshow - Date Mode")
@Composable
fun Preview_Slideshow_Date() {
    val dateRangePickerState = rememberDateRangePickerState()

    com.a602.commonproject.designsystem.theme.LMTheme {
        SlideshowCreationDialogContent(
            mode = SlideshowMode.DATE,
            onModeChange = {},
            selectedKeyword = null,
            onKeywordSelect = {},
            dateRangePickerState = dateRangePickerState,
            uniqueKeywords = emptyList(),
            onConfirm = {},
            onDismiss = {}
        )
    }
}

// Custom Toggle Option Component
@Composable
fun RowScope.ToggleOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(RoundedCornerShape(50))
            .background(if (isSelected) main else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = if (isSelected) Color.White else Color.Gray
        )
    }
}

@Composable
fun Modifier.scaleEffect(isSelected: Boolean): Modifier {
    return this // Placeholder for potential animation, currently just return this
}

enum class SlideshowMode {
    KEYWORD, DATE
}

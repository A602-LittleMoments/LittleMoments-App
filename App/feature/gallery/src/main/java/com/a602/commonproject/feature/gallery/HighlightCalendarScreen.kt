package com.a602.commonproject.feature.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.a602.commonproject.designsystem.component.ButtonSize
import com.a602.commonproject.designsystem.component.FilledButton
import com.a602.commonproject.designsystem.theme.lightblue
import com.a602.commonproject.designsystem.theme.main
import com.a602.commonproject.feature.gallery.viewmodel.HighlightCalendarViewModel
import java.time.Instant
import java.time.ZoneId
import java.util.Calendar

fun Long?.toDay(): String {
    if (this == null) return "-"
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .dayOfMonth
        .toString()
}


@Composable
fun HighlightCalendarRoute(
    onDateRangeSelected: (Long, Long) -> Unit,
    onBack: () -> Unit,
    viewModel: HighlightCalendarViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HighlightCalendarScreen(
        onDateRangeSelected = { start, end ->
            viewModel.selectDateRange(start, end)
            onDateRangeSelected(start, end)
        },
        onBack = {
            viewModel.clearSelection()
            onBack()
        }
    )
}
@Composable
fun HighlightCalendarScreen(
    onDateRangeSelected: (Long, Long) -> Unit,
    onBack: () -> Unit
) {
    DateRangePickerModal(
        onDateRangeSelected = { start, end ->
            onDateRangeSelected(start, end)
        },
        onDismiss = onBack
    )
}

@Composable
fun DateRangePickerModal(
    onDateRangeSelected: (Long, Long) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberDateRangePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val today = System.currentTimeMillis()
                return utcTimeMillis <= today
            }
        }
    )

    // 현재 표시 중인 월의 텍스트
    val currentMonthText = remember(state.displayedMonthMillis) {
        val instant = Instant.ofEpochMilli(state.displayedMonthMillis)
        val date = instant.atZone(ZoneId.systemDefault())
        "${date.year}년 ${date.monthValue}월"
    }

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
            Box {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "닫기",
                        tint = Color.Gray
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(top = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "추억의 날짜를 선택해주세요",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 이전 달 버튼
                        IconButton(
                            onClick = {
                                val calendar = Calendar.getInstance().apply {
                                    timeInMillis = state.displayedMonthMillis
                                    add(Calendar.MONTH, -1)
                                }
                                state.displayedMonthMillis = calendar.timeInMillis
                            }
                        ) {
                            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "이전달", tint = main)
                        }

                        Text(
                            text = currentMonthText,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )

                        // 다음 달 버튼
                        IconButton(
                            onClick = {
                                val calendar = Calendar.getInstance().apply {
                                    timeInMillis = state.displayedMonthMillis
                                    add(Calendar.MONTH, 1)
                                }
                                state.displayedMonthMillis = calendar.timeInMillis
                            }
                        ) {
                            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "다음달", tint = main)
                        }
                    }

                    // 2. 캘린더 영역
                    Box(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                        DateRangePicker(
                            state = state,
                            title = null,
                            headline = null,
                            showModeToggle = false,
                            colors = DatePickerDefaults.colors(
                                containerColor = Color.White,
                                selectedDayContainerColor = main,
                                dayInSelectionRangeContainerColor = main.copy(alpha = 0.15f),
                                weekdayContentColor = Color.Gray
                            ),
                            modifier = Modifier
                                .fillMaxWidth()

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
                        enabled = state.selectedStartDateMillis != null &&
                            state.selectedEndDateMillis != null,
                        onClick = {
                            val start = state.selectedStartDateMillis ?: return@FilledButton
                            val end = state.selectedEndDateMillis ?: return@FilledButton

                            onDismiss()
                            onDateRangeSelected(start, end)
                        },
                        modifier = Modifier.fillMaxWidth(),
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
                onDateRangeSelected = { _, _ -> },
                onDismiss = { }
            )
        }
    }
}


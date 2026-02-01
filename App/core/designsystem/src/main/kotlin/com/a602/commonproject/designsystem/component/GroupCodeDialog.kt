package com.a602.commonproject.designsystem.component

import android.graphics.Color
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.a602.commonproject.designsystem.R
import com.a602.commonproject.designsystem.theme.*
import com.a602.commonproject.designsystem.icon.LMicons
import kotlinx.coroutines.delay

/**
 * 그룹 초대 코드를 보여주고 타이머를 관리하는 팝업입니다.
 *
 */
@Composable
fun GroupCodeDialog(
    onDismissRequest: () -> Unit, // 닫기 버튼 클릭 시 동작
    onRefreshClick: () -> Unit,   // 새로고침 버튼 클릭 시 부모에게 알림
    initialCode: String = "572999", // 처음 보여줄 코드
    initialSeconds: Int = 180,      // 초기 타이머 시간 (3분)
) {
    // 1. 상태 관리: 코드 번호와 남은 시간을 remember로 기억합니다.
    var currentCode by remember { mutableStateOf(initialCode) }
    var remainingSeconds by remember { mutableStateOf(initialSeconds) }

    // 2. 타이머 엔진: 1초마다 숫자를 줄입니다.
    LaunchedEffect(key1 = remainingSeconds) {
        if (remainingSeconds > 0) {
            delay(1000L) // 1초 대기
            remainingSeconds -= 1 // 1초 차감
        }
    }

    // 3. 시간 포맷팅: 초 단위를 "00:00" 형식으로 바꿉니다.
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timerText = "%02d:%02d".format(minutes, seconds)

    // 아이콘 위에 위치시키기 위해
    val badgeSize = 56.dp
    val badgeRadius = badgeSize / 2
    val shape = RoundedCornerShape(24.dp)

    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            modifier = Modifier.padding(top = 14.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                modifier = Modifier
                    .padding(top = badgeRadius/2)
                    .widthIn(min = 328.dp)
                    .wrapContentHeight(),
                shape = shape,
                colors = CardDefaults.cardColors(containerColor = lightbackground),
                border = BorderStroke(width = 4.dp, color = color2)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    // 우측 상단 X 버튼
                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                    ) {
                        Icon(imageVector = LMicons.Close, contentDescription = "닫기", tint = color4)
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(horizontal = 24.dp, vertical = 32.dp)
                            .padding(top = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "그룹 코드",
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                            )
                        Spacer(modifier = Modifier.height(20.dp))

                        // 4. 6자리 코드 표시 컴포넌트 호출
                        CodeDisplay(code = currentCode)

                        Spacer(modifier = Modifier.height(20.dp))

                        // 5. 실시간 타이머 표시 컴포넌트 호출
                        TimerDisplay(time = timerText)

                        Spacer(modifier = Modifier.height(20.dp))

                        // 하단 안내 메시지 영역
                        Text(
                            text = "이 코드를 초대할 구성원에게 공유하세요",
                            style = MaterialTheme.typography.labelMedium,
                            color = color4,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = color2.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp))
                                .border(width = 1.dp, color = color2.copy(alpha = 0.7f), shape = RoundedCornerShape(12.dp))
                                .padding(vertical = 12.dp),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 6. 새로고침 버튼: 클릭 시 번호 랜덤 생성 및 시간 초기화
                        FilledButton(
                            text = "새로고침",
                            onClick = {
                                // 0~999999 사이 숫자를 생성 후 앞자리를 '0'으로 채워 6자리 유지
                                val randomCode = (0..999999).random().toString().padStart(6, '0')
                                currentCode = randomCode // 코드 상태 업데이트
                                remainingSeconds = initialSeconds // 시간 리셋
                                onRefreshClick() // 부모 컴포넌트 로직 실행
                            },
                            modifier = Modifier.fillMaxWidth(),
                            size = ButtonSize.Medium,
                            leadingIcon = Icons.Outlined.Refresh
                        )
                    }
                }
            }
            // 상단 아이콘 박스
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = - (badgeRadius / 2))
                    .clip(CircleShape)
                    .background(color = color2),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.groupdialog),
                    contentDescription = "그룹 코드 아이콘",
                    tint = lightbackground,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

/**
 * 6자리 숫자를 각각의 박스에 담아 보여주는 내부 컴포넌트입니다.
 *
 */
@Composable
private fun CodeDisplay(code: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        code.forEach { char ->
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .border(width = 1.dp, color = color4, shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = char.toString(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        lineHeight = TextUnit.Unspecified,
                        lineHeightStyle = null,
                        letterSpacing = 0.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * 빨간색 타이머 아이콘과 시간을 보여주는 내부 컴포넌트입니다.
 *
 */
@Composable
private fun TimerDisplay(time: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Timer,
            contentDescription = "타이머 아이콘",
            tint = errorRed,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = time, style = MaterialTheme.typography.labelLarge, color = errorRed)
    }
}

@Preview(showBackground = true, widthDp = 411, heightDp = 520)
@Composable
fun GroupCodeDialogPreview() {
    LMTheme {
        GroupCodeDialog(
            onDismissRequest = {},
            onRefreshClick = {}
        )
    }
}

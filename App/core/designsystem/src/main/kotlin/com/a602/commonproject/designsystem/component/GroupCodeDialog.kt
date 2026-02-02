package com.a602.commonproject.designsystem.component

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.a602.commonproject.designsystem.R
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.*
import kotlinx.coroutines.delay

/**
 * 그룹 초대 코드를 보여주고 타이머를 관리하는 팝업입니다.
 *
 */
@Composable
fun GroupCodeDialog(
    onDismissRequest: () -> Unit,
    onRefreshClick: () -> Unit,
    initialCode: String,
    initialSeconds: Int = 180,
) {
    var remainingSeconds by remember { mutableStateOf(initialSeconds) }
    val context = LocalContext.current

    // 부모로부터 새로운 코드나 시간이 내려올 때마다 타이머를 리셋합니다.
    LaunchedEffect(key1 = initialCode, key2 = initialSeconds) {
        remainingSeconds = initialSeconds
    }

    // 1초마다 타이머를 감소시킵니다.
    LaunchedEffect(key1 = remainingSeconds) {
        if (remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds -= 1
        }
    }

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timerText = "%02d:%02d".format(minutes, seconds)
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
                    .padding(top = badgeRadius / 2)
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

                        CodeDisplay(code = initialCode)

                        Spacer(modifier = Modifier.height(20.dp))

                        // 5. 실시간 타이머 표시 컴포넌트 호출
                        TimerDisplay(time = timerText)

                        Spacer(modifier = Modifier.height(20.dp))

                        // 하단 안내 메시지 및 복사 버튼 영역
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = color2.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp))
                                .border(width = 1.dp, color = color2.copy(alpha = 0.7f), shape = RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 4.dp), // 내부 패딩 조정
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "이 코드를 초대할 구성원에게 공유하세요",
                                style = MaterialTheme.typography.labelMedium,
                                color = color4,
                                modifier = Modifier.weight(1f) // 텍스트가 남은 공간을 채우도록
                            )
                            IconButton(onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Invite Code", initialCode)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "코드가 복사되었습니다.", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(
                                    imageVector = Icons.Outlined.ContentCopy,
                                    contentDescription = "복사하기",
                                    tint = color3
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 6. 새로고침 버튼: 클릭 시 번호 랜덤 생성 및 시간 초기화
                        FilledButton(
                            text = "새로고침",
                            onClick = onRefreshClick, // ViewModel에 코드 재요청을 위임
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
                    .offset(y = -(badgeRadius / 2))
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
        horizontalArrangement = Arrangement.spacedBy(8.dp) // 칸 사이 간격 유지
    ) {
        repeat(6) { index ->
            val char = code.getOrNull(index)?.toString() ?: ""
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .border(width = 1.dp, color = color4, shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = char,
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

@Preview(showBackground = true)
@Composable
fun GroupCodeDialogPreview() {
    LMTheme {
        GroupCodeDialog(
            onDismissRequest = {},
            onRefreshClick = {},
            initialCode = "A1B2C3"
        )
    }
}

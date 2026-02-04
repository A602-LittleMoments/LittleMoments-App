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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = OffWhite,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "초대 코드",
                        style = AppTypography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NavyBlue,
                            fontSize = 24.sp
                        ),
                        modifier = Modifier.align(Alignment.Center)
                    )
                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .offset(x = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "닫기",
                            tint = NavyBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 코드 표시 영역
                CodeDisplay(code = initialCode)

                Spacer(modifier = Modifier.height(20.dp))

                // 타이머
                TimerDisplay(time = timerText)

                Spacer(modifier = Modifier.height(24.dp))

                // 안내 및 복사
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyBlue.copy(alpha = 0.05f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "그룹원에게 코드를 공유하세요",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
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
                                tint = NavyBlue
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 새로고침 버튼
                Button(
                    onClick = onRefreshClick,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NavyBlue
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "새로고침",
                            style = AppTypography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
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
        horizontalArrangement = Arrangement.spacedBy(2.dp) // 칸 사이 간격 유지
    ) {
        repeat(6) { index ->
            val char = code.getOrNull(index)?.toString() ?: ""
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .border(width = 1.dp, color = NavyBlue, shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = char,
                    style = AppTypography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = NavyBlue
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

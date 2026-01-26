package com.a602.commonproject.designsystem.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.theme.main
import com.a602.commonproject.designsystem.theme.lightbackground
import androidx.compose.ui.tooling.preview.Preview
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color3


/**
 * - 기본: 파란 배경 + 흰 글씨 + pill
 * - 크기: full(로그인/추가) / medium(그룹 생성/참여) / small(캘린더보기) / round(선택) / counter(저장(갯수)) / outline(남/여)
 */
enum class ButtonSize(
    val height: Dp,
    val horizontalPadding: Dp,
    val shape: RoundedCornerShape,
) {
    Full(height = 56.dp, horizontalPadding = 24.dp, shape = RoundedCornerShape(20.dp)),   // 로그인/추가
    Medium(height = 44.dp, horizontalPadding = 20.dp, shape = RoundedCornerShape(12.dp)), // 그룹 참여/생성
    Small(height = 36.dp, horizontalPadding = 16.dp, shape = RoundedCornerShape(18.dp)),  // 캘린더 보기
    Round(height = 44.dp, horizontalPadding = 18.dp, shape = RoundedCornerShape(50.dp)),  // 선택(동글)
    Counter(height = 44.dp, horizontalPadding = 20.dp, shape = RoundedCornerShape(22.dp)) // 저장(3)
}

/** 기본 버튼 */
@Composable
fun FilledButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: ButtonSize = ButtonSize.Medium,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(size.height),
        shape = size.shape,
        contentPadding = PaddingValues(horizontal = size.horizontalPadding),
        colors = ButtonDefaults.buttonColors(
            containerColor = main,
            contentColor = lightbackground,
            disabledContainerColor = main.copy(alpha = 0.35f),
            disabledContentColor = lightbackground.copy(alpha = 0.7f),
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (leadingIcon != null) {
                Icon(leadingIcon, contentDescription = null)
                Spacer(Modifier.width(8.dp))
            }

            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = lightbackground
            )

            if (trailingIcon != null) {
                Spacer(Modifier.width(8.dp))
                Icon(trailingIcon, contentDescription = null, tint = lightbackground)
            }
        }
    }
}

// 글자 수 맞춰서 버튼 크기 조정
@Composable
fun FillWrapButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(36.dp), // 캡쳐 기준 small 느낌 (원하면 32.dp로 더 줄이기)
        shape = RoundedCornerShape(18.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = main,
            contentColor = lightbackground,
            disabledContainerColor = main.copy(alpha = 0.35f),
            disabledContentColor = lightbackground.copy(alpha = 0.7f),
        ),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Icon(leadingIcon, contentDescription = null, tint = lightbackground)
                Spacer(Modifier.width(6.dp))
            }

            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = lightbackground
            )

            if (trailingIcon != null) {
                Spacer(Modifier.width(6.dp))
                Icon(trailingIcon, contentDescription = null, tint = lightbackground)
            }
        }
    }
}


/** "선택" 처럼 살짝 떠 있는 느낌이 있는 버튼 */
@Composable
fun ElevatedRoundButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    ElevatedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(ButtonSize.Round.height),
        shape = ButtonSize.Round.shape,
        contentPadding = PaddingValues(horizontal = ButtonSize.Round.horizontalPadding),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = main,
            contentColor = lightbackground,
            disabledContainerColor = main.copy(alpha = 0.35f),
            disabledContentColor = lightbackground.copy(alpha = 0.7f),
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp,
            disabledElevation = 0.dp,
        )
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge, color = lightbackground)
    }
}

/** "오늘의 추억 남기기 + 카메라 아이콘" */
@Composable
fun CameraButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String = "오늘의 추억 남기기",
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(44.dp).wrapContentWidth(),
        shape = RoundedCornerShape(22.dp),
        contentPadding = PaddingValues(horizontal = 40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = main,
            contentColor = lightbackground,
            disabledContainerColor = main.copy(alpha = 0.35f),
            disabledContentColor = lightbackground.copy(alpha = 0.7f),
        )
    ) {
        // 🔑 핵심: Box로 중앙 정렬을 강제
        Box(
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = LMicons.Camera,
                    contentDescription = null,
                    tint = lightbackground,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    color = lightbackground
                )
            }
        }
    }
}

// 사진 저장 버튼
@Composable
fun SaveButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    saveIcon: ImageVector? = null,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(50.dp).fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        contentPadding = PaddingValues(horizontal = 40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = background,
            contentColor = color3,
            disabledContainerColor = main.copy(alpha = 0.35f),
            disabledContentColor = lightbackground.copy(alpha = 0.7f),
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = LMicons.Download,
                    contentDescription = null,
                    tint = color3,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "저장하기",
                    style = MaterialTheme.typography.labelLarge,
                    color = color3
                )
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun SaveButtonPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 기본 버튼
        SaveButton(
            onClick = {}
        )

    }
}

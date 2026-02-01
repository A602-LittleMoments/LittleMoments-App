package com.a602.commonproject.designsystem.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.theme.main
import com.a602.commonproject.designsystem.theme.lightbackground
import androidx.compose.ui.tooling.preview.Preview
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color3


/**
 * - 기본: 파란 배경 + 흰 글씨 + pill
 * - 크기(세로): full(로그인/추가) / medium(그룹 생성/참여) / small(캘린더보기) / round(선택) / counter(저장(갯수)) / outline(남/여)
 * 옆에 프리뷰 보고 비슷한 걸로 쓰세요, 가로 길이는 조정 가능, 조정 법 밑에 있음
 */
enum class ButtonSize(
    val height: Dp,
    val widthFraction: Float,
    val maxWidth: Dp,
    val horizontalPadding: Dp,
    val shape: RoundedCornerShape,
) {
    // 추가 / 수정 완료
    Full(48.dp, 0.9f, 350.dp, 24.dp, RoundedCornerShape(16.dp)),
    // 모달 or 팝업에서 쓰는 버튼
    Medium(48.dp, 0.8f, 300.dp,20.dp, RoundedCornerShape(16.dp)),
    // 그룹 생성 or 참여
    Small(44.dp, 0.6f, 192.dp, 16.dp, RoundedCornerShape(14.dp)),
    // 로그인 (동글)
    Round(44.dp, 0.9f, 350.dp, 18.dp, RoundedCornerShape(22.dp)),
}

/* FilledButton
 * ButtonSize.Full - 회원정보 수정, 추가, 그룹관리 등
 * ButtonSize.Medium - 팝업, 모달
 * ButtonSize.Small - 회원가입시 그룹 생성, 그룹 코드를 이용하여 참여
 * ButtonSize Round - 로그인, 비밀번호 변경, 회원가입
 * */

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
        modifier = modifier
            .fillMaxWidth(size.widthFraction)    // 부모 (모달 포함) 폭 기준 비율
            .widthIn(max = size.maxWidth)        // 너무 커지지 않게 상한
            .height(size.height),               // 높이
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
                maxLines = 1, // 좁아질 때 줄바꿈 대신 ...로 표시
                overflow = TextOverflow.Ellipsis
            )

            if (trailingIcon != null) {
                Spacer(Modifier.width(8.dp))
                Icon(trailingIcon, contentDescription = null, tint = lightbackground)
            }
        }
    }
}

// 화면들에서 쓰게 될 버튼
// 글자 수 맞춰서 버튼 크기 (가로) 조정
// 선택, 전체 비우기 등
@Composable
fun FillWrapButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,

    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = main,
        contentColor = lightbackground,
        disabledContainerColor = main.copy(alpha = 0.35f),
        disabledContentColor = lightbackground.copy(alpha = 0.7f),
    ),
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(44.dp).defaultMinSize(minWidth = 80.dp),
        shape = RoundedCornerShape(22.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        colors = colors,
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
        modifier = modifier.height(44.dp).defaultMinSize(minWidth = 188.dp),
        shape = RoundedCornerShape(22.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = main,
            contentColor = lightbackground,
            disabledContainerColor = main.copy(alpha = 0.35f),
            disabledContentColor = lightbackground.copy(alpha = 0.7f),
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,   // 평소 떠 있는 높이 (FAB 기본값과 비슷)
            pressedElevation = 12.dp,  // 눌렀을 때 더 깊이 들어가는 느낌
            disabledElevation = 0.dp
        )
    ) {
        // 🔑 핵심: Box로 중앙 정렬을 강제
        Box(
            modifier = Modifier.fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
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
                )
            }
        }
    }
}

// 하이라이트(슬라이드쇼) 저장 버튼
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
        modifier = modifier.height(48.dp).fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        contentPadding = PaddingValues(horizontal = 40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = lightbackground,
            contentColor = color3,
            disabledContainerColor = lightbackground.copy(alpha = 0.35f),
            disabledContentColor = color3.copy(alpha = 0.7f),
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


/** 프리뷰 */
@Preview(showBackground = true, widthDp = 411)
@Composable
fun ButtonShowcasePreview() {
    LMTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // --- FilledButton ---
            FilledButton(
                text = "추가",
                onClick = {},
                size = ButtonSize.Full,
            )

            FilledButton(
                text = "수정 완료",
                onClick = {},
                size = ButtonSize.Full,
            )

            FilledButton(
                text = "생성하기",
                onClick = {},
                size = ButtonSize.Medium,
                modifier = Modifier.width(200.dp)
            )

            FilledButton(
                text = "그룹 생성",
                onClick = {},
                size = ButtonSize.Small,
                modifier = Modifier.width(200.dp)
            )

            FilledButton(
                text = "로그인",
                onClick = {},
                size = ButtonSize.Round,
            )

            // --- FillWrapButton ---
            FillWrapButton(
                text = "선택",
                onClick = {}
            )

            FillWrapButton(
                text = "전체 비우기",
                onClick = {}
            )

            // --- CameraButton ---
            CameraButton(
                onClick = {}
            )

            // --- SaveButton ---
            SaveButton(
                onClick = {}
            )
        }
    }
}

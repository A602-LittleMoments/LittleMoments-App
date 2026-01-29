package com.a602.commonproject.designsystem.component

import androidx.compose.foundation.layout.*
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
import com.a602.commonproject.designsystem.theme.NiaTheme
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color3


/**
 * - 기본: 파란 배경 + 흰 글씨 + pill
 * - 크기(세로): full(로그인/추가) / medium(그룹 생성/참여) / small(캘린더보기) / round(선택) / counter(저장(갯수)) / outline(남/여)
 * 옆에 프리뷰 보고 비슷한 걸로 쓰세요, 가로 길이는 조정 가능, 조정 법 밑에 있음
 */
enum class ButtonSize(
    val height: Dp,
    val horizontalPadding: Dp,
    val shape: RoundedCornerShape,
) {
    // 로그인/추가
    Full(height = 56.dp, horizontalPadding = 24.dp, shape = RoundedCornerShape(20.dp)),
    // 그룹 참여/생성
    Medium(height = 44.dp, horizontalPadding = 20.dp, shape = RoundedCornerShape(12.dp)),
    // 캘린더 보기
    Small(height = 36.dp, horizontalPadding = 16.dp, shape = RoundedCornerShape(18.dp)),
    // 선택(동글)
    Round(height = 44.dp, horizontalPadding = 18.dp, shape = RoundedCornerShape(50.dp)),
    // 저장(3)
    Counter(height = 44.dp, horizontalPadding = 20.dp, shape = RoundedCornerShape(22.dp))
}

/** 회원가입 쪽에서 쓰는 FilledButton */
// size는 위에 설정해놓은 것들이고, 높이 조정 가능
// 가로 길이 조정하려면 modifier로 조정 가능, 아래 예시
// 꽉 차게
//modifier = Modifier.fillMaxWidth()
//size = ButtonSize.Full
// 가로 크기 조정
//FilledButton(
//text = "확인",
//onClick = {},
//modifier = Modifier.width(200.dp)
//)
// 가로 크기 범위로 조정
//FilledButton(
//text = "완료",
//onClick = {},
//modifier = Modifier.widthIn(min = 160.dp, max = 240.dp)
//)


// 로그인 (size small, 가로 길이는 적용해보고 사용)
// 그룹참여, 추가 이런건 size Medium이나 Full 사용하면 될 것 같음
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
        modifier = modifier.then(Modifier.height(size.height)),
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
                color = lightbackground,
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
// FilledButton 보다 더 둥근 버튼
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


/** 프리뷰 */
@Preview(showBackground = true, widthDp = 411)
@Composable
fun ButtonShowcasePreview() {
    NiaTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // --- FilledButton ---
            FilledButton(
                text = "Full Button",
                onClick = {},
                size = ButtonSize.Full,
                modifier = Modifier.width(200.dp)
            )

            FilledButton(
                text = "Medium Button",
                onClick = {},
                size = ButtonSize.Medium,
                modifier = Modifier.width(200.dp)
            )

            FilledButton(
                text = "Small Button",
                onClick = {},
                size = ButtonSize.Small,
                modifier = Modifier.width(200.dp)
            )

            FilledButton(
                text = "Disabled Button",
                onClick = {},
                enabled = false
            )

            // --- FillWrapButton ---
            FillWrapButton(
                text = "짧은 텍스트",
                onClick = {}
            )

            FillWrapButton(
                text = "텍스트가 조금 긴 버튼",
                onClick = {}
            )

            // --- ElevatedRoundButton ---
            ElevatedRoundButton(
                text = "선택",
                onClick = {}
            )

            ElevatedRoundButton(
                text = "비활성 선택",
                onClick = {},
                enabled = false
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

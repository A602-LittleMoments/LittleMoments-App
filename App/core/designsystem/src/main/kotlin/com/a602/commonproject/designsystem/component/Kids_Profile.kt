package com.a602.commonproject.designsystem.component

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.color5
import com.a602.commonproject.designsystem.theme.gray2
import com.a602.commonproject.designsystem.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.offset

//Preview용
import androidx.compose.material3.Surface
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.padding
import com.a602.commonproject.designsystem.theme.PointYellow
import com.a602.commonproject.designsystem.theme.lightbackground


/**
 * ProfileHead
 *
 * ✔ 아기 프로필 "얼굴" 전용 컴포넌트
 * ✔ 서버 이미지(URL)와 로컬 이미지(Uri)를 동시에 지원
 * ✔ 클릭 가능/불가능 분리 가능 (메인 vs 마이페이지)
 *
 * [이미지 표시 우선순위]
 * 1️ selectedImageUri (사용자가 새로 선택한 사진)
 * 2 remoteImageUrl  (서버에 저장된 프로필 사진)
 * 3️ 아무것도 없으면 카메라 아이콘 표시
 */
@Composable
fun ProfileHead(
    selectedImageUri: Uri?,          // 새로 선택한 로컬 이미지 (편집 시 사용)
    remoteImageUrl: String?,          // 서버에서 내려온 프로필 이미지 URL
    modifier: Modifier = Modifier,

    // --- 외형 설정 ---
    size: Dp = 154.dp,               // 얼굴 크기
    borderWidth: Dp = 10.dp,          // 테두리 두께
    borderColor: Color = PointYellow,      // 테두리 색
    placeholderColor: Color = gray2,  // 이미지 없을 때 배경색

    // --- 상호작용 제어 ---
    clickableEnabled: Boolean = false,// 클릭 가능 여부 (마이페이지 true)
    onClick: (() -> Unit)? = null,    // 클릭 시 동작 (사진 선택)
    showEditBadge: Boolean = false,   // 편집 가능 상태일 때 카메라 배지 표시
    badgeSize: Dp = 34.dp,            // 배지 크기
) {
    // ✔ Uri가 있으면 Uri, 없으면 URL 사용
    // AsyncImage는 Uri/String 둘 다 지원
    val model: Any? = selectedImageUri ?: remoteImageUrl

    // ✔ 클릭 가능 여부에 따라 Modifier 분기
    val clickModifier =
        if (clickableEnabled && onClick != null) {
            Modifier.clickable(onClick = onClick)
        } else {
            Modifier
        }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(placeholderColor)
            .border(BorderStroke(borderWidth, borderColor), CircleShape)
            .then(clickModifier),
        contentAlignment = Alignment.Center
    ) {

        // ---------- 이미지 영역 ----------
        if (model != null) {
            // 서버 URL 또는 로컬 Uri 이미지
            AsyncImage(
                model = model,
                contentDescription = "profile head",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            // 이미지 없을 때 기본 카메라 아이콘
            Icon(
                imageVector = LMicons.Camera,
                contentDescription = "add profile photo",
                tint = lightbackground,
                modifier = Modifier.size(40.dp)
            )
        }

        // ---------- 편집 배지 (마이페이지 전용) ----------
        if (clickableEnabled && showEditBadge) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(badgeSize)
                    .clip(CircleShape)
                    .background(borderColor)
                    .border(
                        BorderStroke(2.dp, lightbackground),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = LMicons.Camera,
                    contentDescription = "edit profile",
                    tint = lightbackground,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}


/**
 * ProfileFullAstronaut
 *
 * ✔ ProfileHead + 우주복 이미지를 합친 풀바디 프로필
 * ✔ 얼굴은 ProfileHead를 그대로 재사용
 * ✔ 클릭/편집 로직도 동일하게 전달
 *
 * 주 사용처:
 * - 메인 화면 캐릭터 표현
 * - 마이페이지 프로필 커스터마이징
 *
 * 메인페이지에서 사용할 때 (읽기 전용)
 * ProfileFullAstronaut(
 *     selectedImageUri = null,
 *     remoteImageUrl = baby.imageUrl,
 *     clickableEnabled = false
 * )
 *
 * 마이페이지에서 사용할 때 (사진 편집 가능)
 * ProfileHead(
 *     selectedImageUri = selectedUri,
 *     remoteImageUrl = baby.imageUrl,
 *     clickableEnabled = true,
 *     onClick = { /* 갤러리 열기 */ },
 *     showEditBadge = true
 * )
 *
 *
 */
@Composable
fun ProfileFullAstronaut(
    selectedImageUri: Uri?,
    remoteImageUrl: String?,
    modifier: Modifier = Modifier,

    // --- 얼굴/몸 크기 설정 ---
    headSize: Dp = 154.dp,            // 얼굴 크기
    bodyResId: Int = R.drawable.astronaut_body_2,
    bodyWidth: Dp = 90.dp,           // 우주복 이미지 크기
    bodyOffsetY: Dp = 134.dp,         // 얼굴 기준 Y 오프셋

    // --- 상호작용 제어 ---
    clickableEnabled: Boolean = false,
    onClick: (() -> Unit)? = null,
    showEditBadge: Boolean = false,
) {
    // ✔ 우주복이 얼굴 아래로 튀어나오는 만큼 하단 여백 확보
    val extraBottom =
        if (bodyOffsetY + bodyWidth > headSize) {
            bodyOffsetY + bodyWidth - headSize
        } else {
            0.dp
        }

    Box(
        modifier = modifier.padding(bottom = extraBottom),
        contentAlignment = Alignment.TopCenter
    ) {

        // ---------- 얼굴 ----------
        ProfileHead(
            selectedImageUri = selectedImageUri,
            remoteImageUrl = remoteImageUrl,
            size = headSize,
            clickableEnabled = clickableEnabled,
            onClick = onClick,
            showEditBadge = showEditBadge
        )

        // ---------- 우주복 ----------
        Image(
            painter = painterResource(bodyResId),
            contentDescription = "astronaut body",
            modifier = Modifier
                .offset(y = bodyOffsetY)
                .size(bodyWidth),
            contentScale = ContentScale.Fit
        )
    }
}


// ---------- 프리뷰 ----------
@Preview(
    showBackground = true,
    backgroundColor = 0xFFF6F1E8,
    widthDp = 411,
    heightDp = 1000
)
@Composable
private fun Preview_ProfileHead_Showcase() {
    Surface {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1️ 메인 페이지 - 이미지 없음 (클릭 불가)
            ProfileHead(
                selectedImageUri = null,
                remoteImageUrl = null,
                clickableEnabled = false
            )

            // 2⃣ 메인 페이지 - 서버 이미지 있음 (클릭 불가)
            ProfileHead(
                selectedImageUri = null,
                remoteImageUrl = "https://example.com/baby.png",
                clickableEnabled = false
            )

            // 3️ 마이페이지 - 서버 이미지 있음 (클릭 가능 + 배지)
            ProfileHead(
                selectedImageUri = null,
                remoteImageUrl = "https://example.com/baby.png",
                clickableEnabled = true,
                onClick = {},
                showEditBadge = true
            )

            // 4 마이페이지 - 로컬 Uri 우선 (수정 중 상태)
            ProfileHead(
                selectedImageUri = Uri.parse("content://local/fake"),
                remoteImageUrl = "https://example.com/baby.png",
                clickableEnabled = true,
                onClick = {},
                showEditBadge = true
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFF6F1E8,
    widthDp = 411,
    heightDp = 1500
)
@Composable
private fun Preview_ProfileFullAstronaut_Showcase() {
    Surface {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1️ 메인 페이지 - 이미지 없음 (클릭 불가)
            ProfileFullAstronaut(
                selectedImageUri = null,
                remoteImageUrl = null,
                clickableEnabled = false
            )

            // 2️ 메인 페이지 - 서버 이미지 있음 (클릭 불가)
            ProfileFullAstronaut(
                selectedImageUri = null,
                remoteImageUrl = "https://example.com/baby.png",
                clickableEnabled = false
            )

            // 3️ 마이페이지 - 서버 이미지 있음 (클릭 가능 + 배지)
            ProfileFullAstronaut(
                selectedImageUri = null,
                remoteImageUrl = "https://example.com/baby.png",
                clickableEnabled = true,
                onClick = {},
                showEditBadge = true
            )

            // 4️ 마이페이지 - 로컬 Uri 우선 (수정 중)
            ProfileFullAstronaut(
                selectedImageUri = Uri.parse("content://local/fake"),
                remoteImageUrl = "https://example.com/baby.png",
                clickableEnabled = true,
                onClick = {},
                showEditBadge = true
            )
        }
    }
}

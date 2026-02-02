package com.a602.commonproject.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.AppTypography
import com.a602.commonproject.designsystem.theme.color3
import com.a602.commonproject.designsystem.theme.lightbackground
import java.time.format.TextStyle

/**
 * @param selected 이 항목이 선택된 상태인지 여부를 나타냅니다.
 * @param onClick 이 항목이 선택되었을 때 호출되는 콜백 함수입니다.
 * @param icon 기본 아이콘으로 표시될 컴포저블 콘텐츠입니다.
 * @param modifier 이 항목에 적용할 Modifier입니다.
 * @param enabled 이 항목의 활성화 상태를 제어합니다. `false`인 경우 클릭할 수 없으며,
 * 접근성 서비스에서도 비활성화된 항목으로 표시됩니다.
 * @param label 항목에 표시될 텍스트 라벨 컴포저블 콘텐츠입니다.
 * 이 항목이 선택되었을 때만 라벨이 표시됩니다.
 */

@Composable
fun RowScope.LMNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    alwaysShowLabel: Boolean = true,
    icon: @Composable () -> Unit,
    label: String? = null,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        label = null,
        // [해결 핵심 2] 아이콘 슬롯 안에 '아이콘 + 간격 + 라벨'을 묶어서 넣습니다.
        icon = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                // [해결 핵심 3] 여기서 top 패딩을 조절하여 아이템 전체를 아래로 내립니다.
                // 10dp~12dp 정도가 적당하며, UI를 보며 조절하세요.
                modifier = Modifier.padding(top = 5.dp),
            ) {
                icon()
                if (label != null && alwaysShowLabel) {
                    Spacer(modifier = Modifier.height(8.dp))
                    // 라벨의 색상과 스타일을 직접 적용합니다.
                    Text(
                        text = label,
                        style = AppTypography.labelMedium, // 폰트 스타일
                        color = if (selected) LMNavigationDefaults.navigationSelectedItemColor()
                        else LMNavigationDefaults.navigationContentColor(), // 색상
                    )
                }
            }
        },
        modifier = modifier,
        enabled = enabled,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = LMNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = LMNavigationDefaults.navigationContentColor(),
            selectedTextColor = LMNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = LMNavigationDefaults.navigationContentColor(),
            indicatorColor = LMNavigationDefaults.navigationIndicatorColor(),
        ),
    )
}


@Composable
fun LMNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = lightbackground, // 배경색 적용
        contentColor = LMNavigationDefaults.navigationContentColor(),
        content = content,
        // 시스템 내비게이션 바 높이에 맞춰 내부 여백을 자동으로 계산합니다.
        windowInsets = NavigationBarDefaults.windowInsets,
    )
}

@Preview(showBackground = true)
@Composable
fun LMNavigationBarPreview() {
    val items = listOf("홈", "앨범", "추억", "마이페이지")
    val icons = listOf(
        LMicons.Home_Selected,
        LMicons.Gallery_Selected,
        LMicons.Memory_Selected,
        LMicons.my_page_Selected,
    )

    LMNavigationBar {
        items.forEachIndexed { index, item ->
            LMNavigationBarItem(
                icon = {
                    Icon(
                        imageVector = icons[index],
                        contentDescription = item,
                    )
                },
                selected = index == 0,
                onClick = { },
            )
        }
    }
}

object LMNavigationDefaults {
    @Composable
    fun navigationContentColor() = color3

    @Composable
    fun navigationSelectedItemColor() = color3

    @Composable
    fun navigationIndicatorColor() = Color.Transparent

    val NavigationBarHeight = 80.dp
}

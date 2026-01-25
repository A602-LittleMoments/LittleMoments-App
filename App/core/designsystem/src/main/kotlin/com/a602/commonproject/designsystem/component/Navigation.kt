package com.a602.commonproject.designsystem.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color3

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
    label: @Composable (() -> Unit)? = null,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
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
        containerColor = background, // 배경색 적용
        contentColor = LMNavigationDefaults.navigationContentColor(),
        tonalElevation = 0.dp,
        content = content,
    )
}
@Preview(showBackground = true)
@Composable
fun LMNavigationBarPreview() {
    val items = listOf("홈", "앨범", "추억", "마이페이지")
    val icons = listOf(
        LMicons.Home,
        LMicons.Photo,
        LMicons.Star,
        LMicons.Mypage,
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
                label = { Text(item) },
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
}

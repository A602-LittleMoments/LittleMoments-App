package com.a602.commonproject.designsystem.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.ui.text.style.TextOverflow
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.designsystem.theme.color3
import com.a602.commonproject.designsystem.theme.color4

/**
 * ScrollableUnderlineTabRow
 *
 * - 아기 이름 탭처럼 개수가 늘어날 수 있는 경우 ScrollableTabRow 사용
 * - 선택/비선택 텍스트 색상 분리
 *   - selected: color3
 *   - unselected: color4
 * - 밑줄(indicator) 색상: color3
 */
@Composable
fun ScrollableUnderlineTabRow(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    // selectedIndex가 범위를 벗어나면 indicator에서 크래시 날 수 있어 방어
    val safeIndex = selectedIndex.coerceIn(0, (tabs.size - 1).coerceAtLeast(0))

    ScrollableTabRow(
        selectedTabIndex = safeIndex,
        modifier = modifier,
        edgePadding = 16.dp, // 좌우 여백 (원하면 0.dp로 더 빡빡하게)
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        divider = {}, // 기본 divider 제거
        indicator = { tabPositions ->
            if (tabPositions.isNotEmpty()) {
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[safeIndex]),
                    height = 2.dp,
                    color = color3 // 밑줄 색상
                )
            }
        }
    ) {
        tabs.forEachIndexed { index, title ->
            val selected = safeIndex == index

            Tab(
                selected = selected,
                onClick = { onTabSelected(index) },
                // Tab의 contentColor는 텍스트에서 직접 color 주므로 크게 의미 없음(그래도 명시)
                selectedContentColor = color3,
                unselectedContentColor = color4,
                text = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineLarge, // ✅ 폰트
                        color = if (selected) color3 else color4,       // ✅ 선택/비선택 글자색
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
}

/* -------------------- Preview -------------------- */

@Preview(showBackground = true, widthDp = 411)
@Composable
fun ScrollableUnderlineTabRowPreview() {
    LMTheme {
        var selectedIndex by remember { mutableIntStateOf(0) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ScrollableUnderlineTabRow(
                tabs = listOf(
                    "하늘", "별이", "구름", "달",
                    "은하", "우주", "초롱", "해님", "달님"
                ),
                selectedIndex = selectedIndex,
                onTabSelected = { selectedIndex = it },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// 사용할 때
// tabs를 이름 목록 받은 거 넣어서 하면 될듯, 위 프리뷰처럼
//val tabs = babies.map { it.babyName }
//
//ScrollableUnderlineTabRow(
//tabs = tabs,
//selectedIndex = selectedIndex,
//onTabSelected = { selectedIndex = it }
//)

package com.a602.commonproject.designsystem.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

//import androidx.compose.runtime.mutableIntStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.tooling.preview.Preview


@Composable
fun UnderlineTabRow(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    TabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        divider = {}, // 기본 하단 divider 제거
        indicator = { tabPositions ->
            TabRowDefaults.PrimaryIndicator(
                modifier = Modifier.tabIndicatorOffset(
                    tabPositions[selectedIndex]
                ),
                height = 2.dp, // 밑줄 두께
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedIndex == index,
                onClick = { onTabSelected(index) },
                selectedContentColor = MaterialTheme.colorScheme.onSurface,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                text = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            )
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun UnderlineTabRowPreview() {
//    MaterialTheme {
//        var selectedTab by remember { mutableIntStateOf(0) }
//
//        UnderlineTabRow(
//            tabs = listOf("하람", "수수"),
//            selectedIndex = selectedTab,
//            onTabSelected = { selectedTab = it }
//        )
//    }
//}

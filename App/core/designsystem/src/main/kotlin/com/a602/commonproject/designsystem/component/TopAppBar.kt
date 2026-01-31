package com.a602.commonproject.designsystem.component

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LMTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: ImageVector = LMicons.Back,
    navigationIconContentDescription: String? = null,
    actionIcon: ImageVector? = null,
    actionIconContentDescription: String? = null,
    colors: TopAppBarColors = LMTopAppBarDefaults.colors(),
    onNavigationClick: () -> Unit = {},
    onActionClick: () -> Unit = {},
) {
    CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigationClick) {
                Icon(
                    imageVector = navigationIcon,
                    contentDescription = navigationIconContentDescription,
                )
            }
        },
        actions = {
            actionIcon?.let {
                IconButton(onClick = onActionClick) {
                    Icon(
                        imageVector = it,
                        contentDescription = actionIconContentDescription,
                    )
                }
            }
        },
        colors = colors,
        modifier = modifier.testTag("lmTopAppBar"),
    )
}


object LMTopAppBarDefaults {

    @Composable
    fun colors(): TopAppBarColors {
        return TopAppBarDefaults.topAppBarColors(
            containerColor = background,
            titleContentColor = color3,
            navigationIconContentColor = color3,
            actionIconContentColor = color3
        )
    }
}

/*

@OptIn(ExperimentalMaterial3Api::class)
@Preview("Top App Bar")
@Composable
private fun LMTopAppBarPreview() {
    NiaTheme {
        LMTopAppBar(
            title = "프리뷰",
            actionIcon = LMicons.Mypage,
        )
    }
}

*/


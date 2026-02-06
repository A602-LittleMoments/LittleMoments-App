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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.LMTheme
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
    actions: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit = {},
) {
    val view = androidx.compose.ui.platform.LocalView.current
    if (!view.isInEditMode) {
        // [Fix] Force Black Icons (Ivory Bg) whenever this TopBar is composed
        // Using SideEffect ensures it runs after parent Theme's SideEffect, enforcing the override.
        androidx.compose.runtime.SideEffect {
            val window = (view.context as android.app.Activity).window
            androidx.core.view.WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
        
        // [Fix] Cleanup on Leave: Revert to White Icons (Starry Bg Default)
        androidx.compose.runtime.DisposableEffect(Unit) {
            onDispose {
                val window = (view.context as android.app.Activity).window
                androidx.core.view.WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

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
            actions()
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
        modifier = modifier.testTag("lmTopAppBar").shadow(8.dp),
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



@OptIn(ExperimentalMaterial3Api::class)
@Preview("Top App Bar")
@Composable
private fun LMTopAppBarPreview() {
    LMTheme {
        LMTopAppBar(
            title = "프리뷰",
            actionIcon = LMicons.my_page_Unselected,
        )
    }
}



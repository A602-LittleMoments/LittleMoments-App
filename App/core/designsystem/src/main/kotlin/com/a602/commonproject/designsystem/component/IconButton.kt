package com.a602.commonproject.designsystem.component

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.color2
import com.a602.commonproject.designsystem.theme.lightbackground

@Composable
fun LMFilledIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
) {
    androidx.compose.material3.FilledIconButton(
        onClick = onClick,
        modifier = modifier,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = color2,
            contentColor = lightbackground
        )
    ) {
        icon()
    }
}



@Preview(showBackground = true)
@Composable
fun IconButtonPreview() {
    LMFilledIconButton(
        onClick = {  }
    ) {
        Icon(
            imageVector = LMicons.Add,
            contentDescription = null
        )
    }
}






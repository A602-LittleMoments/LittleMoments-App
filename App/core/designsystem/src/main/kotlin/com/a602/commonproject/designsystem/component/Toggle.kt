package com.a602.commonproject.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.a602.commonproject.designsystem.theme.lightbackground
import com.a602.commonproject.designsystem.theme.main

enum class Gender {
    Male, Female
}

/**
 * 남/여 토글 컴포넌트
 * - 누르면 main 색으로 채워짐
 * - 다시 누르면 해제(null)
 * - 남 선택 후 여 선택하면 남 해제되고 여만 선택(서로 배타)
 */
@Composable
fun GenderToggle(
    selected: Gender?, // null = 아무것도 선택 안 됨
    onSelectedChange: (Gender?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GenderToggleButton(
            text = "남",
            selected = selected == Gender.Male,
            onClick = {
                onSelectedChange(if (selected == Gender.Male) null else Gender.Male)
            },
            modifier = Modifier.weight(1f)
        )

        GenderToggleButton(
            text = "여",
            selected = selected == Gender.Female,
            onClick = {
                onSelectedChange(if (selected == Gender.Female) null else Gender.Female)
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun GenderToggleButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor = if (selected) main else lightbackground
    val contentColor = if (selected) lightbackground else main

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, main),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = lightbackground.copy(alpha = 0.7f),
            disabledContentColor = main.copy(alpha = 0.4f),
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = contentColor
        )
    }
}

/* -------------------- Preview -------------------- */

@Preview(
    name = "Gender Toggle Preview",
    showBackground = true,
    widthDp = 360
)
@Composable
fun GenderTogglePreview() {
    Surface(
        color = lightbackground,
        modifier = Modifier.padding(16.dp)
    ) {
        var selected by remember { mutableStateOf<Gender?>(null) }

        GenderToggle(
            selected = selected,
            onSelectedChange = { selected = it }
        )
    }
}


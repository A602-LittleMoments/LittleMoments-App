package com.a602.commonproject.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.theme.AppTypography
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.designsystem.theme.color4
import com.a602.commonproject.designsystem.theme.lightblue
import com.a602.commonproject.designsystem.theme.lightbackground
import com.a602.commonproject.designsystem.theme.main

@Composable
fun LMEditInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    trailingIcon: @Composable (() -> Unit)? = null, // 파라미터를 유연한 Composable 람다로 변경
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    isPassword: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = color4.copy(alpha = 0.6f), style = AppTypography.labelMedium) },
        placeholder = { Text(placeholder, color = color4.copy(alpha = 0.3f), style = AppTypography.titleMedium) },
        textStyle = AppTypography.titleMedium.copy(color = color4),
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = trailingIcon, // 전달받은 trailingIcon을 그대로 사용
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = lightbackground,
            unfocusedContainerColor = lightbackground,
            focusedBorderColor = main,
            unfocusedBorderColor = lightblue
        ),
        enabled = enabled,
        readOnly = readOnly
    )
}

// Preview 코드는 trailingIcon을 테스트하도록 수정하면 더 좋습니다.
@Preview(showBackground = true, name = "글자가 입력된 상태")
@Composable
fun LMEditInputFieldWithTextPreview() {
    LMTheme {
        LMEditInputField(
            label = "생년월일",
            value = "2024-01-30",
            onValueChange = {},
            placeholder = "YYYY-MM-DD"
        )
    }
}

@Preview(showBackground = true, name = "입력 전 상태 (Placeholder)")
@Composable
fun LMEditInputFieldEmptyPreview() {
    LMTheme {
        LMEditInputField(
            label = "생년월일",
            value = "", // 빈 값 전달
            onValueChange = {},
            placeholder = "YYYY-MM-DD"
        )
    }
}

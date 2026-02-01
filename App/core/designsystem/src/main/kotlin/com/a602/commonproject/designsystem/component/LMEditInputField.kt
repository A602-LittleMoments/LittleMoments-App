package com.a602.commonproject.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.theme.*

@Composable
fun LMEditInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    isPassword: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    supportingText: String? = null
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, color = color4.copy(alpha = 0.6f), style = MaterialTheme.typography.labelMedium) },
            placeholder = { Text(placeholder, color = color4.copy(alpha = 0.3f), style = MaterialTheme.typography.titleMedium) },
            textStyle = MaterialTheme.typography.titleMedium.copy(color = color4),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = trailingIcon,
            isError = isError, // isError 상태 연결
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = lightbackground,
                unfocusedContainerColor = lightbackground,
                focusedBorderColor = if (isError) errorRed else main,
                unfocusedBorderColor = if (isError) errorRed else lightblue,
                errorBorderColor = errorRed, // 에러 상태일 때의 테두리 색 명시
                errorLabelColor = errorRed, // 에러 상태일 때의 라벨 색 명시
                errorSupportingTextColor = errorRed // 에러 상태일 때의 보조 텍스트 색 명시
            ),
            enabled = enabled,
            readOnly = readOnly
        )
        // 보조 텍스트가 있을 경우에만 표시
        if (supportingText != null) {
            Text(
                text = supportingText,
                color = if(isError) errorRed else color4.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "기본 상태")
@Composable
fun LMEditInputFieldPreview() {
    LMTheme {
        LMEditInputField(
            label = "라벨",
            value = "입력된 텍스트",
            onValueChange = {}
        )
    }
}

@Preview(showBackground = true, name = "에러 상태")
@Composable
fun LMEditInputFieldErorrPreview() {
    LMTheme {
        LMEditInputField(
            label = "라벨",
            value = "잘못된 입력",
            onValueChange = {},
            isError = true,
            supportingText = "오류 메시지가 여기에 표시됩니다."
        )
    }
}

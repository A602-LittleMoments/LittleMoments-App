package com.a602.commonproject.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.a602.commonproject.designsystem.theme.NavyBlue
import com.a602.commonproject.designsystem.theme.PointYellow

@Composable
fun GenderButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (isSelected) PointYellow else Color(0xFFEEEEEE)
            )
            .clickable { onClick() }
            .border(
                width = 1.dp,
                color = if (isSelected) Color.Transparent else Color.Gray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(24.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) NavyBlue else Color.Gray,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

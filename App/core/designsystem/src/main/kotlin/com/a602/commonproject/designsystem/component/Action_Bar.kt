package com.a602.commonproject.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.color3
import com.a602.commonproject.designsystem.theme.lightbackground

/**
 * 캡쳐처럼 생긴 "아이콘 3개 액션 바"
 * - pill 배경 + 그림자
 * - 내부 아이콘 3개(삭제/다운로드/수정)
 */
@Composable
fun IconActionBar(
    onDelete: () -> Unit,
    onDownload: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
    enabledDelete: Boolean = true,
    enabledDownload: Boolean = true,
    enabledEdit: Boolean = true,
) {
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth(0.75f)
            .widthIn(min = 280.dp, max = 350.dp)
            .shadow(8.dp, shape)
            .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f), shape)
            .border(1.dp, androidx.compose.ui.graphics.Color.White.copy(alpha = 0.2f), shape)
            .clip(shape),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ActionIconButton(
                icon = LMicons.Delete,
                enabled = enabledDelete,
                onClick = onDelete
            )

            ActionIconButton(
                icon = LMicons.Download,
                enabled = enabledDownload,
                onClick = onDownload
            )

            ActionIconButton(
                icon = LMicons.Edit,
                enabled = enabledEdit,
                onClick = onEdit
            )
        }
    }
}

@Composable
private fun ActionIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = androidx.compose.ui.graphics.Color.White.copy(alpha = if (enabled) 1f else 0.4f)
        )
    }
}

/* -------------------- Preview -------------------- */

@Preview(showBackground = true, widthDp = 411, heightDp = 140)
@Composable
fun IconActionBarPreview() {
    // 캡쳐 배경이 회색이라 비슷하게 보이게
    Surface(color = androidx.compose.ui.graphics.Color(0xFF3F3F3F)) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconActionBar(
                onDelete = {},
                onDownload = {},
                onEdit = {},
                modifier = Modifier.widthIn(min = 300.dp)
            )
        }
    }
}

package com.a602.commonproject.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.R
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.color3
import com.a602.commonproject.designsystem.theme.lightbackground
import com.a602.commonproject.designsystem.theme.lightblue
import com.a602.commonproject.designsystem.theme.main


@Composable
fun FabMenuExample() {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // 🔹 FAB 메뉴
        if (expanded) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                FabMenuItem(
                    text = "격자보기",
                    iconVector = LMicons.PhotoLibrary
                )

                FabMenuItem(
                    text = "임시 앨범",
                    iconDrawable =  R.drawable.temporary
                )

                FabMenuItem(
                    text = "하이라이트 생성",
                    iconDrawable =   R.drawable.highlight
                )
            }
        }

        // 🔹 FAB
        SmallFloatingActionButton(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = main,
            shape = CircleShape,
            contentColor = lightbackground
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Default.Menu,
                contentDescription = null
            )
        }
    }
}

@Composable
fun FabMenuItem(
    text: String,
    iconVector: ImageVector? = null,
    iconDrawable: Int? = null
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = lightblue,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .height(52.dp)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (iconVector != null) {
                Icon(imageVector = iconVector, contentDescription = null, tint = color3)
            } else if (iconDrawable != null) {
                Icon(painter = painterResource(id = iconDrawable), contentDescription = null, tint = color3)
            }


            Spacer(Modifier.width(12.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = color3
            )
        }
    }
}


@Preview
@Composable
fun FabMenuPreview() {
  FabMenuExample()
}

//  메뉴가 열린 상태를
@Preview(showBackground = true, name = "Expanded Menu")
@Composable
fun FabMenuExpandedPreview() {
    MaterialTheme {

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                FabMenuItem(text = "격자보기", iconVector = LMicons.PhotoLibrary)
                FabMenuItem(text = "임시 앨범", iconDrawable = R.drawable.temporary)
                FabMenuItem(text = "하이라이트 생성", iconDrawable = R.drawable.highlight)
            }
    }
}

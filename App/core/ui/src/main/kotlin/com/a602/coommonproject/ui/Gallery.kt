package com.a602.coommonproject.ui

import android.R
import android.media.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.component.ElevatedRoundButton
import com.a602.commonproject.designsystem.theme.background


@Composable
fun GalleryGrid(
    images: List<Int>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(background),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ElevatedRoundButton(
                text = "전체 비우기",
                onClick = { },
            )
            ElevatedRoundButton(
                "선택",
                onClick = {},
            )
        }
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier,
        contentPadding = PaddingValues(4.dp),
    ) {
            items(images) { imageRes ->
                Card(
                    modifier = Modifier
                        .padding(4.dp)
                        .aspectRatio(1f),
                    elevation = CardDefaults.cardElevation(4.dp),
                ) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GalleryGridPreview() {
        GalleryGrid(
            images = listOf(
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery,

                ),
        )

}


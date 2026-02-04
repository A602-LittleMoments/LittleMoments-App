package com.a602.commonproject.feature.album

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.a602.commonproject.designsystem.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.a602.commonproject.designsystem.component.FillWrapButton
import com.a602.commonproject.designsystem.component.LMNavigationDefaults.NavigationBarHeight
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.feature.album.viewmodel.GridGalleryViewmodel
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.sp
import com.a602.commonproject.model.data.SharedMedia
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import com.a602.commonproject.designsystem.theme.color4
import com.a602.commonproject.designsystem.theme.lightbackground
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage


import androidx.compose.ui.composed
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Fill

@Composable
fun GridRoute(
    date: LocalDate? = null,
    onBackClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onMediaClick: (SharedMedia) -> Unit,
    viewModel: GridGalleryViewmodel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val medias = uiState.medias

    // Group media by date
    val groupedMedias = remember(medias) {
        medias.groupBy {
            Instant.ofEpochMilli(it.dateTaken)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }
    }
    
    // Sorted unique dates (Ascending: Past -> Present)
    val availableDates = remember(groupedMedias) {
        groupedMedias.keys.sorted()
    }
    
    // Determine initial page
    val initialPage = remember(availableDates, date) {
        if (date != null) {
            val idx = availableDates.indexOf(date)
            if (idx >= 0) idx else availableDates.size - 1.coerceAtLeast(0)
        } else {
            availableDates.size - 1.coerceAtLeast(0) // Default to latest
        }
    }

    GridGalleryScreen(
        groupedMedias = groupedMedias,
        availableDates = availableDates,
        initialPage = initialPage,
        onBackClick = onBackClick,
        onMediaClick = onMediaClick
    )
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun GridGalleryScreen(
    groupedMedias: Map<LocalDate, List<SharedMedia>>,
    availableDates: List<LocalDate>,
    initialPage: Int,
    onBackClick: () -> Unit,
    onMediaClick: (SharedMedia) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(
        initialPage = initialPage,
        pageCount = { availableDates.size.takeIf { it > 0 } ?: 1 }
    )
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    Scaffold(
        containerColor = Color(0xFFFFFBE6), // Cream Top bar background
        topBar = {
            LMTopAppBar(
                title = "",
                onNavigationClick = onBackClick,
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFFBE6)
                )
            )
        }
    ) { innerPadding ->
        Box(modifier = modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.gallery_background),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp), // Bottom padding
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                // Card Container
                Box(modifier = Modifier.weight(1f)) {
                    // Spaceship Image (Behind or Overlapping Top Right of Card? User image shows it ON the card corner)
                    // We place it later to be on top Z-index, or use a specific layout.
                    
                    // The Card Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 40.dp) // Space for spaceship overlap
                            .shadow(8.dp, RoundedCornerShape(16.dp)) 
                            .background(Color(0xFFD9D9D9), RoundedCornerShape(16.dp)) 
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                         if (availableDates.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("사진이 없습니다.")
                            }
                        } else {
                            val currentDate = availableDates.getOrNull(pagerState.currentPage)
                            val displayDate = currentDate?.let { 
                                "${it.year}년 ${it.monthValue}월 ${it.dayOfMonth}일" 
                            } ?: "날짜 없음"

                            // Header: Arrow < Date > Arrow (White box style)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                                    .background(Color.White, RoundedCornerShape(4.dp))
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Left Arrow
                                androidx.compose.material3.IconButton(
                                    onClick = {
                                        scope.launch {
                                            if (pagerState.currentPage > 0) {
                                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                            }
                                        }
                                    },
                                    enabled = pagerState.currentPage > 0
                                ) {
                                    androidx.compose.material3.Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.KeyboardArrowLeft,
                                        contentDescription = "Previous",
                                        tint = if(pagerState.currentPage > 0) Color.LightGray else Color.Transparent,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                // Date Text
                                Text(
                                    text = displayDate,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    ),
                                    color = Color.Black
                                )

                                // Right Arrow
                                androidx.compose.material3.IconButton(
                                    onClick = {
                                        scope.launch {
                                            if (pagerState.currentPage < availableDates.size - 1) {
                                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                            }
                                        }
                                    },
                                    enabled = pagerState.currentPage < availableDates.size - 1
                                ) {
                                    androidx.compose.material3.Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.KeyboardArrowRight,
                                        contentDescription = "Next",
                                        tint = if(pagerState.currentPage < availableDates.size - 1) Color.LightGray else Color.Transparent,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))

                            // Pager Content
                            androidx.compose.foundation.pager.HorizontalPager(
                                state = pagerState,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) { page ->
                                val pageDate = availableDates.getOrNull(page)
                                val pageMedias = groupedMedias[pageDate] ?: emptyList()

                                if (pageMedias.isEmpty()) {
                                     Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("사진이 없습니다.")
                                    }
                                } else {
                                    val gridState = androidx.compose.foundation.lazy.grid.rememberLazyGridState()
                                    
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        LazyVerticalGrid(
                                            columns = GridCells.Fixed(2), // Image shows 2 columns in the card
                                            state = gridState,
                                            modifier = Modifier
                                                .fillMaxSize(),
                                            verticalArrangement = Arrangement.spacedBy(12.dp),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp)
                                        ) {
                                            items(pageMedias) { media ->
                                                GridItem(media = media, onClick = onMediaClick)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    // Spaceship Decoration (Top Right)
                    Image(
                        painter = painterResource(id = R.drawable.rocket4),
                        contentDescription = "Spaceship",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 10.dp, y = 0.dp) // Adjust based on mockup
                            .size(120.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GridItem(media: SharedMedia, onClick: (SharedMedia) -> Unit) {
     Box(
        modifier = Modifier
            .aspectRatio(1f)
            .shadow(4.dp, RoundedCornerShape(16.dp)) // Increased corner radius
            .clip(RoundedCornerShape(16.dp))
            .background(androidx.compose.ui.graphics.Color.White)
            .clickable { onClick(media) }
            .border(2.dp, androidx.compose.ui.graphics.Color.White, RoundedCornerShape(16.dp))
    ) {
        AsyncImage(
            model = media.remoteUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

fun Modifier.galleryGridVerticalScrollbar(
    state: androidx.compose.foundation.lazy.grid.LazyGridState,
    width: androidx.compose.ui.unit.Dp = 6.dp
): Modifier = composed {
    val targetAlpha = if (state.isScrollInProgress) 1f else 1f // Always visible as requested
    val duration = if (state.isScrollInProgress) 150 else 500

    val alpha by androidx.compose.animation.core.animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = duration),
        label = "ScrollbarAlpha"
    )

    drawWithContent {
        drawContent()

        // Better approximation for Grid
        val layoutInfo = state.layoutInfo
        val totalItems = layoutInfo.totalItemsCount
        val viewportHeight = this.size.height
        
        if (totalItems == 0) return@drawWithContent
        
        // Simple logic for indicator:
        val firstIndex = state.firstVisibleItemIndex
        val visibleCount = layoutInfo.visibleItemsInfo.size
        
        if (totalItems > visibleCount) {
            val indicatorHeight = viewportHeight * (visibleCount.toFloat() / totalItems.toFloat())
            val indicatorOffset = viewportHeight * (firstIndex.toFloat() / totalItems.toFloat())
            
            drawRect(
                color = androidx.compose.ui.graphics.Color.White.copy(alpha = alpha * 0.5f),
                topLeft = Offset(this.size.width - width.toPx(), indicatorOffset),
                size = Size(width.toPx(), kotlin.math.max(indicatorHeight, 20f)),
                style = Fill
            )
        }
    }
}

// Preview Mock Data ... handling ... (Keep preview logic if possible or commented out)
@Preview(showBackground = true)
@Composable
fun GridGalleryScreenPreviewNew() {
    LMTheme {
        GridGalleryScreen(
            groupedMedias = mapOf(LocalDate.now() to emptyList()), // Mock
            availableDates = listOf(LocalDate.now()),
            initialPage = 0,
            onBackClick = {},
            onMediaClick = {}
        )
    }
}

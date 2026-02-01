package com.a602.commonproject.feature.memory

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MemoryMainContainer(
    viewModel: MemoryMainViewModel,
    onOpenGrid: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        MemoryMainUiState.Empty -> MemoryEmptyScreen()
        is MemoryMainUiState.Main -> {
            val items = (uiState as MemoryMainUiState.Main).collections
            MemoryScreen(items = items, onPlanetClick = onOpenGrid)
        }
        is MemoryMainUiState.Error -> MemoryEmptyScreen()
        // Loading + Make는 같은 로켓 화면으로 묶기
        MemoryMainUiState.Loading,
            is MemoryMainUiState.Make -> MemoryMakeScreen(totalMillis = 1_000)
    }
}

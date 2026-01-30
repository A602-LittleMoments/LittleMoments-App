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

    when (uiState) {
        MemoryMainUiState.Loading -> MemoryMakeScreen()
        MemoryMainUiState.Empty -> MemoryEmptyScreen()
        is MemoryMainUiState.Make -> MemoryMakeScreen()
        is MemoryMainUiState.Main -> {
            val items = (uiState as MemoryMainUiState.Main).collections
            MemoryScreen(items = items, onPlanetClick = onOpenGrid)
        }
        is MemoryMainUiState.Error -> MemoryEmptyScreen()
    }
}

package com.a602.commonproject.feature.gallery.veiwmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.SharedMediaRepository
import com.a602.commonproject.model.data.SharedMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class GridGalleryUiState(
    val medias: List<SharedMedia> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)


@HiltViewModel
class GridGalleryViewmodel @Inject constructor(
    repository: SharedMediaRepository
) : ViewModel() {

    val uiState: StateFlow<GridGalleryUiState> =
        repository.getSharedAlbumStream()
            .map { medias ->
                GridGalleryUiState(
                    medias = medias,
                    isLoading = false
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = GridGalleryUiState(isLoading = true)
            )
}


package com.a602.commonproject.feature.album.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.SavedStateHandle
import com.a602.commonproject.data.repository.CollectionRepository
import com.a602.commonproject.data.repository.SharedMediaRepository

import com.a602.commonproject.model.data.SharedMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.MutableStateFlow
import com.a602.commonproject.feature.album.GridNavKey

data class GridGalleryUiState(
    val medias: List<SharedMedia> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val title: String = "갤러리",
    val showCalendarButton: Boolean = true
)


@HiltViewModel
class GridGalleryViewmodel @Inject constructor(
    private val sharedMediaRepository: SharedMediaRepository,
    private val collectionRepository: CollectionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val filterState = MutableStateFlow(
        GridNavKey(
            keywordId = savedStateHandle["keywordId"],
            title = savedStateHandle["title"]
        )
    )

    fun setFilter(keywordId: String?, title: String?) {
        filterState.value = GridNavKey(keywordId, title)
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<GridGalleryUiState> = filterState
        .flatMapLatest { filter ->
            val flow = if (filter.keywordId != null) {
                // Keyword Filtered (One-shot -> Flow)
                flow {
                    emit(collectionRepository.getCollectionDetail(filter.keywordId).getOrElse { emptyList() })
                }
            } else {
                // All Photos (Stream)
                sharedMediaRepository.getSharedAlbumStream()
            }
            
            flow.map { medias ->
                GridGalleryUiState(
                    medias = medias,
                    isLoading = false,
                    title = filter.title ?: "갤러리",
                    showCalendarButton = filter.keywordId == null
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GridGalleryUiState(isLoading = true)
        )
}


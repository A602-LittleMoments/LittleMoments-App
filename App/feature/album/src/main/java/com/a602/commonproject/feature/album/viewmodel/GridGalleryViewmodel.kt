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
    private val babyRepository: com.a602.commonproject.data.repository.BabyRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val filterState = MutableStateFlow(
        GridNavKey(
            keywordId = savedStateHandle["keywordId"],
            title = savedStateHandle["title"]
        )
    )

    fun setFilter(keywordId: String?, title: String?, babyId: String? = null, year: Int? = null) {
        filterState.value = GridNavKey(keywordId, title, babyId, year)
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<GridGalleryUiState> = filterState
        .flatMapLatest { filter ->
            val mediaFlow = if (filter.keywordId != null) {
                // Keyword Filtered
                flow {
                    emit(collectionRepository.getCollectionDetail(filter.keywordId).getOrElse { emptyList() })
                }
            } else {
                // Stream
                sharedMediaRepository.getSharedAlbumStream(
                    babyId = filter.babyId,
                    year = filter.year
                )
            }

            // Combine with Baby Name if needed
            val babyNameFlow = if (filter.babyId != null) {
                babyRepository.getBabyStream().map { babies ->
                    babies.find { it.babyId == filter.babyId }?.babyName
                }
            } else {
                flow { emit(null) }
            }

            kotlinx.coroutines.flow.combine(mediaFlow, babyNameFlow) { medias, babyName ->
                // Determine Title
                val displayTitle = when {
                    filter.title != null -> filter.title // Passed title has priority
                    babyName != null && filter.year != null -> "${babyName}와의 ${filter.year}년 추억"
                    else -> "갤러리"
                }

                GridGalleryUiState(
                    medias = medias,
                    isLoading = false,
                    title = displayTitle,
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


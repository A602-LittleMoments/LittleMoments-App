package com.a602.commonproject.feature.album.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.SavedStateHandle
import com.a602.commonproject.data.repository.CollectionRepository
import com.a602.commonproject.data.repository.SharedMediaRepository
import com.a602.commonproject.data.repository.BabyRepository

import com.a602.commonproject.model.data.SharedMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.a602.commonproject.feature.album.GridNavKey

enum class SortOrder {
    LATEST, OLDEST
}

data class GridGalleryUiState(
    val medias: List<SharedMedia> = emptyList(),
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false, // 🚀 [ADD] 삭제 중 상태
    val error: String? = null,
    val title: String = "갤러리",
    val showCalendarButton: Boolean = true,
    val sortOrder: SortOrder = SortOrder.LATEST,
    val isSelectMode: Boolean = false, // 🚀 [ADD] 선택 모드 여부
    val selectedIds: Set<String> = emptySet() // 🚀 [ADD] 선택된 ID 세트
)


@HiltViewModel
class GridGalleryViewmodel @Inject constructor(
    private val sharedMediaRepository: SharedMediaRepository,
    private val collectionRepository: CollectionRepository,
    private val babyRepository: BabyRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val sortOrder = MutableStateFlow(SortOrder.LATEST)
    private val isSelectMode = MutableStateFlow(false)
    private val selectedIds = MutableStateFlow<Set<String>>(emptySet())
    private val isDeleting = MutableStateFlow(false)


    private val filterState = MutableStateFlow(
        GridNavKey(
            keywordId = savedStateHandle.get<String>("keywordId"),
            title = savedStateHandle.get<String>("title")
        )
    )

    fun setFilter(keywordId: String?, title: String?, babyId: String? = null, year: Int? = null) {
        filterState.value = GridNavKey(keywordId, title, babyId, year)
    }

    private val refreshSignal = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val mediasFlow = combine(
        filterState,
        sortOrder,
        refreshSignal
    ) { filter, sort, _ -> filter to sort }
        .flatMapLatest { (filter, sort) ->
            val mediaFlow = if (filter.keywordId != null) {
                flow {
                    emit(collectionRepository.getCollectionDetail(filter.keywordId).getOrElse { emptyList() })
                }
            } else {
                sharedMediaRepository.getSharedAlbumStream(
                    babyId = filter.babyId,
                    year = filter.year
                )
            }

            val babyNameFlow = if (filter.babyId != null) {
                babyRepository.getBabyStream().map { babies ->
                    babies.find { it.babyId == filter.babyId }?.babyName
                }
            } else {
                flow { emit(null) }
            }

            combine(mediaFlow, babyNameFlow) { medias, babyName ->
                val sorted = if (sort == SortOrder.LATEST) {
                    medias.sortedByDescending { it.dateTaken }
                } else {
                    medias.sortedBy { it.dateTaken }
                }
                sorted to babyName
            }
        }

    val uiState: StateFlow<GridGalleryUiState> = combine(
        mediasFlow,
        isSelectMode,
        selectedIds,
        isDeleting,
        combine(filterState, sortOrder) { f, s -> f to s }
    ) { (medias, babyName), selectMode, selected, deleting, (filter, sort) ->
        val displayTitle = when {
            filter.title != null -> filter.title
            babyName != null && filter.year != null -> {
                val formattedName = formatBabyName(babyName)
                "${formattedName}와의 ${filter.year}년 추억"
            }
            else -> "갤러리"
        }

        GridGalleryUiState(
            medias = medias,
            isLoading = false,
            isDeleting = deleting,
            title = displayTitle,
            showCalendarButton = filter.keywordId == null,
            sortOrder = sort,
            isSelectMode = selectMode,
            selectedIds = selected
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = GridGalleryUiState(isLoading = true)
    )
    
    fun toggleSortOrder() {
        sortOrder.value = if (sortOrder.value == SortOrder.LATEST) SortOrder.OLDEST else SortOrder.LATEST
    }

    fun refreshData() {
        refreshSignal.value++
    }

    fun toggleSelectMode() {
        isSelectMode.value = !isSelectMode.value
        if (!isSelectMode.value) {
            selectedIds.value = emptySet()
        }
    }

    fun toggleSelect(id: String) {
        val current = selectedIds.value.toMutableSet()
        if (current.contains(id)) {
            current.remove(id)
        } else {
            current.add(id)
        }
        selectedIds.value = current
    }

    fun selectAll(ids: List<String>) {
        selectedIds.value = ids.toSet()
    }

    fun clearSelection() {
        selectedIds.value = emptySet()
    }

    fun deleteSelected(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val idsToDelete = selectedIds.value.toList()
        if (idsToDelete.isEmpty()) return

        viewModelScope.launch {
            isDeleting.value = true
            try {
                // SharedMediaRepository에 다중 삭제가 없으면 루프로 처리 (Result 합산)
                var allSuccess = true
                for (id in idsToDelete) {
                    val result = sharedMediaRepository.deleteMedia(id)
                    if (result.isFailure) {
                        allSuccess = false
                    }
                }
                
                if (allSuccess) {
                    isSelectMode.value = false
                    selectedIds.value = emptySet()
                    refreshData() // 키워드 모드 등 Flow가 아닌 경우 데이터 갱신
                    onSuccess()
                } else {
                    onError("일부 사진 삭제에 실패했습니다.")
                }
            } catch (e: Exception) {
                onError(e.message ?: "삭제 중 오류가 발생했습니다.")
            } finally {
                isDeleting.value = false
            }
        }
    }


    private fun formatBabyName(name: String): String {

        if (name.isEmpty()) return name
        // 1. 성 떼기 (첫 글자 제외) - 외자/세글자 이상 대응을 위해
        val givenName = if (name.length >= 2) name.substring(1) else name
        
        // 2. 받침 유무 확인하여 '이' 붙이기
        val lastChar = givenName.last()
        val hasBatchim = (lastChar - '\uAC00') % 28 > 0
        
        return if (hasBatchim) "${givenName}이" else givenName
    }
}


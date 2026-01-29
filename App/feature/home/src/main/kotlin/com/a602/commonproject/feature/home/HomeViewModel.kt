package com.a602.commonproject.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.BabyRepository
import com.a602.commonproject.data.repository.CollectionRepository
import com.a602.commonproject.data.repository.SharedMediaRepository
import com.a602.commonproject.data.repository.UserRepository
import com.a602.commonproject.model.data.Baby
import com.a602.commonproject.model.data.Collection
import com.a602.commonproject.model.data.SharedMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    babyRepository: BabyRepository,
    sharedMediaRepository: SharedMediaRepository,
    private val collectionRepository: CollectionRepository,
    userRepository: UserRepository
) : ViewModel() {

    private val _collections = MutableStateFlow<List<Collection>>(emptyList())

    val uiState: StateFlow<HomeUiState> = combine(
        babyRepository.getBabyStream(),
        sharedMediaRepository.getSharedAlbumStream(),
        _collections,
        userRepository.authState
    ) { babies, mediaList, collections, authState ->
        val currentBaby = babies.firstOrNull()

        HomeUiState.Success(
            baby = currentBaby,
            mediaList = mediaList,
            collections = collections
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState.Loading
    )

    init {
        fetchCollections()
    }

    private fun fetchCollections() {
        viewModelScope.launch {
            collectionRepository.getCollections()
                .onSuccess {
                    _collections.value = it
                }
                .onFailure {
                    // 에러 처리 (로그 등)
                }
        }
    }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val baby: Baby?,
        val mediaList: List<SharedMedia>,
        val collections: List<Collection> = emptyList(),
        val hasNotifications: Boolean = false
    ) : HomeUiState
    data object Error : HomeUiState
}

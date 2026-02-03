package com.a602.commonproject.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.BabyRepository
import com.a602.commonproject.data.repository.CollectionRepository
import com.a602.commonproject.data.repository.SharedMediaRepository
import com.a602.commonproject.data.repository.UserRepository
import com.a602.commonproject.model.data.Baby
import com.a602.commonproject.model.data.Collection
import com.a602.commonproject.model.data.AuthState
import com.a602.commonproject.model.data.SharedMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import okhttp3.internal.userAgent
import androidx.paging.cachedIn


@HiltViewModel
class HomeViewModel @Inject constructor(
    babyRepository: BabyRepository,
    sharedMediaRepository: SharedMediaRepository,
    private val collectionRepository: CollectionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _collections = MutableStateFlow<List<Collection>>(emptyList())
    private val _isError = MutableStateFlow(false)
    private val _selectedBabyIndex = MutableStateFlow(0)

    // Paging 3 Stream (별도로 노출)
    val mediaPagingFlow: kotlinx.coroutines.flow.Flow<androidx.paging.PagingData<SharedMedia>> =
        sharedMediaRepository.getSharedAlbumPagingStream()
            .cachedIn(viewModelScope)

    val uiState: StateFlow<HomeUiState> = combine(
        babyRepository.getBabyStream(),
        _collections,
        _isError,
        _selectedBabyIndex
    ) { babies, collections, isError, selectedIndex ->
        if (isError) {
            HomeUiState.Error("데이터를 불러오지 못했습니다. 네트워크를 확인해주세요.")
        } else {
            HomeUiState.Success(
                babies = babies,
                selectedBabyIndex = selectedIndex,
                collections = collections,
            )
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState.Loading
    )

    fun updateSelectedBaby(index: Int) {
        _selectedBabyIndex.value = index
    }

    init {
        fetchCollections()
    }
    fun logout(){
        viewModelScope.launch {
            userRepository.logout()
        }
    }

    private fun fetchCollections() {
        viewModelScope.launch {
            collectionRepository.getCollections()
                .onSuccess {
                    _isError.value = false
                    _collections.value = it
                }
                .onFailure {
                    // _isError.value = true
                    // 컬렉션 로딩 실패해도 메인 화면은 보여줘야 함 (DB 데이터 우선)
                }
        }
    }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val babies: List<Baby>,
        val selectedBabyIndex: Int = 0,
        val collections: List<Collection> = emptyList(),
        val hasNotifications: Boolean = false
    ) : HomeUiState {
        val currentBaby: Baby?
            get() = babies.getOrNull(selectedBabyIndex)
    }
    data class Error(
        val errorMessage : String?
    ) : HomeUiState
}

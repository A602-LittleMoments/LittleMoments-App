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

@HiltViewModel
class HomeViewModel @Inject constructor(
    babyRepository: BabyRepository,
    sharedMediaRepository: SharedMediaRepository,
    private val collectionRepository: CollectionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _collections = MutableStateFlow<List<Collection>>(emptyList())
    private val _isError = MutableStateFlow(false)

    val uiState: StateFlow<HomeUiState> = combine(
        babyRepository.getBabyStream(),
        sharedMediaRepository.getSharedAlbumStream(),
        _collections,
        userRepository.authState,
        _isError
    ) { babies, mediaList, collections, authState, isError ->
        if (isError) {
            // 데이터가 아예 없는 경우: 단순히 Success(empty)로 보여줄지, Error로 보여줄지 결정
            // 여기서는 서버 에러가 났을 때를 위해 Error 상태를 활용할 수 있습니다.
            HomeUiState.Error("데이터를 불러오지 못했습니다. 네트워크를 확인해주세요.")
        }else {
            val currentBaby = babies.firstOrNull()

            HomeUiState.Success(
                baby = currentBaby,
                mediaList = mediaList,
                collections = collections
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState.Loading
    )

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
        val baby: Baby?,
        val mediaList: List<SharedMedia>,
        val collections: List<Collection> = emptyList(),
        val hasNotifications: Boolean = false
    ) : HomeUiState
    data class Error(
        val errorMessage : String?
    ) : HomeUiState
}

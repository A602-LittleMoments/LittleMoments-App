package com.a602.commonproject.feature.mypage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.BabyRepository
import com.a602.commonproject.data.repository.GroupRepository
import com.a602.commonproject.data.repository.UserRepository
import com.a602.commonproject.model.data.AuthState
import com.a602.commonproject.model.data.Baby
import com.a602.commonproject.model.data.GroupMember
import com.a602.commonproject.model.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

// 1. 화면에 필요한 모든 데이터를 담을 그릇(data class)을 정의합니다.
//    (로딩 상태, 오류 메시지 등 UI 상태 전체를 포함합니다.)
data class MyPageUiState(
    val user: User? = null,
    val baby: Baby? = null,
    val groupMembers: List<GroupMember> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val babyRepository: BabyRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyPageUiState(isLoading = true))
    val uiState: StateFlow<MyPageUiState> = _uiState.asStateFlow()

    init {
        loadMyPageData()
    }

    private fun loadMyPageData() {
        viewModelScope.launch {
            combine(
                userRepository.authState,
                babyRepository.getBabyStream()
            ) { authState, babyList ->
                val user = if (authState is AuthState.LoggedIn) authState.user else null
                val baby = babyList.firstOrNull()

                val groupMembersResult = groupRepository.getGroupMembers()
                val groupMembers = groupMembersResult.getOrNull() ?: emptyList()

                MyPageUiState(
                    user = user,
                    baby = baby,
                    groupMembers = groupMembers,
                    isLoading = false
                )
            }.collect { combinedState ->
                _uiState.value = combinedState
            }
        }
    }
}

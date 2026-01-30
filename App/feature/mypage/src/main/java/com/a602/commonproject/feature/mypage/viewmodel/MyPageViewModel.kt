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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
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
    userRepository: UserRepository,
    babyRepository: BabyRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {

    val uiState: StateFlow<MyPageUiState> =
        combine(
            userRepository.authState,
            babyRepository.getBabyStream()
        ) { authState, babyList ->
            Pair(authState, babyList)
        }.flatMapLatest { (authState, babyList) ->
            flow {
                val user = if (authState is AuthState.LoggedIn) authState.user else null
                val baby = babyList.firstOrNull()
                val groupMembersResult = groupRepository.getGroupMembers()
                val groupMembers = groupMembersResult.getOrNull() ?: emptyList()

                emit(
                    MyPageUiState(
                        user = user,
                        baby = baby,
                        groupMembers = groupMembers,
                        isLoading = false
                    )
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MyPageUiState(isLoading = true)
        )
}

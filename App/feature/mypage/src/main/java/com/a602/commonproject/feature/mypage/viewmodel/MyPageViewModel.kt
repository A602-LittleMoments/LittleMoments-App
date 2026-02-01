package com.a602.commonproject.feature.mypage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.BabyRepository
import com.a602.commonproject.data.repository.GroupRepository
import com.a602.commonproject.data.repository.UserRepository
import com.a602.commonproject.model.data.AuthState
import com.a602.commonproject.model.data.Baby
import com.a602.commonproject.model.data.Group
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

// 마이페이지
// 1. 화면에 필요한 모든 데이터를 담을 data class을 정의
//    (로딩 상태, 오류 메시지 등 UI 상태 전체를 포함합니다.)
data class MyPageUiState(
    val user: User? = null,
    val babies: List<Baby> = emptyList(),
    val group: Group? = null, // 그룹 정보
    val groupMembers: List<GroupMember> = emptyList(),
    val hasGroup: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class MyPageViewModel @Inject constructor(
    userRepository: UserRepository, // 유저
    babyRepository: BabyRepository, // 아기정보
    private val groupRepository: GroupRepository // 그룹 데이터
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

                // 그룹 정보와 멤버 목록을 모두 가져옵니다.
                val groupResult = groupRepository.getMyGroup()
                val membersResult = groupRepository.getGroupMembers()

                val group = groupResult.getOrNull()
                val groupMembers = membersResult.getOrNull() ?: emptyList()

                emit(
                    MyPageUiState(
                        user = user,
                        babies = babyList,
                        group = group,
                        groupMembers = groupMembers,
                        hasGroup = group != null,
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

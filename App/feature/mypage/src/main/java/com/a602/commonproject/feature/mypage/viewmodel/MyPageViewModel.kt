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

// 마이페이지
// 1. 화면에 필요한 모든 데이터를 담을 data class을 정의
//    (로딩 상태, 오류 메시지 등 UI 상태 전체를 포함합니다.)
data class MyPageUiState(
    val user: User? = null, // 로그인한 사용자 정보
    val babies: List<Baby> = emptyList(), // 등록된 아기 정보 리스트(한 명만 나오는 문제 해결)
    val groupMembers: List<GroupMember> = emptyList(), // 그룹 구성원 리스트
    val hasGroup: Boolean = false, // 그룹 존재 여부
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
            userRepository.authState, // 유저 데이터
            babyRepository.getBabyStream() // 아기 데이터
        ) { authState, babyList ->
            Pair(authState, babyList)
        }.flatMapLatest { (authState, babyList) ->
            flow {
                val user = if (authState is AuthState.LoggedIn) authState.user else null
                val groupMembersResult = groupRepository.getGroupMembers()
                val groupMembers = groupMembersResult.getOrNull() ?: emptyList()

                emit(
                    MyPageUiState(
                        user = user,
                        babies = babyList, // 아기 목록 전체를 전달
                        groupMembers = groupMembers,
                        hasGroup = groupMembers.isNotEmpty(), // 그룹 멤버가 있으면 true
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

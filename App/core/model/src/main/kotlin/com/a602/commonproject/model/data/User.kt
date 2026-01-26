package com.a602.commonproject.model.data

/**
 * 로그인한 사용자의 정보
 */
// 순수 유저 데이터 (이제 Nullable이 필요 없음!)
data class User(
    val id: String,
    val email: String,
    val nickname: String,
    val profileImageUrl: String? = null // 프로필 이미지 추가
)
/**
 * 앱 전체의 인증 상태를 나타내는 상태 모델
 */
sealed interface AuthState {
    data object Loading : AuthState                 // 1. 확인 중 (앱 시작)
    data object NotLoggedIn : AuthState             // 2. 로그인 안 됨
    data class LoggedIn(val user: User) : AuthState // 3. 로그인 됨 (데이터 꽉 채워서)
}
/**
 * 로그인 여부를 쉽게 확인하기 위한 확장 프로퍼티
 */
val AuthState.isLoggedIn: Boolean
    get() = this is AuthState.LoggedIn

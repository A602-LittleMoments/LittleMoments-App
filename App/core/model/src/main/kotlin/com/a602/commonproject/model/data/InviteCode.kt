package com.a602.commonproject.model.data

/**
 * 🎫 그룹 초대 코드 정보
 */
data class InviteCode(
    val codeMember: String, // 멤버용 초대 코드 (수정 권한 O)
    val codeViewer: String, // 구경꾼용 초대 코드 (보기만 가능)
    val expiredAt: String   // 만료 시간 (UI에서 "10분 뒤 만료" 표시용)
)

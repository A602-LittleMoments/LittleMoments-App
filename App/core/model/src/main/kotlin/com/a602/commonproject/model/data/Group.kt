package com.a602.commonproject.model.data

/**
 * 그룹 관련(가족)
 */
data class Group(
    val id: String,          // groupId
    val name: String,        // groupName
    val role : GroupRole,    // 권환 관련
    val relation: String?,   // 내 호칭 (Mother, Father...)
    val members: List<GroupMember> = emptyList()
)

// 권한 Enum (UI 분기 처리용)
enum class GroupRole {
    OWNER,   // 방장 (설정 변경 가능)
    MEMBER,  // 멤버 (초대 가능)
    VIEWER,  // 구경꾼 (보기만 가능)
    UNKNOWN  // 몰루? (에러 방지용)
}

data class GroupMember(
    val userId: String,
    val nickname: String,
    val relation: String,    // 관계 (Mother 등)
    val role: GroupRole      // 권한
)


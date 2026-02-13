package com.a602.commonproject.data.model

import com.a602.commonproject.model.data.Group
import com.a602.commonproject.model.data.GroupMember
import com.a602.commonproject.model.data.GroupRole
import com.a602.commonproject.network.model.GroupMemberResponse
import com.a602.commonproject.network.model.GroupMembersResponse
import com.a602.commonproject.network.model.GroupResponse

/**
 * [Network -> UI] 그룹 정보 변환
 * (주의: GroupResponse에는 멤버 리스트가 없으므로 빈 리스트로 초기화)
 */
fun GroupResponse.asExternalModel(): Group {
    return Group(
        id = groupId,
        name = groupName,
        // String -> Enum 변환
        role = when (role) {
            "OWNER" -> GroupRole.OWNER
            "MEMBER" -> GroupRole.MEMBER
            "VIEWER" -> GroupRole.VIEWER
            else -> GroupRole.UNKNOWN
        },
        relation = relation,
        members = emptyList() // 멤버는 별도 API로 가져와서 채워야 함
    )
}

/**
 * [Network -> UI] 멤버 리스트 응답 전체를 UI 리스트로 변환
 */
fun GroupMembersResponse.asExternalModel(): List<GroupMember> {
    return groupMembers.map { it.asExternalModel() }
}

/**
 * [Network Member -> UI Member] 개별 멤버 변환
 */
fun GroupMemberResponse.asExternalModel(): GroupMember {
    return GroupMember(
        userId = userId,
        nickname = nickname,
        relation = relation,

        role = when (role) {
            "OWNER" -> GroupRole.OWNER
            "MEMBER" -> GroupRole.MEMBER
            "VIEWER" -> GroupRole.VIEWER
            else -> GroupRole.UNKNOWN
        }
    )
}

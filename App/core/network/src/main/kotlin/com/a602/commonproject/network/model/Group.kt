package com.a602.commonproject.network.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@InternalSerializationApi
@Serializable
data class GroupResponse(
    val groupId: String,
    val groupName: String,
    val role: String? = null,      // 조회 시 사용 (OWNER, MEMBER, VIEWER)
    val relation: String? = null,   // Mother, Father 등
)

// 2.2 그룹 생성 요청
@InternalSerializationApi
@Serializable
data class CreateGroupRequest(
    val groupName: String,
    val role: String, // OWNER
    val relation: String,  // Mother
)

// 2.3 그룹 이름 변경 요청
@InternalSerializationApi
@Serializable
data class UpdateGroupRequest(
    val groupName: String, // 새로운 그룹 이름
)

// 2.4 그룹 맴버 목록 응답
@InternalSerializationApi
@Serializable
data class GroupMembersResponse(
    val groupMembers: List<GroupMemberResponse>
)

@InternalSerializationApi
@Serializable
data class GroupMemberResponse(
    val userId: String,
    val nickname: String,
    val relation: String,
    val role: String,
)

// 2.5, 2.6 초대코드 응답
@InternalSerializationApi
@Serializable
data class GroupInviteResponse(
    val inviteCodeMember: String,
    val inviteCodeViewer : String,
    val updatedAt: String
)

// 2.7 초대코드로 그룹 정보 조회 응답
@InternalSerializationApi
@Serializable
data class GroupInviteInfoResponse(
    val groupId: String,
    val groupName: String,
    val role : String // 입장 시에 부여될 역할
)


// 2.7.1 그룹 참여 요청
@InternalSerializationApi
@Serializable
data class JoinGroupRequest(
    val relation: String
)

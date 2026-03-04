package com.a602.commonproject.data.repository

import com.a602.commonproject.model.data.Group
import com.a602.commonproject.model.data.GroupMember
import com.a602.commonproject.model.data.InviteCode
import com.a602.commonproject.network.model.GroupInviteInfoResponse
import kotlinx.coroutines.flow.Flow

interface GroupRepository {

    /**
     * 내 그룹 정보 조회
     * - 서버에서 최신 정보를 가져옵니다.
     * - 성공 시, 안전장치로 DataStore의 GroupId도 함께 갱신합니다.
     */
    fun getMyGroup(): Flow<Group>
    /**
     * 그룹 멤버 목록 조회
     */
    fun getGroupMembers(): Flow<List<GroupMember>>

    // ➕ 그룹 생성 (성공 시 DataStore groupId 갱신)
    suspend fun createGroup(groupName: String, relation : String): Result<Unit>

    // 🔗 그룹 가입 (성공 시 DataStore groupId 갱신)
    suspend fun joinGroup(invitationCode: String, relation: String): Result<Unit>

    // ✏️ 그룹 이름 수정
    suspend fun updateGroupName(newName: String): Result<Unit>

    // 👋 그룹 나가기 (성공 시 DataStore groupId 제거)
    suspend fun leaveGroup(): Result<Unit>

    /**
     * 현재 그룹의 초대 코드 조회
     */
    fun getInvites(): Flow<InviteCode>
    /**
     * 초대 코드 새로고침 (기존 코드 만료 및 재발급)
     */
    suspend fun refreshInvites(): Result<InviteCode>
    /**
     * 초대 코드로 그룹 정보 미리보기 (가입 전 확인용)
     */
    fun getGroupInfoByInvite(inviteCode: String): Flow<GroupInviteInfoResponse>

}

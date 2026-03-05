package com.a602.commonproject.data.repository.impl

import com.a602.commonproject.common.network.Dispatcher
import com.a602.commonproject.common.network.LMDispatchers
import com.a602.commonproject.data.model.asExternalModel
import com.a602.commonproject.data.repository.GroupRepository
import com.a602.commonproject.datastore.datastore.UserPreferencesDataStore
import com.a602.commonproject.model.data.Group
import com.a602.commonproject.model.data.GroupMember
import com.a602.commonproject.model.data.InviteCode
import com.a602.commonproject.network.datasource.GroupNetworkDataSource
import com.a602.commonproject.network.model.CreateGroupRequest
import com.a602.commonproject.network.model.GroupInviteInfoResponse
import com.a602.commonproject.network.model.JoinGroupRequest
import com.a602.commonproject.network.model.UpdateGroupRequest
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class NetworkGroupRepository @Inject constructor(
    private val networkDataSource: GroupNetworkDataSource,
    private val userPreferences: UserPreferencesDataStore, // ✨ DataStore 갱신용
    @param:Dispatcher(LMDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : GroupRepository {

    /**
     * 그룹 정보 조회
     */
    override fun getMyGroup(): Flow<Group> = flow {
        val response = networkDataSource.getMyGroup()
        val group = response.asExternalModel()
        userPreferences.setGroupId(group.id)
        emit(group)
    }.flowOn(ioDispatcher)

    /**
     * 그룹 멥버 조회
     */
    override fun getGroupMembers(): Flow<List<GroupMember>> = flow {
        val groupId = getGroupIdOrThrow()
        val response = networkDataSource.getGroupMembers(groupId)
        emit(response.asExternalModel())
    }.flowOn(ioDispatcher)

    /**
     * 그룹 생성
     */
    override suspend fun createGroup(groupName: String, relation: String): Result<Unit> = runCatching {
        val request = CreateGroupRequest(groupName = groupName, relation = relation)
        val response = networkDataSource.createGroup(request)
        userPreferences.setGroupId(response.groupId)
    }

    /**
     * 그룹 가입
     */
    override suspend fun joinGroup(invitationCode: String, relation: String): Result<Unit> = runCatching {
        val request = JoinGroupRequest(relation = relation)
        val response = networkDataSource.joinGroup(invitationCode, request)
        // ID 저장
        userPreferences.setGroupId(response.groupId)
    }

    /**
     * 그룹 이름 수정
     */
    override suspend fun updateGroupName(newName: String): Result<Unit> = runCatching {
        val groupId = getGroupIdOrThrow()
        val request = UpdateGroupRequest(groupName = newName)
        networkDataSource.updateGroup(groupId, request)
    }

    /**
     * 그룹 탈퇴
     */
    override suspend fun leaveGroup(): Result<Unit> = runCatching {
        // 여기서는 DataStore에 저장된 ID를 가져와서 서버에 보냄
        // (만약 이미 null이면 나갈 그룹이 없으니 성공 처리)
        val groupId = userPreferences.userGroupId.first() ?: return@runCatching
        networkDataSource.leaveGroup(groupId)
        // ID 삭제
        userPreferences.deleteGroupId()
    }


    // ---------------------------------------------------------------------
    // 초대 관련

    /**
     * 초대 코드 가져오기
     */
    override fun getInvites(): Flow<InviteCode> = flow {
        val groupId = getGroupIdOrThrow()
        val response = networkDataSource.getInvites(groupId)
        emit(response.asExternalModel())
    }.flowOn(ioDispatcher)


    /**
     * 초대 코드 재발급
     */
    override suspend fun refreshInvites(): Result<InviteCode> = runCatching {
        val groupId = getGroupIdOrThrow()
        val response = networkDataSource.refreshInvites(groupId)
        response.asExternalModel()
    }

    /**
     * 초대 코드로 그룹 정보 미리보기 (안 씀)
     */
    override fun getGroupInfoByInvite(inviteCode: String): Flow<GroupInviteInfoResponse> = flow {
        val response = networkDataSource.getGroupInfoByInvite(inviteCode)
        emit(response)
    }.flowOn(ioDispatcher)


    /**
     * DataStore에서 현재 내 그룹 ID를 가져옵니다. (없으면 에러)
     * 내부 확장 함수
     */
    private suspend fun getGroupIdOrThrow(): String {
        // userPreferences.userGroupId는 Flow이므로 first()로 현재 값을 스냅샷처럼 가져옵니다.
        return userPreferences.userGroupId.first()
            ?: throw IllegalStateException("로그인된 그룹 정보가 없습니다.")
    }

}

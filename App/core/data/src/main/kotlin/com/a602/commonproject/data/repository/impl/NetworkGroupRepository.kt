package com.a602.commonproject.data.repository.impl

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
import kotlinx.coroutines.flow.first

class NetworkGroupRepository @Inject constructor(
    private val networkDataSource: GroupNetworkDataSource,
    private val userPreferences: UserPreferencesDataStore, // ✨ DataStore 갱신용
) : GroupRepository {

    private suspend fun getGroupIdOrThrow(): String {
        // userPreferences.userGroupId는 Flow이므로 first()로 현재 값을 스냅샷처럼 가져옵니다.
        return userPreferences.userGroupId.first()
            ?: throw IllegalStateException("로그인된 그룹 정보가 없습니다.")
    }

    override suspend fun getMyGroup(): Result<Group> {
        return try {
            // 1. 서버 요청
            val response = networkDataSource.getMyGroup()

            // 2. 도메인 변환
            val group = response.asExternalModel()

            // 3. (안전장치) 서버에서 성공적으로 가져왔다면 내 로컬 ID도 싱크를 맞춤
            userPreferences.setGroupId(group.id)

            Result.success(group)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =================================================================
    // 👥 2. 그룹 멤버 조회
    // =================================================================
    override suspend fun getGroupMembers(): Result<List<GroupMember>> {
        return try {
            val groupId = getGroupIdOrThrow()

            // 1. 서버 요청
            val response = networkDataSource.getGroupMembers(groupId)

            // 2. 도메인 변환 (List<Response> -> List<Domain>)
            Result.success(response.asExternalModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =================================================================
    // ➕ 3. 그룹 생성
    // =================================================================
    override suspend fun createGroup(groupName: String, relation: String): Result<Unit> {
        return try {
            val request = CreateGroupRequest(groupName = groupName, relation = relation)
            val response = networkDataSource.createGroup(request)

            // ID 저장
            userPreferences.setGroupId(response.groupId)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =================================================================
    // 🔗 4. 그룹 가입
    // =================================================================
    override suspend fun joinGroup(invitationCode: String, relation: String): Result<Unit> {
        return try {
            val request = JoinGroupRequest(relation = relation)
            val response = networkDataSource.joinGroup(invitationCode, request)

            // ID 저장
            userPreferences.setGroupId(response.groupId)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =================================================================
    // ✏️ 6. 그룹 이름 수정
    // =================================================================
    override suspend fun updateGroupName(newName: String): Result<Unit> {
        return try {
            val groupId = getGroupIdOrThrow()
            val request = UpdateGroupRequest(groupName = newName)

            networkDataSource.updateGroup(groupId, request)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    // =================================================================
    // 👋 5. 그룹 나가기
    // =================================================================
    override suspend fun leaveGroup(): Result<Unit> {
        return try {
            // 여기서는 DataStore에 저장된 ID를 가져와서 서버에 보냄
            // (만약 이미 null이면 나갈 그룹이 없으니 성공 처리)
            val groupId = userPreferences.userGroupId.first() ?: return Result.success(Unit)

            networkDataSource.leaveGroup(groupId)

            // ID 삭제
            userPreferences.deleteGroupId()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getInvites(): Result<InviteCode> {
        return try {
            val groupId = getGroupIdOrThrow()
            val response = networkDataSource.getInvites(groupId)
            Result.success(response.asExternalModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun refreshInvites(): Result<InviteCode> {
        return try {
            val groupId = getGroupIdOrThrow()
            val response = networkDataSource.refreshInvites(groupId)
            Result.success(response.asExternalModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getGroupInfoByInvite(inviteCode: String): Result<GroupInviteInfoResponse> {
        return try {
            val response = networkDataSource.getGroupInfoByInvite(inviteCode)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

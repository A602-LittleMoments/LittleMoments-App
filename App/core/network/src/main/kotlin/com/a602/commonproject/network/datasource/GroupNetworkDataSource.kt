package com.a602.commonproject.network.datasource

import com.a602.commonproject.network.api.RetrofitGroupApi
import com.a602.commonproject.network.model.CreateGroupRequest
import com.a602.commonproject.network.model.GroupInviteInfoResponse
import com.a602.commonproject.network.model.GroupInviteResponse
import com.a602.commonproject.network.model.GroupMembersResponse
import com.a602.commonproject.network.model.GroupResponse
import com.a602.commonproject.network.model.JoinGroupRequest
import com.a602.commonproject.network.model.UpdateGroupRequest
import javax.inject.Inject


/**
 * 🏠 GroupNetworkDataSource 인터페이스
 *
 * 가족들이 모이는 '방(Group)'을 만들고 관리하는 기능들을 정의합니다.
 * 초대 코드 생성, 조회, 가입 등 그룹핑에 필수적인 로직이 포함됩니다.
 */
interface GroupNetworkDataSource {

    // 2.1 내 그룹 조회: 현재 내가 속한 가족 그룹의 정보를 가져옵니다.
    suspend fun getMyGroup(): GroupResponse

    // 2.2 그룹 생성: 새로운 가족 그룹을 만듭니다. (최초 가입 시)
    suspend fun createGroup(request: CreateGroupRequest): GroupResponse

    // 2.3 그룹 정보 수정: 그룹 이름 등을 바꿉니다.
    suspend fun updateGroup(groupId: String, request: UpdateGroupRequest)

    // 2.4 그룹 멤버 목록: 우리 가족 구성원(엄마, 아빠, 할머니 등) 리스트를 봅니다.
    suspend fun getGroupMembers(groupId: String): GroupMembersResponse

    // 2.5 초대 코드 조회: 다른 가족을 초대하기 위한 코드를 확인합니다.
    suspend fun getInvites(groupId: String): GroupInviteResponse

    // 2.6 초대 코드 갱신: 기존 코드를 만료시키고 새 코드를 발급받습니다.
    suspend fun refreshInvites(groupId: String): GroupInviteResponse

    // 2.7 초대 코드로 그룹 정보 조회: "이 코드가 어느 가족 방인가요?" 확인용 (가입 전 단계)
    suspend fun getGroupInfoByInvite(inviteCode: String): GroupInviteInfoResponse

    // 2.7.1 초대 코드로 그룹 참여: 확인 후 실제로 그룹에 들어갑니다.
    suspend fun joinGroup(inviteCode: String, request: JoinGroupRequest): GroupResponse

    // 2.8 그룹 나가기: 가족 그룹에서 탈퇴합니다.
    suspend fun leaveGroup(groupId: String)

}

/**
 * 🏗️ RetrofitGroupNetwork (구현체)
 *
 * ✨ 중요: 'internal' 키워드를 붙여서 RetrofitGroupApi 노출 에러를 방지했습니다.
 * 그룹 관련 API는 별도의 파일 변환 로직 없이, Retrofit에게 그대로 토스(Delegation)하는 구조가 많습니다.
 */
internal class RetrofitGroupNetwork @Inject constructor(
    private val groupApi: RetrofitGroupApi,
) : GroupNetworkDataSource {

    override suspend fun getMyGroup(): GroupResponse {
        return groupApi.getMyGroup()
    }

    override suspend fun createGroup(request: CreateGroupRequest): GroupResponse {
        return groupApi.createGroup(request)
    }

    override suspend fun updateGroup(groupId: String, request: UpdateGroupRequest) {
        groupApi.updateGroup(groupId, request)
    }

    override suspend fun getGroupMembers(groupId: String): GroupMembersResponse {
        return groupApi.getGroupMembers(groupId)
    }

    override suspend fun getInvites(groupId: String): GroupInviteResponse {
        return groupApi.getInvites(groupId)
    }

    override suspend fun refreshInvites(groupId: String): GroupInviteResponse {
        return groupApi.refreshInvites(groupId)
    }

    override suspend fun getGroupInfoByInvite(inviteCode: String): GroupInviteInfoResponse {
        return groupApi.getGroupInfoByInvite(inviteCode)
    }

    override suspend fun joinGroup(inviteCode: String, request: JoinGroupRequest): GroupResponse {
        return groupApi.joinGroup(inviteCode, request)
    }

    override suspend fun leaveGroup(groupId: String) {
        groupApi.leaveGroup(groupId)
    }
}

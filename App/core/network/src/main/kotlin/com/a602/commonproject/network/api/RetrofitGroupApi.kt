package com.a602.commonproject.network.api

import com.a602.commonproject.network.model.CreateGroupRequest
import com.a602.commonproject.network.model.GroupInviteInfoResponse
import com.a602.commonproject.network.model.GroupInviteResponse
import com.a602.commonproject.network.model.GroupMembersResponse
import com.a602.commonproject.network.model.GroupResponse
import com.a602.commonproject.network.model.JoinGroupRequest
import com.a602.commonproject.network.model.UpdateGroupRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

internal interface RetrofitGroupApi {
    // 2.1 내 그룹 조회
    @GET("groups/me")
    suspend fun getMyGroup(): GroupResponse

    // 2.2 그룹 생성
    @POST("groups")
    suspend fun createGroup(
        @Body request: CreateGroupRequest,
    ): GroupResponse

    // 2.3 그룹 이름 변경
    @PUT("groups/{groupId}")
    suspend fun updateGroup(
        @Path("groupId") groupId: String,
        @Body request: UpdateGroupRequest,
    )

    // 2.4 그룹 멤버 목록 조회
    @GET("groups/{groupId}/members")
    suspend fun getGroupMembers(
        @Path("groupId") groupId: String,
    ): GroupMembersResponse

    // 2.5 초대 코드 조회
    @GET("groups/{groupId}/invites")
    suspend fun getInvites(
        @Path("groupId") groupId: String,
    ): GroupInviteResponse

    // 2.6 초대 코드 갱신
    @POST("groups/{groupId}/invites/refresh")
    suspend fun refreshInvites(
        @Path("groupId") groupId: String
    ): GroupInviteResponse

    // 2.7 초대코드로 그룹 조회
    @GET("groups/invite/{inviteCode}")
    suspend fun getGroupInfoByInvite(
        @Path("inviteCode") inviteCode: String
    ): GroupInviteInfoResponse

    // 2.7.1 초대코드로 그룹 참여
    @POST("groups/invite/{inviteCode}")
    suspend fun joinGroup(
        @Path("inviteCode") inviteCode: String,
        @Body request: JoinGroupRequest
    ): GroupResponse

    // 2.8 그룹 나가기
    @POST("groups/{groupId}/leave")
    suspend fun leaveGroup(
        @Path("groupId") groupId: String
    )
}

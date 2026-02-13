package com.a602.commonproject.data.model

import com.a602.commonproject.model.data.InviteCode
import com.a602.commonproject.network.model.GroupInviteResponse

/**
 * [Network -> UI] 초대 코드 생성/조회 결과 변환
 */
fun GroupInviteResponse.asExternalModel(): InviteCode {
    return InviteCode(
        codeMember = inviteCodeMember,
        codeViewer = inviteCodeViewer,

        // 서버의 updatedAt(갱신 시간)을 UI의 expiredAt(만료 기준)으로 매핑
        // (기획에 따라 +10분 등 시간 계산이 필요하다면 여기서 처리하거나 UI에서 처리)
        expiredAt = updatedAt,
    )
}

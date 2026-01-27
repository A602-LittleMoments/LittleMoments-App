package com.a602.commonproject.data.model

import com.a602.commonproject.model.data.DailyQuestion
import com.a602.commonproject.network.model.DailyQuestionResponse

// =================================================================
// 1. [Network -> UI] 오늘의 질문 조회 결과 변환
// =================================================================
fun DailyQuestionResponse.asExternalModel(): DailyQuestion {
    return DailyQuestion(
        questionId = questionId,  // Network: questionId
        content = content // Network: content
    )
}

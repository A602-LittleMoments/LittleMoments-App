package com.a602.commonproject.data.repository

import com.a602.commonproject.model.data.DailyQuestion

/**
 * 사용 X
 * 📅 DailyQuestionRepository
 * 매일 바뀌는 '오늘의 질문' 데이터를 관리합니다.
 */
interface DailyQuestionRepository {

    /**
     * 오늘의 질문 가져오기
     * - 성공 시: 질문 내용(DailyQuestion) 반환
     * - 실패 시: 에러 반환 (네트워크 오류 등)
     */
    suspend fun getTodayQuestion(): Result<DailyQuestion>
}

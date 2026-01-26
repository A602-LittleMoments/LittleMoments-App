package com.a602.commonproject.network.datasource

import com.a602.commonproject.network.api.RetrofitQuestionApi
import com.a602.commonproject.network.model.DailyQuestionResponse
import javax.inject.Inject

/**
 * 📅 QuestionNetworkDataSource 인터페이스
 *
 * 가족 간의 소통을 위한 '오늘의 질문' 기능을 담당합니다.
 * 매일 변경되는 질문 텍스트를 가져옵니다.
 */
interface QuestionNetworkDataSource {
    // 8.1 오늘의 질문 조회
    suspend fun getTodayQuestion(): DailyQuestionResponse
}
/**
 * 🏗️ RetrofitQuestionNetwork (구현체)
 */
internal class RetrofitQuestionNetwork @Inject constructor(
    private val questionApi: RetrofitQuestionApi
) : QuestionNetworkDataSource {

    override suspend fun getTodayQuestion() = questionApi.getTodayQuestion()
}

package com.a602.commonproject.data.repository.impl

import com.a602.commonproject.data.model.asExternalModel
import com.a602.commonproject.data.repository.DailyQuestionRepository
import com.a602.commonproject.model.data.DailyQuestion
import com.a602.commonproject.network.datasource.QuestionNetworkDataSource
import javax.inject.Inject

class NetworkDailyQuestionRepository @Inject constructor(
    private val networkDataSource: QuestionNetworkDataSource
) : DailyQuestionRepository {

    override suspend fun getTodayQuestion(): Result<DailyQuestion> {
        return try {
            // 1. 서버 요청
            // (GET daily-questions/today)
            val response = networkDataSource.getTodayQuestion()

            // 2. 도메인 모델로 변환
            // (DailyQuestionResponse -> DailyQuestion)
            val domainModel = response.asExternalModel()

            Result.success(domainModel)
        } catch (e: Exception) {
            // 네트워크 오류 등을 Result.failure로 감싸서 반환
            Result.failure(e)
        }
    }
}

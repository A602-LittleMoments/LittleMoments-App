
package com.a602.commonproject.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.SlideshowRepository
import com.a602.commonproject.model.data.Slideshow
import com.a602.commonproject.network.model.CreateSlideshowRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class HighlightLoadingViewModel @Inject constructor(
    private val repository: SlideshowRepository
) : ViewModel() {

    fun createAndWaitSlideshow(
        startMillis: Long,
        endMillis: Long,
        onSuccess: (String) -> Unit, // COMPLETED 상태의 slideshowId 전달
        onFailure: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Long → ISO 8601 String 변환
                val startDate = Instant.ofEpochMilli(startMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .toString()

                val endDate = Instant.ofEpochMilli(endMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .toString()

                val request = CreateSlideshowRequest(
                    projectType = "PERIOD",
                    startDate = startDate,
                    endDate = endDate,
                    keywordId = null
                )

                // 1️Repository: 서버 요청 → DB 저장
                repository.createSlideshow(request)
                    .onSuccess {
                        // DB 저장 완료 대기
                        delay(500)

                        // 2️방금 생성된 슬라이드쇼 찾기
                        val slideshows = repository.getSlideshowsStream().first()
                        val latest = slideshows
                            .filter {
                                it.status == Slideshow.MakeStatus.QUEUED ||
                                    it.status == Slideshow.MakeStatus.PROCESSING
                            }
                            .maxByOrNull { it.createdAt }

                        if (latest != null) {
                            // 3️완성될 때까지 폴링 (주기적 확인)
                            pollUntilCompleted(latest.id, onSuccess, onFailure)
                        } else {
                            throw Exception("생성된 슬라이드쇼를 찾을 수 없습니다")
                        }
                    }
                    .onFailure { error ->
                        onFailure(error)
                    }
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    /**
     * 슬라이드쇼가 완성될 때까지 주기적으로 상태 확인
     * - 5초마다 서버에서 최신 상태 가져오기
     * - COMPLETED → 성공
     * - FAILED → 실패
     * - 최대 5분 대기 (60회 폴링)
     */
    private suspend fun pollUntilCompleted(
        slideshowId: String,
        onSuccess: (String) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val maxAttempts = 60 // 5분 (5초 * 60회)
        var attempts = 0

        while (attempts < maxAttempts) {
            attempts++

            // 서버에서 최신 상태 가져오기
            repository.syncSlideshowDetail(slideshowId)
                .onSuccess {
                    // DB에서 현재 상태 확인
                    val slideshows = repository.getSlideshowsStream().first()
                    val current = slideshows.find { it.id == slideshowId }

                    when (current?.status) {
                        Slideshow.MakeStatus.COMPLETED -> {
                            // 결과 화면으로 이동
                            onSuccess(slideshowId)
                            return
                        }
                        Slideshow.MakeStatus.FAILED -> {
                            // ❌ 실패
                            onFailure(Exception("슬라이드쇼 생성에 실패했습니다"))
                            return
                        }
                        else -> {
                            delay(5000)
                        }
                    }
                }
                .onFailure { error ->
                    delay(5000)
                }
        }

        onFailure(Exception("슬라이드쇼 생성 시간이 초과되었습니다"))
    }
}

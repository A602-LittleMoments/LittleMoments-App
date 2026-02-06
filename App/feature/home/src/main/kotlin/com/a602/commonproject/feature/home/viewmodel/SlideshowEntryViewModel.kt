package com.a602.commonproject.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.SlideshowRepository
import com.a602.commonproject.network.model.CreateSlideshowRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class SlideshowEntryViewModel @Inject constructor(
    private val slideshowRepository: SlideshowRepository
) : ViewModel() {

    /**
     * 슬라이드쇼 생성 API 호출
     * @param request ByKeyword 또는 ByDateRange
     * @param onSuccess 성공 시 콜백
     * @param onFailure 실패 시 콜백
     */
    fun createSlideshow(
        request: SlideshowRequest,
        onSuccess: suspend () -> Unit,
        onFailure: suspend (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val (apiRequest, title) = when (request) {
                    is SlideshowRequest.ByKeyword -> {
                        // 키워드(행성) 기반 슬라이드쇼 생성
                        Pair(
                            CreateSlideshowRequest(
                                projectType = "KEYWORD",
                                startDate = null,
                                endDate = null,
                                keywordId = request.keyword
                            ),
                            request.label // "웃음"
                        )
                    }
                    is SlideshowRequest.ByDateRange -> {
                        // 날짜 범위 기반 슬라이드쇼 생성
                        // [Fix] DateRangePicker returns UTC start-of-day. Use UTC to preserve the selected date.
                        val startDate = Instant.ofEpochMilli(request.startDate)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                            .toString()

                        val endDate = Instant.ofEpochMilli(request.endDate)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                            .toString()

                        Pair(
                            CreateSlideshowRequest(
                                projectType = "PERIOD",
                                startDate = startDate,
                                endDate = endDate,
                                keywordId = null
                            ),
                            request.label // "2023.01.01 ~ 2023.01.31"
                        )
                    }
                }

                slideshowRepository.createSlideshow(apiRequest, title)
                    .onSuccess {
                        onSuccess()
                    }
                    .onFailure { error ->
                        onFailure(error.message ?: "슬라이드쇼 생성 실패")
                    }
            } catch (e: Exception) {
                onFailure(e.message ?: "슬라이드쇼 생성 실패")
            }
        }
    }
}

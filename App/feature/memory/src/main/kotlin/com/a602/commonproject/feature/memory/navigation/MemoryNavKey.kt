package com.a602.commonproject.feature.memory.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// 추억 들어오면 처음 연결될 곳
// 데이터 있을때, 없을 때 분기될 예정
@Serializable
object MemoryNavKey : NavKey

// 행성 선택하면 연결될 사진 목록
@Serializable
object MemoryGridKey : NavKey

// 사진 선택하면 상세 볼 수 있게
@Serializable
object MemoryDetailKey : NavKey

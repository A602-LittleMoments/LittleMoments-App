package com.a602.commonproject.data.repository

import javax.inject.Inject

// 앱 전체에서 하나만 쓰려면 추가
class MediaRepositoryImpl @Inject constructor(
    // 여기에 만약 Retrofit 서비스나 Dao가 있다면 그것들도 Hilt에 등록되어 있어야 함
) : SharedMediaRepository{
    /* ... */
    override suspend fun syncWithServer(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun uploadUnsyncedMedia(): Boolean {
        TODO("Not yet implemented")
    }
}

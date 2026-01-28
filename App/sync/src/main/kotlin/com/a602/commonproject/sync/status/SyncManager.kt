package com.a602.commonproject.sync.status

import kotlinx.coroutines.flow.Flow

/**
 * 외부에서 동기화를 요청할 떄 사용하는 인터페이스
 */
interface SyncManager {
    val isSyncing: Flow<Boolean>
    fun requestSync(groupId: String)
}

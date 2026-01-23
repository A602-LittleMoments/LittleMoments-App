package com.a602.commonproject.sync.status

import android.util.Log
import javax.inject.Inject

/**
 * [StubSyncSubscriber]
 * 실제로는 아무것도 하지 않는 더미 구현체입니다.
 * 나중에 FCM 등을 붙일 때 실제 구현체(FirebaseSyncSubscriber)로 교체하면 됩니다.
 */
class StubSyncSubscriber @Inject constructor() : SyncSubscriber {
    override suspend fun subscribe() {
        Log.d("StubSyncSubscriber", "Subscribing to sync (Nothing to do yet)")
    }
}

package com.a602.commonproject.sync.initializers

import android.content.Context
import androidx.startup.Initializer
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.a602.commonproject.common.network.Dispatcher
import com.a602.commonproject.datastore.datastore.UserPreferencesDataSource
import com.a602.commonproject.sync.status.SyncManager
import com.a602.commonproject.sync.status.SyncSubscriber
import com.a602.commonproject.sync.status.WorkManagerSyncManager.Companion.SYNC_WORK_NAME
import com.a602.commonproject.sync.workers.FetchWorker
import com.a602.commonproject.sync.workers.UploadWorker
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


/**
 * [SyncInitializer]
 * 앱 프로세스가 시작될 때(Application.onCreate 시점) 자동으로 실행되는 초기화 클래스입니다.
 * AndroidX App Startup 라이브러리를 사용합니다.
 */
class SyncInitializer : Initializer<Unit> {

    /**
     * 앱이 시작될 때 실행되는 함수입니다.
     * 여기서 동기화 작업 예약과 FCM 구독을 수행합니다.
     */
    override fun create(context: Context) {


        // 1. Hilt 의존성 주입 (EntryPoint 패턴 사용)
        // App Startup의 Initializer는 안드로이드 시스템이 생성하므로,
        // @Inject 어노테이션을 직접 사용할 수 없습니다.
        // 따라서 EntryPointAccessors를 통해 수동으로 Hilt 그래프에서 객체를 꺼내옵니다.
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            SyncEntryPoint::class.java
        )

        // Hilt로부터 SyncManager(데이터 동기화 관리자)와 SyncSubscriber(FCM 구독자)를 가져옵니다.
        val syncManager = entryPoint.syncManager()
        val syncSubscriber = entryPoint.syncSubscriber()
        // ✨ DataStore에서 groupId를 꺼내기 위해 추가
        val userPreferences = entryPoint.userPreferences()

        // 2. 비동기 작업 실행 (DataStore 읽기 + 동기화 예약 + FCM 구독)
        // Main Thread를 차단하지 않기 위해 IO Dispatcher 사용
        CoroutineScope(Dispatchers.IO).launch {

            // [STEP A] 저장된 Group ID 가져오기
            // first(): 현재 저장된 값 하나만 딱 가져오고 끝냄 (Flow 구독 아님)
            val groupId = userPreferences.userGroupId.first()

            // [STEP B] 로그인이 되어 있어서 Group ID가 있다면 -> 동기화 시작
            if (!groupId.isNullOrBlank()) {
                syncManager.requestSync(groupId)
            }

            // [STEP C] FCM 구독 시작
            syncSubscriber.subscribe()
        }
    }

    /**
     * 이 초기화 작업보다 먼저 실행되어야 할 다른 Initializer가 있다면 여기에 등록합니다.
     * (현재는 없으므로 빈 리스트 반환)
     */
    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }

    // =================================================================
    // 💉 Hilt EntryPoint 인터페이스
    // Hilt에게 "이 객체들을 외부(Initializer)에서 꺼내 쓸 수 있게 해줘"라고 정의하는 창구입니다.
    // =================================================================
    @EntryPoint
    @InstallIn(SingletonComponent::class) // 앱 전체 생명주기 동안 유효한 컴포넌트 사용
    interface SyncEntryPoint {
        fun syncManager(): SyncManager
        fun syncSubscriber(): SyncSubscriber
        fun userPreferences(): UserPreferencesDataSource // ✨ 추가됨
    }
}

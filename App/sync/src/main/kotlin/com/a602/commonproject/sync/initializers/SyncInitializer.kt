package com.a602.commonproject.sync.initializers

import android.content.Context
import androidx.startup.Initializer
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.a602.commonproject.common.network.Dispatcher
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

        // 2. 데이터 동기화 작업 예약 (WorkManager)
        // "서버랑 데이터를 맞춰줘"라고 요청합니다.
        // 내부적으로 [UploadWorker -> FetchWorker] 순서로 작업이 예약되며,
        // 앱이 종료되어도 백그라운드에서 WorkManager가 보장합니다.
        syncManager.requestSync()

        // 3. FCM 알림 주제(Topic) 구독 시작
        // subscribe() 함수는 네트워크 통신을 하는 suspend 함수이므로,
        // 메인 스레드(UI)를 멈추지 않기 위해 별도의 코루틴(IO 스레드)에서 실행합니다.
        CoroutineScope(Dispatchers.IO).launch {
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
    }
}

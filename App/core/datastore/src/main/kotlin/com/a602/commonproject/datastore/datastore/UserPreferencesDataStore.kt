package com.a602.commonproject.datastore.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Singleton
class UserPreferencesDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    // =================================================================
    // 🔑 1. Key 정의 (데이터의 이름표)
    // "어떤 이름으로 저장할지" 정의합니다. (오타 방지를 위해 상수로 관리)
    // =================================================================
    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")

        // 사용자 프로필 정보
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        private val KEY_USER_NICKNAME = stringPreferencesKey("user_nickname")
        private val KEY_USER_PROFILE_IMAGE = stringPreferencesKey("user_profile_image") // ✨ 추가됨

        // ✨ [추가] 그룹 ID (백그라운드 동기화의 핵심 키)
        private val KEY_GROUP_ID = stringPreferencesKey("group_id")

        // ✨ [추가] 기기 고유 식별자 키
        private val KEY_DEVICE_ID = stringPreferencesKey("device_id")
    }

    // =================================================================
    // 2. 조회 (Read) - Flow로 실시간 관찰
    // =================================================================


    /**
     * 저장된 Access Token을 실시간으로 구독합니다.
     * - 값이 없으면(로그인 전) null을 반환합니다.
     * - 값이 변경되면(로그인/로그아웃) 자동으로 새 값을 방출합니다.
     */
    val accessToken: Flow<String?> = dataStore.data.map { prefs ->
        prefs[KEY_ACCESS_TOKEN]
    }

    /**
     * 저장된 Refresh Token을 가져옵니다.
     * - Access Token이 만료되었을 때, 이 토큰으로 재발급 요청을 합니다.
     */
    val refreshToken: Flow<String?> = dataStore.data.map { prefs ->
        prefs[KEY_REFRESH_TOKEN]
    }

    // 사용자 정보 전체
    // (화면에 뿌리기 좋게 데이터 클래스로 묶지 않고 Flow로 각각 제공하거나, 필요 시 묶습니다)
    // 여기서는 간단하게 각각 제공하는 방식과 묶는 방식을 보여드릴게요.
    val userEmail: Flow<String?> = dataStore.data.map { prefs -> prefs[KEY_USER_EMAIL] }       // ✨ 추가됨
    val userNickname: Flow<String?> = dataStore.data.map { prefs -> prefs[KEY_USER_NICKNAME] }
    val userProfileImage: Flow<String?> = dataStore.data.map { prefs -> prefs[KEY_USER_PROFILE_IMAGE] } // ✨ 추가됨

    // ✨ [추가] 그룹 ID 조회 (Worker나 FCM 서비스에서 사용)
    val userGroupId: Flow<String?> = dataStore.data.map { prefs -> prefs[KEY_GROUP_ID] }

    /**
     * ✨ [추가] 저장된 기기 고유 ID(UUID)를 가져옵니다.
     */
    val deviceId: Flow<String?> = dataStore.data.map { prefs ->
        prefs[KEY_DEVICE_ID]
    }



    // =================================================================
    // 3. 쓰기 (Write) - suspend 함수 (비동기)
    // =================================================================

    /**
     * [로그인 성공 시] 서버에서 받은 모든 정보를 한 번에 저장합니다.
     * - edit 블록은 "트랜잭션"으로 동작하여, 중간에 실패하면 저장되지 않습니다. (안전함)
     */
    suspend fun setAuthData(
        accessToken: String,
        refreshToken: String,
        email: String,
        nickname: String,
        profileImageUrl: String? = null,
        groupId: String? // ✨ 파라미터 추가됨
    ) {
        dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = accessToken
            prefs[KEY_REFRESH_TOKEN] = refreshToken
            prefs[KEY_USER_EMAIL] = email
            prefs[KEY_USER_NICKNAME] = nickname
            if (profileImageUrl != null) {
                prefs[KEY_USER_PROFILE_IMAGE] = profileImageUrl
            }
            // ✨ 그룹 ID 저장
            if (groupId != null) {
                prefs[KEY_GROUP_ID] = groupId
            }
        }
    }

    /**
     * [내 정보 수정/새로고침 시] 유저 정보만 업데이트 (토큰 유지)
     */
    suspend fun setUserData(
        email: String?, // 이메일은 변경 안되면 null 전달
        nickname: String,
        profileImageUrl: String? = null,
    ) {
        dataStore.edit { prefs ->
            if (email != null) prefs[KEY_USER_EMAIL] = email
            prefs[KEY_USER_NICKNAME] = nickname

            if (profileImageUrl != null) {
                prefs[KEY_USER_PROFILE_IMAGE] = profileImageUrl
            }
        }
    }

    /**
     * ✨ [신규] 그룹 ID만 확실하게 업데이트
     */
    suspend fun setGroupId(id: String) {
        dataStore.edit { prefs ->
            prefs[KEY_GROUP_ID] = id
        }
    }

    /**
     * ✨ [신규] 그룹 ID만 확실하게 삭제 (탈퇴 시 사용)
     */
    suspend fun deleteGroupId() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_GROUP_ID)
        }
    }

    /**
     * [토큰 갱신 시] Access Token만 갈아끼웁니다.
     * - 사용자가 앱을 쓰는 도중 토큰이 만료되어 재발급받았을 때 호출합니다.
     */
    suspend fun updateAccessToken(newAccessToken: String) {
        dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = newAccessToken
        }
    }

    /**
     * [로그아웃/회원탈퇴 시] 저장된 모든 정보를 삭제합니다.
     * - prefs.clear()를 호출하면 파일 내용이 깨끗하게 비워집니다.
     * - 이때 accessToken Flow는 자동으로 null을 방출하게 되어,
     * 앱이 이를 감지하고 로그인 화면으로 이동시킬 수 있습니다.
     */
    suspend fun clear() {
        dataStore.edit { prefs ->
            // [Fix] Device ID는 기기 식별자이므로 로그아웃/초기화 시에도 유지되어야 합니다.
            val currentDeviceId = prefs[KEY_DEVICE_ID]

            prefs.clear()

            // Device ID 복구
            if (currentDeviceId != null) {
                prefs[KEY_DEVICE_ID] = currentDeviceId
            }
        }
    }


    /**
     * ✨ [추가] 앱 최초 실행 시 기기 고유 ID를 생성하거나 기존 ID를 반환합니다.
     * 이 함수는 앱이 시작될 때나 FCM 토큰을 서버에 보낼 때 호출하여 기기 식별자로 사용하세요.
     */
    suspend fun getOrCreateDeviceId(): String {
        val currentId = deviceId.first() // 현재 저장된 값이 있는지 확인
        if (currentId != null) return currentId

        // 저장된 값이 없으면 새로 생성 후 저장
        val newId = UUID.randomUUID().toString()
        dataStore.edit { prefs ->
            prefs[KEY_DEVICE_ID] = newId
        }
        return newId
    }
}

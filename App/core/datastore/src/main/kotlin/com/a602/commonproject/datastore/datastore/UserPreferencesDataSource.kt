package com.a602.commonproject.datastore.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class UserPreferencesDataSource @Inject constructor(
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
        private val KEY_USER_ID = stringPreferencesKey("user_id")     // UUID or Server ID
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        private val KEY_USER_NICKNAME = stringPreferencesKey("user_nickname")
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
    val userNickname: Flow<String?> = dataStore.data.map { prefs ->
        prefs[KEY_USER_NICKNAME]
    }

    // =================================================================
    // 3. 쓰기 (Write) - suspend 함수 (비동기)
    // =================================================================

    /**
     * [로그인 성공 시] 서버에서 받은 모든 정보를 한 번에 저장합니다.
     * - edit 블록은 "트랜잭션"으로 동작하여, 중간에 실패하면 저장되지 않습니다. (안전함)
     */
    suspend fun saveLoginInfo(
        accessToken: String,
        refreshToken: String,
        userId: String,
        email: String,
        nickname: String,
    ) {
        dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = accessToken
            prefs[KEY_REFRESH_TOKEN] = refreshToken
            prefs[KEY_USER_ID] = userId
            prefs[KEY_USER_EMAIL] = email
            prefs[KEY_USER_NICKNAME] = nickname
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
            prefs.clear()
        }
    }
}

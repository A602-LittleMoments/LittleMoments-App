package com.a602.commonproject.model.data

/**
 * 🖼️ 사진/동영상 UI 데이터 모델
 * (DB의 효율성과 Network의 풍부한 정보를 모두 담음)
 */
data class Media(
    val id: String,            // DB: mediaId, Net: mediaId
    val type: MediaType,       // DB: "PHOTO"/"VIDEO" -> Enum 변환

    // 1. 경로 정보 (뒷 사진)
    val localUri: String?,     // 업로드 전 원본 경로
    val remoteUrl: String?,    // 서버 저장 경로 (storageUrl)
    val thumbnailUrl: String?, // 리스트용 썸네일 (thumbUrl)

    // 2. ✨ 서브 경로 (앞 사진)
    // Map 대신 이렇게 변수를 직접 만들면 Converter가 필요 없습니다!
    val subLocalUri: String? = null,
    val subRemoteUrl: String? = null,
    val subThumbnailUrl: String? = null,

    // 3. ✨ 카메라 방향 정보 ("REAR", "FRONT", "DUAL")
    val cameraFacing: String = "REAR",

    // 2. 메타 데이터
    val caption: String?,      // 사진 설명
    val dateTaken: Long,       // ⚠️ DB(Long) 기준 통일. (Net의 String은 변환해서 넣음)
    val orientation: Int,      // 회전 정보 (0, 90...)

    val uploaderName: String?, // 업로더 닉네임 (DB: uploaderName)

    // 4. 상태 관리
    val syncStatus: SyncStatus // 동기화 상태 (구름 아이콘)
) {

    enum class MediaType {
        PHOTO, VIDEO;

        // DB 문자열("PHOTO")을 Enum으로 변환하기 위한 헬퍼
        companion object {
            fun from(value: String): MediaType = try {
                valueOf(value)
            } catch (e: Exception) {
                PHOTO // 기본값
            }
        }
    }

    enum class SyncStatus {
        NOT_UPLOADED, // ☁️ 업로드 대기
        SYNCED,       // ✅ 업로드 완료
        TO_BE_DELETED // 🗑️ 삭제 예정
    }

    /**
     * 💡 UI 표시 우선순위 로직
     * 1. 로컬 원본이 있으면 가장 먼저 보여줌 (빠름)
     * 2. 없으면 서버 URL (고화질)
     * 3. 정 없으면 썸네일이라도 보여줌
     */
    val displayUrl: String?
        get() = localUri ?: remoteUrl ?: thumbnailUrl

}

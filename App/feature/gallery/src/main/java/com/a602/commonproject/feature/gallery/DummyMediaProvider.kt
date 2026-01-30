package com.a602.commonproject.feature.gallery

import com.a602.commonproject.model.data.SharedMedia

object DummyMediaProvider {

    val medias: List<SharedMedia> = listOf(
        SharedMedia(
            id = "m1",
            type = SharedMedia.MediaType.PHOTO,
            localUri = null,
            remoteUrl = "https://picsum.photos/seed/a/900/1200",
            thumbnailUrl = "https://picsum.photos/seed/a/400/600",
            subLocalUri = null,
            subRemoteUrl = null,
            subThumbnailUrl = null,
            cameraFacing = "REAR",
            caption = "첫 번째 사진",
            dateTaken = System.currentTimeMillis(),
            orientation = 0,
            uploaderName = "엄마",
            syncStatus = SharedMedia.SyncStatus.SYNCED
        ),
        SharedMedia(
            id = "m2",
            type = SharedMedia.MediaType.PHOTO,
            localUri = null,
            remoteUrl = "https://picsum.photos/seed/b/900/1200",
            thumbnailUrl = "https://picsum.photos/seed/b/400/600",
            subLocalUri = null,
            subRemoteUrl = null,
            subThumbnailUrl = null,
            cameraFacing = "REAR",
            caption = "두 번째 사진",
            dateTaken = System.currentTimeMillis(),
            orientation = 0,
            uploaderName = "아빠",
            syncStatus = SharedMedia.SyncStatus.SYNCED
        )
    )
}

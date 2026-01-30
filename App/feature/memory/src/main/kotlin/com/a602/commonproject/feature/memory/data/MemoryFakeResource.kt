package com.a602.commonproject.feature.memory.data

import com.a602.commonproject.model.data.SharedMedia
import com.a602.commonproject.model.data.Collection

object MemoryFakeResource {

    val collections: List<Collection> = listOf(
        Collection("c1", "물건", "k1", "인형", 12),
        Collection("c2", "음식", "k2", "밥", 20),
        Collection("c3", "인물", "k3", "엄마", 30),
        Collection("c4", "기념", "k4", "생일", 5),
        Collection("c5", "여행", "k5", "바다", 50),
    )

    fun medias(keywordId: String): List<SharedMedia> {
        val base = "https://picsum.photos/seed/$keywordId"
        return List(12) { i ->
            SharedMedia(
                id = "$keywordId-$i",
                type = SharedMedia.MediaType.PHOTO,
                localUri = null,
                remoteUrl = "$base-$i/900/1200",
                thumbnailUrl = "$base-$i/450/600",
                subLocalUri = null,
                subRemoteUrl = "https://picsum.photos/seed/sub-$keywordId-$i/360/480",
                subThumbnailUrl = null,
                cameraFacing = "DUAL",
                caption = "$keywordId 더미 $i",
                dateTaken = System.currentTimeMillis() - i * 86_400_000L,
                orientation = 0,
                uploaderName = if (i % 2 == 0) "엄마" else "아빠",
                syncStatus = SharedMedia.SyncStatus.SYNCED
            )
        }
    }

    fun media(keywordId: String, mediaId: String): SharedMedia? =
        medias(keywordId).firstOrNull { it.id == mediaId }
}

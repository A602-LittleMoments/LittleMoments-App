package com.a602.commonproject.feature.memory.data

import com.a602.commonproject.data.repository.SharedMediaRepository
import com.a602.commonproject.model.data.SharedMedia
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.io.File

class FakeSharedMediaRepository : SharedMediaRepository {

    override fun getSharedAlbumStream(): Flow<List<SharedMedia>> =
        flowOf(
            MemoryFakeResource.medias("k1")
        )

    override suspend fun saveNewMedia(
        groupId: String,
        mainFile: File,
        subFile: File?,
        caption: String?,
        cameraFacing: String
    ): Result<Unit> = Result.success(Unit)

    override suspend fun deleteMedia(mediaId: String): Result<Unit> =
        Result.success(Unit)

    override suspend fun updateCaption(mediaId: String, caption: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun syncDeletedMedia(groupId: String): Boolean = true
    override suspend fun syncWithServer(groupId: String): Boolean = true
    override suspend fun uploadUnsyncedMedia(groupId: String): Boolean = true
}

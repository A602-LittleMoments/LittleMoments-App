package com.a602.commonproject.feature.memory.data

import com.a602.commonproject.data.repository.CollectionRepository
import com.a602.commonproject.model.data.Collection
import com.a602.commonproject.model.data.SharedMedia

class FakeCollectionRepository : CollectionRepository {

    override suspend fun getCollections(
        type: String,
        limit: Int
    ): Result<List<Collection>> {
        return Result.success(
            MemoryFakeResource.collections.take(limit)
        )
    }

    override suspend fun getCollectionDetail(
        keywordId: String,
        cursor: String?
    ): Result<List<SharedMedia>> {
        return Result.success(
            MemoryFakeResource.medias(keywordId)
        )
    }
}

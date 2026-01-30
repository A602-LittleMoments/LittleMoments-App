package com.a602.commonproject.feature.memory.data

import com.a602.commonproject.data.repository.CollectionRepository
import com.a602.commonproject.data.repository.SharedMediaRepository

/**
 * core/data 안 건드리고 feature에서만 Fake ↔ Real 전환용
 */
object MemoryRepoProvider {

    val collectionRepository: CollectionRepository =
        FakeCollectionRepository()

    val sharedMediaRepository: SharedMediaRepository =
        FakeSharedMediaRepository()
}

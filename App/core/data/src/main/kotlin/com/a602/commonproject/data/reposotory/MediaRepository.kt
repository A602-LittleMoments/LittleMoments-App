package com.a602.commonproject.data.reposotory

interface MediaRepository {

    suspend fun syncWithServer() : Boolean

    suspend fun uploadUnsyncedMedia() :Boolean
}

package com.a602.commonproject.database.di

import android.content.Context
import androidx.room.Room
import com.a602.commonproject.database.LMDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun provideLMDatabase(
        @ApplicationContext context: Context
    ): LMDatabase = Room.databaseBuilder(
        context,
        LMDatabase::class.java,
        "lm-database"
    ).build()

}

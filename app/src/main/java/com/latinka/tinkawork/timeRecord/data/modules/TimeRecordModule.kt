package com.latinka.tinkawork.timeRecord.data.modules

import com.latinka.tinkawork.timeRecord.data.repositories.TimeRecordRepository
import com.latinka.tinkawork.timeRecord.data.repositories.TimeRecordRepositoryImpl

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class TimeRecordModule {

    @Singleton
    @Provides
    fun provideTimeRecordRepository() : TimeRecordRepository {
        return TimeRecordRepositoryImpl()
    }
}
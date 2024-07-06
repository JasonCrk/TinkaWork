package com.latinka.tinkawork.breaks.data.modules

import com.latinka.tinkawork.breaks.data.repositories.BreakRepository
import com.latinka.tinkawork.breaks.data.repositories.BreakRepositoryImpl

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class BreakModule {

    @Singleton
    @Provides
    fun provideBreakRepository() : BreakRepository {
        return BreakRepositoryImpl()
    }
}
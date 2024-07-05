package com.latinka.tinkawork.establishment.data.modules

import com.latinka.tinkawork.establishment.data.repositories.EstablishmentRepository
import com.latinka.tinkawork.establishment.data.repositories.EstablishmentRepositoryImpl

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class EstablishmentModule {

    @Singleton
    @Provides
    fun provideEstablishmentRepository(): EstablishmentRepository {
        return EstablishmentRepositoryImpl()
    }
}
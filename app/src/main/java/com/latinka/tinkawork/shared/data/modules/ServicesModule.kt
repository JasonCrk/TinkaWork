package com.latinka.tinkawork.shared.data.modules

import com.latinka.tinkawork.shared.data.services.FirebaseStorageService
import com.latinka.tinkawork.shared.data.services.FirebaseStorageServiceImpl

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ServicesModule {

    @Provides
    @Singleton
    fun provideFirebaseStorageService(): FirebaseStorageService {
        return FirebaseStorageServiceImpl()
    }
}
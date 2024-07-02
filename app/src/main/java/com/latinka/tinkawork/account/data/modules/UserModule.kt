package com.latinka.tinkawork.account.data.modules

import com.latinka.tinkawork.account.data.repositories.UserRepository
import com.latinka.tinkawork.account.data.repositories.UserRepositoryImpl

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UserModule {

    @Singleton
    @Provides
    fun provideUserRepository(): UserRepository {
        return UserRepositoryImpl()
    }
}
package com.dalvik.dimadenyslessons.dec262024.di

import com.dalvik.dimadenyslessons.dec262024.repository.ContactsRepository
import com.dalvik.dimadenyslessons.dec262024.repository.ContactsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {

    @Binds
    fun bindContactsRepository(impl: ContactsRepositoryImpl): ContactsRepository
}
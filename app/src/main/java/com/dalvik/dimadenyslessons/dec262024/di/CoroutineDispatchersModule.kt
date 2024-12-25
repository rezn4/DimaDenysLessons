package com.dalvik.dimadenyslessons.dec262024.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Singleton

@Singleton
open class CoroutineDispatchers @Inject constructor() {
    open val IO: CoroutineDispatcher = Dispatchers.IO
    open val Main: MainCoroutineDispatcher = Dispatchers.Main
    open val Default: CoroutineDispatcher = Dispatchers.Default
    open val Unconfined: CoroutineDispatcher = Dispatchers.Unconfined
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

@Module
@InstallIn(SingletonComponent::class)
object CoroutinesModule {
    @Provides
    @Singleton
    @ApplicationScope
    fun applicationScope(dispatchers: CoroutineDispatchers) = CoroutineScope(
        SupervisorJob() + dispatchers.Main.immediate
    )
}
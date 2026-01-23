package com.example.chatapp.feature.work

import com.example.chatapp.core.domain.repository.IWorkScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WorkModule {

    @Binds
    @Singleton
    abstract fun bindWorkScheduler(
        workSchedulerImpl: WorkSchedulerImpl
    ): IWorkScheduler
}

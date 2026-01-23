package com.example.chatapp.di

import android.content.Context
import com.example.chatapp.core.data.local.DataStoreManager
import com.example.chatapp.core.data.repository.MessageRepository
import com.example.chatapp.core.data.repository.UserRepository
import com.example.chatapp.core.domain.repository.IMessageRepository
import com.example.chatapp.core.domain.repository.IUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext context: Context): DataStoreManager {
        return DataStoreManager(context)
    }

    @Provides
    @Singleton
    fun provideUserRepository(repository: UserRepository): IUserRepository = repository

    @Provides
    @Singleton
    fun provideMessageRepository(repository: MessageRepository): IMessageRepository = repository

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        // Explicit database URL based on project ID from google-services.json
        return FirebaseDatabase.getInstance("https://chat-app-680df-default-rtdb.firebaseio.com/")
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()
}

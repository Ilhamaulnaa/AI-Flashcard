package com.ilham.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ilham.data.BuildConfig
import com.ilham.domain.repository.AIFlashCardRepository
import com.ilham.domain.repository.AIFlashCardRepositoryImpl
import com.ilham.domain.repository.AuthRepository
import com.ilham.domain.repository.FirebaseAuthRepositoryImpl
import com.ilham.domain.repository.FirebaseDeckRepository
import com.ilham.domain.repository.FirebaseDeckRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth
    ): AuthRepository = FirebaseAuthRepositoryImpl(
        firebaseAuth = firebaseAuth
    )

    @Provides
    fun provideDeckRepository(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore
    ): FirebaseDeckRepository = FirebaseDeckRepositoryImpl(
        firebaseAuth = firebaseAuth,
        firebaseFireStore = firebaseFirestore
    )

    @Provides
    fun provideAiFlashCardRepository(): AIFlashCardRepository = AIFlashCardRepositoryImpl(
        apiKey = BuildConfig.GROQ_API_KEY,
    )

}
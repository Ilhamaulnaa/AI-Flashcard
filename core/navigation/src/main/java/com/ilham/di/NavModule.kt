package com.ilham.di

import com.ilham.navigation.AppNavigator
import com.ilham.navigation.Navigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NavModule  {

    @Provides
    fun provideNavigator(): Navigator{
        return AppNavigator()
    }

}
package com.example.athleteos.core.di

import android.content.Context
import com.example.athleteos.data.DefaultPreferencesRepository
import com.example.athleteos.data.PreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun providePreferencesRepository(
        @ApplicationContext context: Context
    ): PreferencesRepository {
        return DefaultPreferencesRepository(context)
    }
}

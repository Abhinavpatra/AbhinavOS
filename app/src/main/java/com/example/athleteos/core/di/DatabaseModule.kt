package com.example.athleteos.core.di

import android.content.Context
import androidx.room.Room
import com.example.athleteos.data.DataRepository
import com.example.athleteos.data.DefaultDataRepository
import com.example.athleteos.database.AthleteDao
import com.example.athleteos.database.AthleteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AthleteDatabase {
        return Room.databaseBuilder(
            context,
            AthleteDatabase::class.java,
            "athlete_os.db"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    @Singleton
    fun provideAthleteDao(database: AthleteDatabase): AthleteDao {
        return database.athleteDao()
    }

    @Provides
    @Singleton
    fun provideDataRepository(
        @ApplicationContext context: Context,
        athleteDao: AthleteDao
    ): DataRepository {
        return DefaultDataRepository(context, athleteDao)
    }
}

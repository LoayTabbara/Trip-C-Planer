package com.codecamp.tripcplaner.model.di

import android.content.Context
import androidx.room.Room
import com.codecamp.tripcplaner.model.data.TripDao
import com.codecamp.tripcplaner.model.data.TripDatabase
import com.codecamp.tripcplaner.model.data.TripRepository
import com.codecamp.tripcplaner.model.data.TripRepositoryImplementation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TripDataModule {

    @Singleton
    @Provides
    fun provideTripDatabase(@ApplicationContext context: Context): TripDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            TripDatabase::class.java,
            "trip_database"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideTripDao(database: TripDatabase): TripDao = database.tripDao()

    @Singleton
    @Provides
    fun provideTripRepository(dao: TripDao): TripRepositoryImplementation = TripRepositoryImplementation(dao)
}
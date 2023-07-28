package com.codecamp.tripcplaner.model.di

import com.codecamp.tripcplaner.model.data.TripRepository
import com.codecamp.tripcplaner.model.data.TripRepositoryImplementation
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton


@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindTripRepository(
        tripRepositoryImplementation: TripRepositoryImplementation
    ): TripRepository
}
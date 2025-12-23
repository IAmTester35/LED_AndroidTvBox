package com.reecotech.androidtvbox.di




import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

// DeviceRepository binding removed

    @Binds
    @Singleton
    abstract fun bindStationRepository(
        stationRepositoryImpl: com.reecotech.androidtvbox.data.repository.StationRepositoryImpl
    ): com.reecotech.androidtvbox.domain.StationRepository


}

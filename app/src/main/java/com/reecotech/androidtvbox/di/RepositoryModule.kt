package com.reecotech.androidtvbox.di


import com.reecotech.androidtvbox.data.repository.DeviceRepositoryImpl
import com.reecotech.androidtvbox.domain.DeviceRepository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindDeviceRepository(
        deviceRepositoryImpl: DeviceRepositoryImpl
    ): DeviceRepository

    @Binds
    @Singleton
    abstract fun bindStationRepository(
        stationRepositoryImpl: com.reecotech.androidtvbox.data.repository.StationRepositoryImpl
    ): com.reecotech.androidtvbox.domain.StationRepository


}

package me.smbduknow.transport.app

import dagger.Module
import dagger.Provides
import me.smbduknow.transport.data.TransportRepositoryImpl
import me.smbduknow.transport.data.UserLocationRepositoryImpl
import me.smbduknow.transport.domain.repository.TransportRepository
import me.smbduknow.transport.domain.repository.UserLocationRepository
import javax.inject.Singleton


@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideTransportRepository(): TransportRepository = TransportRepositoryImpl()

    @Provides
    @Singleton
    fun provideUserLocationRepository(): UserLocationRepository = UserLocationRepositoryImpl()

}
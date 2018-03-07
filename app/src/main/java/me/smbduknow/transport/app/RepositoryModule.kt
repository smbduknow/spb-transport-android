package me.smbduknow.transport.app

import dagger.Module
import dagger.Provides
import me.smbduknow.transport.data.TransportRepositoryImpl
import me.smbduknow.transport.data.UserLocationRepositoryImpl
import me.smbduknow.transport.data.assets.RoutesProvider
import me.smbduknow.transport.data.network.VehiclesApi
import me.smbduknow.transport.data.playservices.PlayServiceProvider
import me.smbduknow.transport.domain.repository.TransportRepository
import me.smbduknow.transport.domain.repository.UserLocationRepository
import javax.inject.Singleton


@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideTransportRepository(remote: VehiclesApi,
                                   routesProvider: RoutesProvider): TransportRepository
            = TransportRepositoryImpl(remote, routesProvider)

    @Provides
    @Singleton
    fun provideUserLocationRepository(servicesProvider: PlayServiceProvider): UserLocationRepository
            = UserLocationRepositoryImpl(servicesProvider)

}
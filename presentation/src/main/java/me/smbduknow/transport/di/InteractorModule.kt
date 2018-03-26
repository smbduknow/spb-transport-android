package me.smbduknow.transport.di

import dagger.Module
import dagger.Provides
import me.smbduknow.transport.domain.MapInteractor
import me.smbduknow.transport.domain.interactor.MapInteractorImpl
import me.smbduknow.transport.domain.repository.TransportRepository
import me.smbduknow.transport.domain.repository.UserLocationRepository
import me.smbduknow.transport.presentation.main.MapViewModelFactory
import javax.inject.Singleton

@Module
class InteractorModule {

    @Provides
    fun provideMapInteractor(transportRepository: TransportRepository,
                             locationRepository: UserLocationRepository): MapInteractor
            = MapInteractorImpl(transportRepository, locationRepository)

    @Provides
    @Singleton
    fun provideMapViewModelFactory(interactor: MapInteractor): MapViewModelFactory
            = MapViewModelFactory(interactor)

}
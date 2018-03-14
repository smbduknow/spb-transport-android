package me.smbduknow.transport.di

import dagger.Module
import dagger.Provides
import me.smbduknow.transport.domain.MapInteractor
import me.smbduknow.transport.domain.interactor.MapInteractorImpl
import me.smbduknow.transport.domain.repository.TransportRepository
import me.smbduknow.transport.domain.repository.UserLocationRepository
import me.smbduknow.transport.presentation.main.MainMvpPresenter
import me.smbduknow.transport.presentation.main.MainPresenter
import javax.inject.Singleton

@Module
class InteractorModule {

    @Provides
    @Singleton
    fun provideMapInteractor(transportRepository: TransportRepository,
                             locationRepository: UserLocationRepository): MapInteractor
            = MapInteractorImpl(transportRepository, locationRepository)

    @Provides
    fun provideMapPresenter(interactor: MapInteractor): MainMvpPresenter
            = MainPresenter(interactor)

}
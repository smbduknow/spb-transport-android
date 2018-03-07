package me.smbduknow.transport.di

import dagger.Module
import dagger.Provides
import me.smbduknow.transport.domain.IMapInteractor
import me.smbduknow.transport.domain.MapInteractor
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
                             locationRepository: UserLocationRepository): IMapInteractor
            = MapInteractor(transportRepository, locationRepository)

    @Provides
    @Singleton
    fun provideMapPresenter(interactor: IMapInteractor): MainMvpPresenter
            = MainPresenter(interactor)

}
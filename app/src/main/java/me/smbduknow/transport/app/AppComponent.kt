package me.smbduknow.transport.app

import dagger.Component
import me.smbduknow.transport.domain.MapInteractor
import javax.inject.Singleton

@Component(modules = [(RepositoryModule::class)])
@Singleton
interface MapComponent {

    fun injectTo(interactor: MapInteractor)

}
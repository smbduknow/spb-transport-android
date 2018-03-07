package me.smbduknow.transport.app

import dagger.Component
import me.smbduknow.transport.presentation.main.MainActivity
import javax.inject.Singleton

@Component(modules = [
    AppModule::class,
    RepositoryModule::class,
    InteractorModule::class
])
@Singleton
interface AppComponent {

    fun injectTo(activity: MainActivity)

}
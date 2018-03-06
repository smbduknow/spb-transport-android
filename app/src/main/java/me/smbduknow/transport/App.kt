package me.smbduknow.transport

import android.app.Application
import android.support.v7.app.AppCompatDelegate
import me.smbduknow.transport.app.AppComponent
import me.smbduknow.transport.app.AppModule
import me.smbduknow.transport.app.DaggerAppComponent
import me.smbduknow.transport.app.RepositoryModule
import me.smbduknow.transport.data.assets.AssetsProvider
import me.smbduknow.transport.data.playservices.PlayServiceProvider
import me.smbduknow.transport.data.session.Session
import timber.log.Timber


class App : Application() {

    companion object {
        lateinit var graph: AppComponent
    }

    override fun onCreate() {
        super.onCreate()

//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return
//        }
//
//        //enable leak analyzer
//        LeakCanary.install(this)

        //enable vector drawables
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        //init data-layer services
        with(applicationContext) {
            PlayServiceProvider.init(this)
            Session.routes = AssetsProvider.getRoutes(this)
        }

        graph = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .repositoryModule(RepositoryModule())
                .build()
//
//        appComponent.injectTo(MapInteractor())

        //init logger
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
        }

    }

}
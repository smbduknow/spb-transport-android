package me.smbduknow.transport

import android.app.Application
import android.support.v7.app.AppCompatDelegate
import me.smbduknow.transport.di.AppComponent
import me.smbduknow.transport.di.AppModule
import me.smbduknow.transport.di.DaggerAppComponent
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

        graph = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()

        //init logger
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
        }

    }

}
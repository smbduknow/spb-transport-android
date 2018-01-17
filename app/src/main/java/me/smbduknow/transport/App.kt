package me.smbduknow.transport

import android.app.Application
import android.support.v7.app.AppCompatDelegate
import me.smbduknow.transport.commons.CSVUtil
import me.smbduknow.transport.data.playservices.PlayServiceProvider
import me.smbduknow.transport.data.session.Session
import timber.log.Timber


class App : Application() {

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
            //init PlayServices
            PlayServiceProvider.init(this)
            //init Google Analytics
            Session.routes = CSVUtil.readCsv(this)
        }

        Session.routes = CSVUtil.readCsv(applicationContext)

        //init logger
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
        }

    }

}
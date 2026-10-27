package com.ilham.aiflash

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MainApp : Application(){

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

//    @Inject
//    lateinit var factory: WorkerFactory
//
//    override fun onCreate() {
//        super.onCreate()
//        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
//    }
//
//    override fun getWorkManagerConfiguration(): Configuration {
//        return Configuration.Builder()
//            .setWorkerFactory(factory)
//            .build()
//    }
}
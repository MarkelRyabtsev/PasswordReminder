package com.markel.passwordreminder

import android.app.Application
import com.markel.passwordreminder.di.initKoin
import timber.log.Timber

open class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin(this)
        initYandexAppMetric()
        initTimber()
        initFirebase()
    }

    private fun initFirebase() {
        //FirebaseApp.initializeApp(this)
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initYandexAppMetric() {
        /*YandexMetrica.activate(
            applicationContext,
            YandexMetricaConfig.newConfigBuilder(Constant.YANDEX_METRICA_API_KEY).build()
        )
        if (!BuildConfig.DEBUG) {
            YandexMetrica.enableActivityAutoTracking(this)
        }*/
    }
}
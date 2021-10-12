package ie.wit.tennisapp.main

import android.app.Application
import ie.wit.tennisapp.models.ResultsMemStore
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    val matches = ResultsMemStore()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("Tennis App started")
    }
}
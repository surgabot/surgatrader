package com.surgatrader

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SurgaTraderApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}

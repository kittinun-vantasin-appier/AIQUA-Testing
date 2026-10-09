package com.github.kittinunf.aiqua_testing

import android.app.Application
import com.github.kittinunf.aiqua_testing.aiqua.Aiqua
import com.github.kittinunf.aiqua_testing.aiqua.init

class GroceryApplication : Application() {
    val container: AppContainer by lazy { AppContainer() }

    override fun onCreate() {
        super.onCreate()
        Aiqua.init(application = this)
    }
}

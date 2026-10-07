package com.github.kittinunf.aiqua_testing

import android.app.Application

class GroceryApplication : Application() {
    /** One per process, so the in-memory Cart survives rotation but not the app being killed. */
    val container: AppContainer by lazy { AppContainer(PlatformContext(this)) }
}

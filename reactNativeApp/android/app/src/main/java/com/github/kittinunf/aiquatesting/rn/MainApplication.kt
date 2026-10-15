package com.github.kittinunf.aiquatesting.rn

import android.app.Application
import com.appier.sdk.Appier
import com.facebook.react.PackageList
import com.facebook.react.ReactApplication
import com.facebook.react.ReactHost
import com.facebook.react.ReactNativeApplicationEntryPoint.loadReactNative
import com.facebook.react.defaults.DefaultReactHost.getDefaultReactHost

class MainApplication : Application(), ReactApplication {

  override val reactHost: ReactHost by lazy {
    getDefaultReactHost(
      context = applicationContext,
      packageList =
        PackageList(this).packages.apply {
          // Packages that cannot be autolinked yet can be added manually here, for example:
          // add(MyReactNativePackage())
        },
    )
  }

  override fun onCreate() {
    super.onCreate()
    // Appier's React Native SDK 2.x needs its native instance created here; JS then calls configure().
    if (BuildConfig.DEBUG) Appier.setLogLevel(Appier.LogLevel.VERBOSE)
    Appier.create(this)
    loadReactNative(this)
  }
}

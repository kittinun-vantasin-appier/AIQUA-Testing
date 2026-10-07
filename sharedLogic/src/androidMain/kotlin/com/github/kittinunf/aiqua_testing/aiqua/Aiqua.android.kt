package com.github.kittinunf.aiqua_testing.aiqua

import android.app.Application
import android.content.pm.ApplicationInfo
import com.appier.sdk.Appier
import com.github.kittinunf.aiqua_testing.Constants
import com.github.kittinunf.aiqua_testing.getPlatform
import com.quantumgraph.sdk.QG
import kotlin.uuid.Uuid

actual object Aiqua {

    private val platform = getPlatform()

    fun init(application: Application) {
        if (application.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            Appier.setLogLevel(Appier.LogLevel.VERBOSE)
        }
        QG.initializeSdk(application, Constants.APP_ID)
        val qg = QG.getInstance(application)
        qg.setUserId("kittinun.vantasin@appier.com")
        qg.setCustomUserParameter("platform", platform.name)
    }
}

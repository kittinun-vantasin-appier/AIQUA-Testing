package com.github.kittinunf.aiqua_testing.aiqua

import android.app.Application
import android.content.pm.ApplicationInfo
import com.appier.sdk.Appier
import com.github.kittinunf.aiqua_testing.AppContextProvider
import com.github.kittinunf.aiqua_testing.Constants
import com.github.kittinunf.aiqua_testing.getPlatform
import org.json.JSONObject
import com.quantumgraph.sdk.QG as AppierQG

actual object Aiqua : EventLogger {

    fun init(application: Application) {
        if (application.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            Appier.setLogLevel(Appier.LogLevel.VERBOSE)
        }
        AppierQG.initializeSdk(application, Constants.APP_ID)
        setCustomUserAttributes(AppierQG.getInstance(application))
    }

    actual fun setCustomUserAttributes(qg: QG) {
        qg.setUserId("kittinun.vantasin@appier.com")

        val platform = getPlatform()
        qg.setCustomUserParameter("platform", platform.name)
    }

    // The SDK keeps its single client itself; it only needs a Context to hand it back.
    actual override fun logEvent(name: String, parameters: Map<String, Any>, valueToSum: Double?, valueToSumCurrency: String?) {
        AppierQG.getInstance(AppContextProvider.appContext)
            .logEvent(name, JSONObject(parameters), valueToSum, valueToSumCurrency)
    }
}

actual typealias QG = AppierQG

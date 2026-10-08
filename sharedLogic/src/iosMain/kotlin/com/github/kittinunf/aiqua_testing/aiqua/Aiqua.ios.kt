package com.github.kittinunf.aiqua_testing.aiqua

import com.appier.ios.QGSdk
import com.github.kittinunf.aiqua_testing.Constants
import com.github.kittinunf.aiqua_testing.getPlatform
import platform.Foundation.NSNumber

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
actual typealias QG = QGSdk

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
actual object Aiqua : EventLogger {

    @ObjCName(swiftName = "configure") // `init` is reserved in Swift (it would otherwise become `doInit`)
    fun init() {
        QGSdk.getSharedInstance().onStart(Constants.APP_ID)
        setCustomUserAttributes(QGSdk.getSharedInstance())
    }

    actual fun setCustomUserAttributes(qg: QG) {
        qg.setUserId("kittinun.vantasin@appier.com")

        val platform = getPlatform()
        qg.setCustomKey("platform", platform.name)
    }

    actual override fun logEvent(name: String, parameters: Map<String, Any>, valueToSum: Double?, valueToSumCurrency: String?) {
        QGSdk.getSharedInstance().logEvent(
            name,
            withParameters = parameters as Map<Any?, *>,
            withValueToSum = valueToSum?.let { NSNumber(double = it) },
            withValueToSumCurrency = valueToSumCurrency,
        )
    }
}

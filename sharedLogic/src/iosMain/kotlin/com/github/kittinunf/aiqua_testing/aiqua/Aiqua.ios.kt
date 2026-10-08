package com.github.kittinunf.aiqua_testing.aiqua

import com.appier.ios.QGSdk
import com.github.kittinunf.aiqua_testing.Constants
import com.github.kittinunf.aiqua_testing.getPlatform

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
actual typealias QG = QGSdk

actual object Aiqua {
    private val platform = getPlatform()

    @OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
    @ObjCName(swiftName = "configure")
    fun init() {
        QGSdk.getSharedInstance().onStart(Constants.APP_ID)
        val sdk = QGSdk.getSharedInstance()
        setCustomUserAttributes(sdk)
    }

    @OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
    actual fun setCustomUserAttributes(qg: QG) {
        qg.setUserId("kittinun.vantasin@appier.com")
        qg.setCustomKey("platform", platform.name)
    }
}

package com.github.kittinunf.aiqua_testing.aiqua

import com.appier.ios.QGSdk
import com.github.kittinunf.aiqua_testing.Constants
import com.github.kittinunf.aiqua_testing.getPlatform
import kotlinx.cinterop.ExperimentalForeignApi
import kotlin.native.ObjCName

actual object Aiqua {
    private val platform = getPlatform()

    @OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
    @ObjCName(swiftName = "configure")
    fun init() {
        QGSdk.getSharedInstance().onStart(Constants.APP_ID)
        val sdk = QGSdk.getSharedInstance()
        sdk.setUserId("kittinun.vantasin@appier.com")
        sdk.setCustomKey("platform", platform.name)
    }
}

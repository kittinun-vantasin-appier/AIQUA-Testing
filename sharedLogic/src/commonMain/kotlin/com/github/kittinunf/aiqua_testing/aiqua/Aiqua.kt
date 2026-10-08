package com.github.kittinunf.aiqua_testing.aiqua

/**
 * The AIQUA SDK. Android and iOS each implement it over their native SDK (Aiqua.android.kt / Aiqua.ios.kt).
 * It keeps no state: the SDK holds its own single client. Members both platforms share go here. Each platform adds
 * its own `init`, because Android needs the Application and iOS doesn't; the app calls it once at launch.
 */
expect object Aiqua : EventLogger {

    fun setCustomUserAttributes(qg: QG)

    override fun logEvent(name: String, parameters: Map<String, Any>)
}

expect class QG

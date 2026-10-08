package com.github.kittinunf.aiqua_testing.aiqua

/** Where ViewModels send analytics events. The app uses [Aiqua]; tests use a fake. */
fun interface EventLogger {
    fun logEvent(name: String, parameters: Map<String, Any>)
}

/** A screen became visible: the app opened on it, or the Shopper navigated to it. */
internal fun EventLogger.screenViewed(screenName: String) =
    logEvent("screen_viewed", mapOf("screen_name" to screenName))

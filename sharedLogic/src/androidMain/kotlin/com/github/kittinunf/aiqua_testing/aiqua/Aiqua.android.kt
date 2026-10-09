package com.github.kittinunf.aiqua_testing.aiqua

import android.app.Application
import android.content.pm.ApplicationInfo
import com.appier.sdk.Appier
import com.github.kittinunf.aiqua_testing.AppContextProvider
import com.github.kittinunf.aiqua_testing.Constants
import com.github.kittinunf.aiqua_testing.inbox.InboxMessage
import com.quantumgraph.sdk.AiqInbox
import org.json.JSONObject
import com.quantumgraph.sdk.QG as AppierQG

fun Aiqua.init(application: Application) {
    if (application.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
        Appier.setLogLevel(Appier.LogLevel.VERBOSE)
    }
    AppierQG.initializeSdk(application, Constants.APP_ID)
    setUserAttributes()
}

// The SDK keeps its single client itself; it only needs a Context to hand it back.
private val qg: AppierQG get() = AppierQG.getInstance(AppContextProvider.appContext)

internal actual fun setNativeUserId(userId: String) = qg.setUserId(userId)

internal actual fun setNativeCustomAttribute(key: String, value: String) =
    qg.setCustomUserParameter(key, value)

internal actual fun logNativeEvent(
    name: String,
    parameters: Map<String, Any>,
    valueToSum: Double?,
    valueToSumCurrency: String?,
) = qg.logEvent(name, JSONObject(parameters), valueToSum, valueToSumCurrency)

internal actual typealias NativeInbox = AiqInbox

internal actual fun NativeInbox.toInboxMessage() = InboxMessage(
    id = notificationId,
    title = title.orEmpty(),
    text = text.orEmpty(),
    startTime = startTime,
    isRead = status == AiqInbox.Status.READ,
)

internal actual fun NativeInbox.markRead() =
    setStatus(AppContextProvider.appContext, AiqInbox.Status.READ)

internal actual fun NativeInbox.logMessageEvent(name: String) =
    logEvent(AppContextProvider.appContext, name, JSONObject(), null, null)

internal actual fun fetchNativeInboxes(onDone: () -> Unit) =
    AppierQG.fetchInboxMessages(AppContextProvider.appContext) { _, _ -> onDone() }

internal actual fun nativeInboxes(includeDeleted: Boolean): List<NativeInbox> =
    AppierQG.getInboxes(true, true, includeDeleted).toList()

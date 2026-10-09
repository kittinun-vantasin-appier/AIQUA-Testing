@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.github.kittinunf.aiqua_testing.aiqua

import com.appier.ios.QGInbox
import com.appier.ios.QGInboxStatusRead
import com.appier.ios.QGSdk
import com.github.kittinunf.aiqua_testing.Constants
import com.github.kittinunf.aiqua_testing.inbox.InboxMessage
import platform.Foundation.NSNumber

@ObjCName(swiftName = "configure") // `init` is reserved in Swift (it would otherwise become `doInit`)
fun Aiqua.init() {
    QGSdk.getSharedInstance().onStart(Constants.APP_ID)
    setUserAttributes()
}

internal actual fun setNativeUserId(userId: String) = QGSdk.getSharedInstance().setUserId(userId)

internal actual fun setNativeCustomAttribute(key: String, value: String) =
    QGSdk.getSharedInstance().setCustomKey(key, value)

@Suppress("UNCHECKED_CAST")
internal actual fun logNativeEvent(
    name: String,
    parameters: Map<String, Any>,
    valueToSum: Double?,
    valueToSumCurrency: String?,
) = QGSdk.getSharedInstance().logEvent(
    name,
    withParameters = parameters as Map<Any?, *>,
    withValueToSum = valueToSum?.let { NSNumber(double = it) },
    withValueToSumCurrency = valueToSumCurrency,
)

internal actual typealias NativeInbox = QGInbox

internal actual fun NativeInbox.toInboxMessage() = InboxMessage(
    id = notificationId.stringValue,
    title = title,
    text = text,
    startTime = startTime,
    isRead = status == QGInboxStatusRead,
)

internal actual fun NativeInbox.markRead() = updateStatus(QGInboxStatusRead)

internal actual fun NativeInbox.logMessageEvent(name: String) =
    logEvent(name, withParameters = null, withValueToSum = null, withValueToSumCurrency = null)

internal actual fun fetchNativeInboxes(onDone: () -> Unit) =
    QGSdk.getSharedInstance().fetchInboxMessages { _, _ -> onDone() }

internal actual fun nativeInboxes(includeDeleted: Boolean): List<NativeInbox> =
    QGSdk.getSharedInstance()
        .getInboxesWithStatusRead(true, statusUnread = true, statusDeleted = includeDeleted)
        .filterIsInstance<NativeInbox>()

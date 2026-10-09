package com.github.kittinunf.aiqua_testing.aiqua

import com.github.kittinunf.aiqua_testing.getPlatform
import com.github.kittinunf.aiqua_testing.inbox.InboxMessage
import com.github.kittinunf.aiqua_testing.inbox.InboxService
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * The AIQUA SDK as one backend unit: events and the inbox. All the logic is here; each platform only supplies the SDK
 * calls that differ (the `expect` declarations below, in Aiqua.android.kt / Aiqua.ios.kt), plus its own `init`, because
 * Android needs the Application and iOS doesn't; the app calls it once at launch.
 * It keeps no state: the SDK holds its own single client, and stores the inbox messages and their read status.
 */
object Aiqua : EventLogger, InboxService {

    internal fun setUserAttributes() {
        setNativeUserId("kittinun.vantasin@appier.com")
        setNativeCustomAttribute("platform", getPlatform().name)
    }

    override fun logEvent(
        name: String,
        parameters: Map<String, Any>,
        valueToSum: Double?,
        valueToSumCurrency: String?
    ) = logNativeEvent(name, parameters, valueToSum, valueToSumCurrency)

    @NativeCoroutinesIgnore
    override suspend fun fetchMessages(): List<InboxMessage> {
        suspendCancellableCoroutine { continuation ->
            fetchNativeInboxes { continuation.resume(Unit) }
        }
        return nativeInboxes(includeDeleted = false).map { it.toInboxMessage() }
    }

    override fun markRead(messageId: String) {
        find(messageId)?.markRead()
    }

    override fun logMessageEvent(messageId: String, name: String) {
        find(messageId)?.logMessageEvent(name)
    }

    // Looked up again each time rather than kept, so this object stays stateless.
    private fun find(messageId: String): NativeInbox? =
        nativeInboxes(includeDeleted = true).firstOrNull { it.toInboxMessage().id == messageId }
}

// What the two SDKs do differently: method names and signatures, field types (an inbox message's ID is a String vs an
// NSNumber), and the read-status enum. Each is a one-line SDK call per platform.

internal expect fun setNativeUserId(userId: String)

internal expect fun setNativeCustomAttribute(key: String, value: String)

internal expect fun logNativeEvent(
    name: String,
    parameters: Map<String, Any>,
    valueToSum: Double?,
    valueToSumCurrency: String?,
)

/**
 * One inbox message as the native SDK holds it: each platform makes this a typealias of the SDK's own class
 * (`AiqInbox` / `QGInbox`), so common code passes the SDK's objects around as they are. It can't declare their shared
 * fields (title, text, startTime): Android's are Java fields, and Kotlin doesn't let those fulfil an `expect` property.
 */
internal expect class NativeInbox

internal expect fun NativeInbox.toInboxMessage(): InboxMessage

internal expect fun NativeInbox.markRead()

internal expect fun NativeInbox.logMessageEvent(name: String)

internal expect fun fetchNativeInboxes(onDone: () -> Unit)

internal expect fun nativeInboxes(includeDeleted: Boolean): List<NativeInbox>

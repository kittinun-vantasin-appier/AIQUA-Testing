package com.github.kittinunf.aiqua_testing.inbox

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore

/**
 * AIQUA's inbox on this device. Stateless: the SDK stores the messages and their read status itself.
 * The app uses [com.github.kittinunf.aiqua_testing.aiqua.Aiqua]; tests use a fake.
 */
interface InboxService {
    /**
     * Downloads the latest messages from AIQUA, then returns every message stored on the device except deleted ones.
     * If the download fails, it still returns the stored ones.
     */
    @NativeCoroutinesIgnore // Swift goes through ViewModels, never Services.
    suspend fun fetchMessages(): List<InboxMessage>

    fun markRead(messageId: String)

    /** Logs an event for one message, such as AIQUA's impression and click events. The SDK adds the message's ID. */
    fun logMessageEvent(messageId: String, name: String)
}

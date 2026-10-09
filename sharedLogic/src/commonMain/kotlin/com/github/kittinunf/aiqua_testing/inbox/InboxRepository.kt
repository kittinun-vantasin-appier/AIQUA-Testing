package com.github.kittinunf.aiqua_testing.inbox

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Owns the inbox messages the app shows, newest first. */
interface InboxRepository {
    /** Empty until the first [refresh]. */
    @NativeCoroutinesIgnore // Swift goes through ViewModels.
    val messages: StateFlow<List<InboxMessage>>

    @NativeCoroutinesIgnore
    suspend fun refresh()

    fun markRead(messageId: String)

    fun logMessageEvent(messageId: String, name: String)
}

class DefaultInboxRepository(private val inboxService: InboxService) : InboxRepository {
    private val _messages = MutableStateFlow<List<InboxMessage>>(emptyList())
    override val messages: StateFlow<List<InboxMessage>> = _messages.asStateFlow()

    override suspend fun refresh() {
        _messages.value = inboxService.fetchMessages().sortedByDescending { it.startTime }
    }

    // The SDK saves the status for next time; the list here is updated in place, so the message keeps its position.
    override fun markRead(messageId: String) {
        inboxService.markRead(messageId)
        _messages.update { messages -> messages.map { if (it.id == messageId) it.copy(isRead = true) else it } }
    }

    override fun logMessageEvent(messageId: String, name: String) =
        inboxService.logMessageEvent(messageId, name)
}

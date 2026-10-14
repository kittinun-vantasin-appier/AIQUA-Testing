package com.github.kittinunf.aiqua_testing.inbox

import com.github.kittinunf.aiqua_testing.WhileUiSubscribed
import com.github.kittinunf.aiqua_testing.aiqua.EventLogger
import com.github.kittinunf.aiqua_testing.aiqua.screenViewed
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.stateIn
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

/** The inbox messages, newest first. Read ones stay; nothing is ever deleted. */
data class InboxUiState(val messages: List<InboxMessage> = emptyList()) {
    val isEmpty: Boolean get() = messages.isEmpty()
}

class InboxViewModel(
    private val inboxRepository: InboxRepository,
    private val eventLogger: EventLogger,
) : ViewModel() {
    @NativeCoroutinesState
    val uiState: StateFlow<InboxUiState> = inboxRepository.messages
        .map(::InboxUiState)
        .stateIn(viewModelScope, WhileUiSubscribed, InboxUiState(inboxRepository.messages.value))

    /**
     * The screen became visible. The UI calls this; a ViewModel can't tell on its own. Opening the inbox shows every
     * message, so each one counts as an impression. The SDK doesn't log those for inbox messages, so the app does.
     */
    fun onScreenViewed() {
        eventLogger.screenViewed("inbox")
        inboxRepository.messages.value.forEach { inboxRepository.logMessageEvent(it.id, MESSAGE_DISPLAYED) }
    }

    fun onMessageClick(message: InboxMessage) {
        inboxRepository.logMessageEvent(message.id, MESSAGE_CLICKED)
        if (!message.isRead) inboxRepository.markRead(message.id)
    }

    private companion object {
        // AIQUA's own campaign events, which show as impressions and clicks on the campaign's dashboard.
        const val MESSAGE_DISPLAYED = "qg_inapp_displayed"
        const val MESSAGE_CLICKED = "qg_inapp_clicked"
    }
}

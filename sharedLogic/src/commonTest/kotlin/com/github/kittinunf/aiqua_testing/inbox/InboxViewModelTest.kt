package com.github.kittinunf.aiqua_testing.inbox

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class InboxViewModelTest {
    private val older = InboxMessage("1", "Welcome", "Thanks for joining", startTime = 100, isRead = true)
    private val newer = InboxMessage("2", "Sale", "Fruit is 20% off", startTime = 200, isRead = false)

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun openingTheInboxShowsNewestFirstAndLogsAnImpressionPerMessage() = runTest {
        val service = FakeInboxService(older, newer)
        val repository = DefaultInboxRepository(service).apply { refresh() }
        val screens = mutableListOf<Any?>()
        val viewModel = InboxViewModel(repository) { _, parameters, _, _ -> screens += parameters["screen_name"] }

        viewModel.onScreenViewed()

        assertEquals(listOf(newer, older), viewModel.uiState.value.messages)
        assertEquals(listOf<Any?>("inbox"), screens)
        assertEquals(listOf("2" to "qg_inapp_displayed", "1" to "qg_inapp_displayed"), service.loggedEvents)
    }

    @Test
    fun tappingAMessageLogsAClickAndMarksItRead() = runTest {
        val service = FakeInboxService(newer)
        val repository = DefaultInboxRepository(service).apply { refresh() }
        val viewModel = InboxViewModel(repository) { _, _, _, _ -> }

        viewModel.onMessageClick(newer)
        viewModel.onMessageClick(viewModel.uiState.value.messages.single())

        assertEquals(listOf("2" to "qg_inapp_clicked", "2" to "qg_inapp_clicked"), service.loggedEvents)
        assertEquals(listOf(newer.copy(isRead = true)), viewModel.uiState.value.messages)
    }
}

/** Stores messages in memory, like the SDK does on the device, and records the events logged against them. */
internal class FakeInboxService(vararg messages: InboxMessage) : InboxService {
    private var stored = messages.toList()
    val loggedEvents = mutableListOf<Pair<String, String>>()

    override suspend fun fetchMessages() = stored

    override fun markRead(messageId: String) {
        stored = stored.map { if (it.id == messageId) it.copy(isRead = true) else it }
    }

    override fun logMessageEvent(messageId: String, name: String) {
        loggedEvents += messageId to name
    }
}

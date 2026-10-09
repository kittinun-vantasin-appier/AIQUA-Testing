package com.github.kittinunf.aiqua_testing.inbox

/**
 * One message from an AIQUA inbox campaign, as stored on the device. [id] is AIQUA's notification ID, and
 * [startTime] is when the campaign started showing it.
 */
data class InboxMessage(
    val id: String,
    val title: String,
    val text: String,
    val startTime: Long,
    val isRead: Boolean,
)

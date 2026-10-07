package com.github.kittinunf.aiqua_testing

import kotlin.test.Test
import kotlin.test.assertEquals

class BadgeTextTest {
    @Test
    fun hiddenWhenEmptyAndCappedAt99() {
        assertEquals(listOf(null, "1", "99", "99+"), listOf(0, 1, 99, 100).map(::badgeText))
    }
}

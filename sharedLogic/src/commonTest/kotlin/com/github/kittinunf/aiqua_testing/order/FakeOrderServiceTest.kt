package com.github.kittinunf.aiqua_testing.order

import com.github.kittinunf.aiqua_testing.cart.Cart
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FakeOrderServiceTest {
    @Test
    fun everyFifthAttemptFails() = runTest {
        val service = FakeOrderService()

        val outcomes = (1..10).map {
            try {
                service.placeOrder(Cart())
                "ok"
            } catch (e: OrderFailedException) {
                "failed"
            }
        }

        assertEquals(listOf(5, 10), outcomes.withIndex().filter { it.value == "failed" }.map { it.index + 1 })
    }
}

package com.github.kittinunf.aiqua_testing.order

import com.github.kittinunf.aiqua_testing.cart.Cart
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore
import kotlinx.coroutines.delay
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/** A confirmed purchase of everything that was in the Cart. [id] is the Order ID. */
data class Order(val id: String, val cart: Cart) {
    val total: Int get() = cart.total
    val count: Int get() = cart.unitCount
}

/** Thrown when an Order can't be placed. No Order exists, and the Cart is left as it was. */
class OrderFailedException(message: String) : Exception(message)

/** Places Orders. Stateless: it doesn't remember Orders or touch the Cart. */
interface OrderService {
    /** Throws [OrderFailedException] if the Order can't be placed. */
    @NativeCoroutinesIgnore // Swift goes through ViewModels, never Services.
    suspend fun placeOrder(cart: Cart): Order
}

/**
 * Pretends to call a backend: waits [latency], then succeeds with a random Order ID like "ORD-482913", except that
 * every [failEvery]th attempt (5th, 10th, …) fails, so a failure can be reproduced on demand. The attempt counter
 * stands in for the backend's behaviour; it isn't app state.
 */
class FakeOrderService(
    private val latency: Duration = 2.seconds,
    private val failEvery: Int = 5,
) : OrderService {
    private var attempts = 0

    override suspend fun placeOrder(cart: Cart): Order {
        delay(latency)
        attempts++
        if (attempts % failEvery == 0) throw OrderFailedException("Simulated failure on attempt $attempts")
        val id = "ORD-" + Random.nextInt(1_000_000).toString().padStart(6, '0')
        return Order(id = id, cart = cart)
    }
}

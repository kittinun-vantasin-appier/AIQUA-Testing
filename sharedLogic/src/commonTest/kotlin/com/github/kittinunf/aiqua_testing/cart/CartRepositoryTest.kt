package com.github.kittinunf.aiqua_testing.cart

import com.github.kittinunf.aiqua_testing.catalog.Product
import com.github.kittinunf.aiqua_testing.order.FakeOrderService
import com.github.kittinunf.aiqua_testing.order.Order
import com.github.kittinunf.aiqua_testing.order.OrderFailedException
import com.github.kittinunf.aiqua_testing.order.OrderService
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class CartRepositoryTest {
    private val carrot = Product("carrot", "Carrot", "vegetables", "🥕", 98)
    private val milk = Product("milk", "Milk", "dairy & eggs", "🥛", 228)

    private val repository = DefaultCartRepository(FakeOrderService())

    @Test
    fun keepsFirstAddedOrderAndTotals() {
        repository.add(carrot)
        repository.add(milk)
        repository.add(carrot)

        val cart = repository.cart.value
        assertEquals(listOf("carrot", "milk"), cart.lines.map { it.product.id })
        assertEquals(3, cart.unitCount)
        assertEquals(98 * 2 + 228, cart.total)
    }

    @Test
    fun quantityStopsAtNine() {
        repeat(12) { repository.add(carrot) }

        assertEquals(9, repository.cart.value.quantityOf("carrot"))
    }

    @Test
    fun decrementingTheLastUnitRemovesTheLine() {
        repository.add(carrot)
        repository.add(milk)
        repository.decrement("carrot")

        assertEquals(listOf("milk"), repository.cart.value.lines.map { it.product.id })
    }

    @Test
    fun checkoutPlacesAnOrderForTheCartThenEmptiesIt() = runTest {
        repository.add(carrot)
        repository.add(milk)

        val order = repository.checkout()

        assertEquals(98 + 228, order.total)
        assertTrue(order.id.startsWith("ORD-"))
        assertTrue(repository.cart.value.lines.isEmpty())
    }

    @Test
    fun aChangeMadeDuringCheckoutStaysInTheCart() = runTest {
        repository.add(carrot)

        val checkout = async { repository.checkout() }
        runCurrent() // checkout is now waiting on the OrderService
        repository.add(milk)
        checkout.await()

        assertEquals(listOf("milk"), repository.cart.value.lines.map { it.product.id })
    }

    @Test
    fun aFailedCheckoutLeavesTheCartAsItWas() = runTest {
        val failing = DefaultCartRepository(object : OrderService {
            override suspend fun placeOrder(cart: Cart): Order = throw OrderFailedException("declined")
        })
        failing.add(carrot)

        assertFailsWith<OrderFailedException> { failing.checkout() }

        assertEquals(1, failing.cart.value.quantityOf("carrot"))
    }
}

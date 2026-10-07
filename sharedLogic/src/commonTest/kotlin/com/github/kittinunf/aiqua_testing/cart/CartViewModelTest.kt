package com.github.kittinunf.aiqua_testing.cart

import com.github.kittinunf.aiqua_testing.catalog.Product
import com.github.kittinunf.aiqua_testing.order.Order
import com.github.kittinunf.aiqua_testing.order.OrderFailedException
import com.github.kittinunf.aiqua_testing.order.OrderService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {
    private val carrot = Product("carrot", "Carrot", "vegetables", "🥕", 98)

    // runTest shares this dispatcher's virtual clock, so delays inside the ViewModel are skipped on demand.
    @BeforeTest
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun aFailedOrderKeepsTheCartAndARetryPlacesIt() = runTest {
        var online = false
        val orderService = object : OrderService {
            override suspend fun placeOrder(cart: Cart): Order {
                delay(2.seconds)
                if (!online) throw OrderFailedException("declined")
                return Order("ORD-000001", cart)
            }
        }
        val cartRepository = DefaultCartRepository(orderService)
        val viewModel = CartViewModel(cartRepository)
        cartRepository.add(carrot)
        runCurrent()

        viewModel.onBuyClick()
        runCurrent()
        assertTrue(viewModel.uiState.value.isPlacingOrder)

        advanceTimeBy(2.seconds)
        runCurrent()
        viewModel.uiState.value.let {
            assertFalse(it.isPlacingOrder)
            assertNotNull(it.orderError)
            assertEquals(1, it.lines.size) // the Cart is still there
        }

        advanceTimeBy(2.seconds)
        runCurrent()
        assertFalse(viewModel.uiState.value.isOrderOverlayVisible)

        online = true
        viewModel.onBuyClick()
        advanceTimeBy(2.seconds)
        runCurrent()
        viewModel.uiState.value.let {
            assertEquals("¥98", it.placedOrderTotalText)
            assertNull(it.orderError)
            assertTrue(it.isEmpty)
        }
    }
}

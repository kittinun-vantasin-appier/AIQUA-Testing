package com.github.kittinunf.aiqua_testing.cart

import com.github.kittinunf.aiqua_testing.catalog.Product
import com.github.kittinunf.aiqua_testing.order.Order
import com.github.kittinunf.aiqua_testing.order.OrderFailedException
import com.github.kittinunf.aiqua_testing.order.OrderService
import com.github.kittinunf.aiqua_testing.order.FakeOrderService
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
    fun screenViewsIncludeCurrentCartCountIncludingEmptyCart() = runTest {
        val repository = DefaultCartRepository(FakeOrderService())
        val logged = mutableListOf<LoggedEvent>()
        val viewModel = CartViewModel(repository, { name, parameters, value, currency ->
            logged += LoggedEvent(name, parameters, value, currency)
        })

        viewModel.onScreenViewed()
        repository.add(carrot)
        repository.add(carrot)
        viewModel.onScreenViewed()
        repository.remove(carrot.id)
        viewModel.onScreenViewed()

        assertEquals(
            listOf(0, 2, 0).map {
                LoggedEvent("screen_viewed", mapOf("screen_name" to "cart", "cart_unit_count" to it))
            },
            logged,
        )
    }

    @Test
    fun cartAdditionsIncludeAllUnitsAndSkipQuantityLimitAndRemovals() = runTest {
        val repository = DefaultCartRepository(FakeOrderService())
        val milk = Product("milk", "Milk", "dairy & eggs", "milk", 228)
        repository.add(milk)
        val logged = mutableListOf<LoggedEvent>()
        val viewModel = CartViewModel(repository, { name, parameters, value, currency ->
            logged += LoggedEvent(name, parameters, value, currency)
        })

        repeat(10) { viewModel.onAddClick(carrot) }
        viewModel.onDecrementClick(carrot)
        viewModel.onRemoveClick(carrot)

        assertEquals(9, logged.size)
        assertEquals((2..10).toList(), logged.map { it.parameters["cart_unit_count"] })
        assertEquals((1..9).toList(), logged.map { it.parameters["product_quantity"] })
        assertEquals(
            LoggedEvent(
                "cart_added",
                mapOf(
                    "product_id" to "carrot", "product_name" to "Carrot", "category" to "vegetables",
                    "price" to 98, "quantity" to 1, "product_quantity" to 1, "cart_unit_count" to 2,
                ),
            ),
            logged.first(),
        )
    }

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
        val logged = mutableListOf<LoggedEvent>()
        val viewModel = CartViewModel(
            cartRepository,
            eventLogger = { name, parameters, value, currency ->
                logged += LoggedEvent(
                    name,
                    parameters,
                    value,
                    currency
                )
            },
        )
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
        assertEquals(listOf(LoggedEvent("checkout_failed", mapOf("reason" to "declined"))), logged)
        logged.clear()

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
        assertEquals(
            listOf(
                LoggedEvent(
                    "product_purchased",
                    mapOf(
                        "order_id" to "ORD-000001",
                        "product_id" to "carrot",
                        "product_name" to "Carrot",
                        "category" to "vegetables",
                        "price" to 98, "quantity" to 1,
                    ),
                ),
                LoggedEvent(
                    "checkout_completed",
                    mapOf("order_id" to "ORD-000001", "product_count" to 1),
                    value = 98.0,
                    currency = "JPY"
                ),
            ),
            logged,
        )
    }

    private data class LoggedEvent(
        val name: String,
        val parameters: Map<String, Any>,
        val value: Double? = null,
        val currency: String? = null,
    )
}

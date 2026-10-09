package com.github.kittinunf.aiqua_testing.home

import com.github.kittinunf.aiqua_testing.cart.DefaultCartRepository
import com.github.kittinunf.aiqua_testing.catalog.Product
import com.github.kittinunf.aiqua_testing.order.FakeOrderService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val carrot = Product("carrot", "Carrot", "vegetables", "🥕", 98)

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun aFailedLoadShowsTheErrorAndRetryRecovers() {
        var online = false
        val repository = object : HomeRepository {
            override val products = MutableStateFlow<List<Product>?>(null)
            override suspend fun refresh() {
                if (!online) error("offline")
                products.value = listOf(carrot)
            }
        }
        val viewModel = HomeViewModel(repository, DefaultCartRepository(FakeOrderService()), eventLogger = { _, _, _, _ -> })

        viewModel.uiState.value.let {
            assertFalse(it.isLoading)
            assertNotNull(it.error)
            assertTrue(it.sections.isEmpty())
        }

        online = true
        viewModel.onRetryClick()

        viewModel.uiState.value.let {
            assertFalse(it.isLoading)
            assertNull(it.error)
            assertEquals(listOf("VEGETABLES"), it.sections.map { section -> section.title })
        }
    }

    @Test
    fun additionsLogUpdatedCountsButNotQuantityLimitOrRemovals() {
        val logged = mutableListOf<Map<String, Any>>()
        val cartRepository = DefaultCartRepository(FakeOrderService())
        val repository = object : HomeRepository {
            override val products = MutableStateFlow<List<Product>?>(listOf(carrot))
            override suspend fun refresh() = Unit
        }
        val viewModel = HomeViewModel(repository, cartRepository) { name, parameters, value, currency ->
            assertEquals("cart_added", name)
            assertNull(value)
            assertNull(currency)
            logged += parameters
        }

        repeat(10) { viewModel.onAddClick(carrot) }
        viewModel.onDecrementClick(carrot)
        viewModel.onRemoveClick(carrot)

        assertEquals(9, logged.size)
        assertEquals((1..9).toList(), logged.map { it["cart_unit_count"] })
        assertEquals((1..9).toList(), logged.map { it["product_quantity"] })
        assertEquals(
            mapOf<String, Any>(
                "product_id" to "carrot", "product_name" to "Carrot", "category" to "vegetables",
                "price" to 98, "quantity" to 1, "product_quantity" to 1, "cart_unit_count" to 1,
            ),
            logged.first(),
        )
    }

    @Test
    fun aScreenViewLogsTheHomeScreen() {
        val logged = mutableListOf<Pair<String, Map<String, Any>>>()
        val repository = object : HomeRepository {
            override val products = MutableStateFlow<List<Product>?>(listOf(carrot))
            override suspend fun refresh() = Unit
        }
        val viewModel = HomeViewModel(repository, DefaultCartRepository(FakeOrderService())) { name, parameters, _, _ ->
            logged += name to parameters
        }

        viewModel.onScreenViewed()

        assertEquals(listOf("screen_viewed" to mapOf<String, Any>("screen_name" to "home")), logged)
    }
}

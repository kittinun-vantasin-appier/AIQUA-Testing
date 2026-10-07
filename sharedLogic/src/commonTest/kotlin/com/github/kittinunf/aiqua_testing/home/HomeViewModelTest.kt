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
        val viewModel = HomeViewModel(repository, DefaultCartRepository(FakeOrderService()))

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
}

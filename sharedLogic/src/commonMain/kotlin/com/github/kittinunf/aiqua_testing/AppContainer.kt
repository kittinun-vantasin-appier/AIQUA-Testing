package com.github.kittinunf.aiqua_testing

import com.github.kittinunf.aiqua_testing.cart.CartRepository
import com.github.kittinunf.aiqua_testing.cart.CartViewModel
import com.github.kittinunf.aiqua_testing.cart.DefaultCartRepository
import com.github.kittinunf.aiqua_testing.catalog.CatalogService
import com.github.kittinunf.aiqua_testing.catalog.JsonCatalogService
import com.github.kittinunf.aiqua_testing.home.DefaultHomeRepository
import com.github.kittinunf.aiqua_testing.home.HomeRepository
import com.github.kittinunf.aiqua_testing.home.HomeViewModel
import com.github.kittinunf.aiqua_testing.order.FakeOrderService
import com.github.kittinunf.aiqua_testing.order.OrderService

/**
 * Creates the app's shared objects once: stateless Services, the stateful Repositories built on them,
 * and the ViewModels that use the Repositories. Android keeps one in its Application, and iOS keeps one in its App.
 */
class AppContainer(catalogService: CatalogService, orderService: OrderService) {
    /** The real app setup: the Catalog comes from the bundled `product.json`, Orders from the fake backend. */
    constructor(platformContext: PlatformContext) : this(
        JsonCatalogService(readJson = { platformContext.readProductJson() }),
        FakeOrderService(),
    )

    private val homeRepository: HomeRepository = DefaultHomeRepository(catalogService)
    private val cartRepository: CartRepository = DefaultCartRepository(orderService)

    fun mainViewModel() = MainViewModel(cartRepository)

    fun homeViewModel() = HomeViewModel(homeRepository, cartRepository)

    fun cartViewModel() = CartViewModel(cartRepository)
}

package com.github.kittinunf.aiqua_testing

import com.github.kittinunf.aiqua_testing.aiqua.Aiqua
import com.github.kittinunf.aiqua_testing.aiqua.EventLogger
import com.github.kittinunf.aiqua_testing.cart.CartRepository
import com.github.kittinunf.aiqua_testing.cart.CartViewModel
import com.github.kittinunf.aiqua_testing.cart.DefaultCartRepository
import com.github.kittinunf.aiqua_testing.catalog.CatalogService
import com.github.kittinunf.aiqua_testing.catalog.JsonCatalogService
import com.github.kittinunf.aiqua_testing.catalog.readProductJson
import com.github.kittinunf.aiqua_testing.home.DefaultHomeRepository
import com.github.kittinunf.aiqua_testing.home.HomeRepository
import com.github.kittinunf.aiqua_testing.home.HomeViewModel
import com.github.kittinunf.aiqua_testing.inbox.DefaultInboxRepository
import com.github.kittinunf.aiqua_testing.inbox.InboxRepository
import com.github.kittinunf.aiqua_testing.inbox.InboxService
import com.github.kittinunf.aiqua_testing.inbox.InboxViewModel
import com.github.kittinunf.aiqua_testing.order.FakeOrderService
import com.github.kittinunf.aiqua_testing.order.OrderService

/**
 * Creates the app's shared objects once: stateless Services, the stateful Repositories built on them,
 * and the ViewModels that use the Repositories. Android keeps one in its Application, and iOS keeps one in its App.
 */
class AppContainer(
    catalogService: CatalogService,
    orderService: OrderService,
    inboxService: InboxService,
    private val eventLogger: EventLogger,
) {

    /** The real app setup: the bundled Catalog, the fake order backend, and AIQUA for the inbox and events. */
    constructor() : this(
        catalogService = JsonCatalogService(readJson = ::readProductJson),
        orderService = FakeOrderService(),
        inboxService = Aiqua,
        eventLogger = Aiqua,
    )

    private val homeRepository: HomeRepository = DefaultHomeRepository(catalogService)
    private val cartRepository: CartRepository = DefaultCartRepository(orderService)
    private val inboxRepository: InboxRepository = DefaultInboxRepository(inboxService)

    fun mainViewModel() = MainViewModel(cartRepository, inboxRepository)

    fun homeViewModel() = HomeViewModel(homeRepository, cartRepository, inboxRepository, eventLogger)

    fun cartViewModel() = CartViewModel(cartRepository, eventLogger)

    fun inboxViewModel() = InboxViewModel(inboxRepository, eventLogger)
}

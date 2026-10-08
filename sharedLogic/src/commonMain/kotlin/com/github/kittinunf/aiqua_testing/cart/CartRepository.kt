package com.github.kittinunf.aiqua_testing.cart

import com.github.kittinunf.aiqua_testing.catalog.Product
import com.github.kittinunf.aiqua_testing.order.Order
import com.github.kittinunf.aiqua_testing.order.OrderService
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Owns the one in-memory Cart shared by every screen and the Cart tab badge, along with the Cart's rules. */
interface CartRepository {
    @NativeCoroutinesIgnore // Swift goes through ViewModels, never Repositories.
    val cart: StateFlow<Cart>

    /** Adds one unit, appending a new Cart Line if the Product isn't in the Cart yet. Stops at [CartLine.MAX_QUANTITY]. */
    fun add(product: Product)

    /** Takes away one unit. Taking away the last unit removes the Cart Line. */
    fun decrement(productId: String)

    fun remove(productId: String)

    /**
     * Places an Order for everything in the Cart, then takes exactly the ordered units out of the Cart.
     * Throws [com.github.kittinunf.aiqua_testing.order.OrderFailedException] if it fails, leaving the Cart unchanged.
     */
    @NativeCoroutinesIgnore
    suspend fun checkout(): Order
}

class DefaultCartRepository(private val orderService: OrderService) : CartRepository {
    private val _cart = MutableStateFlow(Cart())
    override val cart: StateFlow<Cart> = _cart.asStateFlow()

    override fun add(product: Product) = _cart.update { cart ->
        val quantity = cart.quantityOf(product.id)
        when {
            quantity == 0 -> cart.copy(lines = cart.lines + CartLine(product, 1))
            quantity >= CartLine.MAX_QUANTITY -> cart
            else -> cart.withQuantity(product.id, quantity + 1)
        }
    }

    override fun decrement(productId: String) = _cart.update { cart ->
        val quantity = cart.quantityOf(productId)
        if (quantity <= 1) cart.without(productId) else cart.withQuantity(productId, quantity - 1)
    }

    override fun remove(productId: String) = _cart.update { it.without(productId) }

    override suspend fun checkout(): Order {
        val cart = _cart.value
        check(cart.lines.isNotEmpty()) { "Can't check out an empty Cart" }
        val order = orderService.placeOrder(cart)
        // Take away what was ordered rather than emptying the Cart, so a change made while the Order
        // was being placed isn't lost.
        _cart.update { it.minus(order.cart) }
        return order
    }
}

private fun Cart.minus(ordered: Cart) = copy(
    lines = lines.mapNotNull { line ->
        val left = line.quantity - ordered.quantityOf(line.product.id)
        if (left > 0) line.copy(quantity = left) else null
    },
)

private fun Cart.withQuantity(productId: String, quantity: Int) =
    copy(lines = lines.map { if (it.product.id == productId) it.copy(quantity = quantity) else it })

private fun Cart.without(productId: String) =
    copy(lines = lines.filterNot { it.product.id == productId })

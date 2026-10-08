package com.github.kittinunf.aiqua_testing.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kittinunf.aiqua_testing.aiqua.EventLogger
import com.github.kittinunf.aiqua_testing.aiqua.screenViewed
import com.github.kittinunf.aiqua_testing.catalog.Product
import com.github.kittinunf.aiqua_testing.catalog.formatPrice
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Independent facts about the Cart screen. While an Order is being placed, or its result is showing, a full-screen
 * overlay blocks the app ([isOrderOverlayVisible]).
 */
data class CartUiState(
    val lines: List<CartLineRow> = emptyList(),
    val totalText: String = formatPrice(0),
    val isPlacingOrder: Boolean = false,
    /** Set for a moment after an Order is placed, to show "Order placed" with its total. */
    val placedOrderTotalText: String? = null,
    /** Set for a moment after placing an Order fails. The Cart is unchanged, so the Shopper can tap Buy again. */
    val orderError: Throwable? = null,
) {
    val isEmpty: Boolean get() = lines.isEmpty()
    val isOrderOverlayVisible: Boolean get() = isPlacingOrder || placedOrderTotalText != null || orderError != null
}

class CartViewModel(
    private val cartRepository: CartRepository,
    private val eventLogger: EventLogger,
    private val resultDisplayTime: Duration = 2.seconds,
) : ViewModel() {
    private val orderState = MutableStateFlow(OrderState())

    @NativeCoroutinesState
    val uiState: StateFlow<CartUiState> = combine(cartRepository.cart, orderState, ::uiStateOf)
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            uiStateOf(cartRepository.cart.value, orderState.value)
        )

    /** The screen became visible. The UI calls this; a ViewModel can't tell on its own. */
    fun onScreenViewed() = eventLogger.screenViewed("cart")

    fun onAddClick(product: Product) = cartRepository.add(product)

    fun onDecrementClick(product: Product) = cartRepository.decrement(product.id)

    fun onRemoveClick(product: Product) = cartRepository.remove(product.id)

    fun onBuyClick() {
        if (uiState.value.isEmpty || uiState.value.isOrderOverlayVisible) return
        orderState.value = OrderState(isPlacing = true)
        viewModelScope.launch {
            orderState.value = try {
                val order = cartRepository.checkout()
                OrderState(placedTotalText = formatPrice(order.total))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                OrderState(error = e)
            }
            delay(resultDisplayTime)
            orderState.value = OrderState()
        }
    }

    private data class OrderState(
        val isPlacing: Boolean = false,
        val placedTotalText: String? = null,
        val error: Throwable? = null,
    )

    private fun uiStateOf(cart: Cart, orderState: OrderState) = CartUiState(
        lines = cart.lines.map {
            CartLineRow(
                product = it.product,
                quantity = it.quantity,
                unitPriceText = formatPrice(it.product.price),
                totalText = formatPrice(it.total),
            )
        },
        totalText = formatPrice(cart.total),
        isPlacingOrder = orderState.isPlacing,
        placedOrderTotalText = orderState.placedTotalText,
        orderError = orderState.error,
    )
}

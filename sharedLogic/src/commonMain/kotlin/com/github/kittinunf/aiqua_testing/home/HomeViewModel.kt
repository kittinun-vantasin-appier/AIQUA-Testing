package com.github.kittinunf.aiqua_testing.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kittinunf.aiqua_testing.aiqua.EventLogger
import com.github.kittinunf.aiqua_testing.aiqua.screenViewed
import com.github.kittinunf.aiqua_testing.cart.Cart
import com.github.kittinunf.aiqua_testing.cart.CartRepository
import com.github.kittinunf.aiqua_testing.catalog.Product
import com.github.kittinunf.aiqua_testing.catalog.formatPrice
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Independent facts about Home that can be true at the same time, e.g. still showing [sections] while a refresh
 * [isLoading] or after it failed with [error].
 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val sections: List<HomeSection> = emptyList(),
)

class HomeViewModel(
    private val homeRepository: HomeRepository,
    private val cartRepository: CartRepository,
    private val eventLogger: EventLogger,
) : ViewModel() {
    // Loading and error change together, so they live in one flow and never show a half-updated state.
    private val loadState =
        MutableStateFlow(LoadState(isLoading = homeRepository.products.value == null))

    @NativeCoroutinesState
    val uiState: StateFlow<HomeUiState> =
        combine(homeRepository.products, cartRepository.cart, loadState, ::uiStateOf)
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                uiStateOf(
                    homeRepository.products.value,
                    cartRepository.cart.value,
                    loadState.value
                ),
            )

    init {
        if (homeRepository.products.value == null) load()
    }

    /** The screen became visible. The UI calls this; a ViewModel can't tell on its own. */
    fun onScreenViewed() = eventLogger.screenViewed("home")

    fun onRetryClick() = load()

    fun onAddClick(product: Product) = cartRepository.add(product)

    fun onDecrementClick(product: Product) = cartRepository.decrement(product.id)

    fun onRemoveClick(product: Product) = cartRepository.remove(product.id)

    private fun load() {
        loadState.value = LoadState(isLoading = true)
        viewModelScope.launch {
            loadState.value = try {
                homeRepository.refresh()
                LoadState()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                LoadState(error = e)
            }
        }
    }

    private data class LoadState(val isLoading: Boolean = false, val error: Throwable? = null)

    private fun uiStateOf(products: List<Product>?, cart: Cart, loadState: LoadState) = HomeUiState(
        isLoading = loadState.isLoading,
        error = loadState.error,
        sections = products?.let { sections(it, cart) }.orEmpty(),
    )
}

internal fun sections(products: List<Product>, cart: Cart): List<HomeSection> =
    products
        .groupBy { it.category }
        .entries
        .sortedBy { it.key.lowercase() }
        .map { (category, products) ->
            HomeSection(
                title = category.uppercase(),
                rows = products
                    .sortedBy { it.name.lowercase() }
                    .map { ProductRow(it, formatPrice(it.price), cart.quantityOf(it.id)) },
            )
        }

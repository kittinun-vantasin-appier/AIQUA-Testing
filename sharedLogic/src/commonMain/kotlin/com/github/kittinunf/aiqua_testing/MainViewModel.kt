package com.github.kittinunf.aiqua_testing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kittinunf.aiqua_testing.cart.CartRepository
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** What the app's top level (the tabs) shows, outside any one screen. [cartBadgeText] is null when there's no badge. */
data class MainUiState(val cartBadgeText: String?)

/** Backs the tab bar, so the UI never reads a Repository directly. */
class MainViewModel(cartRepository: CartRepository) : ViewModel() {
    @NativeCoroutinesState
    val uiState: StateFlow<MainUiState> = cartRepository.cart
        .map { MainUiState(cartBadgeText = badgeText(it.unitCount)) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, MainUiState(badgeText(cartRepository.cart.value.unitCount)))
}

/** No badge for an empty Cart, and "99+" past 99 units, following the Material badge convention. */
internal fun badgeText(unitCount: Int): String? = when {
    unitCount <= 0 -> null
    unitCount > 99 -> "99+"
    else -> unitCount.toString()
}

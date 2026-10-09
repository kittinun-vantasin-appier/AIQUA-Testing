package com.github.kittinunf.aiqua_testing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kittinunf.aiqua_testing.cart.CartRepository
import com.github.kittinunf.aiqua_testing.inbox.InboxRepository
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** What the app's top level (the tabs) shows, outside any one screen. [cartBadgeText] is null when there's no badge. */
data class MainUiState(val cartBadgeText: String?)

/** Backs the tab bar, so the UI never reads a Repository directly. It lives as long as the app's UI does. */
class MainViewModel(cartRepository: CartRepository, inboxRepository: InboxRepository) : ViewModel() {
    @NativeCoroutinesState
    val uiState: StateFlow<MainUiState> = cartRepository.cart
        .map { MainUiState(cartBadgeText = badgeText(it.unitCount)) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, MainUiState(badgeText(cartRepository.cart.value.unitCount)))

    init {
        // Inbox messages are fetched once per launch.
        viewModelScope.launch { inboxRepository.refresh() }
    }
}

/** No badge at zero, and "99+" past 99, following the Material badge convention. */
internal fun badgeText(count: Int): String? = when {
    count <= 0 -> null
    count > 99 -> "99+"
    else -> count.toString()
}

package com.github.kittinunf.aiqua_testing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.kittinunf.aiqua_testing.cart.CartViewModel
import com.github.kittinunf.aiqua_testing.home.HomeViewModel
import kotlin.reflect.KClass

/**
 * Owns one screen's ViewModel on iOS. Swift calls [clear] when the screen goes away, which cancels the
 * ViewModel's coroutines. `ViewModel.clear()` isn't public, so this goes through a [ViewModelStore] instead.
 */
class ViewModelOwner<VM : ViewModel> internal constructor(kClass: KClass<VM>, create: () -> VM) {
    private val store = ViewModelStore()

    val viewModel: VM = ViewModelProvider
        .create(store, viewModelFactory { addInitializer(kClass) { create() } })
        .get(kClass)

    fun clear() = store.clear()
}

fun AppContainer.mainViewModelOwner(): ViewModelOwner<MainViewModel> =
    ViewModelOwner(MainViewModel::class) { mainViewModel() }

fun AppContainer.homeViewModelOwner(): ViewModelOwner<HomeViewModel> =
    ViewModelOwner(HomeViewModel::class) { homeViewModel() }

fun AppContainer.cartViewModelOwner(): ViewModelOwner<CartViewModel> =
    ViewModelOwner(CartViewModel::class) { cartViewModel() }

package com.github.kittinunf.aiqua_testing.ui

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.github.kittinunf.aiqua_testing.AppContainer
import com.github.kittinunf.aiqua_testing.R
import com.github.kittinunf.aiqua_testing.resources.MR
import com.github.kittinunf.aiqua_testing.resources.tab_cart
import com.github.kittinunf.aiqua_testing.resources.tab_home
import com.github.kittinunf.aiqua_testing.ui.cart.CartScreen
import com.github.kittinunf.aiqua_testing.ui.home.HomeScreen
import com.github.kittinunf.aiqua_testing.ui.theme.GroceryTheme
import com.github.kittinunf.aiqua_testing.ui.theme.stringResource
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute : NavKey

@Serializable
data object CartRoute : NavKey

/**
 * Two tabs on a single back stack: `[Home]` or `[Home, Cart]`.
 * Back from Cart returns to Home (which keeps its scroll position); back from Home exits.
 */
@Composable
fun GroceryApp(container: AppContainer) {
    GroceryTheme {
        val backStack = rememberNavBackStack(HomeRoute)
        val main by viewModel { container.mainViewModel() }.uiState.collectAsStateWithLifecycle()
        val current = backStack.lastOrNull()
        val goHome = { while (backStack.size > 1) backStack.removeAt(backStack.lastIndex) }
        val goCart = { if (current != CartRoute) backStack.add(CartRoute) }

        Scaffold(
            bottomBar = {
                val tabColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                )
                NavigationBar {
                    NavigationBarItem(
                        selected = current == HomeRoute,
                        onClick = goHome,
                        icon = {
                            Icon(
                                painterResource(R.drawable.ic_home),
                                contentDescription = null
                            )
                        },
                        label = { Text(stringResource(MR.strings.tab_home)) },
                        colors = tabColors,
                    )
                    NavigationBarItem(
                        selected = current == CartRoute,
                        onClick = goCart,
                        icon = {
                            BadgedBox(badge = { main.cartBadgeText?.let { Badge { Text(it) } } }) {
                                Icon(painterResource(R.drawable.ic_cart), contentDescription = null)
                            }
                        },
                        label = { Text(stringResource(MR.strings.tab_cart)) },
                        colors = tabColors,
                    )
                }
            },
        ) { padding ->
            NavDisplay(
                backStack = backStack,
                modifier = Modifier
                    .padding(padding)
                    .consumeWindowInsets(padding),
                onBack = { backStack.removeLastOrNull() },
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
                entryProvider = entryProvider {
                    entry<HomeRoute> { HomeScreen(viewModel { container.homeViewModel() }) }
                    entry<CartRoute> {
                        CartScreen(
                            viewModel { container.cartViewModel() },
                            onStartShopping = goHome
                        )
                    }
                },
            )
        }
    }
}

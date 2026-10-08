package com.github.kittinunf.aiqua_testing.ui.cart

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.kittinunf.aiqua_testing.cart.CartLineRow
import com.github.kittinunf.aiqua_testing.cart.CartUiState
import com.github.kittinunf.aiqua_testing.cart.CartViewModel
import com.github.kittinunf.aiqua_testing.catalog.Product
import com.github.kittinunf.aiqua_testing.resources.MR
import com.github.kittinunf.aiqua_testing.resources.buy
import com.github.kittinunf.aiqua_testing.resources.cart_empty
import com.github.kittinunf.aiqua_testing.resources.cart_title
import com.github.kittinunf.aiqua_testing.resources.order_failed
import com.github.kittinunf.aiqua_testing.resources.order_failed_hint
import com.github.kittinunf.aiqua_testing.resources.order_placed
import com.github.kittinunf.aiqua_testing.resources.placing_order
import com.github.kittinunf.aiqua_testing.resources.price_each
import com.github.kittinunf.aiqua_testing.resources.start_shopping
import com.github.kittinunf.aiqua_testing.resources.total
import com.github.kittinunf.aiqua_testing.ui.ProductEmoji
import com.github.kittinunf.aiqua_testing.ui.QuantityStepper
import com.github.kittinunf.aiqua_testing.ui.ScreenTopBar
import com.github.kittinunf.aiqua_testing.ui.theme.stringResource

@Composable
fun CartScreen(viewModel: CartViewModel, onStartShopping: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CartContent(
        state = state,
        onStartShopping = onStartShopping,
        onAdd = viewModel::onAddClick,
        onDecrement = viewModel::onDecrementClick,
        onRemove = viewModel::onRemoveClick,
        onBuy = viewModel::onBuyClick,
    )
    if (state.isOrderOverlayVisible) OrderOverlayDialog(state)
}

@Composable
private fun CartContent(
    state: CartUiState,
    onStartShopping: () -> Unit,
    onAdd: (Product) -> Unit,
    onDecrement: (Product) -> Unit,
    onRemove: (Product) -> Unit,
    onBuy: () -> Unit,
) {
    Scaffold(
        topBar = { ScreenTopBar(MR.strings.cart_title) },
        bottomBar = { if (!state.isEmpty) CheckoutBar(state.totalText, onBuy) },
    ) { padding ->
        val modifier = Modifier
            .fillMaxSize()
            .padding(padding)
        if (state.isEmpty) {
            EmptyCart(onStartShopping, modifier)
        } else {
            LazyColumn(modifier) {
                items(state.lines, key = { it.product.id }) { line ->
                    CartLineItem(line, onAdd, onDecrement, onRemove)
                }
            }
        }
    }
}

@Composable
private fun EmptyCart(onStartShopping: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("🛒", fontSize = 64.sp)
        Text(
            text = stringResource(MR.strings.cart_empty),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp),
        )
        Button(onClick = onStartShopping, modifier = Modifier.padding(top = 24.dp)) {
            Text(stringResource(MR.strings.start_shopping))
        }
    }
}

@Composable
private fun CartLineItem(
    line: CartLineRow,
    onAdd: (Product) -> Unit,
    onDecrement: (Product) -> Unit,
    onRemove: (Product) -> Unit,
) {
    ListItem(
        leadingContent = { ProductEmoji(line.product.emoji) },
        headlineContent = { Text(line.product.name) },
        supportingContent = {
            Row {
                Text(
                    line.totalText,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "  " + stringResource(
                        MR.strings.price_each.resourceId,
                        line.unitPriceText
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        trailingContent = {
            QuantityStepper(
                quantity = line.quantity,
                canAdd = line.canAdd,
                onAdd = { onAdd(line.product) },
                onDecrement = { onDecrement(line.product) },
                onRemove = { onRemove(line.product) },
            )
        },
    )
}

@Composable
private fun CheckoutBar(totalText: String, onBuy: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
        Column(Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(MR.strings.total), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.weight(1f))
                Text(
                    totalText,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                onClick = onBuy,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height(56.dp),
            ) {
                Text(stringResource(MR.strings.buy), style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

/** Blocks the whole app, including the tabs and the back button, until the overlay closes itself. */
@Composable
private fun OrderOverlayDialog(state: CartUiState) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                // Animate only when the phase changes: placing → placed or failed.
                AnimatedContent(
                    targetState = state,
                    contentKey = { it.isPlacingOrder to (it.orderError != null) }) { state ->
                    Column(
                        modifier = Modifier
                            .padding(32.dp)
                            .widthIn(min = 220.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        val placedTotalText = state.placedOrderTotalText
                        when {
                            state.isPlacingOrder -> {
                                CircularProgressIndicator()
                                Text(
                                    text = stringResource(MR.strings.placing_order),
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(top = 24.dp),
                                )
                            }

                            placedTotalText != null -> {
                                Text("✅", fontSize = 56.sp)
                                Text(
                                    text = stringResource(MR.strings.order_placed),
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier.padding(top = 12.dp),
                                )
                                Text(
                                    text = placedTotalText,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 8.dp),
                                )
                            }

                            state.orderError != null -> {
                                Text("❌", fontSize = 56.sp)
                                Text(
                                    text = stringResource(MR.strings.order_failed),
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier.padding(top = 12.dp),
                                )
                                Text(
                                    text = stringResource(MR.strings.order_failed_hint),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 8.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

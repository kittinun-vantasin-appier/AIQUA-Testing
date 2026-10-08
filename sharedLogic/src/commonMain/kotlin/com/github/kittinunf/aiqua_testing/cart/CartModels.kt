package com.github.kittinunf.aiqua_testing.cart

import com.github.kittinunf.aiqua_testing.catalog.Product

/** One Product in the Cart plus how many units the Shopper wants, from 1 to [MAX_QUANTITY]. */
data class CartLine(val product: Product, val quantity: Int) {
    val total: Int get() = product.price * quantity

    companion object {
        const val MAX_QUANTITY = 9
    }
}

/** The Products the Shopper intends to buy, as Cart Lines in the order they were first added. */
data class Cart(val lines: List<CartLine> = emptyList()) {
    val total: Int get() = lines.sumOf { it.total }
    val unitCount: Int get() = lines.sumOf { it.quantity }

    fun quantityOf(productId: String): Int =
        lines.find { it.product.id == productId }?.quantity ?: 0
}

/** One Cart Line as the Cart screen shows it. */
data class CartLineRow(
    val product: Product,
    val quantity: Int,
    val unitPriceText: String,
    val totalText: String,
) {
    val canAdd: Boolean get() = quantity < CartLine.MAX_QUANTITY
}

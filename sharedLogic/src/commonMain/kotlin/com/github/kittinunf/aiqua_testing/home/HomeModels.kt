package com.github.kittinunf.aiqua_testing.home

import com.github.kittinunf.aiqua_testing.cart.CartLine
import com.github.kittinunf.aiqua_testing.catalog.Product

/** One Category header (uppercased) and its Products, both sorted A→Z. */
data class HomeSection(val title: String, val rows: List<ProductRow>)

/** One Product card on Home, with how many units of it are in the Cart. */
data class ProductRow(val product: Product, val priceText: String, val quantity: Int) {
    val canAdd: Boolean get() = quantity < CartLine.MAX_QUANTITY
}

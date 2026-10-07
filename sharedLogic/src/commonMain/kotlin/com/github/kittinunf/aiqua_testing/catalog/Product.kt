package com.github.kittinunf.aiqua_testing.catalog

import kotlinx.serialization.Serializable

/** One thing for sale in the Catalog. [price] is in whole Japanese yen. */
@Serializable
data class Product(
    val id: String,
    val name: String,
    val category: String,
    val emoji: String,
    val price: Int,
)

/** Formats whole yen for display, e.g. 1280 -> "¥1,280". */
fun formatPrice(yen: Int): String =
    "¥" + yen.toString().reversed().chunked(3).joinToString(",").reversed()

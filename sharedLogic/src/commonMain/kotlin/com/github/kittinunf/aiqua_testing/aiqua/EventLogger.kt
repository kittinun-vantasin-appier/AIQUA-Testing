package com.github.kittinunf.aiqua_testing.aiqua

import com.github.kittinunf.aiqua_testing.catalog.CURRENCY
import com.github.kittinunf.aiqua_testing.order.Order

/** Where ViewModels send analytics events. The app uses [Aiqua]; tests use a fake. */
fun interface EventLogger {
    /**
     * Mirrors the AIQUA SDK: [valueToSum] is added up per event name in AIQUA (e.g. revenue), in the ISO 4217
     * [valueToSumCurrency]. Events without a value pass null for both.
     */
    fun logEvent(name: String, parameters: Map<String, Any>, valueToSum: Double?, valueToSumCurrency: String?)
}

// The events the app sends, so names and parameter keys live in one place.

/** A screen became visible: the app opened on it, or the Shopper navigated to it. */
internal fun EventLogger.screenViewed(screenName: String) =
    logEvent("screen_viewed", mapOf("screen_name" to screenName), null, null)

/**
 * An Order was placed: one `product_purchased` per Cart Line (AIQUA's recommendation models learn from these),
 * then `checkout_completed`, which alone carries the Order total as revenue so it's counted once. All share `order_id`.
 */
internal fun EventLogger.orderPlaced(order: Order) {
    order.cart.lines.forEach { line ->
        logEvent(
            "product_purchased",
            mapOf(
                "order_id" to order.id,
                "product_id" to line.product.id,
                "product_name" to line.product.name,
                "category" to line.product.category,
                "price" to line.product.price,
                "quantity" to line.quantity,
            ),
            null,
            null,
        )
    }
    logEvent("checkout_completed", mapOf("order_id" to order.id, "product_count" to order.count), order.total.toDouble(), CURRENCY)
}

/** Placing an Order failed. Nothing was bought, so there's no value and no per-item events. */
internal fun EventLogger.checkoutFailed(error: Throwable) =
    logEvent("checkout_failed", mapOf("reason" to (error.message ?: error::class.simpleName ?: "unknown")), null, null)

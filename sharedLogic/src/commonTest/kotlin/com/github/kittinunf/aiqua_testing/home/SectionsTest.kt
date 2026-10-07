package com.github.kittinunf.aiqua_testing.home

import com.github.kittinunf.aiqua_testing.cart.Cart
import com.github.kittinunf.aiqua_testing.cart.CartLine
import com.github.kittinunf.aiqua_testing.catalog.parseProducts
import kotlin.test.Test
import kotlin.test.assertEquals

class SectionsTest {
    @Test
    fun sortsCategoriesAndProductsAlphabeticallyWithUppercaseHeaders() {
        val products = parseProducts(
            """
            [
              { "id": "carrot", "name": "Carrot", "category": "vegetables", "emoji": "🥕", "price": 98 },
              { "id": "pork-belly", "name": "Pork Belly", "category": "meat", "emoji": "🥓", "price": 580 },
              { "id": "cabbage", "name": "Cabbage", "category": "vegetables", "emoji": "🥬", "price": 178 }
            ]
            """,
        )

        val sections = sections(products, Cart(listOf(CartLine(products[0], 1))))

        assertEquals(listOf("MEAT", "VEGETABLES"), sections.map { it.title })
        assertEquals(listOf("Cabbage", "Carrot"), sections[1].rows.map { it.product.name })
        assertEquals(listOf(0, 1), sections[1].rows.map { it.quantity })
        assertEquals("¥98", sections[1].rows[1].priceText)
    }
}

package com.github.kittinunf.aiqua_testing.home

import com.github.kittinunf.aiqua_testing.cart.Cart
import com.github.kittinunf.aiqua_testing.cart.CartLine
import com.github.kittinunf.aiqua_testing.catalog.Product
import kotlin.test.Test
import kotlin.test.assertEquals

class SectionsTest {
    private val carrot = Product("carrot", "Carrot", "vegetables", "🥕", 98)
    private val porkBelly = Product("pork-belly", "Pork Belly", "meat", "🥓", 580)
    private val cabbage = Product("cabbage", "Cabbage", "vegetables", "🥬", 178)

    @Test
    fun sortsCategoriesAndProductsAlphabeticallyWithUppercaseHeaders() {
        val sections = sections(listOf(carrot, porkBelly, cabbage), Cart(listOf(CartLine(carrot, 1))))

        assertEquals(listOf("MEAT", "VEGETABLES"), sections.map { it.title })
        assertEquals(listOf("Cabbage", "Carrot"), sections[1].rows.map { it.product.name })
        assertEquals(listOf(0, 1), sections[1].rows.map { it.quantity })
        assertEquals("¥98", sections[1].rows[1].priceText)
    }
}

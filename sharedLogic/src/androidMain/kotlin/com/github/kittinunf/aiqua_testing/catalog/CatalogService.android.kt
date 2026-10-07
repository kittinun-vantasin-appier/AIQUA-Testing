package com.github.kittinunf.aiqua_testing.catalog

// commonMain/resources is packaged at the root of the APK, so it's a plain classpath resource.
internal actual fun readProductJson(): String {
    val stream = Product::class.java.classLoader?.getResourceAsStream("product.json")
        ?: error("product.json is missing from the APK")
    return stream.bufferedReader().use { it.readText() }
}

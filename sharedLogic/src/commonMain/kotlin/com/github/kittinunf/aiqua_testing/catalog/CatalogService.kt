package com.github.kittinunf.aiqua_testing.catalog

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/** Fetches the Catalog's Products. Stateless: every call fetches again. */
interface CatalogService {
    @NativeCoroutinesIgnore // Swift goes through ViewModels, never Services.
    suspend fun fetchProducts(): List<Product>
}

/** Reads the Products from JSON (the bundled `product.json`), waiting [latency] first to feel like a network call. */
class JsonCatalogService(
    private val readJson: () -> String,
    private val latency: Duration = 500.milliseconds,
) : CatalogService {
    override suspend fun fetchProducts(): List<Product> {
        delay(latency)
        return withContext(Dispatchers.Default) {
            parseProducts(
                Json { ignoreUnknownKeys = true },
                readJson()
            )
        }
    }
}

/** `product.json` is a bare array of Products. */
internal fun parseProducts(json: Json, text: String): List<Product> = json.decodeFromString(text)

/** Reads the bundled `product.json` (moko-resources `MR.files.product_json`) on this platform. */
internal expect fun readProductJson(): String

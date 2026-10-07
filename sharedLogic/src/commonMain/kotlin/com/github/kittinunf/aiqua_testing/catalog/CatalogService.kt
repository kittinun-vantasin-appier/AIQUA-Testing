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

/** Reads the bundled `product.json`, waiting [latency] first to feel like a network call. */
class JsonCatalogService(
    private val latency: Duration = 500.milliseconds,
    private val readJson: () -> String = ::readProductJson,
) : CatalogService {
    override suspend fun fetchProducts(): List<Product> {
        delay(latency)
        return withContext(Dispatchers.Default) { parseProducts(readJson()) }
    }
}

private val json = Json { ignoreUnknownKeys = true }

/** `product.json` is a bare array of Products. */
fun parseProducts(text: String): List<Product> = json.decodeFromString(text)

/** Reads `product.json` as packaged for this platform: the APK classpath on Android, the app bundle on iOS. */
internal expect fun readProductJson(): String

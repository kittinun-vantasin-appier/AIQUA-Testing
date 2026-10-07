package com.github.kittinunf.aiqua_testing.home

import com.github.kittinunf.aiqua_testing.catalog.CatalogService
import com.github.kittinunf.aiqua_testing.catalog.Product
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesIgnore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Owns everything the Home screen shows. Today that's the Catalog's Products. */
interface HomeRepository {
    /** The Products, or null until the first successful [refresh]. */
    @NativeCoroutinesIgnore // Swift goes through HomeViewModel.
    val products: StateFlow<List<Product>?>

    /**
     * Loads the Home content again. This is the one place it gets (re)loaded, whether on first open or, later,
     * on pull-to-refresh. Throws if loading fails, and keeps whatever was loaded before.
     */
    @NativeCoroutinesIgnore
    suspend fun refresh()
}

class DefaultHomeRepository(private val catalogService: CatalogService) : HomeRepository {
    private val _products = MutableStateFlow<List<Product>?>(null)
    override val products: StateFlow<List<Product>?> = _products.asStateFlow()

    override suspend fun refresh() {
        _products.value = catalogService.fetchProducts()
    }
}

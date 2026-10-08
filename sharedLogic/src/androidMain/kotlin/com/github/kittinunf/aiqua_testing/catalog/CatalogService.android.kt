package com.github.kittinunf.aiqua_testing.catalog

import com.github.kittinunf.aiqua_testing.AppContextProvider
import com.github.kittinunf.aiqua_testing.resources.MR
import com.github.kittinunf.aiqua_testing.resources.product_json

internal actual fun readProductJson(): String =
    MR.files.product_json.readText(AppContextProvider.appContext)

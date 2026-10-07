package com.github.kittinunf.aiqua_testing

import com.github.kittinunf.aiqua_testing.resources.MR
import com.github.kittinunf.aiqua_testing.resources.product_json

actual class PlatformContext

internal actual fun PlatformContext.readProductJson(): String = MR.files.product_json.readText()

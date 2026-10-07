package com.github.kittinunf.aiqua_testing

import android.content.Context
import com.github.kittinunf.aiqua_testing.resources.MR
import com.github.kittinunf.aiqua_testing.resources.product_json

actual class PlatformContext(internal val context: Context)

internal actual fun PlatformContext.readProductJson(): String = MR.files.product_json.readText(context)

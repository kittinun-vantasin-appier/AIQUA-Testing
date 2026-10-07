package com.github.kittinunf.aiqua_testing.catalog

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithContentsOfFile

// The iOS app copies sharedLogic/src/commonMain/resources/product.json into its bundle
// with a "Copy Bundle Resources" entry in Xcode.
@OptIn(ExperimentalForeignApi::class)
internal actual fun readProductJson(): String {
    val path = NSBundle.mainBundle.pathForResource("product", ofType = "json")
        ?: error("product.json is missing from the app bundle")
    return NSString.stringWithContentsOfFile(path, encoding = NSUTF8StringEncoding, error = null)
        ?: error("product.json could not be read")
}

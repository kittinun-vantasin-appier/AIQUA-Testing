package com.github.kittinunf.aiqua_testing

/**
 * What shared code needs from the platform to read bundled resources (moko-resources).
 * Android: the app's Context. iOS: nothing, resources come from the app bundle.
 */
expect class PlatformContext

/** Reads the bundled `product.json` from moko-resources' `files/`. */
internal expect fun PlatformContext.readProductJson(): String

package com.github.kittinunf.aiqua_testing.aiqua

/**
 * The AIQUA SDK. Android and iOS each implement it over their native SDK (Aiqua.android.kt / Aiqua.ios.kt).
 * Members both platforms share go here. Each platform adds its own `init`, because Android needs the Application
 * and iOS doesn't; the app calls it once at launch.
 */
expect object Aiqua

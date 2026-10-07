package com.github.kittinunf.aiqua_testing

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
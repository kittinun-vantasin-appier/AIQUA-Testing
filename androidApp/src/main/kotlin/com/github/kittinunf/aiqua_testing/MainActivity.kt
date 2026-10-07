package com.github.kittinunf.aiqua_testing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.github.kittinunf.aiqua_testing.ui.GroceryApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val container = (application as GroceryApplication).container
        setContent {
            GroceryApp(container)
        }
    }
}

package com.github.kittinunf.aiqua_testing

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri

/**
 * Captures the app's Context as the process starts (before `Application.onCreate`), so shared code can read
 * bundled moko-resources files on Android without the app passing a Context in. Declared in sharedLogic's manifest.
 */
internal class AppContextProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        appContext = requireNotNull(context).applicationContext
        return true
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? = null
    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0

    companion object {
        lateinit var appContext: Context
            private set
    }
}

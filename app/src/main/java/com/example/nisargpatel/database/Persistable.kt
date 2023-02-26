package com.example.nisargpatel.database

import android.content.ContentValues
import android.database.Cursor

interface Persistable {
    val id: Long
    fun writeToContentValues(
        placeid: Int,
        address: String?,
        lattitude: Double,
        longitude: Double,
        Cityg: String?
    ): ContentValues

    fun buildFromCursor(cursor: Cursor)

}
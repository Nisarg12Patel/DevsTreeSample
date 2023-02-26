package com.example.nisargpatel.database

import android.content.ContentValues
import android.net.Uri

class ContentValuesBuilder(private val contentValues: ContentValues = ContentValues()) {

    fun with(key: String, value: String?): ContentValuesBuilder {
        return withValue(key, value)
    }

    fun with(key: String, value: Int?): ContentValuesBuilder {
        return withValue(key, value)
    }

    fun with(key: String, value: Double?): ContentValuesBuilder {
        return withValue(key, value)
    }

    private fun withValue(key: String, value: Any?): ContentValuesBuilder {
        when (value) {
            null -> contentValues.putNull(key)
            is String -> contentValues.put(key, value as String?)
            is Byte -> contentValues.put(key, value as Byte?)
            is Short -> contentValues.put(key, value as Short?)
            is Int -> contentValues.put(key, value as Int?)
            is Long -> contentValues.put(key, value as Long?)
            is Float -> contentValues.put(key, value as Float?)
            is Double -> contentValues.put(key, value as Double?)
            is Boolean -> contentValues.put(key, value as Boolean?)
            is ByteArray -> contentValues.put(key, value as ByteArray?)
            else -> throw IllegalArgumentException("Values of type " + value.javaClass.name + " cannot be inserted into a ContentValues object.")
        }
        return this
    }

    fun build(): ContentValues {
        return contentValues
    }
}

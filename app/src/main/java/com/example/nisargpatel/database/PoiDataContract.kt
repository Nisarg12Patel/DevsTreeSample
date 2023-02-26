package com.example.nisargpatel.database

import android.net.Uri
import com.example.nisargpatel.BuildConfig

object PoiDataContract {
    const val TABLE = "poi"
    const val TABLE1 = "places"
    const val AUTHORITY = "${BuildConfig.APPLICATION_ID}.$TABLE1"
    private const val BASE_URI_STRING = "content://$AUTHORITY/$TABLE"
    @JvmField val URI: Uri = Uri.parse(BASE_URI_STRING)

    @JvmStatic
    val columnNames = arrayOf(
        Columns.PLACE_ID,
        Columns.ADDRESS,
        Columns.LATITUDE,
        Columns.LONGITUDE,
        Columns.PLACE_ID,
        Columns.CITY
    )

    object Columns {
        const val PLACE_ID = "place_id"
        const val ADDRESS = "address"
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
        const val CITY = "City"

    }
}

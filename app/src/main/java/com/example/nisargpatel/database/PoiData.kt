package com.example.nisargpatel.database


import android.content.ContentValues
import android.database.Cursor

class PoiData : Persistable {
    override val id: Long
    get() = 0

    override fun writeToContentValues(
        placeid: Int,
        address: String?,
        lattitude: Double,
        longitude: Double,
        Cityg: String?
    ): ContentValues {
        val builder = ContentValuesBuilder()
        if (placeid > 0) {
            builder.with(PoiDataContract.Columns.PLACE_ID, placeid)
        }
        return builder
            .with(PoiDataContract.Columns.ADDRESS, address)
            .with(PoiDataContract.Columns.LATITUDE, lattitude)
            .with(PoiDataContract.Columns.LONGITUDE, longitude)
            .with(PoiDataContract.Columns.CITY, Cityg)
            .build()
    }

    override fun buildFromCursor(cursor: Cursor) {}
}
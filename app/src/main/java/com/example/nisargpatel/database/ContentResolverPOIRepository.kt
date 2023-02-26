package com.example.nisargpatel.database

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.nisargpatel.database.PoiDataContract.Columns

@SuppressLint("Recycle")
class ContentResolverPOIRepository(
    private  val context: Context,
    private  val database: SQLiteDatabase,
    private val contentResolver: ContentResolver = context.contentResolver

) : POIRepository {


    override fun insertPOI(
        placeid: Int,
        address: String,
        lattitude: Double,
        longitude: Double,
        City: String
    ) {
        database.insert(PoiDataContract.TABLE, null,  PoiData().writeToContentValues(0, address, lattitude, longitude, City));
    }

    override fun updatePOI(
        placeid: Int,
        address: String,
        lattitude: Double,
        longitude: Double,
        City: String
    ): Int {
        val count = database.update(PoiDataContract.TABLE,PoiData().writeToContentValues(0, address, lattitude, longitude, City),"place_id=$placeid", null );
        return count
    }

    override fun deletePOI(placeid: Int): Int {
        val count = database.delete(PoiDataContract.TABLE, "place_id=$placeid", null );
        return count
    }


}
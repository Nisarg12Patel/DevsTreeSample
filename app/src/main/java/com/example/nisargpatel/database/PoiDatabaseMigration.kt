package com.example.nisargpatel.database

import android.database.sqlite.SQLiteDatabase

class PoiDatabaseMigration : Migration() {
    override fun doMigration(sqLiteDatabase: SQLiteDatabase?) {
        sqLiteDatabase?.execSQL(
            "CREATE TABLE poi (" +
                    "place_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "address TEXT, " +
                    "latitude INTEGER, " +
                    "longitude INTEGER, " +
                    "City TEXT" +
                    ");"
        )
    }
}
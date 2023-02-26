package com.example.nisargpatel.database

import android.database.sqlite.SQLiteDatabase

abstract class Migration {
    fun execute(db: SQLiteDatabase?) {
        onPreMigrate()
        doMigration(db)
        onPostMigrate()
    }

    private fun onPreMigrate() {}
    protected abstract fun doMigration(db: SQLiteDatabase?)
    private fun onPostMigrate() {}
}
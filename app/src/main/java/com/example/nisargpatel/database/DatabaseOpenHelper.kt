package com.example.nisargpatel.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import java.util.*
import kotlin.jvm.Synchronized

class DatabaseOpenHelper constructor(
    context: Context?,
    name: String?,
    factory: CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(database: SQLiteDatabase) {
        for (migration in MIGRATIONS) {
            migration.execute(database)
        }
    }

    //    @AddTrace(name = "database_open_helper_onUpgrade")
    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (newVersion > oldVersion) {
            Log.d("Database", "onUpgrade from v$oldVersion to v$newVersion")
            for (i in oldVersion until MIGRATIONS.size) {
                MIGRATIONS[i].execute(database)
            }
        }
    }

    @Synchronized
    fun getDatabase(context: Context): SQLiteDatabase {
        val databaseFilename = databaseFilename
        var databaseOpenHelper = DATABASE_OPEN_HELPERS[databaseFilename]
        if (databaseOpenHelper == null) {
            databaseOpenHelper =
                DatabaseOpenHelper(context, databaseFilename, null, MIGRATIONS.size)
            DATABASE_OPEN_HELPERS[databaseFilename] = databaseOpenHelper
        }
        return databaseOpenHelper.writableDatabase
    }

    @Synchronized
    fun closeDatabases() {
        for (helpers in DATABASE_OPEN_HELPERS.values) {
            helpers.close()
        }
        DATABASE_OPEN_HELPERS.clear()
    }

    private val databaseFilename: String
        get() = String.format(null as Locale?, DATABASE_NAME_FORMAT)

    companion object {
        private val LOG_TAG = DatabaseOpenHelper::class.java.simpleName
        private const val DATABASE_NAME_FORMAT = "places"
        private val DATABASE_OPEN_HELPERS: MutableMap<String, DatabaseOpenHelper> = HashMap()
        private val MIGRATIONS = arrayOf<Migration>(
            PoiDatabaseMigration()
        )

    }
}
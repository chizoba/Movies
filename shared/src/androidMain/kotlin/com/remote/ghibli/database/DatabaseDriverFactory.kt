package com.remote.ghibli.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

class AndroidDatabaseDriverFactory(private val context: Context) : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver =
        AndroidSqliteDriver(AppDatabase.Schema, context, DATABASE_NAME)
}

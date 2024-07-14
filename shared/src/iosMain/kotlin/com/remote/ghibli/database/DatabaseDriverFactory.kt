package com.remote.ghibli.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

class IOSDatabaseDriverFactory : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver = NativeSqliteDriver(AppDatabase.Schema, DATABASE_NAME)
}

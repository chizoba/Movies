package com.remote.ghibli.database

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

actual fun getDatabaseDriverFactory(): DatabaseDriverFactory = DatabaseDriverFactory {
    JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
        AppDatabase.Schema.create(it)
    }
}

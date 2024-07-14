package com.remote.ghibli.database

import app.cash.sqldelight.driver.native.inMemoryDriver

actual fun getDatabaseDriverFactory(): DatabaseDriverFactory = DatabaseDriverFactory {
    inMemoryDriver(AppDatabase.Schema)
}

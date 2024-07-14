package com.remote.ghibli.database

import app.cash.sqldelight.db.SqlDriver

const val DATABASE_NAME = "app.db"

fun interface DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

package com.remote.ghibli.database

import app.cash.sqldelight.ColumnAdapter

val listOfStringAdapter = object : ColumnAdapter<List<String>, String> {
    override fun decode(databaseValue: String): List<String> =
        if (databaseValue.isEmpty()) {
            listOf()
        } else {
            databaseValue.split(",")
        }

    override fun encode(value: List<String>): String = value.joinToString(separator = ",")
}

val setOfStringAdapter = object : ColumnAdapter<Set<String>, String> {
    override fun decode(databaseValue: String): Set<String> =
        if (databaseValue.isEmpty()) {
            setOf()
        } else {
            databaseValue.split(",").toSet()
        }

    override fun encode(value: Set<String>): String = value.joinToString(separator = ",")
}

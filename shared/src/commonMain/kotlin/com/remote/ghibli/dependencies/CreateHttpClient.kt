package com.remote.ghibli.dependencies

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

fun createHttpClient() = HttpClient {
    install(ContentNegotiation) {
        json(
            Json {
                // More flexible parsing
                // > In lenient mode quoted boolean literals, and unquoted string literals are allowed.
                isLenient = true
                ignoreUnknownKeys = true
                encodeDefaults = true
                prettyPrint = true
                @OptIn(ExperimentalSerializationApi::class)
                decodeEnumsCaseInsensitive = true
            }
        )
    }

    install(Logging) {
        level = LogLevel.BODY
        logger = object : io.ktor.client.plugins.logging.Logger {
            override fun log(message: String) {
                co.touchlab.kermit.Logger.d { message }
            }
        }
    }
}
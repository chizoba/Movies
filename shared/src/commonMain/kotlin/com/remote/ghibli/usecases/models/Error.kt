package com.remote.ghibli.usecases.models

data class Error(val tag: ErrorTag) : Throwable()

enum class ErrorTag { Api, Favorite }

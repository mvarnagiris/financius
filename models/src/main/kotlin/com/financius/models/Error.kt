package com.financius.models

sealed class Error(cause: Throwable) : Throwable(cause)
class UnknownError(cause: Throwable) : Error(cause)

fun Throwable.toError(): Error {
    return when (this) {
        is Error -> this
        else -> UnknownError(this)
    }
}
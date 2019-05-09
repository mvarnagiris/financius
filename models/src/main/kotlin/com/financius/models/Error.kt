package com.financius.models

sealed class Error(cause: Throwable? = null) : Throwable(cause)
class UnknownError(cause: Throwable) : Error(cause)
object UserNotLoggedInError : Error()

fun Throwable.toError(): Error {
    return when (this) {
        is Error -> this
        else -> UnknownError(this)
    }
}
package com.financius.extensions

import kotlinx.coroutines.CoroutineScope

fun ignoreException(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
    }
}

suspend fun CoroutineScope.ignoreException(block: suspend CoroutineScope.() -> Unit) {
    try {
        block(this)
    } catch (e: Exception) {
    }
}
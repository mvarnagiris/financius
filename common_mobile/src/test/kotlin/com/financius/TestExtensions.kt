package com.financius

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.runBlocking

//suspend inline fun <reified T : Any> never() = CompletableDeferred<T>().await()
inline fun <reified T : Any> channel() = BroadcastChannel<T>(1)

fun coTest(block: suspend CoroutineScope.() -> Unit) {
    runBlocking { block() }
}
package com.financius

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.BroadcastChannel

suspend inline fun <reified T : Any> never() = CompletableDeferred<T>().await()
inline fun <reified T : Any> channel() = BroadcastChannel<T>(1)
inline fun <reified T: Any> aRandom() = ro.kreator.aRandom<T>()
inline fun <reified T: Any> deferred() = CompletableDeferred<T>()
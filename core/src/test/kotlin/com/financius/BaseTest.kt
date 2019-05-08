package com.financius

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.test.setMain
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume

@UseExperimental(InternalCoroutinesApi::class)
abstract class BaseTest {
    init {
        Dispatchers.setMain(TestUiContext())
    }

    @InternalCoroutinesApi
    private class TestUiContext : CoroutineDispatcher(), Delay {
        override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
            continuation.resume(Unit)
        }

        override fun dispatch(context: CoroutineContext, block: Runnable) {
            block.run()
        }
    }
}
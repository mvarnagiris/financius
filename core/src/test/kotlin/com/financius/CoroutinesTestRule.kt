package com.financius

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@UseExperimental(ExperimentalCoroutinesApi::class)
class CoroutinesTestRule : TestWatcher() {

    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
        testScope.cleanupTestCoroutines()
    }

    fun coTest(block: suspend CoroutineScope.() -> Unit) {
        testScope.runBlockingTest { block() }
    }
}
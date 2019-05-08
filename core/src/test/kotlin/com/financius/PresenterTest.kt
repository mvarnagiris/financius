package com.financius

import io.mockk.*
import kotlinx.coroutines.launch
import org.amshove.kluent.invoking
import org.amshove.kluent.shouldThrow
import org.junit.Rule
import org.junit.Test

class PresenterTest {

    @Rule
    @JvmField
    val rule = CoroutinesTestRule()

    private val presenter = TestPresenter()
    private val view = mockk<TestPresenter.View>(relaxed = true)
    private val otherView = mockk<TestPresenter.View>(relaxed = true)

    @Test
    fun `throws when attaching after dispose`() {
        presenter.dispose()
        invoking { presenter attach view } shouldThrow IllegalStateException::class
    }

    @Test
    fun `immediately shows latest state after attach starting with initial state and ignores state changes while view is detached`() {
        presenter attach view
        verify { view.onStateChanged("0") }

        clearMocks(view)
        presenter detach view
        presenter attach view
        verify { view.onStateChanged("0") }

        clearMocks(view)
        presenter.doIntent(2)
        verify { view.onStateChanged("2") }

        clearMocks(view)
        presenter detach view
        presenter attach view
        verify { view.onStateChanged("2") }

        clearMocks(view)
        presenter detach view
        presenter.doIntent(4)
        verify(exactly = 0) { view.onStateChanged(any()) }
        presenter attach view
        verify { view.onStateChanged("4") }
    }

    @Test
    fun `current view will be detached when new view is attached`() {
        presenter attach view
        presenter attach otherView

        verifyOrder {
            view.onDetached()
            otherView.onAttached()
            otherView.onStateChanged("0")
        }
    }

    @Test
    fun `attaching same view more than once has no effect`() {
        presenter attach view

        clearMocks(view)
        excludeRecords { view.equals(any()) }
        presenter attach view

        confirmVerified(view)
    }

    @Test
    fun `side effects are consumed only once`() {
        presenter attach view
        verify(exactly = 0) { view.onSideEffectReceived(any()) }

        clearMocks(view)
        presenter detach view
        presenter attach view
        verify(exactly = 0) { view.onSideEffectReceived(any()) }

        clearMocks(view)
        presenter.doIntent(1)
        verify { view.onSideEffectReceived(1f) }

        clearMocks(view)
        presenter detach view
        presenter attach view
        verify(exactly = 0) { view.onSideEffectReceived(any()) }

        clearMocks(view)
        presenter detach view
        presenter.doIntent(3)
        presenter.doIntent(5)
        verify(exactly = 0) { view.onSideEffectReceived(any()) }
        presenter attach view
        presenter.doIntent(2)
        verifyOrder {
            view.onSideEffectReceived(3f)
            view.onSideEffectReceived(5f)
            view.onStateChanged("2")
        }
    }

    @Test
    fun `can set new state while handling a state`() {
        presenter attach view

        presenter.doIntent(10)

        verify {
            view.onStateChanged("10")
            view.onStateChanged("20")
        }
    }

    private class TestPresenter : Presenter<Int, String, Float, TestPresenter.View>() {

        override fun getInitialState(): String = "0"

        override fun onAttached(view: View) {
            super.onAttached(view)
            view.onAttached()
        }

        override fun onDetached(view: View) {
            super.onDetached(view)
            view.onDetached()
        }

        override suspend fun mapIntentToState(oldState: String, intent: Int): String {
            return if (intent % 2 == 0) intent.toString()
            else oldState
        }

        override suspend fun mapIntentToSideEffect(currentState: String, intent: Int): Float? {
            return if (intent % 2 == 1) intent.toFloat()
            else null
        }

        override fun onStateChanged(view: View, state: String) {
            view.onStateChanged(state)
            if (state == "10") setState("20")
        }

        override fun onSideEffectReceived(view: View, sideEffect: Float) {
            view.onSideEffectReceived(sideEffect)
        }

        fun doIntent(intent: Int) {
            launch { intent(intent) }
        }

        interface View : Presenter.View {
            fun onAttached()
            fun onDetached()
            fun onStateChanged(state: String)
            fun onSideEffectReceived(sideEffect: Float)
        }
    }

}
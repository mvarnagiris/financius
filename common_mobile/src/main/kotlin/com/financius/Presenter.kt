package com.financius

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.CoroutineContext

abstract class Presenter<INTENT, STATE, SIDE_EFFECT, VIEW : Presenter.View> : CoroutineScope {

    override val coroutineContext: CoroutineContext by lazy { SupervisorJob() + Dispatchers.Main }

    private val state by lazy { AtomicReference(getInitialState()) }
    private val sideEffectChannel by lazy { Channel<SIDE_EFFECT>(Channel.UNLIMITED) }

    private var currentView: VIEW? = null
    private var jobsUntilDetached: List<Job>? = null

    infix fun attach(view: VIEW) {
        if (!coroutineContext.isActive) throw IllegalStateException("Presenter $this has been disposed")

        val currentlyAttachedView = currentView
        if (view == currentlyAttachedView) return
        if (currentlyAttachedView != null) detach(currentlyAttachedView)

        this.currentView = view

        onAttached(view)

        onStateChanged(view, state.get())

        launchUntilDetached {
            for (sideEffect in sideEffectChannel) {
                onSideEffectReceived(view, sideEffect)
            }
        }
    }

    infix fun detach(view: VIEW) {
        currentView = null
        jobsUntilDetached?.forEach { it.cancel() }
        jobsUntilDetached = null
        onDetached(view)
    }

    fun dispose() {
        currentView?.run { detach(this) }
        sideEffectChannel.cancel()
        coroutineContext.cancel()
    }

    protected suspend fun intent(intent: INTENT) {
        val state = mapIntentToState(state.get(), intent)
        setState(state)

        val sideEffect = mapIntentToSideEffect(state, intent)
        if (sideEffect != null) addSideEffect(sideEffect)
    }

    protected open suspend fun mapIntentToState(oldState: STATE, intent: INTENT): STATE {
        return oldState
    }

    protected open suspend fun mapIntentToSideEffect(currentState: STATE, intent: INTENT): SIDE_EFFECT? {
        return null
    }

    protected fun setState(state: STATE) {
        val oldState = this.state.get()
        if (state != oldState) {
            this.state.set(state)
            currentView?.run { onStateChanged(this, state) }
        }
    }

    protected fun addSideEffect(sideEffect: SIDE_EFFECT) {
        sideEffectChannel.offer(sideEffect)
    }

    protected fun ReceiveChannel<INTENT>.receiveUntilDetached() = launchUntilDetached {
        for (value in this@receiveUntilDetached) {
            intent(value)
        }
    }

    protected fun launchUntilDetached(block: suspend () -> Unit): Job {
        val job = launch { block() }
        jobsUntilDetached = jobsUntilDetached.orEmpty() + job
        return job
    }

    protected abstract fun getInitialState(): STATE
    protected abstract fun onStateChanged(view: VIEW, state: STATE)
    protected open fun onSideEffectReceived(view: VIEW, sideEffect: SIDE_EFFECT) {}

    protected open fun onAttached(view: VIEW) {}
    protected open fun onDetached(view: VIEW) {}

    interface View
}
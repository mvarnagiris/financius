package com.financius

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class Presenter<INTENT, STATE, SIDE_EFFECT, VIEW : Presenter.View> : CoroutineScope {

    override val coroutineContext: CoroutineContext by lazy { SupervisorJob() + Dispatchers.Main }

    private val stateChannel by lazy { ConflatedBroadcastChannel(getInitialState()) }
    private val sideEffectChannel by lazy { Channel<SIDE_EFFECT>(Channel.UNLIMITED) }

    private var currentView: VIEW? = null
    private var currentOpenStateChannel: ReceiveChannel<STATE>? = null
    private var currentSideEffectsJob: Job? = null
    private var viewIntentChannels: List<ReceiveChannel<INTENT>>? = null

    infix fun attach(view: VIEW) {
        if (!coroutineContext.isActive) throw IllegalStateException("Presenter $this has been disposed")

        val currentlyAttachedView = currentView
        if (view == currentlyAttachedView) return
        if (currentlyAttachedView != null) detach(currentlyAttachedView)

        this.currentView = view
        val openStateChannel = stateChannel.openSubscription()
        currentOpenStateChannel = openStateChannel
        onAttached(view)

        launch {
            for (state in openStateChannel) {
                onStateChanged(view, state)
            }
        }

        currentSideEffectsJob = launch {
            for (sideEffect in sideEffectChannel) {
                onSideEffectReceived(view, sideEffect)
            }
        }
    }

    infix fun detach(view: VIEW) {
        currentView = null
        currentOpenStateChannel?.cancel()
        currentOpenStateChannel = null
        viewIntentChannels?.forEach { it.cancel() }
        viewIntentChannels = null
        currentSideEffectsJob?.cancel()
        currentSideEffectsJob = null
        onDetached(view)
    }

    fun dispose() {
        currentView?.run { detach(this) }
        stateChannel.cancel()
        sideEffectChannel.cancel()
        coroutineContext.cancel()
    }

    protected suspend fun intent(intent: INTENT) {
        val state = mapIntentToState(stateChannel.value, intent)
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

    protected suspend fun setState(state: STATE) {
        val oldState = stateChannel.value
        if (state != oldState) stateChannel.send(state)
    }

    protected suspend fun addSideEffect(sideEffect: SIDE_EFFECT) {
        sideEffectChannel.send(sideEffect)
    }

    protected fun ReceiveChannel<INTENT>.untilDetached() = launch {
        viewIntentChannels = viewIntentChannels.orEmpty() + this@untilDetached
        for (value in this@untilDetached) {
            intent(value)
        }
    }

    protected abstract fun getInitialState(): STATE
    protected abstract fun onStateChanged(view: VIEW, state: STATE)
    protected open fun onSideEffectReceived(view: VIEW, sideEffect: SIDE_EFFECT) {}

    protected open fun onAttached(view: VIEW) {}
    protected open fun onDetached(view: VIEW) {}

    interface View
}
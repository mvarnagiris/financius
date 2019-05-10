package com.financius.features.debug

import com.financius.AttachListeningPresenter
import com.financius.data.LogoutService
import com.financius.features.debug.DebugPresenter.DebugItem.Action
import com.financius.features.debug.DebugPresenter.DebugItem.Action.Logout
import com.financius.features.debug.DebugPresenter.Intent
import com.financius.features.debug.DebugPresenter.SideEffect
import com.financius.features.debug.DebugPresenter.SideEffect.RestartApp
import com.financius.features.debug.DebugPresenter.State
import com.financius.features.debug.DebugPresenter.State.ExecutingAction
import com.financius.features.debug.DebugPresenter.State.Loaded
import com.financius.features.debug.DebugPresenter.View
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

class DebugPresenter(private val logoutService: LogoutService) : AttachListeningPresenter<Intent, State, SideEffect, View>() {

    override fun onAttached(view: View) {
        super.onAttached(view)
        view.debugItemSelects().receiveUntilDetached()
    }

    override fun getInitialState(): State = Loaded(listOf(Logout(logoutService)))

    override suspend fun mapIntentToSideEffect(currentState: State, intent: Intent): SideEffect? {
        when (intent.debugItem) {
            is Action -> {
                launch {
                    intent.debugItem.execute()
                    addSideEffect(intent.debugItem.sideEffectAfterExecution())
                }
            }
        }

        return null
    }

    override fun onStateChanged(view: View, state: State) {
        when (state) {
            is Loaded -> {
                view.showDebugItems(state.debugItems)
                view.hideExecuting()
            }
            is ExecutingAction -> {
                view.showDebugItems(state.debugItems)
                view.showExecuting(state.action)
                launch {
                    state.action.execute()
                }
            }
        }
    }

    override fun onSideEffectReceived(view: View, sideEffect: SideEffect) {
        when (sideEffect) {
            RestartApp -> view.restartApp()
        }
    }

    sealed class DebugItem {
        sealed class Action : DebugItem() {

            internal abstract suspend fun execute()
            internal abstract fun sideEffectAfterExecution(): SideEffect

            data class Logout(private val logoutService: LogoutService) : Action() {
                override suspend fun execute() = logoutService.logout()
                override fun sideEffectAfterExecution(): SideEffect = RestartApp
            }
        }
    }

    data class Intent(val debugItem: DebugItem)

    sealed class State {
        data class Loaded(val debugItems: List<DebugItem>) : State()
        data class ExecutingAction(val action: Action, val debugItems: List<DebugItem>) : State()
    }

    sealed class SideEffect {
        object RestartApp : SideEffect()
    }

    interface View : AttachListeningPresenter.View {
        fun debugItemSelects(): ReceiveChannel<Intent>
        fun showDebugItems(debugItems: List<DebugItem>)
        fun showExecuting(debugItem: DebugItem)
        fun hideExecuting()
        fun restartApp()
    }
}
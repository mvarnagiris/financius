package com.financius.features.login

import com.financius.AttachListeningPresenter
import com.financius.data.models.Login.GoogleLogin
import com.financius.features.login.LoginPresenter.Intent
import com.financius.features.login.LoginPresenter.SideEffect
import com.financius.features.login.LoginPresenter.State
import com.financius.features.login.LoginPresenter.State.LoginMethodSelection
import com.financius.features.login.LoginPresenter.View

class LoginPresenter : AttachListeningPresenter<Intent, State, SideEffect, View>() {

    override fun getInitialState(): State = LoginMethodSelection

    override fun onStateChanged(view: View, state: State) {
        when (state) {
            LoginMethodSelection -> view.showLoginMethodSelection()
        }
    }

    sealed class Intent

    sealed class State {
        object LoginMethodSelection : State()
    }

    sealed class SideEffect

    interface View : AttachListeningPresenter.View {
        suspend fun loginWithGoogle(): GoogleLogin
        fun showLoginMethodSelection()
    }
}
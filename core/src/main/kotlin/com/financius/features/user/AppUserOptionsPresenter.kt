package com.financius.features.user

import com.financius.AttachListeningPresenter
import com.financius.data.LogoutService
import com.financius.features.user.AppUserOptionsPresenter.Intent
import com.financius.features.user.AppUserOptionsPresenter.SideEffect
import com.financius.features.user.AppUserOptionsPresenter.State
import com.financius.features.user.AppUserOptionsPresenter.View

class AppUserOptionsPresenter(private val logoutService: LogoutService) : AttachListeningPresenter<Intent, State, SideEffect, View>() {

    override fun getInitialState(): State {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStateChanged(view: View, state: State) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    sealed class Intent
    sealed class State
    sealed class SideEffect
    interface View : AttachListeningPresenter.View
}
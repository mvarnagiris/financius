package com.financius.features.splash

import com.financius.AttachListeningPresenter
import com.financius.data.AuthenticationDataSource
import com.financius.features.splash.SplashPresenter.*
import com.financius.features.splash.SplashPresenter.SideEffect.ShowLoggedIn
import com.financius.features.splash.SplashPresenter.SideEffect.ShowNotLoggedIn
import com.financius.features.splash.SplashPresenter.State.Loading
import kotlinx.coroutines.launch

class SplashPresenter(private val authenticationDataSource: AuthenticationDataSource) : AttachListeningPresenter<Unit, State, SideEffect, View>() {

    init {
        launch {
            val authentication = authenticationDataSource.getAuthentication()
            if (authentication.isLoggedIn) addSideEffect(ShowLoggedIn)
            else addSideEffect(ShowNotLoggedIn)
        }
    }

    override fun getInitialState(): State = Loading

    override fun onStateChanged(view: View, state: State) {
        view.showLoading()
    }

    override fun onSideEffectReceived(view: View, sideEffect: SideEffect) {
        when (sideEffect) {
            ShowLoggedIn -> view.showLoggedIn()
            ShowNotLoggedIn -> view.showNotLoggedIn()
        }
    }

    sealed class State {
        object Loading : State()
    }

    sealed class SideEffect {
        object ShowNotLoggedIn : SideEffect()
        object ShowLoggedIn : SideEffect()
    }

    interface View : AttachListeningPresenter.View {
        fun showLoading()
        fun showNotLoggedIn()
        fun showLoggedIn()
    }
}
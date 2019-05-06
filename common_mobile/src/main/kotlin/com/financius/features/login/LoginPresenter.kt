package com.financius.features.login

import com.financius.AttachListeningPresenter
import com.financius.data.models.Error
import com.financius.data.models.Login
import com.financius.data.models.Login.GoogleLogin
import com.financius.data.models.toError
import com.financius.features.login.LoginPresenter.Intent
import com.financius.features.login.LoginPresenter.Intent.LogAAA
import com.financius.features.login.LoginPresenter.Intent.LoginWithGoogle
import com.financius.features.login.LoginPresenter.SideEffect
import com.financius.features.login.LoginPresenter.SideEffect.ShowError
import com.financius.features.login.LoginPresenter.SideEffect.ShowLoggedIn
import com.financius.features.login.LoginPresenter.State
import com.financius.features.login.LoginPresenter.State.LoggingIn
import com.financius.features.login.LoginPresenter.State.LoggingInToGoogle
import com.financius.features.login.LoginPresenter.State.LoginMethodSelection
import com.financius.features.login.LoginPresenter.View
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginPresenter(private val loginService: LoginService) : AttachListeningPresenter<Intent, State, SideEffect, View>() {

    override fun onAttached(view: View) {
        super.onAttached(view)
        view.loginWithGoogleRequests().receiveUntilDetached()
    }

    override fun getInitialState(): State = LoginMethodSelection

    override suspend fun mapIntentToState(oldState: State, intent: Intent): State {
        return when {
            oldState is LoginMethodSelection && intent is LoginWithGoogle -> LoggingInToGoogle
            intent is LogAAA -> LoginMethodSelection
            else -> oldState
        }
    }

    override fun onStateChanged(view: View, state: State) {
        println("State = ${state::class.java.simpleName}")
        when (state) {
            LoginMethodSelection -> view.showLoginMethodSelection()
            LoggingInToGoogle -> loginWithGoogle(view)
            LoggingIn -> view.showLoggingIn()
        }
    }

    override fun onSideEffectReceived(view: View, sideEffect: SideEffect) {
        when (sideEffect) {
            ShowLoggedIn -> view.showLoggedIn()
            is ShowError -> view.showError(sideEffect.error)
        }
    }

    private fun loginWithGoogle(view: View) {
//        launch { intent(LogAAA) }
        launch { setState(LoggingIn) }
//        setState(LoginMethodSelection)
//        setState(LoggingIn)
//        launch { setState(LoginMethodSelection) }
//        launch { delay(500);setState(LoggingIn) }
        view.showLoggingIn()
//        launchUntilDetached {
//            val googleLogin = getGoogleLogin(view)
//            login(googleLogin)
//        }
    }

    private suspend fun getGoogleLogin(view: View): GoogleLogin {
        return try {
            view.loginWithGoogle()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            addShowErrorSideEffect(e.toError())
            throw e
        }
    }

    private fun login(login: Login) {
        launch {
            try {
                val authentication = loginService.login(login)
                if (authentication.isLoggedIn) addSideEffect(ShowLoggedIn)
                else addShowErrorSideEffect(RuntimeException("Failed to log in").toError())
            } catch (e: Exception) {
                addShowErrorSideEffect(e.toError())
            }
        }
    }

    private fun addShowErrorSideEffect(error: Error) {
//        setState(LoginMethodSelection)
        addSideEffect(ShowError(error))
    }

    sealed class Intent {
        object LoginWithGoogle : Intent()
        object LogAAA : Intent()
    }

    sealed class State {
        internal object LoginMethodSelection : State()
        internal object LoggingInToGoogle : State()
        internal object LoggingIn : State()
    }

    sealed class SideEffect {
        internal object ShowLoggedIn : SideEffect()
        internal data class ShowError(val error: Error) : SideEffect()
    }

    interface View : AttachListeningPresenter.View {
        fun loginWithGoogleRequests(): ReceiveChannel<LoginWithGoogle>
        fun showLoginMethodSelection()
        fun showLoggingIn()
        fun showError(error: Error)
        fun showLoggedIn()
        suspend fun loginWithGoogle(): GoogleLogin
    }
}
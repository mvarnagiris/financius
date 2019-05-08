package com.financius.features.login

import com.financius.BaseTest
import com.financius.channel
import com.financius.models.Authentication
import com.financius.models.Login.GoogleLogin
import com.financius.features.login.LoginPresenter.Intent.LoginWithGoogle
import com.financius.loggedInAuthentication
import com.financius.never
import com.financius.notLoggedInAuthentication
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CompletableDeferred
import org.junit.Before
import org.junit.Test

class LoginPresenterTest : BaseTest() {

    private val loginWithGoogleRequestsChannel = channel<LoginWithGoogle>()

    private val loginService = mockk<LoginService>()
    private val presenter by lazy { LoginPresenter(loginService) }
    private val view = mockk<LoginPresenter.View>(relaxed = true)

    @Before
    fun setUp() {
        every { view.loginWithGoogleRequests() } returns loginWithGoogleRequestsChannel.openSubscription()
    }

    @Test
    fun `initially shows login method selection`() {
        presenter attach view

        verify { view.showLoginMethodSelection() }
    }

    @Test
    fun `can login with google`() {
        val googleLogin = mockk<GoogleLogin>()
        val authentication = loggedInAuthentication
        val deferredGoogleLogin = CompletableDeferred<GoogleLogin>()
        val deferredAuthentication = CompletableDeferred<Authentication>()
        coEvery { loginService.login(googleLogin) } coAnswers { deferredAuthentication.await() }
        presenter attach view

        coEvery { view.loginWithGoogle() } coAnswers { never() }
        loginWithGoogle()
        loginWithGoogle()
        presenter detach view
        coEvery { view.loginWithGoogle() } coAnswers { deferredGoogleLogin.await() }
        presenter attach view
        deferredGoogleLogin.complete(googleLogin)
        presenter detach view
        presenter attach view
        deferredAuthentication.complete(authentication)

        coVerifyOrder {
            view.showLoggingIn()
            view.loginWithGoogle()
            view.showLoggingIn()
            view.loginWithGoogle()
            loginService.login(googleLogin)
            view.showLoggingIn()
            view.showLoggedIn()
        }

        coVerify(exactly = 1) { loginService.login(any()) }
        coVerify(exactly = 2) { view.loginWithGoogle() }
    }

    @Test
    fun `handles google login errors`() {
        val googleLogin = mockk<GoogleLogin>()
        coEvery { loginService.login(googleLogin) } throws mockk<Exception>(relaxed = true)
        presenter attach view

        clearMocks(view)
        coEvery { view.loginWithGoogle() } throws mockk<Exception>(relaxed = true)
        loginWithGoogle()
        verify {
            view.showLoggingIn()
            view.showLoginMethodSelection()
            view.showError(any())
        }

        clearMocks(view)
        coEvery { view.loginWithGoogle() } returns googleLogin
        loginWithGoogle()
        verify {
            view.showLoggingIn()
            view.showLoginMethodSelection()
            view.showError(any())
        }

        clearMocks(view)
        coEvery { view.loginWithGoogle() } returns googleLogin
        coEvery { loginService.login(googleLogin) } returns notLoggedInAuthentication
        loginWithGoogle()
        verify {
            view.showLoggingIn()
            view.showLoginMethodSelection()
            view.showError(any())
        }

        clearMocks(view)
        coEvery { view.loginWithGoogle() } returns googleLogin
        coEvery { loginService.login(googleLogin) } returns loggedInAuthentication
        loginWithGoogle()
        verify {
            view.showLoggedIn()
        }
    }

    private fun loginWithGoogle() = loginWithGoogleRequestsChannel.offer(LoginWithGoogle)
}
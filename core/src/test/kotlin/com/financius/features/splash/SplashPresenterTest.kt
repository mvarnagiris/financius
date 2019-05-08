package com.financius.features.splash

import com.financius.CoroutinesTestRule
import com.financius.data.AuthenticationDataSource
import com.financius.loggedInAuthentication
import com.financius.models.Authentication
import com.financius.notLoggedInAuthentication
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verifyOrder
import kotlinx.coroutines.CompletableDeferred
import org.junit.Rule
import org.junit.Test

class SplashPresenterTest {

    @Rule
    @JvmField
    val rule = CoroutinesTestRule()

    private val authenticationDataSource = mockk<AuthenticationDataSource>()
    private val presenter by lazy { SplashPresenter(authenticationDataSource) }
    private val view = mockk<SplashPresenter.View>(relaxed = true)

    @Test
    fun `shows not logged in when user is not logged in`() = rule.coTest {
        val asyncAuthentication = CompletableDeferred<Authentication>()
        coEvery { authenticationDataSource.getAuthentication() } coAnswers { asyncAuthentication.await() }

        presenter.attach(view)
        asyncAuthentication.complete(notLoggedInAuthentication)

        verifyOrder {
            view.showLoading()
            view.showNotLoggedIn()
        }
    }

    @Test
    fun `shows logged in when user is logged in`() = rule.coTest {
        val asyncAuthentication = CompletableDeferred<Authentication>()
        coEvery { authenticationDataSource.getAuthentication() } coAnswers { asyncAuthentication.await() }

        presenter.attach(view)
        asyncAuthentication.complete(loggedInAuthentication)

        verifyOrder {
            view.showLoading()
            view.showLoggedIn()
        }
    }

}
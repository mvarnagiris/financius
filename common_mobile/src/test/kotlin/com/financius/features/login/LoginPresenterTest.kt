package com.financius.features.login

import com.financius.BaseTest
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class LoginPresenterTest : BaseTest() {

    private val presenter by lazy { LoginPresenter() }
    private val view = mockk<LoginPresenter.View>(relaxed = true)

    @Test
    fun `initially shows login method selection`() {
        presenter attach view

        verify { view.showLoginMethodSelection() }
    }

    @Test
    fun `can login with google`() {
        presenter attach view

        loginWithGoogle()
        presenter detach view
        presenter attach view

        verify { view.showLoginMethodSelection() }
    }

    private fun loginWithGoogle() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
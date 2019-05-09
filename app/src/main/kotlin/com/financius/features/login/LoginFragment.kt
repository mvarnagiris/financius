package com.financius.features.login

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.financius.R
import com.financius.extensions.clicks
import com.financius.features.errorShowToast
import com.financius.features.login.LoginModule.loginPresenter
import com.financius.features.login.LoginPresenter.Intent.LoginWithGoogle
import com.financius.models.Error
import com.financius.models.Login.GoogleLogin
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.android.synthetic.main.login_fragment.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.ReceiveChannel
import life.shank.android.AutoAttachable
import life.shank.android.AutoScoped

private const val REQUEST_GOOGLE_LOGIN = 1

class LoginFragment : Fragment(R.layout.login_fragment), LoginPresenter.View, AutoScoped, AutoAttachable {

    private val googleSignInOptions
        get() =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

    private val googleSignInClient get() = GoogleSignIn.getClient(requireContext(), googleSignInOptions)
    private var currentDeferredGoogleLogin: CompletableDeferred<GoogleLogin>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginPresenter.register(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_GOOGLE_LOGIN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val googleLogin = GoogleLogin(account.idToken!!)
                getDeferredGoogleLogin().complete(googleLogin)
            } catch (e: Exception) {
                getDeferredGoogleLogin().completeExceptionally(e)
            }
        }
    }

    override fun loginWithGoogleRequests(): ReceiveChannel<LoginWithGoogle> = googleLoginButton.clicks { LoginWithGoogle }

    override fun showLoginMethodSelection() {
        progressBar.isVisible = false
        googleLoginButton.isVisible = true
    }

    override fun showLoggingIn() {
        progressBar.isVisible = true
        googleLoginButton.isVisible = false
    }

    override fun showError(error: Error) {
        currentDeferredGoogleLogin = null
        errorShowToast(error)
    }

    override fun showLoggedIn() = findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())

    override suspend fun loginWithGoogle(): GoogleLogin {
        if (currentDeferredGoogleLogin == null) startActivityForResult(googleSignInClient.signInIntent, REQUEST_GOOGLE_LOGIN)
        return getDeferredGoogleLogin().await()
    }

    private fun getDeferredGoogleLogin(): CompletableDeferred<GoogleLogin> {
        val deferredGoogleLogin = currentDeferredGoogleLogin
        if (deferredGoogleLogin != null) return deferredGoogleLogin

        val newDeferredGoogleLogin = CompletableDeferred<GoogleLogin>()
        currentDeferredGoogleLogin = newDeferredGoogleLogin
        return newDeferredGoogleLogin
    }
}
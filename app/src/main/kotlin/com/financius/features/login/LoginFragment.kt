package com.financius.features.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.financius.R
import com.financius.data.models.Login.GoogleLogin
import com.financius.features.BaseFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.CompletableDeferred

private const val REQUEST_GOOGLE_LOGIN = 1

class LoginFragment : BaseFragment(), LoginPresenter.View {

    private val googleSignInOptions
        get() =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

    private val googleSignInClient get() = GoogleSignIn.getClient(requireContext(), googleSignInOptions)

    private var deferredGoogleLogin: CompletableDeferred<GoogleLogin>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_GOOGLE_LOGIN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val googleLogin = GoogleLogin(account.idToken!!)
                deferredGoogleLogin?.complete(googleLogin)
            } catch (e: Exception) {
                deferredGoogleLogin?.completeExceptionally(e)
            }
        }
    }

    override suspend fun loginWithGoogle(): GoogleLogin {
        startActivityForResult(googleSignInClient.signInIntent, REQUEST_GOOGLE_LOGIN)
        return TODO()//getDeferredGoogleLogin()
    }

    override fun showLoginMethodSelection() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun getDeferredGoogleLogin(): CompletableDeferred<GoogleLogin> {
        val deferred = deferredGoogleLogin ?: CompletableDeferred()
        deferredGoogleLogin = deferred
        return deferred
    }
}
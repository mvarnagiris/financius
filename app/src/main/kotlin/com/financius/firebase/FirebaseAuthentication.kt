package com.financius.firebase

import com.financius.data.datasources.AuthenticationDataSource
import com.financius.data.models.Authentication
import com.financius.data.models.Login
import com.financius.data.models.Login.GoogleLogin
import com.financius.data.models.UserId
import com.financius.data.models.noAuthentication
import com.financius.features.login.LoginService
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseAuthentication : AuthenticationDataSource, LoginService {

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    override suspend fun getAuthentication(): Authentication = withContext(Dispatchers.IO) {
        firebaseAuth.currentUser.toAuthentication()
    }

    override suspend fun login(login: Login): Authentication {
        val authentication = getAuthentication()

        return suspendCancellableCoroutine { continuation ->
            val authResultTask =
                if (authentication.isLoggedIn) linkToCurrentAccount(login)
                else createNewAccount(login)

            authResultTask.addOnCompleteListener {
                if (it.isSuccessful) continuation.resume(it.result!!.user.toAuthentication())
                else continuation.resumeWithException(it.exception!!)
            }
        }
    }

    private fun linkToCurrentAccount(login: Login): Task<AuthResult> = firebaseAuth.currentUser!!.linkWithCredential(login.createCredential())
    private fun createNewAccount(login: Login): Task<AuthResult> = firebaseAuth.signInWithCredential(login.createCredential())

    private fun Login.createCredential() = when (this) {
        is GoogleLogin -> GoogleAuthProvider.getCredential(token, null)
    }

    private fun FirebaseUser?.toAuthentication() = if (this != null) Authentication(UserId(uid)) else noAuthentication

}
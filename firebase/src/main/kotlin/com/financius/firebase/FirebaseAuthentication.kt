package com.financius.firebase

import com.financius.data.AppUserDataSource
import com.financius.data.AuthenticationDataSource
import com.financius.data.LoginService
import com.financius.models.AppUser
import com.financius.models.Authentication
import com.financius.models.Login
import com.financius.models.Login.GoogleLogin
import com.financius.models.NoImage
import com.financius.models.RemoteImage
import com.financius.models.Uri
import com.financius.models.UserId
import com.financius.models.UserNotLoggedInError
import com.financius.models.noAuthentication
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

class FirebaseAuthentication : AuthenticationDataSource, LoginService, AppUserDataSource {

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

    override suspend fun getAppUser(): AppUser {
        val authentication = getAuthentication().takeIf { it.isLoggedIn } ?: throw UserNotLoggedInError
        return AppUser(authentication.userId, firebaseAuth.currentUser.photo)
    }

    private fun linkToCurrentAccount(login: Login): Task<AuthResult> =
        firebaseAuth.currentUser!!.linkWithCredential(login.createCredential())

    private fun createNewAccount(login: Login): Task<AuthResult> =
        firebaseAuth.signInWithCredential(login.createCredential())

    private fun Login.createCredential() = when (this) {
        is GoogleLogin -> GoogleAuthProvider.getCredential(token, null)
    }

    private fun FirebaseUser?.toAuthentication() = if (this != null) Authentication(userId) else noAuthentication
    private val FirebaseUser.userId get() = UserId(uid)
    private val FirebaseUser?.photo get() = this?.photoUrl?.toString()?.let { RemoteImage(Uri(it)) } ?: NoImage
}
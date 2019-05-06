package com.financius.firebase

import com.financius.data.datasources.AuthenticationDataSource
import com.financius.data.models.Authentication
import com.financius.data.models.UserId
import com.financius.data.models.noAuthentication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirebaseAuthentication : AuthenticationDataSource {

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    override suspend fun getAuthentication(): Authentication = withContext(Dispatchers.IO) {
        firebaseAuth.currentUser.toAuthentication()
    }

    private fun FirebaseUser?.toAuthentication() = if (this != null) Authentication(UserId(uid)) else noAuthentication

}
package com.financius.data.datasources

import com.financius.data.AuthenticationDataSource
import com.financius.firebase.FirebaseModule.firebaseAuthentication
import life.shank.ShankModule
import life.shank.singleton

object DataSourcesModule : ShankModule {

    val authenticationDataSource = singleton<AuthenticationDataSource> { firebaseAuthentication() }

}
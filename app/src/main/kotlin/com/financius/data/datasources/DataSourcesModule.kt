package com.financius.data.datasources

import com.financius.FirebaseModule.firebaseAuthentication
import com.financius.data.AuthenticationDataSource
import life.shank.ShankModule
import life.shank.singleton

object DataSourcesModule : ShankModule {

    val authenticationDataSource = singleton<AuthenticationDataSource> { firebaseAuthentication() }

}
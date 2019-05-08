package com.financius.features.login

import com.financius.data.LoginService
import com.financius.firebase.FirebaseModule.firebaseAuthentication
import life.shank.ShankModule
import life.shank.android.supportAutoAttach
import life.shank.new
import life.shank.scoped

object LoginModule : ShankModule {

    val loginService = new<LoginService> { firebaseAuthentication() }
    val loginPresenter = scoped { -> LoginPresenter(loginService()) }.supportAutoAttach()

}
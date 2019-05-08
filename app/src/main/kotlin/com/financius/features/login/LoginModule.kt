package com.financius.features.login

import com.financius.data.services.ServicesModule.loginService
import life.shank.ShankModule
import life.shank.android.supportAutoAttach
import life.shank.scoped

object LoginModule : ShankModule {

    val loginPresenter = scoped { -> LoginPresenter(loginService()) }.supportAutoAttach()

}
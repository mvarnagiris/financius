package com.financius.data.services

import com.financius.FirebaseModule.firebaseAuthentication
import com.financius.data.LoginService
import life.shank.ShankModule
import life.shank.new

object ServicesModule : ShankModule {

    val loginService = new<LoginService> { firebaseAuthentication() }

}
package com.financius

import com.financius.firebase.FirebaseAuthentication
import life.shank.ShankModule
import life.shank.singleton

object FirebaseModule : ShankModule {

    val firebaseAuthentication = singleton { -> FirebaseAuthentication() }

}
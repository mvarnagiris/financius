package com.financius.firebase

import life.shank.ShankModule
import life.shank.singleton

object FirebaseModule : ShankModule {

    val firebaseAuthentication = singleton { -> FirebaseAuthentication() }

}
package com.financius.features.login

import com.financius.models.Authentication
import com.financius.models.Login

interface LoginService {
    suspend fun login(login: Login): Authentication
}
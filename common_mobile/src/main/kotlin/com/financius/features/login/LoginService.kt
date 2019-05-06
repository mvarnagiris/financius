package com.financius.features.login

import com.financius.data.models.Authentication
import com.financius.data.models.Login

interface LoginService {
    suspend fun login(login: Login): Authentication
}
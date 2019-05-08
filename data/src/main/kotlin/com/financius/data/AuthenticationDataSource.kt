package com.financius.data

import com.financius.models.Authentication

interface AuthenticationDataSource {
    suspend fun getAuthentication(): Authentication
}

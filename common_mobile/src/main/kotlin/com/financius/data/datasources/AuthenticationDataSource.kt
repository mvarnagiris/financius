package com.financius.data.datasources

import com.financius.models.Authentication

interface AuthenticationDataSource {
    suspend fun getAuthentication(): Authentication
}
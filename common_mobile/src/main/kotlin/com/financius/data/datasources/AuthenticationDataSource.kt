package com.financius.data.datasources

import com.financius.data.models.Authentication

interface AuthenticationDataSource {
    suspend fun getAuthentication(): Authentication
}
package com.financius.data

import com.financius.models.AppUser

interface AppUserDataSource {
    suspend fun getAppUser(): AppUser
}

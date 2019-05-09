package com.financius.models

data class UserId(val value: String)

data class AppUser(
    val id: UserId,
    val photo: Image
)
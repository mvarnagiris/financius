package com.financius.models

data class Authentication(val userId: UserId) {
    val isLoggedIn get() = userId.value.isNotBlank()
}

sealed class Login {
    data class GoogleLogin(val token: String) : Login()
}

val noAuthentication = Authentication(UserId(""))

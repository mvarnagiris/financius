package com.financius

import com.financius.models.Authentication
import io.mockk.every
import io.mockk.mockk


val loggedInAuthentication by lazy { mockk<Authentication>(relaxed = true) { every { isLoggedIn } returns true } }
val notLoggedInAuthentication by lazy { mockk<Authentication>(relaxed = true) { every { isLoggedIn } returns false } }
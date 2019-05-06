package com.financius.data.services

import com.financius.data.models.PhoneNumber
import com.financius.data.models.VerificationCode

interface VerifyPhoneService {
    suspend fun sendVerificationCode(phoneNumber: PhoneNumber): ResendCodeDelaySeconds
    suspend fun verify(phoneNumber: PhoneNumber, verificationCode: VerificationCode)
}

data class ResendCodeDelaySeconds(val value: Int)
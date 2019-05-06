package com.financius.data.services

import com.financius.data.models.PhoneNumber

interface PhoneNumberValidator {
    fun validatePhoneNumber(phoneNumber: PhoneNumber): Boolean
}
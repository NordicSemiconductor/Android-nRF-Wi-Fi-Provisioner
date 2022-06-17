package com.nordicsemi.wifi.provisioner.repository

import javax.inject.Inject

class PasswordManager @Inject constructor() {

    private val passwords = mutableMapOf<String, String>()

    fun getPasswordOrNull(ssid: String): String? {
        return passwords[ssid]
    }

    fun storePassword(ssid: String, password: String) {
        passwords[ssid] = password
    }
}

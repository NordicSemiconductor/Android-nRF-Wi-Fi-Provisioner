package no.nordicsemi.android.wifi.provisioner.nfc.domain

import android.util.Log

/**
 * Enum class that represents the authentication mode of a wifi network.
 */
enum class AuthMode(val id: Int) {
    UNKNOWN(-1),
    OPEN(0),
    WEP(1),
    WPA_PSK(2),
    WPA2_EAP(3),
    SAE(4),
    EAP_WPA3_ENTERPRISE_192_BIT(5),
    OWE(6),
    WAPI_PSK(7),
    WAPI_CERT(8),
    EAP_WPA3_ENTERPRISE(9),
    OSEN(10),
    PASSPOINT_R1_R2(11),
    PASSPOINT_R3(12),
    DPP(13); // Security type for Easy Connect (DPP) network

    companion object {
        /**
         * List of all the authentication modes.
         */
        fun getListOfAuthModes(): List<String> {
            val list = AuthMode.entries.map { mapToUi(it) }
            Log.d("AAA", "getListOfAuthModes: $list")
            return AuthMode.entries.map { mapToUi(it) }
        }

        /**
         * Get the authentication mode of a given security type.
         * @param securityTypes The security type of the wifi network.
         * @return The authentication mode of the wifi network.
         */
        fun getMatchedAuthMode(securityTypes: IntArray): String {
            securityTypes.forEach { type ->
                val authMode = AuthMode.entries.find { it.id == type }
                if (authMode != null) {
                    return mapToUi(authMode)
                }
            }
            val authMode = AuthMode.entries.find { it.id in securityTypes } ?: OPEN
            return mapToUi(authMode)
        }

        /**
         * Maps the authentication mode to a user friendly string.
         */
        private fun mapToUi(authMode: AuthMode): String {
            return when (authMode) {
                UNKNOWN -> "Unknown"
                OPEN -> "OPEN"
                WEP -> "WEP"
                WPA_PSK -> "WPA2-PSK"
                WPA2_EAP -> "WPA2-EAP"
                SAE -> "SAE"
                EAP_WPA3_ENTERPRISE_192_BIT -> "EAP-WPA3-ENTERPRISE-192-BIT"
                OWE -> "OWE"
                WAPI_PSK -> "WAPI-PSK"
                WAPI_CERT -> "WAPI-CERT"
                EAP_WPA3_ENTERPRISE -> "EAP-WPA3-ENTERPRISE"
                OSEN -> "OSEN"
                PASSPOINT_R1_R2 -> "PASSPOINT-R1-R2"
                PASSPOINT_R3 -> "PASSPOINT-R3"
                DPP -> "DPP"
            }
        }
    }
}

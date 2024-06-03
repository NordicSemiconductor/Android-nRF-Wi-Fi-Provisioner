package no.nordicsemi.android.wifi.provisioner.feature.nfc.data

import android.net.wifi.ScanResult
import android.os.Build

object WifiAuthType {
    /**
     * Constants for security types.
     *
     * Note: This is for Build version sdk S and below.
     */
    private const val OPEN = "Open"
    private const val WEP = "WEP"
    private const val WPA_PSK = "WPA-PSK"
    private const val WPA2_PSK = "WPA2-PSK"
    private const val WPA_WPA2_PSK = "WPA/WPA2-PSK"
    private const val WPA2_EAP = "WPA2-EAP"
    private const val WPA3_PSK = "WPA3-PSK"

    private val securityTypes =
        listOf(OPEN, WEP, WPA_PSK, WPA2_PSK, WPA_WPA2_PSK, WPA2_EAP, WPA3_PSK)

    /**
     * @return The security of a given [ScanResult].
     */
    fun getSecurityTypes(scanResult: ScanResult): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return AuthMode.getMatchedAuthMode(scanResult.securityTypes)
        } else {
            val cap = scanResult.capabilities
            val securityModes = securityTypes
            for (i in securityModes.indices.reversed()) {
                if (cap.contains(securityModes[i])) {
                    return mapToUi(securityModes[i])
                }
            }
            return mapToUi(AuthMode.OPEN.name)
        }
    }

    /**
     * Maps the security type to a user friendly string.
     *
     * Note: This is for Build version sdk S and below.
     */
    private fun mapToUi(mode: String): String {
        return when (mode) {
            "WPA-PSK", "WPA_PSK" -> "WPA-Personal"
            "WPA2-PSK", "WPA2_PSK" -> "WPA2-Personal"
            "WPA/WPA2-PSK", "WPA_WPA2_PSK" -> "WPA/WPA2-Personal"
            "WPA2-EAP", "WPA2_EAP" -> "WPA2-Enterprise"
            "WPA3-PSK", "WPA3_PSK" -> "WPA3-Personal"
            "WEP" -> "Shared"
            else -> "Open"
        }
    }

    /**
     * @return The list of security types.
     */
    fun authList(): List<String> {
        return securityTypes.map { mapToUi(it) }
    }
}

/**
 * Enum class that represents the authentication mode of a wifi network.
 *
 * Note: This is for Build version sdk Tiramisu (API 31) and above.
 */
enum class AuthMode(val id: Int) {
    UNKNOWN(-1),
    OPEN(0),
    WEP(1), // Shared
    WPA_PSK(2),
    WPA2_EAP(3),
    WPA3_PSK(4), // WPA3-Personal (SAE) password)
    EAP_WPA3_ENTERPRISE_192_BIT(5),
    OWE(6), // Opportunistic Wireless Encryption
    WAPI_PSK(7), // WAPI pre-shared key (PSK)
    WAPI_CERT(8), // WAPI certificate to be specified.
    EAP_WPA3_ENTERPRISE(9),
    OSEN(10), // Hotspot 2.
    PASSPOINT_R1_R2(11),
    PASSPOINT_R3(12),
    DPP(13); // Security type for Easy Connect (DPP) network

    override fun toString(): String {
        return when (this) {
            UNKNOWN -> "Unknown"
            OPEN -> "Open"
            WEP -> "WEP"
            WPA_PSK -> "WPA-Personal"
            WPA2_EAP -> "WPA2-Enterprise"
            WPA3_PSK -> "WPA3-Personal"
            EAP_WPA3_ENTERPRISE_192_BIT -> "EAP-WPA3-ENTERPRISE-192-BIT"
            OWE -> "OWE"
            WAPI_PSK -> "WAPI-Personal"
            WAPI_CERT -> "WAPI-CERT"
            EAP_WPA3_ENTERPRISE -> "EAP-WPA3-ENTERPRISE"
            OSEN -> "OSEN"
            PASSPOINT_R1_R2 -> "PASSPOINT-R1-R2"
            PASSPOINT_R3 -> "PASSPOINT-R3"
            DPP -> "DPP"
        }
    }

    companion object {

        /**
         * Get the authentication mode of a given security type.
         * @param securityTypes The security type of the wifi network.
         * @return The authentication mode of the wifi network.
         */
        fun getMatchedAuthMode(securityTypes: IntArray): String {
            val matchedType = mutableListOf<String>()
            securityTypes.forEach { type ->
                val authMode = AuthMode.entries.find { it.id == type }
                if (authMode != null) {
                    matchedType.add(authModeTOString(authMode))
                }
            }
            return matchedType.joinToString(", ")
        }

        /**
         * Maps the authentication mode to a user friendly string.
         *
         * Note: This is for Build version sdk Tiramisu (API 31) and above.
         */
        private fun authModeTOString(authMode: AuthMode): String {
            return authMode.toString()
        }
    }
}

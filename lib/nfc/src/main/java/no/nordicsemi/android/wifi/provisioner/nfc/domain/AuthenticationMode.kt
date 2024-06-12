package no.nordicsemi.android.wifi.provisioner.nfc.domain

import android.net.wifi.ScanResult
import android.os.Build
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Interface that represents the authentication mode of a Wi-Fi network.
 */
@Parcelize
sealed interface AuthenticationMode : Parcelable {

    companion object {

        fun get(scanResult: ScanResult): List<AuthenticationMode> {
            return when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    WifiAuthTypeTiramisuOrAbove.getMatchedAuthMode(scanResult.securityTypes)
                }

                else -> {
                    listOf(WifiAuthTypeBelowTiramisu.getSecurityTypes(scanResult))
                }
            }
        }
    }
}

/**
 * Enum class that represents the authentication mode of a wifi network.
 *
 * Note: This is for Build version sdk below Tiramisu.
 */
@Parcelize
enum class WifiAuthTypeBelowTiramisu(private val stringRep: String) : AuthenticationMode {
    OPEN("Open"),
    WEP("WEP"),
    WPA_PSK("WPA-PSK"),
    WPA2_PSK("WPA2-PSK"),
    WPA_EAP("WPA-EAP"),
    WPA_WPA2_PSK("WPA/WPA2-PSK"),
    WPA2_EAP("WPA2-EAP"),
    WPA3_PSK("WPA3-PSK");

    companion object {

        /**
         * @return The security of a given [ScanResult].
         */
        fun getSecurityTypes(scanResult: ScanResult): WifiAuthTypeBelowTiramisu {
            val cap = scanResult.capabilities
            val securityModes = entries.map { it.stringRep }
            for (i in securityModes.indices.reversed()) {
                if (cap.contains(securityModes[i])) {
                    WifiAuthTypeBelowTiramisu.entries.find { it.stringRep == securityModes[i] }
                        ?.let {
                            return it
                        }
                }
            }
            return OPEN
        }

    }
}

/**
 * Enum class that represents the authentication mode of a wifi network.
 *
 * Note: This is for Build version sdk Tiramisu and above.
 */
@Parcelize
enum class WifiAuthTypeTiramisuOrAbove(val id: Int) : AuthenticationMode {
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

    companion object {

        /**
         * Returns the authentication for provided security type.
         */
        fun getMatchedAuthMode(securityTypes: IntArray): List<WifiAuthTypeTiramisuOrAbove> {
            val matchedType = mutableListOf<WifiAuthTypeTiramisuOrAbove>()
            securityTypes.forEach { type ->
                val authMode = WifiAuthTypeTiramisuOrAbove.entries.find { it.id == type }
                if (authMode != null) {
                    matchedType.add(authMode)
                }
            }
            return matchedType.toList()
        }
    }
}

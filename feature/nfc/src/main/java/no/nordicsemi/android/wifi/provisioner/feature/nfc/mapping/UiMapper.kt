package no.nordicsemi.android.wifi.provisioner.feature.nfc.mapping

import no.nordicsemi.android.wifi.provisioner.nfc.domain.AuthenticationMode
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiAuthTypeBelowTiramisu
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiAuthTypeTiramisuOrAbove

/**
 * Converts the [AuthenticationMode] to a display string.
 *
 * @return The display string.
 */
fun AuthenticationMode.toDisplayString(): String = when (this) {
    WifiAuthTypeBelowTiramisu.OPEN, WifiAuthTypeTiramisuOrAbove.OPEN -> "Open"
    WifiAuthTypeBelowTiramisu.WEP, WifiAuthTypeTiramisuOrAbove.WEP -> "Shared"
    WifiAuthTypeBelowTiramisu.WPA_PSK, WifiAuthTypeTiramisuOrAbove.WPA_PSK -> "WPA-Personal"
    WifiAuthTypeBelowTiramisu.WPA2_PSK -> "WPA2-Personal"
    WifiAuthTypeBelowTiramisu.WPA_WPA2_PSK -> "WPA/WPA2-Personal"
    WifiAuthTypeBelowTiramisu.WPA2_EAP, WifiAuthTypeTiramisuOrAbove.WPA2_EAP -> "WPA2-Enterprise"
    WifiAuthTypeBelowTiramisu.WPA3_PSK, WifiAuthTypeTiramisuOrAbove.WPA3_PSK -> "WPA3-Personal"
    WifiAuthTypeTiramisuOrAbove.UNKNOWN -> "Unknown"
    WifiAuthTypeTiramisuOrAbove.EAP_WPA3_ENTERPRISE_192_BIT -> "EAP-WPA3-Enterprise-192-Bit"
    WifiAuthTypeTiramisuOrAbove.OWE -> "Opportunistic-Wireless-Encryption"
    WifiAuthTypeTiramisuOrAbove.WAPI_PSK -> "WAPI-PSK"
    WifiAuthTypeTiramisuOrAbove.WAPI_CERT -> "WAPI-Certificate"
    WifiAuthTypeTiramisuOrAbove.EAP_WPA3_ENTERPRISE -> "EAP-WPA3-Enterprise"
    WifiAuthTypeTiramisuOrAbove.OSEN -> "Hotspot-2"
    WifiAuthTypeTiramisuOrAbove.PASSPOINT_R1_R2 -> "Passpoint-R1-R2"
    WifiAuthTypeTiramisuOrAbove.PASSPOINT_R3 -> "Passpoint-R3"
    WifiAuthTypeTiramisuOrAbove.DPP -> "DPP"
    WifiAuthTypeBelowTiramisu.WPA_EAP -> "WPA-Enterprise"
}

/**
 * @return The list of security types supported to display in the dropdown.
 */
fun authListToDisplay(): List<String> {
    return WifiAuthTypeBelowTiramisu.entries.map { it.toDisplayString() }
}

/**
 * Converts the display string to [AuthenticationMode].
 *
 * @return The [AuthenticationMode].
 */
fun String.toAuthenticationMode(): AuthenticationMode = when (this) {
    "Shared" -> WifiAuthTypeBelowTiramisu.WEP
    "WPA-Personal" -> WifiAuthTypeBelowTiramisu.WPA_PSK
    "WPA2-Personal" -> WifiAuthTypeBelowTiramisu.WPA2_PSK
    "WPA/WPA2-Personal" -> WifiAuthTypeBelowTiramisu.WPA_WPA2_PSK
    "WPA2-Enterprise" -> WifiAuthTypeBelowTiramisu.WPA2_EAP
    "WPA3-Personal" -> WifiAuthTypeBelowTiramisu.WPA3_PSK
    else -> WifiAuthTypeBelowTiramisu.OPEN
}

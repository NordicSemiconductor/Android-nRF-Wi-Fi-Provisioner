package no.nordicsemi.android.wifi.provisioner.feature.nfc.mapping

import no.nordicsemi.android.wifi.provisioner.nfc.domain.AuthenticationMode
import no.nordicsemi.android.wifi.provisioner.nfc.domain.EncryptionMode

/**
 * Converts the [AuthenticationMode] to a display string.
 *
 * @return The display string.
 */
fun AuthenticationMode.toDisplayString(): String = when (this) {
    AuthenticationMode.OPEN -> "Open"
    AuthenticationMode.WEP -> "Shared"
    AuthenticationMode.WPA_PSK -> "WPA-Personal"
    AuthenticationMode.WPA2_PSK -> "WPA2-Personal"
    AuthenticationMode.WPA_WPA2_PSK -> "WPA/WPA2-Personal"
    AuthenticationMode.WPA2_EAP -> "WPA2-Enterprise"
    AuthenticationMode.WPA3_PSK -> "WPA3-Personal"
    AuthenticationMode.WPA_EAP -> "WPA-Enterprise"
}

/**
 * @return The list of security types supported to display in the dropdown.
 *
 * Note: The security types that are not supported (such as WPA-Enterprise) are removed.
 */
fun authListToDisplay(): List<String> {
    return listOf(
        "Open",
        "Shared",
        "WPA-Personal",
        "WPA2-Personal",
        "WPA/WPA2-Personal",
    )
}

/**
 * Converts the display string to [AuthenticationMode].
 *
 * @return The [AuthenticationMode].
 */
fun String.toAuthenticationMode(): AuthenticationMode = when (this) {
    "Shared" -> AuthenticationMode.WEP
    "WPA-Personal" -> AuthenticationMode.WPA_PSK
    "WPA2-Personal" -> AuthenticationMode.WPA2_PSK
    "WPA/WPA2-Personal" -> AuthenticationMode.WPA_WPA2_PSK
    "WPA2-Enterprise" -> AuthenticationMode.WPA2_EAP
    "WPA3-Personal" -> AuthenticationMode.WPA3_PSK
    else -> AuthenticationMode.OPEN
}

/**
 * Converts the [EncryptionMode] to a display string.
 *
 * @return The display string.
 */
fun EncryptionMode.toDisplayString(): String = when (this) {
    EncryptionMode.NONE -> "None"
    EncryptionMode.WEP -> "WEP"
    EncryptionMode.TKIP -> "TKIP"
    EncryptionMode.AES -> "AES"
    EncryptionMode.AES_TKIP -> "AES/TKIP"
}

/**
 * Converts the display string to [EncryptionMode].
 *
 * @return The [EncryptionMode].
 */
fun String.toEncryptionMode(): EncryptionMode = when (this) {
    "WEP" -> EncryptionMode.WEP
    "TKIP" -> EncryptionMode.TKIP
    "AES" -> EncryptionMode.AES
    "AES/TKIP" -> EncryptionMode.AES_TKIP
    else -> EncryptionMode.NONE
}
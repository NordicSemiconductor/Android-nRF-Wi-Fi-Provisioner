package no.nordicsemi.android.wifi.provisioner.nfc.domain

import android.net.wifi.ScanResult

/**
 * This enum class represents the encryption mode of a Wi-Fi network.
 */
enum class EncryptionMode {
    NONE,
    WEP,
    TKIP,
    AES,
    AES_TKIP;

    companion object {

        /**
         * Returns the encryption mode of the given scan result.
         *
         * @param result the scan result.
         * @return the encryption mode.
         */
        fun getEncryption(result: ScanResult): String {
            val firstCapabilities =
                result.capabilities.substring(1, result.capabilities.indexOf("]"))
            val capabilities = firstCapabilities.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            val auth = capabilities[0] + "-" + capabilities[1]
            val encryptionMode = when {
                auth.contains("WPA2-PSK") -> AES
                auth.contains("WPA-PSK") -> TKIP
                auth.contains("WPA2-EAP") -> AES
                auth.contains("WPA-EAP") -> AES_TKIP // TODO: Verify this.
                auth.contains("WPA/WPA2-PSK") -> AES_TKIP
                auth.contains("WEP") -> WEP
                auth.contains("Open") -> NONE
                auth.contains("WPA3-PSK") -> AES
                else -> NONE
            }

            return encryptionMode.name
        }

        // Encryption modes to be displayed in the UI
        private val encryptionModesToUi = mapOf(
            "NONE" to NONE,
            "WEP" to WEP,
            "TKIP" to TKIP,
            "AES" to AES,
            "AES/TKIP" to AES_TKIP
        )

        /** Returns the list of encryption modes to be displayed in the UI. */
        fun getEncryptionList(): List<String> {
            return encryptionModesToUi.keys.toList()
        }
    }
}

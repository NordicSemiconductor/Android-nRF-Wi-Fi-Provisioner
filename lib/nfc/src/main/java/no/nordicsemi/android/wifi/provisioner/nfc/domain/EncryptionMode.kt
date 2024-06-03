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

    override fun toString() = when (this) {
        NONE -> "NONE"
        WEP -> "WEP"
        TKIP -> "TKIP"
        AES -> "AES"
        AES_TKIP -> "AES/TKIP"

    }

    companion object {

        /**
         * Returns the encryption mode of the given scan result.
         *
         * Examples of capabilities:
         * 1. `[WPA2-PSK-CCMP][ESS]`
         * 2. `[WPA2-PSK-CCMP+TKIP][ESS]`
         * 3. `[WPA-PSK-CCMP+TKIP][WPA2-PSK-CCMP+TKIP][ESS]`
         * [see more](https://stackoverflow.com/questions/11956874/scanresult-capabilities-interpretation), [here](https://security.stackexchange.com/questions/229986/does-wpa2-use-tkip-or-not),
         * [here](https://developer.android.com/reference/kotlin/android/net/wifi/ScanResult#capabilities)
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
                auth.contains("WPA2-PSK") ||
                        auth.contains("WPA3-PSK") ||
                        auth.contains("WPA2-EAP") -> AES

                auth.contains("WPA-PSK") -> TKIP
                auth.contains("WPA-EAP") ||
                        auth.contains("WPA/WPA2-PSK") -> AES_TKIP

                auth.contains("WEP") -> WEP
                else -> NONE
            }

            return encryptionMode.toString()
        }
    }
}

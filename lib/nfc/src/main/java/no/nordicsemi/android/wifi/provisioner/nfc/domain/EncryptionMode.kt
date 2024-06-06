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
        // Constants for the encryption modes.
        private const val WPA2_PSK = "WPA2-PSK"
        private const val WPA3_PSK = "WPA3-PSK"
        private const val WPA2_EAP = "WPA2-EAP"
        private const val WPA_EAP = "WPA2-EAP"
        private const val WPA_PSK = "WPA-PSK"
        private const val WPA_WPA2_PSK = "WPA/WPA2-PSK"
        private const val WEP_S = "WEP"

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
        fun get(result: ScanResult): EncryptionMode {
            return try {
                val firstCapabilities =
                    result.capabilities.substring(1, result.capabilities.indexOf("]"))
                val capabilities =
                    firstCapabilities.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                val auth = capabilities[0] + "-" + capabilities[1]
                return when {
                    auth.contains(WPA2_PSK) ||
                            auth.contains(WPA3_PSK) ||
                            auth.contains(WPA2_EAP) -> AES

                    auth.contains(WPA_PSK) -> TKIP
                    auth.contains(WPA_EAP) ||
                            auth.contains(WPA_WPA2_PSK) -> AES_TKIP

                    auth.contains(WEP_S) -> WEP
                    else -> NONE
                }

            } catch (e: ArrayIndexOutOfBoundsException) {
                // Handle the case where capabilities array doesn't have enough elements
                NONE
            } catch (e: StringIndexOutOfBoundsException) {
                // Handle the case where substring or indexOf operations fail
                NONE
            }
        }
    }
}

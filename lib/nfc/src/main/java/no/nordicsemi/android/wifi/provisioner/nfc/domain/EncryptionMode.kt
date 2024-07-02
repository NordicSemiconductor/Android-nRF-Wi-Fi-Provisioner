/*
 * Copyright (c) 2024, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific prior
 * written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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

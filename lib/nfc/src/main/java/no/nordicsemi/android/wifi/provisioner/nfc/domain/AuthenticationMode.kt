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
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * An enum class that represents the authentication mode of a Wi-Fi network.
 */
@Parcelize
enum class AuthenticationMode(private val stringRep: String) : Parcelable {
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
        private fun getSecurityTypes(scanResult: ScanResult): AuthenticationMode {
            val cap = scanResult.capabilities
            val securityModes = AuthenticationMode.entries.map { it.stringRep }
            for (i in securityModes.indices.reversed()) {
                if (cap.contains(securityModes[i])) {
                    AuthenticationMode.entries.find { it.stringRep == securityModes[i] }
                        ?.let {
                            return it
                        }
                }
            }
            return OPEN
        }

        /**
         * @return The authentication mode(s) of a given [ScanResult].
         */
        fun get(scanResult: ScanResult): AuthenticationMode {
            return AuthenticationMode.getSecurityTypes(scanResult)
        }
    }
}

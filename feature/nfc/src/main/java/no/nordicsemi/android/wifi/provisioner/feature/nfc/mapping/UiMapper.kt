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
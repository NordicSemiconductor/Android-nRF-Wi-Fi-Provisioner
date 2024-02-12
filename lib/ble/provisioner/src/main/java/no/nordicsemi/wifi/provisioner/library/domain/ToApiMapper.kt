/*
 * Copyright (c) 2022, Nordic Semiconductor
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

package no.nordicsemi.wifi.provisioner.library.domain

import no.nordicsemi.android.wifi.provisioning.AuthMode
import no.nordicsemi.android.wifi.provisioning.Band
import no.nordicsemi.android.wifi.provisioning.WifiConfig
import no.nordicsemi.android.wifi.provisioning.WifiInfo
import okio.ByteString.Companion.toByteString

internal fun WifiConfigDomain.toApi(): WifiConfig {
    return WifiConfig(
        wifi = info.toApi(),
        passphrase = password?.toByteArray()?.toByteString(),
        volatileMemory = volatileMemory,
        anyChannel = anyChannel
    )
}

internal fun WifiInfoDomain.toApi(): WifiInfo {
    return WifiInfo(
        ssid = ssid.toByteArray().toByteString(),
        bssid = bssid,
        band = band?.toApi(),
        channel = channel,
        auth = authModeDomain?.toApi()
    )
}

internal fun BandDomain.toApi(): Band {
    return when (this) {
        BandDomain.BAND_ANY -> Band.BAND_ANY
        BandDomain.BAND_2_4_GH -> Band.BAND_2_4_GH
        BandDomain.BAND_5_GH -> Band.BAND_5_GH
    }
}

internal fun AuthModeDomain.toApi(): AuthMode {
    return when (this) {
        AuthModeDomain.OPEN -> AuthMode.OPEN
        AuthModeDomain.WEP -> AuthMode.WEP
        AuthModeDomain.WPA_PSK -> AuthMode.WPA_PSK
        AuthModeDomain.WPA2_PSK -> AuthMode.WPA2_PSK
        AuthModeDomain.WPA_WPA2_PSK -> AuthMode.WPA_WPA2_PSK
        AuthModeDomain.WPA2_ENTERPRISE -> AuthMode.WPA2_ENTERPRISE
        AuthModeDomain.WPA3_PSK -> AuthMode.WPA3_PSK
    }
}

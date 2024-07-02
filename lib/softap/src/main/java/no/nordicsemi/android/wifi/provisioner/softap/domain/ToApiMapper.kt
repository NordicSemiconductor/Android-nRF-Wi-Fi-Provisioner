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

package no.nordicsemi.android.wifi.provisioner.softap.domain

import no.nordicsemi.android.wifi.provisioner.softap.proto.AuthMode
import no.nordicsemi.android.wifi.provisioner.softap.proto.Band
import no.nordicsemi.android.wifi.provisioner.softap.proto.ScanRecord
import no.nordicsemi.android.wifi.provisioner.softap.proto.ScanResults
import no.nordicsemi.android.wifi.provisioner.softap.proto.WifiConfig
import no.nordicsemi.android.wifi.provisioner.softap.proto.WifiInfo
import no.nordicsemi.kotlin.wifi.provisioner.domain.AuthModeDomain
import no.nordicsemi.kotlin.wifi.provisioner.domain.BandDomain
import no.nordicsemi.kotlin.wifi.provisioner.domain.ScanRecordDomain
import no.nordicsemi.kotlin.wifi.provisioner.domain.WifiInfoDomain
import okio.ByteString.Companion.toByteString

internal fun AuthModeDomain.toApi() = when(this) {
    AuthModeDomain.OPEN -> AuthMode.OPEN
    AuthModeDomain.WEP -> AuthMode.WEP
    AuthModeDomain.WPA_PSK -> AuthMode.WPA_PSK
    AuthModeDomain.WPA2_PSK -> AuthMode.WPA2_PSK
    AuthModeDomain.WPA_WPA2_PSK -> AuthMode.WPA_WPA2_PSK
    AuthModeDomain.WPA2_ENTERPRISE -> AuthMode.WPA2_ENTERPRISE
    AuthModeDomain.WPA3_PSK -> AuthMode.WPA3_PSK
}

internal fun BandDomain.toApi() = when(this) {
    BandDomain.BAND_ANY -> Band.BAND_ANY
    BandDomain.BAND_2_4_GH -> Band.BAND_2_4_GHZ
    BandDomain.BAND_5_GH -> Band.BAND_5_GHZ
}

@Suppress("unused")
internal fun WifiConfigDomain.toApi() = WifiConfig(
    wifi = info?.toApi(),
    passphrase = passphrase?.toByteArray()?.toByteString(),
)

internal fun WifiInfoDomain.toApi() = WifiInfo(
    ssid = ssid.toByteArray().toByteString(),
    bssid = bssid,
    band = band?.toApi(),
    channel = channel,
    auth = authModeDomain?.toApi()
)

internal fun ScanRecordDomain.toApi() = ScanRecord(
    wifi = wifiInfo?.toApi(),
    rssi = rssi ?: 0
)

@Suppress("unused")
internal fun ScanResultsDomain.toApi() = ScanResults(
    results = results.map { it.toApi() }
)

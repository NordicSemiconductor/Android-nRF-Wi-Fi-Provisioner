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

package no.nordicsemi.android.wifi.provisioner.ui.mapping

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SignalWifi4BarLock
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import no.nordicsemi.android.wifi.provisioner.ui.R
import no.nordicsemi.kotlin.wifi.provisioner.domain.AuthModeDomain
import no.nordicsemi.kotlin.wifi.provisioner.domain.BandDomain

fun AuthModeDomain?.toImageVector() = when (this) {
    AuthModeDomain.OPEN -> Icons.Outlined.Wifi
    AuthModeDomain.WEP,
    AuthModeDomain.WPA_PSK,
    AuthModeDomain.WPA2_PSK,
    AuthModeDomain.WPA_WPA2_PSK,
    AuthModeDomain.WPA2_ENTERPRISE,
    AuthModeDomain.WPA3_PSK,
    null -> Icons.Outlined.SignalWifi4BarLock
}

fun AuthModeDomain.toDisplayString(): String = when(this){
    AuthModeDomain.OPEN -> "Open"
    AuthModeDomain.WEP -> "WEP"
    AuthModeDomain.WPA_PSK -> "WPA PSK"
    AuthModeDomain.WPA2_PSK -> "WPA2 PSK"
    AuthModeDomain.WPA_WPA2_PSK -> "WPA/WPA2 PSK"
    AuthModeDomain.WPA2_ENTERPRISE -> "WPA2 Enterprise"
    AuthModeDomain.WPA3_PSK -> "WPA3 PSK"
}

@Composable
fun BandDomain.toDisplayString() = when (this) {
    BandDomain.BAND_ANY -> R.string.any
    BandDomain.BAND_2_4_GH -> R.string.band_2_4
    BandDomain.BAND_5_GH -> R.string.band_5
}.let { stringResource(id = it) }
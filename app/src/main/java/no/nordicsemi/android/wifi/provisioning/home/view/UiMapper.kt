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

package no.nordicsemi.android.wifi.provisioning.home.view

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import no.nordicsemi.android.wifi.provisioning.R
import no.nordicsemi.wifi.provisioner.library.domain.AuthModeDomain
import no.nordicsemi.wifi.provisioner.library.domain.BandDomain
import no.nordicsemi.wifi.provisioner.library.domain.WifiConnectionFailureReasonDomain
import no.nordicsemi.wifi.provisioner.library.domain.WifiConnectionStateDomain

@DrawableRes
internal fun WifiConnectionStateDomain?.toIcon(): Int {
    return when (this) {
        WifiConnectionStateDomain.DISCONNECTED -> R.drawable.ic_wifi_error
        WifiConnectionStateDomain.AUTHENTICATION,
        WifiConnectionStateDomain.ASSOCIATION,
        WifiConnectionStateDomain.OBTAINING_IP -> R.drawable.ic_wifi_loading
        WifiConnectionStateDomain.CONNECTED -> R.drawable.ic_wifi_ok
        WifiConnectionStateDomain.CONNECTION_FAILED -> R.drawable.ic_wifi_error
        null -> R.drawable.ic_no_wifi
    }
}

@Composable
internal fun WifiConnectionStateDomain?.toDisplayString(): String {
    return when (this) {
        WifiConnectionStateDomain.DISCONNECTED -> R.string.wifi_status_disconnected
        WifiConnectionStateDomain.AUTHENTICATION -> R.string.wifi_status_authentication
        WifiConnectionStateDomain.ASSOCIATION -> R.string.wifi_status_association
        WifiConnectionStateDomain.OBTAINING_IP -> R.string.wifi_status_obtaining_ip
        WifiConnectionStateDomain.CONNECTED -> R.string.wifi_status_connected
        WifiConnectionStateDomain.CONNECTION_FAILED -> R.string.wifi_status_error
        null -> R.string.wifi_status_unprovisioned
    }.let { stringResource(id = it) }
}

@DrawableRes
internal fun AuthModeDomain?.toIcon(): Int {
    return when (this) {
        AuthModeDomain.OPEN -> R.drawable.ic_wifi_open
        AuthModeDomain.WEP,
        AuthModeDomain.WPA_PSK,
        AuthModeDomain.WPA2_PSK,
        AuthModeDomain.WPA_WPA2_PSK,
        AuthModeDomain.WPA2_ENTERPRISE,
        AuthModeDomain.WPA3_PSK,
        null -> R.drawable.ic_wifi_lock
    }
}

@Composable
internal fun BandDomain.toDisplayString(): String {
    return when (this) {
        BandDomain.BAND_ANY -> R.string.any
        BandDomain.BAND_2_4_GH -> R.string.band_2_4
        BandDomain.BAND_5_GH -> R.string.band_5
    }.let { stringResource(id = it) }
}

@Composable
internal fun WifiConnectionFailureReasonDomain.toDisplayString(): String {
    return when (this) {
        WifiConnectionFailureReasonDomain.AUTH_ERROR -> R.string.error_auth
        WifiConnectionFailureReasonDomain.NETWORK_NOT_FOUND -> R.string.error_network_not_found
        WifiConnectionFailureReasonDomain.TIMEOUT -> R.string.error_timeout
        WifiConnectionFailureReasonDomain.FAIL_IP -> R.string.error_ip_fail
        WifiConnectionFailureReasonDomain.FAIL_CONN -> R.string.error_fail_connection
    }.let { stringResource(id = it) }
}

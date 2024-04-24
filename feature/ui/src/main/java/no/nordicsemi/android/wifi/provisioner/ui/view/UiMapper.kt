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

package no.nordicsemi.android.wifi.provisioner.ui.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SignalWifi4Bar
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material.icons.filled.SignalWifiStatusbarConnectedNoInternet4
import androidx.compose.material.icons.filled.WifiFind
import androidx.compose.material.icons.outlined.SignalWifiStatusbarConnectedNoInternet4
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import no.nordicsemi.kotlin.wifi.provisioner.domain.WifiConnectionFailureReasonDomain
import no.nordicsemi.kotlin.wifi.provisioner.domain.WifiConnectionStateDomain
import no.nordicsemi.android.wifi.provisioner.ui.R

@Composable
fun WifiConnectionStateDomain?.toImageVector() = when (this) {
    WifiConnectionStateDomain.DISCONNECTED -> Icons.Outlined.SignalWifiStatusbarConnectedNoInternet4
    WifiConnectionStateDomain.AUTHENTICATION,
    WifiConnectionStateDomain.ASSOCIATION,
    WifiConnectionStateDomain.OBTAINING_IP -> Icons.Default.WifiFind
    WifiConnectionStateDomain.CONNECTED -> Icons.Default.SignalWifi4Bar
    WifiConnectionStateDomain.CONNECTION_FAILED -> Icons.Default.SignalWifiStatusbarConnectedNoInternet4
    null -> Icons.Default.SignalWifiOff
}

@Composable
fun WifiConnectionStateDomain?.toDisplayString() = when (this) {
    WifiConnectionStateDomain.DISCONNECTED -> R.string.wifi_status_disconnected
    WifiConnectionStateDomain.AUTHENTICATION -> R.string.wifi_status_authentication
    WifiConnectionStateDomain.ASSOCIATION -> R.string.wifi_status_association
    WifiConnectionStateDomain.OBTAINING_IP -> R.string.wifi_status_obtaining_ip
    WifiConnectionStateDomain.CONNECTED -> R.string.wifi_status_connected
    WifiConnectionStateDomain.CONNECTION_FAILED -> R.string.wifi_status_error
    null -> R.string.wifi_status_unprovisioned
}.let { stringResource(id = it) }

@Composable
fun WifiConnectionFailureReasonDomain.toDisplayString() = when (this) {
    WifiConnectionFailureReasonDomain.AUTH_ERROR -> R.string.error_auth
    WifiConnectionFailureReasonDomain.NETWORK_NOT_FOUND -> R.string.error_network_not_found
    WifiConnectionFailureReasonDomain.TIMEOUT -> R.string.error_timeout
    WifiConnectionFailureReasonDomain.FAIL_IP -> R.string.error_ip_fail
    WifiConnectionFailureReasonDomain.FAIL_CONN -> R.string.error_fail_connection
}.let { stringResource(id = it) }

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

package no.nordicsemi.android.wifi.provisioner.ble.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import no.nordicsemi.android.common.ui.view.ProgressItemStatus
import no.nordicsemi.android.wifi.provisioner.feature.ble.R
import no.nordicsemi.kotlin.wifi.provisioner.domain.WifiConnectionFailureReasonDomain
import no.nordicsemi.kotlin.wifi.provisioner.domain.WifiConnectionStateDomain


@Composable
fun WifiConnectionStateDomain?.toDisplayString(
    state: ProgressItemStatus = ProgressItemStatus.SUCCESS
) = when (this) {
        WifiConnectionStateDomain.Disconnected -> when (state) {
            ProgressItemStatus.WORKING -> R.string.wifi_status_provisioning
            ProgressItemStatus.SUCCESS -> R.string.wifi_status_provisioned
            else -> R.string.wifi_status_provision
        }
        WifiConnectionStateDomain.Authentication -> when (state) {
            ProgressItemStatus.WORKING -> R.string.wifi_status_authenticating
            ProgressItemStatus.SUCCESS -> R.string.wifi_status_authenticated
            else -> R.string.wifi_status_authenticate
        }
        WifiConnectionStateDomain.Association -> when (state) {
            ProgressItemStatus.WORKING -> R.string.wifi_status_associating
            ProgressItemStatus.SUCCESS -> R.string.wifi_status_associated
            else -> R.string.wifi_status_associate
        }
        WifiConnectionStateDomain.ObtainingIp -> when (state) {
            ProgressItemStatus.WORKING -> R.string.wifi_status_obtaining_ip
            ProgressItemStatus.SUCCESS -> R.string.wifi_status_obtained_ip
            else -> R.string.wifi_status_obtain_ip
        }
        WifiConnectionStateDomain.Connected -> when (state) {
            ProgressItemStatus.WORKING -> R.string.wifi_status_connecting
            ProgressItemStatus.SUCCESS -> R.string.wifi_status_connected
            else -> R.string.wifi_status_connect
        }
        WifiConnectionStateDomain.Disconnected -> R.string.wifi_status_disconnected
        else -> R.string.wifi_status_unprovisioned
    }.let { stringResource(id = it) }

@Composable
fun WifiConnectionFailureReasonDomain.toDisplayString() = when (this) {
    WifiConnectionFailureReasonDomain.AUTH_ERROR -> R.string.error_auth
    WifiConnectionFailureReasonDomain.NETWORK_NOT_FOUND -> R.string.error_network_not_found
    WifiConnectionFailureReasonDomain.TIMEOUT -> R.string.error_timeout
    WifiConnectionFailureReasonDomain.FAIL_IP -> R.string.error_ip_fail
    WifiConnectionFailureReasonDomain.FAIL_CONN -> R.string.error_fail_connection
}.let { stringResource(id = it) }

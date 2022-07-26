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

package com.nordicsemi.android.wifi.provisioning.home.view.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nordicsemi.android.wifi.provisioning.R
import com.nordicsemi.android.wifi.provisioning.home.view.components.DataItem
import com.nordicsemi.android.wifi.provisioning.home.view.components.ErrorDataItem
import com.nordicsemi.android.wifi.provisioning.home.view.components.LoadingItem
import com.nordicsemi.android.wifi.provisioning.home.view.toDisplayString
import com.nordicsemi.android.wifi.provisioning.home.view.toIcon
import com.nordicsemi.wifi.provisioner.library.Error
import com.nordicsemi.wifi.provisioner.library.Loading
import com.nordicsemi.wifi.provisioner.library.Resource
import com.nordicsemi.wifi.provisioner.library.Success
import com.nordicsemi.wifi.provisioner.library.domain.DeviceStatusDomain
import no.nordicsemi.ui.scanner.ui.exhaustive

@Composable
internal fun StatusSection(status: Resource<DeviceStatusDomain>) {
    when (status) {
        is Error -> ErrorSection(status.error)
        is Loading -> LoadingItem()
        is Success -> StatusSection(status = status.data)
    }.exhaustive
}

@Composable
private fun ErrorSection(error: Throwable) {
    ErrorDataItem(
        iconRes = R.drawable.ic_wifi_error,
        title = stringResource(id = R.string.status_info),
        error = error
    )
}

@Composable
private fun StatusSection(status: DeviceStatusDomain) {
    if (status.isContentEmpty()) {
        DataItem(
            iconRes = status.wifiState.toIcon(),
            title = stringResource(id = R.string.status_info),
            description = status.wifiState.toDisplayString()
        )
        return
    }

    DataItem(
        iconRes = status.wifiState.toIcon(),
        title = stringResource(id = R.string.status_info),
        description = status.wifiState.toDisplayString()
    ) {
        Column(modifier = Modifier.padding(start = 32.dp, end = 32.dp, top = 16.dp)) {

            status.wifiInfo?.let {
                Text(
                    text = stringResource(id = R.string.status_title),
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = stringResource(id = R.string.status_ip_4, it.ipv4Address),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = stringResource(id = R.string.status_ssid, it.wifiInfo.ssid),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = stringResource(id = R.string.status_bssid, it.wifiInfo.bssid),
                    style = MaterialTheme.typography.bodySmall
                )
                it.wifiInfo.band?.toDisplayString()?.let {
                    Text(
                        text = stringResource(id = R.string.status_band, it),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    text = stringResource(id = R.string.status_channel, it.wifiInfo.channel.toString()),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.size(16.dp))
            }

            status.scanParamsDomain?.let {
                Text(
                    text = stringResource(id = R.string.scan_param_title),
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = stringResource(id = R.string.scan_param_band, it.band.toDisplayString()),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = stringResource(id = R.string.scan_param_passive, it.passive.toString()),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = stringResource(id = R.string.scan_param_period_ms, it.periodMs.toString()),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = stringResource(id = R.string.scan_param_group_channels, it.groupChannels.toString()),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.size(16.dp))
            }

            status.failureReason?.let {
                Text(
                    text = stringResource(id = R.string.connection_failure_title),
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = it.toDisplayString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

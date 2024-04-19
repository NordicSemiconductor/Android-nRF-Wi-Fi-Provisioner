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

package no.nordicsemi.android.wifi.provisioner.ble.sections

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
import no.nordicsemi.android.wifi.provisioner.ble.view.toDisplayString
import no.nordicsemi.android.wifi.provisioner.ble.view.toImageVector
import no.nordicsemi.android.wifi.provisioner.ble.Error
import no.nordicsemi.android.wifi.provisioner.ble.Loading
import no.nordicsemi.android.wifi.provisioner.ble.Resource
import no.nordicsemi.android.wifi.provisioner.ble.Success
import no.nordicsemi.android.wifi.provisioner.ble.domain.DeviceStatusDomain
import no.nordicsemi.android.wifi.provisioner.feature.ble.R
import no.nordicsemi.android.wifi.provisioner.ui.DataItem
import no.nordicsemi.android.wifi.provisioner.ui.ErrorDataItem
import no.nordicsemi.android.wifi.provisioner.ui.LoadingItem
import no.nordicsemi.android.wifi.provisioner.ui.mapping.toDisplayString

@Composable
internal fun StatusSection(status: Resource<DeviceStatusDomain>) {
    when (status) {
        is Error -> ErrorSection(status.error)
        is Loading -> LoadingItem()
        is Success -> StatusSection(status = status.data)
    }
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
            imageVector = status.wifiState.toImageVector(),
            title = stringResource(id = R.string.status_info),
            description = status.wifiState.toDisplayString()
        )
        return
    }

    DataItem(
        imageVector = status.wifiState.toImageVector(),
        title = stringResource(id = R.string.status_info),
        description = status.wifiState.toDisplayString()
    ) {
        Column(modifier = Modifier.padding(start = 32.dp + 8.dp, end = 16.dp, top = 16.dp)) {

            status.wifiInfo?.let {
                Text(
                    text = stringResource(id = R.string.status_title),
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = stringResource(id = R.string.ssid, it.ssid),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = stringResource(id = R.string.bssid, it.macAddress),
                    style = MaterialTheme.typography.bodySmall
                )
                it.band?.toDisplayString()?.let {
                    Text(
                        text = stringResource(id = R.string.band, it),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    text = stringResource(id = R.string.channel, it.channel.toString()),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            status.connectionInfo?.let {
                if (status.wifiInfo != null) {
                    Spacer(modifier = Modifier.size(16.dp))
                }

                Text(
                    text = stringResource(id = R.string.connection_info),
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = stringResource(id = R.string.ip_4, it.ipv4Address),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            status.scanParams?.let {
                if (status.wifiInfo != null || status.connectionInfo != null) {
                    Spacer(modifier = Modifier.size(16.dp))
                }

                Text(
                    text = stringResource(id = R.string.scan_param_title),
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = stringResource(id = R.string.band, it.band.toDisplayString()),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = stringResource(id = R.string.scan_param_passive, it.passive.toString()),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = stringResource(
                        id = R.string.scan_param_period_ms,
                        it.periodMs.toString()
                    ),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = stringResource(
                        id = R.string.scan_param_group_channels,
                        it.groupChannels.toString()
                    ),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

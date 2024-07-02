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

package no.nordicsemi.android.wifi.provisioner.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.common.ui.view.RssiIcon
import no.nordicsemi.android.wifi.provisioner.ui.mapping.toDisplayString
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.ScanRecordsForSsid
import no.nordicsemi.kotlin.wifi.provisioner.domain.ScanRecordDomain

@Composable
fun SelectChannelDialog(
    records: ScanRecordsForSsid,
    onDismiss: () -> Unit,
    onRecordSelected: (ScanRecordDomain?) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = stringResource(id = R.string.select_wifi))
        },
        text = {
            LazyColumn {
                records.items.forEach {
                    item { ChannelListItem(record = it) { onRecordSelected(it) } }
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onRecordSelected(null) }
            ) {
                Text(stringResource(id = R.string.clear))
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onDismiss() }
            ) {
                Text(stringResource(id = R.string.dismiss))
            }
        }
    )
}

@Composable
private fun ChannelListItem(
    record: ScanRecordDomain,
    onRecordSelected: (ScanRecordDomain) -> Unit
) {
    val wifi = record.wifiInfo

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onRecordSelected(record) }
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(id = R.string.channel, wifi?.channel.toString()),
                style = MaterialTheme.typography.labelLarge
            )

            wifi?.macAddress?.takeIf {
                it.isNotEmpty()
            }?.let {
                Text(
                    text = stringResource(id = R.string.bssid, wifi.macAddress),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            wifi?.band?.toDisplayString()?.let {
                Text(
                    text = stringResource(id = R.string.band, it),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        record.rssi?.let { RssiIcon(rssi = it) }
    }
}

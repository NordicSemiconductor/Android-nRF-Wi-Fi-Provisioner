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

package no.nordicsemi.android.wifi.provisioner.softap.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material.icons.filled.NetworkWifi1Bar
import androidx.compose.material.icons.filled.NetworkWifi3Bar
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.common.ui.view.NordicAppBar
import no.nordicsemi.android.common.ui.view.WarningView
import no.nordicsemi.android.wifi.provisioner.feature.softap.R
import no.nordicsemi.android.wifi.provisioner.ui.R as RUI
import no.nordicsemi.android.wifi.provisioner.ui.SelectChannelDialog
import no.nordicsemi.android.wifi.provisioner.ui.mapping.toDisplayString
import no.nordicsemi.android.wifi.provisioner.ui.mapping.toImageVector
import no.nordicsemi.android.wifi.provisioner.ui.view.WifiLoadingItem
import no.nordicsemi.android.wifi.provisioner.ui.view.WifiSortView
import no.nordicsemi.kotlin.wifi.provisioner.domain.ScanRecordDomain
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.ScanRecordsForSsid
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.WifiScannerViewEntity
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.NavigateUpEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnSortOptionSelected
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.WifiScannerViewEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.WifiSelectedEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SoftApWifiScannerScreen(
    viewEntity: WifiScannerViewEntity,
    onEvent: (WifiScannerViewEvent) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        NordicAppBar(
            title = { Text(text = stringResource(id = R.string.wifi_access_points_title)) },
            onNavigationButtonClick = { onEvent(NavigateUpEvent) }
        )

        val insets = WindowInsets.displayCutout
            .union(WindowInsets.navigationBars)
            .only(WindowInsetsSides.Horizontal)

        WifiSortView(
            sortOption = viewEntity.sortOption,
            enabled = !viewEntity.isLoading && viewEntity.error == null,
            insets = insets,
            onChanged = { onEvent(OnSortOptionSelected(it)) }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(insets)
        ) {
            if (viewEntity.isLoading) {
                LoadingItem()
            } else if (viewEntity.error != null) {
                ErrorItem(viewEntity.error!!)
            } else {
                WifiList(viewEntity, onEvent)
            }
        }
    }
}

@Composable
private fun LoadingItem() {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        repeat(5) {
            item { WifiLoadingItem() }
        }
    }
}

@Composable
private fun ErrorItem(error: Throwable) {
    WarningView(
        imageVector = Icons.Default.Warning,
        title = stringResource(id = R.string.error_scanning_title),
        hint = error.message ?: stringResource(id = RUI.string.unknown_error),
    ) {}
}

@Composable
private fun WifiList(viewEntity: WifiScannerViewEntity, onEvent: (WifiScannerViewEvent) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        viewEntity.sortedItems.forEach {
            item { WifiItem(records = it, onEvent = onEvent) }
        }
    }
}

@Composable
private fun WifiItem(records: ScanRecordsForSsid, onEvent: (WifiScannerViewEvent) -> Unit) {
    val wifiData = records.wifiData
    val selectedScanRecord = remember { mutableStateOf<ScanRecordDomain?>(null) }
    val scanRecord = selectedScanRecord.value
    val wifi = scanRecord?.wifiInfo

    val showSelectChannelDialog = rememberSaveable { mutableStateOf(false) }

    if (showSelectChannelDialog.value) {
        SelectChannelDialog(
            records = records,
            onDismiss = { showSelectChannelDialog.value = false }
        ) {
            selectedScanRecord.value = it
            showSelectChannelDialog.value = false
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onEvent(WifiSelectedEvent(wifiData.copy(selectedChannel = scanRecord))) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = wifiData.authMode.toImageVector(),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.secondary, shape = CircleShape
                )
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.size(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = wifiData.ssid, style = MaterialTheme.typography.labelLarge
            )

            if (wifi != null) {
                if (wifi.macAddress.isNotEmpty()) {
                    Text(
                        text = stringResource(id = RUI.string.bssid, wifi.macAddress),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (wifi.band != null) {
                    Text(
                        text = stringResource(
                            id = RUI.string.band_and_channel,
                            wifi.band!!.toDisplayString(),
                            wifi.channel.toString()
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    Text(
                        text = stringResource(id = RUI.string.channel, wifi.channel.toString()),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else {
                Text(
                    text = stringResource(id = RUI.string.channel, stringResource(id = RUI.string.any)),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        val displayRssi = scanRecord?.rssi ?: records.biggestRssi

        Row(modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { showSelectChannelDialog.value = true }
            .border(
                1.dp,
                MaterialTheme.colorScheme.onSurface,
                RoundedCornerShape(10.dp)
            )
            .padding(9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(getWiFiRes(displayRssi), contentDescription = "")

            Icon(Icons.Default.ArrowDropDown, contentDescription = "")
        }
    }
}

private fun getWiFiRes(rssi: Int): ImageVector {
    return when {
        rssi < -80 -> Icons.Default.NetworkWifi1Bar
        rssi < -60 -> Icons.Default.NetworkWifi3Bar
        else -> Icons.Default.NetworkWifi
    }
}
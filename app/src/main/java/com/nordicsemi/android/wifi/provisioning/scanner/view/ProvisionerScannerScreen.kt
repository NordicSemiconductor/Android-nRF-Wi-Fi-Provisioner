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

package com.nordicsemi.android.wifi.provisioning.scanner.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nordicsemi.android.wifi.provisioning.R
import com.nordicsemi.android.wifi.provisioning.scanner.ProvisioningSection
import com.nordicsemi.android.wifi.provisioning.scanner.provisioningData
import com.nordicsemi.android.wifi.provisioning.scanner.viewmodel.ProvisionerViewModel
import no.nordicsemi.android.common.ui.scanner.ScannerResultCancel
import no.nordicsemi.android.common.ui.scanner.ScannerResultSuccess
import no.nordicsemi.android.common.ui.scanner.ScannerScreenResult
import no.nordicsemi.android.common.ui.scanner.main.DeviceListItem
import no.nordicsemi.android.common.ui.scanner.main.DevicesListView
import no.nordicsemi.android.common.ui.scanner.main.ScannerAppBar
import no.nordicsemi.android.common.ui.scanner.model.DiscoveredBluetoothDevice
import no.nordicsemi.android.common.ui.scanner.repository.ScanningState
import no.nordicsemi.android.common.theme.R as themeR
import no.nordicsemi.android.common.ui.scanner.R as scannerR

@Composable
fun ProvisionerScannerScreen(
    isLocationPermissionRequired: Boolean,
    onResult: (ScannerScreenResult) -> Unit,
    onDevicesDiscovered: () -> Unit
) {
    val viewModel = hiltViewModel<ProvisionerViewModel>()

    val result = viewModel.devices.collectAsState().value
    val allDevices = viewModel.allDevices.collectAsState().value

    LaunchedEffect(result) {
        (result as? ScanningState.DevicesDiscovered)?.let {
            if (!it.isEmpty()) {
                onDevicesDiscovered()
            }
        }
    }

    Column {
        ScannerAppBar(stringResource(id = scannerR.string.scanner_screen), result.isRunning()) {
            onResult(
                ScannerResultCancel
            )
        }
        FilterView(allDevices) {
            viewModel.switchFilter()
        }
        DevicesListView(isLocationPermissionRequired, result, { ProvisionerExtrasSection(it) }) {
            onResult(ScannerResultSuccess(it))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterView(allDevices: Boolean, onChange: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = themeR.color.appBarColor))
            .padding(start = 56.dp)
    ) {
        ElevatedFilterChip(
            selected = !allDevices,
            onClick = { onChange() },
            label = { Text(text = stringResource(id = R.string.unprovisioned),) },
            modifier = Modifier.padding(end = 8.dp),
            leadingIcon = {
                if (!allDevices) {
                    Icon(Icons.Default.Done, contentDescription = "")
                } else {
                    Icon(Icons.Default.Filter, contentDescription = "")
                }
            },
        )
    }
}

@Composable
private fun ProvisionerExtrasSection(device: DiscoveredBluetoothDevice) {
    DeviceListItem(device) {
        it.provisioningData()?.let { ProvisioningSection(data = it) }
    }
}

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

package no.nordicsemi.android.wifi.provisioning.scanner.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.nordicsemi.android.common.permission.RequireBluetooth
import no.nordicsemi.android.common.permission.RequireLocation
import no.nordicsemi.android.common.ui.scanner.DeviceSelected
import no.nordicsemi.android.common.ui.scanner.ScannerScreenResult
import no.nordicsemi.android.common.ui.scanner.ScanningCancelled
import no.nordicsemi.android.common.ui.scanner.main.DeviceListItem
import no.nordicsemi.android.common.ui.scanner.main.DeviceListItems
import no.nordicsemi.android.common.ui.scanner.model.DiscoveredBluetoothDevice
import no.nordicsemi.android.common.ui.scanner.repository.ScanningState
import no.nordicsemi.android.wifi.provisioning.scanner.ProvisioningSection
import no.nordicsemi.android.wifi.provisioning.scanner.provisioningData
import no.nordicsemi.android.wifi.provisioning.scanner.viewmodel.ProvisionerViewModel
import no.nordicsemi.android.common.ui.scanner.R as scannerR

@Composable
fun ProvisionerScannerScreen(
    onResult: (ScannerScreenResult) -> Unit
) {
    val isLoading = rememberSaveable { mutableStateOf(false) }
    val allDevices = rememberSaveable { mutableStateOf(false) }

    Column {
        ProvisionerScannerAppBar(
            stringResource(id = scannerR.string.scanner_screen),
            isLoading.value,
            allDevices.value,
            { allDevices.value = !allDevices.value }) {
            onResult(ScanningCancelled)
        }

        RequireBluetooth {
            RequireLocation { isLocationPermissionRequired ->
                Box(modifier = Modifier.fillMaxSize()) {

                    val viewModel = hiltViewModel<ProvisionerViewModel>()

                    val scanningState by viewModel.devices.collectAsStateWithLifecycle()

                    LaunchedEffect(scanningState.isRunning()) {
                        isLoading.value = scanningState.isRunning()
                    }

                    LaunchedEffect(allDevices.value) {
                        viewModel.switchFilter()
                    }

                    LazyColumn(contentPadding = PaddingValues(horizontal = 8.dp)) {
                        item { Spacer(modifier = Modifier.size(16.dp)) }

                        when (val result = scanningState) {
                            is ScanningState.Loading -> item {
                                ProvisionerScanEmptyView(
                                    isLocationPermissionRequired
                                )
                            }
                            is ScanningState.DevicesDiscovered -> {
                                if (result.isEmpty()) {
                                    item { ProvisionerScanEmptyView(isLocationPermissionRequired) }
                                } else {
                                    DeviceListItems(result, { onResult(DeviceSelected(it)) }) {
                                        ProvisionerExtrasSection(it)
                                    }
                                }
                            }
                            is ScanningState.Error -> item { ErrorSection() }
                        }

                        item { Spacer(modifier = Modifier.size(16.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorSection() {
    Text(
        text = stringResource(id = no.nordicsemi.android.common.ui.scanner.R.string.scan_failed),
        color = MaterialTheme.colorScheme.error
    )
}

@Composable
private fun ProvisionerExtrasSection(device: DiscoveredBluetoothDevice) {
    DeviceListItem(device.name, device.address) {
        device.provisioningData()?.let { ProvisioningSection(data = it) }
    }
}

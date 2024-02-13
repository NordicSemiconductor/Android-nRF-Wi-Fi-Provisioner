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

package no.nordicsemi.android.wifi.provisioner.scanner

import androidx.hilt.navigation.compose.hiltViewModel
import no.nordicsemi.android.common.navigation.createDestination
import no.nordicsemi.android.common.navigation.defineDestination
import no.nordicsemi.android.common.navigation.viewmodel.SimpleNavigationViewModel
import no.nordicsemi.android.kotlin.ble.core.ServerDevice
import no.nordicsemi.android.kotlin.ble.ui.scanner.DeviceSelected
import no.nordicsemi.android.kotlin.ble.ui.scanner.ScannerScreen
import no.nordicsemi.android.kotlin.ble.ui.scanner.ScanningCancelled
import no.nordicsemi.android.kotlin.ble.ui.scanner.main.DeviceListItem

val ProvisionerScannerDestinationId =
    createDestination<Unit, ServerDevice>("uiscanner-destination")

val ProvisionerScannerDestination = defineDestination(ProvisionerScannerDestinationId) {
    val viewModel = hiltViewModel<SimpleNavigationViewModel>()

    ScannerScreen(
        uuid = ProvisioningData.parcelUuid,
        onResult = { result ->
            when (result) {
                is DeviceSelected -> viewModel.navigateUpWithResult(
                    from = ProvisionerScannerDestinationId,
                    result = result.scanResults.device
                )
                ScanningCancelled -> viewModel.navigateUp()
            }
        },
        deviceItem = {
            DeviceListItem(
                name = it.advertisedName,
                address = it.device.address,
                extras = { it.provisioningData()?.let { ProvisioningSection(data = it) }}
            )
        }
    )
}

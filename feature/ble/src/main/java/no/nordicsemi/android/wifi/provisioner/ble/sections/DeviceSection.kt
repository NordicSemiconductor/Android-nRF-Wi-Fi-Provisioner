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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import no.nordicsemi.android.common.theme.NordicTheme
import no.nordicsemi.android.kotlin.ble.core.ServerDevice
import no.nordicsemi.android.wifi.provisioner.ble.view.BleProvisioningViewEvent
import no.nordicsemi.android.wifi.provisioner.ble.view.OnProvisionNextDeviceEvent
import no.nordicsemi.android.wifi.provisioner.ble.view.OnSelectDeviceClickEvent
import no.nordicsemi.android.wifi.provisioner.feature.ble.R
import no.nordicsemi.android.wifi.provisioner.ui.ClickableDataItem

@Composable
internal fun DeviceSection(
    device: ServerDevice?,
    isEditable: Boolean = false,
    onEvent: (BleProvisioningViewEvent) -> Unit
) {
    if (device == null) {
        DeviceNotSelectedSection(onEvent)
    } else {
        BluetoothDevice(device, isEditable, onEvent)
    }
}

@Composable
private fun BluetoothDevice(
    device: ServerDevice,
    isEditable: Boolean = false,
    onEvent: (BleProvisioningViewEvent) -> Unit
) {
    ClickableDataItem(
        imageVector = Icons.Outlined.PhoneAndroid,
        title = device.name ?: device.address,
        isEditable = isEditable,
        description = device.address,
        onClick = { onEvent(OnSelectDeviceClickEvent) },
        buttonText = stringResource(id = R.string.change_device)
    )
}

@Composable
private fun DeviceNotSelectedSection(
    onEvent: (BleProvisioningViewEvent) -> Unit
) {
    ClickableDataItem(
        imageVector = Icons.Outlined.PhoneAndroid,
        title = "Not selected",
        isEditable = false,
        description = "Please select a device to provision",
        onClick = { onEvent(OnProvisionNextDeviceEvent) },
        buttonText = stringResource(id = R.string.change_device)
    )
}

@Preview
@Composable
private fun DeviceNotSelectedSectionPreview() {
    NordicTheme {
        DeviceNotSelectedSection {}
    }
}
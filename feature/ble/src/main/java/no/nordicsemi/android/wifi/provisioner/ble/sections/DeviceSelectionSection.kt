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

package no.nordicsemi.android.wifi.provisioner.ble.sections

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.BluetoothSearching
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import no.nordicsemi.android.common.core.parseBold
import no.nordicsemi.android.common.theme.NordicTheme
import no.nordicsemi.android.common.ui.view.StatusItem
import no.nordicsemi.android.common.ui.view.WizardStepAction
import no.nordicsemi.android.common.ui.view.WizardStepComponent
import no.nordicsemi.android.common.ui.view.WizardStepState
import no.nordicsemi.android.kotlin.ble.core.MockServerDevice
import no.nordicsemi.android.wifi.provisioner.ble.view.BleViewEntity
import no.nordicsemi.android.wifi.provisioner.feature.ble.R
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Loading
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnProvisionNextDeviceEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnSelectDeviceClickEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.ProvisioningViewEvent

private val icon = Icons.AutoMirrored.Default.BluetoothSearching

@Composable
fun DeviceSelectionSection(
    state: BleViewEntity,
    onEvent: (ProvisioningViewEvent) -> Unit,
) {
    if (state.device == null) {
        NotSelectedDeviceView(onEvent = onEvent)
    } else {
        SelectedDeviceView(state = state, onEvent = onEvent)
    }
}

@Composable
private fun NotSelectedDeviceView(
    onEvent: (ProvisioningViewEvent) -> Unit
) {
    WizardStepComponent(
        icon = icon,
        title = stringResource(id = R.string.section_device),
        decor = WizardStepAction.Action(
            text = stringResource(id = R.string.action_select),
            onClick = { onEvent(OnSelectDeviceClickEvent) }
        ),
        state = WizardStepState.CURRENT,
    ) {
        StatusItem {
            Text(
                text = "Select a device",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

private const val DEVICE_NAME = "Name: <b>%s</b>"
private const val DEVICE_ADDRESS = "Address: <b>%s</b>"

@Composable
private fun SelectedDeviceView(
    state: BleViewEntity,
    onEvent: (ProvisioningViewEvent) -> Unit,
) {
    WizardStepComponent(
        icon = icon,
        title = stringResource(id = R.string.section_device),
        decor = WizardStepAction.Action(
            text = stringResource(id = R.string.action_change),
            onClick = { onEvent(OnProvisionNextDeviceEvent) },
            enabled = !state.isRunning() && !state.isProvisioningComplete(),
        ),
        state = WizardStepState.COMPLETED,
    ) {
        StatusItem {
            Text(
                text = String.format(DEVICE_NAME, state.device?.name ?: "No name").parseBold(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = String.format(DEVICE_ADDRESS, state.device?.address).parseBold(),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Preview(heightDp = 200)
@Composable
private fun NotSelectedDeviceViewPreview() {
    NordicTheme {
        NotSelectedDeviceView(onEvent = {})
    }
}

@Preview(heightDp = 200)
@Composable
private fun SelectedDeviceViewPreview() {
    NordicTheme {
        SelectedDeviceView(
            state = BleViewEntity(
                device = MockServerDevice(),
                version = Loading(),
            ),
            onEvent = {},
        )
    }
}
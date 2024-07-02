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

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.WifiPassword
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import no.nordicsemi.android.common.ui.view.StatusItem
import no.nordicsemi.android.common.ui.view.WizardStepComponent
import no.nordicsemi.android.common.ui.view.WizardStepState
import no.nordicsemi.android.wifi.provisioner.ble.view.BleViewEntity
import no.nordicsemi.android.wifi.provisioner.ble.view.OnVolatileMemoryChangedEvent
import no.nordicsemi.android.wifi.provisioner.feature.ble.R
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.ProvisioningViewEvent


@Composable
fun PersistentStorage(
    state: BleViewEntity,
    onEvent: (ProvisioningViewEvent) -> Unit,
) {
    Row {
        WizardStepComponent(
            modifier = Modifier.weight(weight = 1f),
            icon = Icons.Default.SdStorage,
            title = stringResource(id = R.string.section_persistent_storage),
            state = when {
                state.isConnected && state.network != null && state.password == null && state.network.isPasswordRequired() -> WizardStepState.CURRENT
                state.isConnected && state.network != null && (state.password != null || !state.network.isPasswordRequired()) -> WizardStepState.COMPLETED
                else -> WizardStepState.INACTIVE
            },
            decor = null,
        ) {
            StatusItem {
                Text(text = stringResource(id = R.string.persist_credentials_rationale))
            }
        }
        if (
            state.isProvisioningAvailable() ||
            state.isProvisioningInProgress() ||
            state.isProvisioningComplete() ||
            state.hasProvisioningFailed() ||
            state.isValidationInProgress()
        )
            Switch(
                checked = state.persistentMemory,
                onCheckedChange = { onEvent(OnVolatileMemoryChangedEvent) },
                enabled = state.isProvisioningAvailable()
            )
    }
}
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
import androidx.compose.material.icons.filled.Verified
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.common.ui.view.ProgressItem
import no.nordicsemi.android.common.ui.view.ProgressItemStatus
import no.nordicsemi.android.common.ui.view.WizardStepAction
import no.nordicsemi.android.common.ui.view.WizardStepComponent
import no.nordicsemi.android.common.ui.view.WizardStepState
import no.nordicsemi.android.wifi.provisioner.ble.view.BleViewEntity
import no.nordicsemi.android.wifi.provisioner.ble.view.toDisplayString
import no.nordicsemi.android.wifi.provisioner.feature.ble.R
import no.nordicsemi.kotlin.wifi.provisioner.domain.WifiConnectionStateDomain
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Error
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Loading
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Resource
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Success
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.ProvisioningViewEvent
import no.nordicsemi.android.wifi.provisioner.ui.R as RUI

@Composable
fun StatusSection(
    state: BleViewEntity,
    onEvent: (ProvisioningViewEvent) -> Unit,
) {
    WizardStepComponent(
        icon = Icons.Default.Verified,
        title = stringResource(id = R.string.section_status),
        state = when {
            state.isProvisioningComplete() -> WizardStepState.COMPLETED
            state.isValidationInProgress() -> WizardStepState.CURRENT
            else -> WizardStepState.INACTIVE
        },
        decor = when {
            state.isValidationInProgress() -> WizardStepAction.ProgressIndicator
            else -> null
        },
    ) {
        ProgressItem(
            text = state.provisioningStatus.getText(WifiConnectionStateDomain.Authentication),
            status = state.provisioningStatus.getStatus(WifiConnectionStateDomain.Authentication),
        )
        ProgressItem(
            text = state.provisioningStatus.getText(WifiConnectionStateDomain.Association),
            status = state.provisioningStatus.getStatus(WifiConnectionStateDomain.Association),
        )
        ProgressItem(
            text = state.provisioningStatus.getText(WifiConnectionStateDomain.ObtainingIp),
            status = state.provisioningStatus.getStatus(WifiConnectionStateDomain.ObtainingIp),
        )
        ProgressItem(
            text = state.provisioningStatus.getText(WifiConnectionStateDomain.Connected),
            status = state.provisioningStatus.getStatus(WifiConnectionStateDomain.Connected),
        )
    }
}

private fun Resource<WifiConnectionStateDomain>?.getStatus(state: WifiConnectionStateDomain): ProgressItemStatus {
    return when (this) {
        is Loading -> when (state) {
            WifiConnectionStateDomain.Disconnected -> ProgressItemStatus.WORKING
            else -> ProgressItemStatus.DISABLED
        }

        is Success -> when {
            data is WifiConnectionStateDomain.ConnectionFailed && state == WifiConnectionStateDomain.Connected -> ProgressItemStatus.ERROR
            state.id == data.id + 1 -> ProgressItemStatus.WORKING
            state.id <= data.id -> ProgressItemStatus.SUCCESS
            else -> ProgressItemStatus.DISABLED
        }

        is Error -> ProgressItemStatus.ERROR

        else -> ProgressItemStatus.DISABLED
    }
}

@Composable
private fun Resource<WifiConnectionStateDomain>?.getText(state: WifiConnectionStateDomain): String {
    val status = getStatus(state)
    return when (this) {
        is Loading, null -> state.toDisplayString(status)
        is Success -> {
            val data = data
            when {
                data is WifiConnectionStateDomain.ConnectionFailed && state == WifiConnectionStateDomain.Connected -> data.reason?.toDisplayString() ?: "Connection failed"
                else -> state.toDisplayString(status)
            }
        }
        is Error -> when (state) {
            WifiConnectionStateDomain.Connected -> error.message ?: stringResource(id = RUI.string.unknown_error)
            else -> state.toDisplayString(status)
        }
    }
}
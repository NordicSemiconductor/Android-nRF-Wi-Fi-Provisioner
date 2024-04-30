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

package no.nordicsemi.android.wifi.provisioner.softap.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.nordicsemi.android.common.logger.view.LoggerAppBarIcon
import no.nordicsemi.android.common.theme.view.NordicAppBar
import no.nordicsemi.android.wifi.provisioner.feature.softap.R
import no.nordicsemi.android.wifi.provisioner.softap.sections.ActionButtonSection
import no.nordicsemi.android.wifi.provisioner.softap.view.entity.SoftApViewEntity
import no.nordicsemi.android.wifi.provisioner.softap.viewmodel.SoftApProvisioningViewModel
import no.nordicsemi.android.wifi.provisioner.ui.PasswordDialog
import no.nordicsemi.android.wifi.provisioner.ui.view.section.DisconnectedDeviceStatus
import no.nordicsemi.android.wifi.provisioner.ui.view.section.PasswordSection
import no.nordicsemi.android.wifi.provisioner.ui.view.section.SoftApDevice
import no.nordicsemi.android.wifi.provisioner.ui.view.section.SoftApDeviceNotSelected
import no.nordicsemi.android.wifi.provisioner.ui.view.section.WifiSection
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnHidePasswordDialog
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnPasswordSelectedEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.PasswordSetDialogEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.ProvisioningViewEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoftApProvisioningScreen() {
    val viewModel = hiltViewModel<SoftApProvisioningViewModel>()

    val state by viewModel.state.collectAsStateWithLifecycle()
    val onEvent: (ProvisioningViewEvent) -> Unit = { viewModel.onEvent(it) }
    Column {
        NordicAppBar(
            text = stringResource(id = R.string.label_wifi_provisioner),
            actions = {
                LoggerAppBarIcon(
                    onClick = { }
                )
            },
            showBackButton = true,
            onNavigationButtonClick = viewModel::navigateUp
        )
        Box(modifier = Modifier.weight(1f)) {
            Content(state) { viewModel.onEvent(it) }
        }
        ActionButtonSection(state, onEvent)
        Spacer(modifier = Modifier.size(16.dp))
    }

    if (state.showSoftApDialog) {
        SoftApConnectorDialog(
            isNetworkServiceDiscoveryCompleted = state.isNetworkServiceDiscoveryCompleted,
            connect = { ssid, passphrase ->
                onEvent(OnSoftApConnectEvent(ssid, passphrase))
            }, dismiss = {
                onEvent(OnHidePasswordDialog)
            }
        )
    }

    if (state.showPasswordDialog == true) {
        PasswordDialog { event ->
            (event as? PasswordSetDialogEvent)?.let {
                onEvent(OnPasswordSelectedEvent(it.password))
            }
            onEvent(OnHidePasswordDialog)
        }
    }
}

@Composable
private fun Content(state: SoftApViewEntity, onEvent: (ProvisioningViewEvent) -> Unit) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.size(12.dp))

        state.device?.let {
            SoftApDevice(it.ssid, it.connectionInfoDomain!!.ipv4Address, true, onEvent)
        } ?: SoftApDeviceNotSelected(onEvent = onEvent)

        Spacer(modifier = Modifier.size(12.dp))

        if (!state.isConnected && state.device != null) {
            DisconnectedDeviceStatus()
        } else {

            Spacer(modifier = Modifier.size(12.dp))

            // state.status?.let { StatusSection(it) }

            state.network?.let {
                Spacer(modifier = Modifier.size(12.dp))

                SectionTitle(text = stringResource(id = R.string.section_provisioning))

                Spacer(modifier = Modifier.size(12.dp))

                WifiSection(it, true, onEvent)
            }

            state.password?.let {
                Spacer(modifier = Modifier.size(10.dp))

                PasswordSection(true, onEvent)
            }

            // TODO verification
            /*state.provisioningStatus?.let {
                SectionTitle(text = stringResource(id = R.string.section_status))

                Spacer(modifier = Modifier.size(8.dp))

                ProvisioningSection(it)
            }*/
        }
    }
}


@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.secondary
    )
}

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

package no.nordicsemi.android.wifi.provisioning.home.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import no.nordicsemi.android.wifi.provisioning.R
import no.nordicsemi.android.wifi.provisioning.home.view.components.LoggerIconAppBar
import no.nordicsemi.android.wifi.provisioning.home.view.sections.ActionButtonSection
import no.nordicsemi.android.wifi.provisioning.home.view.sections.DeviceSection
import no.nordicsemi.android.wifi.provisioning.home.view.sections.DisconnectedDeviceStatus
import no.nordicsemi.android.wifi.provisioning.home.view.sections.PasswordSection
import no.nordicsemi.android.wifi.provisioning.home.view.sections.ProvisioningSection
import no.nordicsemi.android.wifi.provisioning.home.view.sections.StatusSection
import no.nordicsemi.android.wifi.provisioning.home.view.sections.UnprovisioningSection
import no.nordicsemi.android.wifi.provisioning.home.view.sections.VersionSection
import no.nordicsemi.android.wifi.provisioning.home.view.sections.VolatileMemorySwitch
import no.nordicsemi.android.wifi.provisioning.home.view.sections.WifiSection
import no.nordicsemi.android.wifi.provisioning.home.viewmodel.HomeViewModel
import no.nordicsemi.android.wifi.provisioning.password.PasswordDialog
import no.nordicsemi.android.wifi.provisioning.password.PasswordSetDialogEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val viewModel = hiltViewModel<HomeViewModel>()

    val state = viewModel.state.collectAsState().value
    val onEvent: (HomeScreenViewEvent) -> Unit = { viewModel.onEvent(it) }

    Scaffold(
        topBar = {
            LoggerIconAppBar(
                text = stringResource(id = R.string.app_name),
                onLoggerClick = { viewModel.onEvent(OpenLoggerEvent) }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        bottomBar = { ActionButtonSection(state, onEvent) }
    ) {
        Column(modifier = Modifier.padding(it)) {
            Content(state) { viewModel.onEvent(it) }
        }
    }

    if (state.showPasswordDialog == true) {
        PasswordDialog {
            (it as? PasswordSetDialogEvent)?.let { onEvent(OnPasswordSelectedEvent(it.password)) }
            onEvent(OnHidePasswordDialog)
        }
    }
}

@Composable
private fun Content(state: HomeViewEntity, onEvent: (HomeScreenViewEvent) -> Unit) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.size(12.dp))

        DeviceSection(state.device, !state.isRunning(), onEvent)

        Spacer(modifier = Modifier.size(12.dp))

        if (!state.isConnected && state.device != null) {
            DisconnectedDeviceStatus()
        } else {
            state.version?.let {
                SectionTitle(text = stringResource(id = R.string.section_device))

                Spacer(modifier = Modifier.size(12.dp))

                VersionSection(it)
            }

            Spacer(modifier = Modifier.size(12.dp))

            state.status?.let { StatusSection(it) }

            state.network?.let {
                Spacer(modifier = Modifier.size(12.dp))

                SectionTitle(text = stringResource(id = R.string.section_provisioning))

                Spacer(modifier = Modifier.size(12.dp))

                WifiSection(it, !state.isRunning(), onEvent)
            }

            state.password?.let {
                Spacer(modifier = Modifier.size(10.dp))

                PasswordSection(!state.isRunning(), onEvent)
            }

            state.network?.let {
                VolatileMemorySwitch(
                    volatileMemory = state.persistentMemory,
                    enabled = !state.isRunning(),
                    onEvent = onEvent
                )
            }

            state.provisioningStatus?.let {
                SectionTitle(text = stringResource(id = R.string.section_status))

                Spacer(modifier = Modifier.size(8.dp))

                ProvisioningSection(it)
            }

            state.unprovisioningStatus?.let {
                SectionTitle(text = stringResource(id = R.string.section_status))

                Spacer(modifier = Modifier.size(8.dp))

                UnprovisioningSection(it)
            }
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

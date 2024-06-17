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

package no.nordicsemi.android.wifi.provisioner.ble.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.nordicsemi.android.common.logger.view.LoggerAppBarIcon
import no.nordicsemi.android.common.permissions.ble.RequireBluetooth
import no.nordicsemi.android.common.theme.view.NordicAppBar
import no.nordicsemi.android.wifi.provisioner.ble.sections.ConnectionSection
import no.nordicsemi.android.wifi.provisioner.ble.sections.DeviceSelectionSection
import no.nordicsemi.android.wifi.provisioner.ble.sections.NetworkStatusSection
import no.nordicsemi.android.wifi.provisioner.ble.sections.ProvisioningSection
import no.nordicsemi.android.wifi.provisioner.ble.sections.SecuritySection
import no.nordicsemi.android.wifi.provisioner.ble.viewmodel.BleViewModel
import no.nordicsemi.android.wifi.provisioner.feature.ble.R
import no.nordicsemi.android.wifi.provisioner.ui.PasswordDialog
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnHidePasswordDialog
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnPasswordSelectedEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.ProvisioningViewEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BleProvisioningScreen() {
    val viewModel = hiltViewModel<BleViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onEvent: (ProvisioningViewEvent) -> Unit = { viewModel.onEvent(it) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            NordicAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.provision_over_ble),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    LoggerAppBarIcon(
                        onClick = { viewModel.onEvent(OpenLoggerEvent) }
                    )
                },
                showBackButton = true,
                onNavigationButtonClick = viewModel::navigateUp
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RequireBluetooth {
                OutlinedCard(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 16.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DeviceSelectionSection(state = state, onEvent = onEvent)
                        ConnectionSection(state = state, onEvent = onEvent)
                        NetworkStatusSection(state = state, onEvent = onEvent)
                        SecuritySection(state = state, onEvent = onEvent)
                        ProvisioningSection(state = state, onEvent = onEvent)
                    }
                }
            }
        }
    }

    if (state.showPasswordDialog == true) {
        PasswordDialog(
            onConfirmPressed = { onEvent(OnPasswordSelectedEvent(it)) },
            onDismiss = { onEvent(OnHidePasswordDialog) }
        )
    }
}

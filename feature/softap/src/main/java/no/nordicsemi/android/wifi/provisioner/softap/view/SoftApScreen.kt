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

package no.nordicsemi.android.wifi.provisioner.softap.view

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiFind
import androidx.compose.material.icons.filled.WifiPassword
import androidx.compose.material.icons.outlined.NetworkCheck
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import no.nordicsemi.android.common.logger.view.LoggerAppBarIcon
import no.nordicsemi.android.common.permissions.wifi.RequireWifi
import no.nordicsemi.android.common.ui.view.NordicAppBar
import no.nordicsemi.android.common.ui.view.ProgressItem
import no.nordicsemi.android.common.ui.view.ProgressItemStatus
import no.nordicsemi.android.common.ui.view.StatusItem
import no.nordicsemi.android.common.ui.view.WizardStepAction
import no.nordicsemi.android.common.ui.view.WizardStepComponent
import no.nordicsemi.android.common.ui.view.WizardStepState
import no.nordicsemi.android.wifi.provisioner.feature.softap.R
import no.nordicsemi.android.wifi.provisioner.softap.FailedToBindToNetwork
import no.nordicsemi.android.wifi.provisioner.softap.OnConnectionLost
import no.nordicsemi.android.wifi.provisioner.softap.Open
import no.nordicsemi.android.wifi.provisioner.softap.PassphraseConfiguration
import no.nordicsemi.android.wifi.provisioner.softap.UnableToConnectToNetwork
import no.nordicsemi.android.wifi.provisioner.softap.WifiNotEnabledException
import no.nordicsemi.android.wifi.provisioner.softap.viewmodel.SoftApScreenState
import no.nordicsemi.android.wifi.provisioner.ui.PasswordDialog
import no.nordicsemi.android.wifi.provisioner.ui.mapping.toDisplayString
import no.nordicsemi.android.wifi.provisioner.ui.mapping.toImageVector
import no.nordicsemi.kotlin.wifi.provisioner.domain.AuthModeDomain
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.WifiData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoftApScreen(
    context: Context,
    state: SoftApScreenState,
    onLoggerAppBarIconPressed: () -> Unit,
    start: (String, PassphraseConfiguration) -> Unit,
    onSelectWifiPressed: () -> Unit,
    onPasswordEntered: (String) -> Unit,
    onProvisionPressed: () -> Unit,
    verify: () -> Unit,
    navigateUp: (() -> Unit),
    resetError: () -> Unit
) {
    var ssidName by rememberSaveable { mutableStateOf("nrf-wifiprov") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    if (state.error != null) {
        showSnackBar(
            context = context,
            scope = scope,
            snackbarHostState = snackbarHostState,
            throwable = state.error
        ) {
            resetError()
        }
    }
    Scaffold(
        topBar = {
            NordicAppBar(
                title = { Text(text = stringResource(id = R.string.provision_over_wifi)) },
                actions = {
                    if (state.isConnectionRequested || state.error != null) {
                        LoggerAppBarIcon(onClick = onLoggerAppBarIconPressed)
                    }
                },
                showBackButton = true,
                onNavigationButtonClick = navigateUp
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RequireWifi(isNearbyWifiDevicesPermissionRequired = false) {
                OutlinedCard(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 16.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ConfigureSoftAp(
                            configureState = state.configureState,
                            connectionState = state.connectionState,
                            isConnectionRequested = state.isConnectionRequested,
                            ssidName = ssidName,
                            onSsidChange = { ssidName = it }
                        )
                        ConnectToSoftAp(
                            connectionState = state.connectionState,
                            serviceDiscoveryState = state.discoveringServicesState,
                            isConnectionRequested = state.isConnectionRequested,
                            start = {
                                start(ssidName, Open)
                            }
                        )
                        SelectWifi(
                            provisioningState = state.provisionState,
                            isProvisioningRequested = state.isProvisioningRequested,
                            selectWifiState = state.selectWifiState,
                            providePasswordState = state.providePasswordState,
                            wifiData = state.selectedWifi,
                            onSelectWifiPressed = onSelectWifiPressed,
                        )
                        SetPassphrase(
                            provisioningState = state.provisionState,
                            providePasswordState = state.providePasswordState,
                            isProvisioningRequested = state.isProvisioningRequested,
                            wifiData = state.selectedWifi,
                            password = state.password,
                            onPasswordEntered = onPasswordEntered
                        )
                        Provisioning(
                            provisioningState = state.provisionState,
                            isProvisioningRequested = state.isProvisioningRequested,
                            onProvisionPressed = onProvisionPressed
                        )
                        Verify(
                            verificationState = state.verifyState,
                            isVerificationRequested = state.isVerificationRequested,
                            verify = verify
                        )
                    }
                }
            }
        }
    }
}

private fun showSnackBar(
    context: Context,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    throwable: Throwable,
    onDismissed: () -> Unit
) {
    scope.launch {
        val message = when (throwable) {
            is WifiNotEnabledException -> context.getString(R.string.please_enable_wi_fi)
            is FailedToBindToNetwork -> context.getString(R.string.failed_to_bind_to_network)
            is UnableToConnectToNetwork -> context.getString(R.string.unable_to_connect_to_network)
            is OnConnectionLost -> context.getString(R.string.connection_lost)
            is TimeoutCancellationException -> context.getString(R.string.timeout)
            else -> "${throwable::class.simpleName}: ${throwable.message}"
        }
        val result = snackbarHostState.showSnackbar(message = message)
        if (result == SnackbarResult.Dismissed) onDismissed()
    }
}

@Composable
private fun ConfigureSoftAp(
    configureState: WizardStepState,
    connectionState: WizardStepState,
    isConnectionRequested: Boolean,
    ssidName: String,
    onSsidChange: (String) -> Unit,
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    WizardStepComponent(
        icon = Icons.Default.Settings,
        title = stringResource(R.string.section_configure),
        state = configureState,
        decor = WizardStepAction.Action(
            text = stringResource(id = R.string.action_edit_ssid),
            onClick = { showDialog = true },
            enabled = connectionState != WizardStepState.COMPLETED && !isConnectionRequested
        ),
    ) {
        StatusItem {
            Text(style = MaterialTheme.typography.bodyMedium, text = "SSID: $ssidName")
        }
    }
    if (showDialog) {
        EditSsidDialog(
            ssidName = ssidName,
            onSsidChange = {
                onSsidChange(it)
                showDialog = false
            },
            dismiss = {
                showDialog = false
            }
        )
    }
}

@Composable
private fun ConnectToSoftAp(
    connectionState: WizardStepState,
    isConnectionRequested: Boolean,
    serviceDiscoveryState: WizardStepState,
    start: () -> Unit,
) {
    WizardStepComponent(
        icon = Icons.Default.Wifi,
        title = stringResource(id = R.string.section_connect),
        state = connectionState,
        decor = if (isConnectionRequested && serviceDiscoveryState != WizardStepState.COMPLETED) {
            WizardStepAction.ProgressIndicator
        } else WizardStepAction.Action(
            text = stringResource(id = R.string.action_start),
            onClick = start,
            enabled = connectionState == WizardStepState.CURRENT && !isConnectionRequested
        ),
    ) {
        ProgressItem(
            text = when {
                isConnectionRequested && connectionState == WizardStepState.CURRENT ->
                    stringResource(id = R.string.connecting)

                connectionState == WizardStepState.COMPLETED ->
                    stringResource(id = R.string.connected)

                else -> stringResource(id = R.string.connect)
            },
            status = when {
                isConnectionRequested && connectionState == WizardStepState.CURRENT -> ProgressItemStatus.WORKING
                connectionState == WizardStepState.COMPLETED -> ProgressItemStatus.SUCCESS
                else -> ProgressItemStatus.DISABLED
            },
        )
        ProgressItem(
            text = when (serviceDiscoveryState) {
                WizardStepState.INACTIVE -> stringResource(id = R.string.discover_services)
                WizardStepState.CURRENT -> stringResource(id = R.string.discovering_services)
                else -> stringResource(id = R.string.discovered_services)
            },
            status = when (serviceDiscoveryState) {
                WizardStepState.CURRENT -> ProgressItemStatus.WORKING
                WizardStepState.COMPLETED -> ProgressItemStatus.SUCCESS
                else -> ProgressItemStatus.DISABLED
            },
        )
    }
}

@Composable
private fun SelectWifi(
    provisioningState: WizardStepState,
    isProvisioningRequested: Boolean,
    selectWifiState: WizardStepState,
    providePasswordState: WizardStepState,
    wifiData: WifiData?,
    onSelectWifiPressed: () -> Unit,
) {
    WizardStepComponent(
        icon = Icons.Default.WifiFind,
        title = stringResource(id = R.string.section_network),
        state = selectWifiState,
        decor = if (selectWifiState == WizardStepState.CURRENT
            || providePasswordState == WizardStepState.CURRENT
            || provisioningState == WizardStepState.CURRENT
            || providePasswordState == WizardStepState.COMPLETED
        ) {
            WizardStepAction.Action(
                text = stringResource(id = R.string.action_select),
                onClick = onSelectWifiPressed,
                enabled = !isProvisioningRequested,
            )
        } else null,
    ) {
        StatusItem {
            if (wifiData != null && selectWifiState != WizardStepState.INACTIVE) {
                Text(style = MaterialTheme.typography.bodyMedium, text = "SSID: ${wifiData.ssid}")
                Text(style = MaterialTheme.typography.bodyMedium,
                    text = "Band: ${
                        wifiData.let {
                            it.selectedChannel?.wifiInfo?.band?.toDisplayString()
                                ?: it.channelFallback.wifiInfo?.band?.toDisplayString()
                        }
                    }"
                )
            } else {
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    text = stringResource(R.string.select_wifi)
                )
            }
        }
    }
}

@Composable
private fun SetPassphrase(
    providePasswordState: WizardStepState,
    provisioningState: WizardStepState,
    isProvisioningRequested: Boolean,
    wifiData: WifiData?,
    password: String? = null,
    onPasswordEntered: (String) -> Unit,
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    WizardStepComponent(
        icon = wifiData?.authMode?.toImageVector() ?: Icons.Default.WifiPassword,
        title = stringResource(id = R.string.section_security),
        state = providePasswordState,
        decor = if (providePasswordState == WizardStepState.CURRENT
            || provisioningState == WizardStepState.CURRENT
            || provisioningState == WizardStepState.COMPLETED
        ) {
            WizardStepAction.Action(
                text = stringResource(id = R.string.action_set_password),
                onClick = { showDialog = true },
                enabled = wifiData?.authMode != AuthModeDomain.OPEN && !isProvisioningRequested
            )
        } else null,
    ) {
        StatusItem {
            if (wifiData != null) {
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    text = "Security: ${wifiData.authMode.toDisplayString()}"
                )

                if (wifiData.authMode != AuthModeDomain.OPEN && password != null) {
                    Text(
                        style = MaterialTheme.typography.bodyMedium,
                        text = stringResource(R.string.set_passphrase_value)
                    )
                }
            } else {
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    text = stringResource(R.string.set_wifi_passphrase)
                )
            }
        }
    }
    if (showDialog) {
        PasswordDialog(
            onConfirmPressed = {
                onPasswordEntered(it)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
private fun Provisioning(
    provisioningState: WizardStepState,
    isProvisioningRequested: Boolean,
    onProvisionPressed: () -> Unit
) {
    WizardStepComponent(
        icon = Icons.Outlined.NetworkCheck,
        title = stringResource(id = R.string.section_provision),
        state = provisioningState,
        decor = if (isProvisioningRequested && provisioningState == WizardStepState.CURRENT) {
            WizardStepAction.ProgressIndicator
        } else if (provisioningState == WizardStepState.CURRENT
            || provisioningState == WizardStepState.COMPLETED
        ) {
            WizardStepAction.Action(
                text = stringResource(id = R.string.action_provision),
                onClick = onProvisionPressed,
                enabled = !isProvisioningRequested
            )
        } else null,
    ) {
        ProgressItem(
            text = when {
                isProvisioningRequested && provisioningState == WizardStepState.CURRENT -> stringResource(
                    R.string.wifi_status_provisioning
                )

                provisioningState == WizardStepState.COMPLETED -> stringResource(R.string.wifi_status_provisioned)
                else -> stringResource(R.string.wifi_status_provision)
            },
            status = when {
                isProvisioningRequested && provisioningState == WizardStepState.CURRENT -> ProgressItemStatus.WORKING
                provisioningState == WizardStepState.COMPLETED -> ProgressItemStatus.SUCCESS
                else -> ProgressItemStatus.DISABLED
            },
        )
    }
}

@Composable
private fun Verify(
    verificationState: WizardStepState,
    isVerificationRequested: Boolean,
    verify: () -> Unit
) {
    WizardStepComponent(
        icon = Icons.Default.Verified,
        title = stringResource(id = R.string.section_verify),
        state = verificationState,
        decor = if (verificationState == WizardStepState.CURRENT || verificationState == WizardStepState.COMPLETED) {
            if (isVerificationRequested && verificationState == WizardStepState.CURRENT) {
                WizardStepAction.ProgressIndicator
            } else {
                WizardStepAction.Action(
                    text = stringResource(id = R.string.action_verify),
                    onClick = verify,
                    enabled = !isVerificationRequested,
                )
            }
        } else null,
    ) {
        ProgressItem(
            text = when {
                isVerificationRequested && verificationState == WizardStepState.CURRENT ->
                    stringResource(R.string.wifi_status_verifying)

                verificationState == WizardStepState.COMPLETED -> stringResource(R.string.wifi_status_verified)
                else -> stringResource(R.string.wifi_status_verify)
            },
            status = when {
                isVerificationRequested && verificationState == WizardStepState.CURRENT -> ProgressItemStatus.WORKING
                verificationState == WizardStepState.COMPLETED -> ProgressItemStatus.SUCCESS
                else -> ProgressItemStatus.DISABLED
            },
        )
    }
}
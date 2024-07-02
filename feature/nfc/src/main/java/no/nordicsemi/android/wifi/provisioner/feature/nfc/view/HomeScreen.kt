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

package no.nordicsemi.android.wifi.provisioner.feature.nfc.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiFind
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import no.nordicsemi.android.common.ui.view.NordicAppBar
import no.nordicsemi.android.wifi.provisioner.feature.nfc.R
import no.nordicsemi.android.wifi.provisioner.feature.nfc.uicomponent.AddWifiManuallyDialog
import no.nordicsemi.android.wifi.provisioner.feature.nfc.uicomponent.OutlinedCardItem
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.NfcProvisioningViewEvent
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.NfcProvisioningViewModel
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.OnAddWifiNetworkClickEvent
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.OnBackClickEvent
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.OnScanClickEvent

@RequiresApi(Build.VERSION_CODES.M)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen() {
    val viewModel: NfcProvisioningViewModel = hiltViewModel()
    val onEvent: (NfcProvisioningViewEvent) -> Unit = { viewModel.onEvent(it) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            NordicAppBar(
                title = { Text(text = stringResource(id = R.string.wifi_provision_over_nfc_appbar)) },
                showBackButton = true,
                onNavigationButtonClick = { onEvent(OnBackClickEvent) }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Show the home screen.
                var isDialogOpen by rememberSaveable { mutableStateOf(false) }

                // Show an option to enter the WiFi credentials manually.
                OutlinedCardItem(
                    headline = stringResource(id = R.string.enter_wifi_credentials),
                    description = {
                        Text(
                            text = stringResource(id = R.string.enter_wifi_credentials_des),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    icon = Icons.Default.Wifi,
                    onCardClick = { isDialogOpen = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { isDialogOpen = true }
                            .padding(8.dp)
                    )
                }

                // Show an option to search for a WiFi network.
                OutlinedCardItem(
                    headline = stringResource(id = R.string.search_for_wifi_networks),
                    description = {
                        Text(
                            text = stringResource(id = R.string.search_for_wifi_networks_des),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    icon = Icons.Default.WifiFind,
                    onCardClick = { onEvent(OnScanClickEvent) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onEvent(OnScanClickEvent) }
                            .padding(8.dp)
                    )
                }

                if (isDialogOpen) {
                    // Open a dialog to enter the WiFi credentials manually.
                    AddWifiManuallyDialog(
                        onCancelClick = {
                            isDialogOpen = false
                        },
                        onConfirmClick = {
                            isDialogOpen = false
                            onEvent(OnAddWifiNetworkClickEvent(it))
                        },
                    )
                }
            }
        }
    }
}

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

package no.nordicsemi.android.wifi.provisioner

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import no.nordicsemi.android.common.navigation.DestinationId
import no.nordicsemi.android.common.navigation.viewmodel.SimpleNavigationViewModel
import no.nordicsemi.android.common.theme.view.NordicAppBar
import no.nordicsemi.android.wifi.provisioner.app.BuildConfig
import no.nordicsemi.android.wifi.provisioner.app.R
import no.nordicsemi.android.wifi.provisioner.ble.view.BleDestination
import no.nordicsemi.android.wifi.provisioner.feature.nfc.NfcDestination
import no.nordicsemi.android.wifi.provisioner.softap.view.SoftApDestination
import no.nordicsemi.android.wifi.provisioner.ui.view.section.ProvisionSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val vm: SimpleNavigationViewModel = hiltViewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val isLargeScreen =
        LocalConfiguration.current.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            NordicAppBar(
                text = stringResource(id = R.string.app_name)
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { innerPadding ->

        when {
            !isLargeScreen && isLandscape -> {
                SmallScreenLandscapeContent(
                    context = context,
                    scope = scope,
                    snackbarHostState = snackbarHostState,
                    innerPadding = innerPadding,
                    navigateTo = vm::navigateTo
                )
            }

            else -> {
                PortraitContent(
                    context = context,
                    scope = scope,
                    snackbarHostState = snackbarHostState,
                    innerPadding = innerPadding,
                    navigateTo = vm::navigateTo
                )
            }
        }
    }
}

@Composable
private fun PortraitContent(
    context: Context,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    innerPadding: PaddingValues,
    navigateTo: (DestinationId<Unit, *>) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
            .padding(innerPadding)
            .padding(bottom = 56.dp)
            .consumeWindowInsets(innerPadding)
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Horizontal,
                ),
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_nrf70),
            contentDescription = stringResource(id = R.string.ic_nrf70),
            modifier = Modifier
                .widthIn(max = 200.dp)
                .weight(0.5f, fill = true)
                .padding(8.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.size(16.dp))
            ProvisionSection(
                sectionTitle = stringResource(R.string.provision_over_ble),
                sectionRational = stringResource(R.string.provision_over_ble_rationale),
                onClick = { navigateTo(BleDestination) }
            )
            Spacer(modifier = Modifier.size(16.dp))
            ProvisionSection(
                sectionTitle = stringResource(R.string.provision_over_wifi),
                sectionRational = stringResource(R.string.provision_over_wifi_rationale),
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        navigateTo(SoftApDestination)
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = context.getString(R.string.error_softap_not_supported),
                                actionLabel = context.getString(no.nordicsemi.android.wifi.provisioner.ui.R.string.dismiss)
                            )
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.size(16.dp))
            ProvisionSection(
                sectionTitle = stringResource(R.string.provision_over_nfc),
                sectionRational = stringResource(R.string.provision_over_nfc_rationale)
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    navigateTo(NfcDestination)
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.error_nfc_not_supported),
                            actionLabel = context.getString(no.nordicsemi.android.wifi.provisioner.ui.R.string.dismiss)
                        )
                    }
                }
            }
        }
        Text(
            text = stringResource(
                id = R.string.app_version,
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE
            ),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun SmallScreenLandscapeContent(
    context: Context,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    innerPadding: PaddingValues,
    navigateTo: (DestinationId<Unit, *>) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp)
            .consumeWindowInsets(innerPadding)
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Horizontal,
                ),
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Image(
                modifier = Modifier.padding(horizontal = 56.dp),
                painter = painterResource(id = R.drawable.ic_nrf70),
                contentDescription = stringResource(id = R.string.ic_nrf70),
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(state = rememberScrollState())
        ) {
            Spacer(modifier = Modifier.size(16.dp))
            ProvisionSection(
                sectionTitle = stringResource(R.string.provision_over_ble),
                sectionRational = stringResource(R.string.provision_over_ble_rationale),
                onClick = { navigateTo(BleDestination) }
            )
            Spacer(modifier = Modifier.size(16.dp))
            ProvisionSection(
                sectionTitle = stringResource(R.string.provision_over_wifi),
                sectionRational = stringResource(R.string.provision_over_wifi_rationale),
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        navigateTo(SoftApDestination)
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = context.getString(R.string.error_softap_not_supported),
                                actionLabel = context.getString(no.nordicsemi.android.wifi.provisioner.ui.R.string.dismiss)
                            )
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.size(16.dp))
            ProvisionSection(
                sectionTitle = stringResource(R.string.provision_over_nfc),
                sectionRational = stringResource(R.string.provision_over_nfc_rationale)
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    navigateTo(NfcDestination)
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.error_nfc_not_supported),
                            actionLabel = context.getString(no.nordicsemi.android.wifi.provisioner.ui.R.string.dismiss)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.size(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = stringResource(
                        id = R.string.app_version,
                        BuildConfig.VERSION_NAME,
                        BuildConfig.VERSION_CODE
                    ),
                    modifier = Modifier.padding(bottom = 48.dp),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}
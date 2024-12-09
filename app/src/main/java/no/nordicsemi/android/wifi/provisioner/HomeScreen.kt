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

package no.nordicsemi.android.wifi.provisioner

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
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
import no.nordicsemi.android.common.ui.view.NordicAppBar
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
        topBar = {
            NordicAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) }
            )
        },
        contentWindowInsets = WindowInsets.navigationBars
            .union(WindowInsets.displayCutout)
            .union(WindowInsets(left = 16.dp, right = 16.dp))
            .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    navigateTo = vm::navigateTo
                )
            }

            else -> {
                PortraitContent(
                    context = context,
                    scope = scope,
                    snackbarHostState = snackbarHostState,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(state = rememberScrollState())
                        .padding(innerPadding),
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
    modifier: Modifier = Modifier,
    navigateTo: (DestinationId<Unit, *>) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.5f))
        Image(
            painter = painterResource(id = R.drawable.ic_nrf70),
            contentDescription = stringResource(id = R.string.ic_nrf70),
            modifier = Modifier
                .fillMaxHeight(0.5f)
                .heightIn(min = 120.dp, max = 200.dp)
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.weight(0.5f))

        val insets = WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)
        ProvisioningMenu(
            context = context,
            scope = scope,
            snackbarHostState = snackbarHostState,
            navigateTo = navigateTo,
            modifier = Modifier
                .widthIn(max = 600.dp)
                .windowInsetsPadding(insets)
                .padding(vertical = 16.dp)
        )
    }
}

@Composable
private fun SmallScreenLandscapeContent(
    context: Context,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    navigateTo: (DestinationId<Unit, *>) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.width(200.dp).padding(horizontal = 32.dp),
            painter = painterResource(id = R.drawable.ic_nrf70),
            contentDescription = stringResource(id = R.string.ic_nrf70),
        )
        val insets = WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)
        ProvisioningMenu(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(state = rememberScrollState())
                .windowInsetsPadding(insets)
                .padding(top = 16.dp),
            context = context,
            scope = scope,
            snackbarHostState = snackbarHostState,
            navigateTo = navigateTo
        )
    }
}

@Composable
private fun ProvisioningMenu(
    context: Context,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    navigateTo: (DestinationId<Unit, *>) -> Unit
) {
    Column(
        modifier = modifier,
    ) {
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
                            message = context.getString(R.string.error_android_10_required),
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
                if (isNfcSupported(context)) {
                    navigateTo(NfcDestination)
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.error_nfc_not_supported),
                            actionLabel = context.getString(no.nordicsemi.android.wifi.provisioner.ui.R.string.dismiss)
                        )
                    }
                }
            } else {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.error_android_6_required),
                        actionLabel = context.getString(no.nordicsemi.android.wifi.provisioner.ui.R.string.dismiss)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.size(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Text(
                text = stringResource(
                    id = R.string.app_version,
                    BuildConfig.VERSION_NAME,
                    BuildConfig.VERSION_CODE
                ),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.labelMedium,
            )
        }

        Spacer(modifier = Modifier.size(4.dp))
    }
}

private fun isNfcSupported(context: Context): Boolean {
    return context.packageManager.hasSystemFeature(PackageManager.FEATURE_NFC)
}
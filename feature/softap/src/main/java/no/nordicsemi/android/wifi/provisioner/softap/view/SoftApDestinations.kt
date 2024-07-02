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

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.nordicsemi.android.common.navigation.createDestination
import no.nordicsemi.android.common.navigation.createSimpleDestination
import no.nordicsemi.android.common.navigation.defineDestination
import no.nordicsemi.android.common.navigation.with
import no.nordicsemi.android.wifi.provisioner.softap.viewmodel.SoftApViewModel
import no.nordicsemi.android.wifi.provisioner.softap.viewmodel.SoftApWifiScannerViewModel
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.WifiData
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.WifiScannerViewEvent

val SoftApDestination = createSimpleDestination("softap")
val SoftApProvisionerDestination = createSimpleDestination("softap-provisioner-destination")
val SoftApWifiScannerDestination = createDestination<Unit, Result<WifiData>>(
    name = "wifi-access-points-softap-destination"
)

@RequiresApi(Build.VERSION_CODES.Q)
private val softApProvisionerDestination = defineDestination(SoftApProvisionerDestination) {
    val viewModel = hiltViewModel<SoftApViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    SoftApScreen(
        context = context,
        state = state,
        onLoggerAppBarIconPressed = {
            viewModel.onLoggerAppBarIconPressed(context)
        },
        start = { ssid, configuration ->
            viewModel.start(
                context = context,
                ssid = ssid,
                passphraseConfiguration = configuration
            )
        },
        onSelectWifiPressed = viewModel::onSelectWifiPressed,
        onPasswordEntered = {
            viewModel.onPasswordEntered(it)
        },
        onProvisionPressed = viewModel::onProvisionPressed,
        verify = viewModel::verify,
        navigateUp = viewModel::navigateUp,
        resetError = viewModel::onSnackBarDismissed,
    )
}

private val softApWifiScannerDestination = defineDestination(SoftApWifiScannerDestination) {
    val viewModel = hiltViewModel<SoftApWifiScannerViewModel>()
    val viewEntity by viewModel.state.collectAsStateWithLifecycle()
    val onEvent: (WifiScannerViewEvent) -> Unit = { viewModel.onEvent(it) }
    SoftApWifiScannerScreen(viewEntity, onEvent)
}

@RequiresApi(Build.VERSION_CODES.Q)
val SoftApProvisionerDestinations = SoftApDestination with listOf(
    softApProvisionerDestination,
    softApWifiScannerDestination,
)

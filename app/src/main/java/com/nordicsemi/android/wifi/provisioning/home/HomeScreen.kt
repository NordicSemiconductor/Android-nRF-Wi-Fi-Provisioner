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

package com.nordicsemi.android.wifi.provisioning.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nordicsemi.wifi.provisioner.R
import no.nordicsemi.ui.scanner.ui.exhaustive

enum class HomeScreenViewEvent {
    ON_SELECT_BUTTON_CLICK, FINISH
}

@Composable
fun HomeScreen() {
    val viewModel = hiltViewModel<HomeViewModel>()
    val state = viewModel.status.collectAsState().value
    val onEvent: (HomeScreenViewEvent) -> Unit = { viewModel.onEvent(it) }

    Column {
        CloseIconAppBar(stringResource(id = R.string.app_name)) {
            viewModel.onEvent(HomeScreenViewEvent.FINISH)
        }

        Column(modifier = Modifier.padding(16.dp)) {
            when (state) {
                IdleHomeViewEntity -> DeviceNotSelectedSection(onEvent)
                is DeviceSelectedEntity -> DeviceSelectedSection(state.device.displayNameOrAddress(), onEvent)
                is NetworkSelectedEntity -> NetworkSelectedSection(state.device.displayNameOrAddress(), onEvent)
            }.exhaustive
        }
    }
}

@Composable
private fun DeviceNotSelectedSection(onEvent: (HomeScreenViewEvent) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        FloatingActionButton(onClick = { onEvent(HomeScreenViewEvent.ON_SELECT_BUTTON_CLICK) }) {
            Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.add_device))
        }

        Spacer(modifier = Modifier.size(16.dp))

        Text(text = stringResource(id = R.string.add_device))
    }

    Spacer(modifier = Modifier.size(16.dp))

    Text(text = stringResource(id = R.string.app_info))
}

@Composable
private fun DeviceSelectedSection(deviceName: String, onEvent: (HomeScreenViewEvent) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        FloatingActionButton(onClick = { onEvent(HomeScreenViewEvent.ON_SELECT_BUTTON_CLICK) }) {
            Icon(Icons.Default.Wifi, contentDescription = stringResource(id = R.string.cd_wifi_available))
        }

        Spacer(modifier = Modifier.size(16.dp))

        Text(text = deviceName)
    }
}

@Composable
private fun NetworkSelectedSection(deviceName: String, onEvent: (HomeScreenViewEvent) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        FloatingActionButton(onClick = { onEvent(HomeScreenViewEvent.ON_SELECT_BUTTON_CLICK) }) {
            Icon(Icons.Default.Wifi, contentDescription = stringResource(id = R.string.cd_wifi_available))
        }

        Spacer(modifier = Modifier.size(16.dp))

        Text(text = deviceName)
    }
}

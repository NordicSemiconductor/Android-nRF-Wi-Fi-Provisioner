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

package com.nordicsemi.android.wifi.provisioning.home.view

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nordicsemi.android.wifi.provisioning.R
import com.nordicsemi.android.wifi.provisioning.home.CloseIconAppBar
import com.nordicsemi.android.wifi.provisioning.home.viewmodel.HomeViewModel
import com.nordicsemi.wifi.provisioner.library.Error
import com.nordicsemi.wifi.provisioner.library.Loading
import com.nordicsemi.wifi.provisioner.library.Resource
import com.nordicsemi.wifi.provisioner.library.Success
import com.nordicsemi.wifi.provisioner.library.domain.DeviceStatusDomain
import com.nordicsemi.wifi.provisioner.library.domain.VersionDomain
import no.nordicsemi.ui.scanner.ui.exhaustive

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
                is DeviceSelectedEntity -> DeviceSelectedSection(state, onEvent)
                is VersionDownloadedEntity -> VersionDownloadedSection(state, onEvent)
                is StatusDownloadedEntity -> StatusDownloadedSection(state, onEvent)
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
private fun DeviceSelectedSection(viewEntity: DeviceSelectedEntity, onEvent: (HomeScreenViewEvent) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        FloatingActionButton(onClick = { onEvent(HomeScreenViewEvent.ON_SELECT_BUTTON_CLICK) }) {
            Icon(Icons.Default.Wifi, contentDescription = stringResource(id = R.string.cd_wifi_available))
        }

        Spacer(modifier = Modifier.size(16.dp))

        Text(text = viewEntity.device.displayNameOrAddress())
    }

    Spacer(modifier = Modifier.size(16.dp))

    VersionInfo(version = viewEntity.version)
}

@Composable
private fun VersionInfo(version: Resource<VersionDomain>) {
    when (version) {
        is Error -> Text(stringResource(id = R.string.error_version))
        is Loading -> CircularProgressIndicator()
        is Success -> VersionInfo(version = version.data)
    }.exhaustive
}

@Composable
private fun VersionInfo(version: VersionDomain) {
    Text(stringResource(id = R.string.dk_version, version.value))
}

@Composable
private fun VersionDownloadedSection(viewEntity: VersionDownloadedEntity, onEvent: (HomeScreenViewEvent) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        FloatingActionButton(onClick = { onEvent(HomeScreenViewEvent.ON_SELECT_BUTTON_CLICK) }) {
            Icon(Icons.Default.Wifi, contentDescription = stringResource(id = R.string.cd_wifi_available))
        }

        Spacer(modifier = Modifier.size(16.dp))

        Text(text = viewEntity.device.displayNameOrAddress())
    }

    Spacer(modifier = Modifier.size(16.dp))

    VersionInfo(version = viewEntity.version)

    Spacer(modifier = Modifier.size(16.dp))

    StatusInfo(status = viewEntity.status)
}

@Composable
private fun StatusInfo(status: Resource<DeviceStatusDomain>) {
    when (status) {
        is Error -> Text(stringResource(id = R.string.error_status))
        is Loading -> CircularProgressIndicator()
        is Success -> StatusInfo(status = status.data)
    }.exhaustive
}

@Composable
private fun StatusInfo(status: DeviceStatusDomain) {
    Text(stringResource(id = R.string.dk_version, status))
}

@Composable
private fun StatusDownloadedSection(viewEntity: StatusDownloadedEntity, onEvent: (HomeScreenViewEvent) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            FloatingActionButton(onClick = { onEvent(HomeScreenViewEvent.ON_SELECT_BUTTON_CLICK) }) {
                Icon(Icons.Default.Wifi, contentDescription = stringResource(id = R.string.cd_wifi_available))
            }

            Spacer(modifier = Modifier.size(16.dp))

            Text(text = viewEntity.device.displayNameOrAddress())
        }

        Spacer(modifier = Modifier.size(16.dp))

        VersionInfo(version = viewEntity.version)

        Spacer(modifier = Modifier.size(16.dp))

        StatusInfo(viewEntity.status)

        Spacer(modifier = Modifier.size(16.dp))

        Button(onClick = { onEvent(HomeScreenViewEvent.SELECT_WIFI) }) {
            Text(stringResource(id = R.string.wifi_select))
        }
    }
}

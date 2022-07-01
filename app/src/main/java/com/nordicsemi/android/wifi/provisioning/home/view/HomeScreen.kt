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

import androidx.compose.foundation.layout.*
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
import com.nordicsemi.android.wifi.provisioning.R
import com.nordicsemi.android.wifi.provisioning.home.view.components.CloseIconAppBar
import com.nordicsemi.android.wifi.provisioning.home.view.sections.*
import com.nordicsemi.android.wifi.provisioning.home.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val viewModel = hiltViewModel<HomeViewModel>()
    val state = viewModel.state.collectAsState().value
    val onEvent: (HomeScreenViewEvent) -> Unit = { viewModel.onEvent(it) }

    Scaffold(
        topBar = {
            CloseIconAppBar(stringResource(id = R.string.app_name)) {
                viewModel.onEvent(OnFinishedEvent)
            }
        },
        floatingActionButton = { ActionButtonSection(state, onEvent) }
    ) {
        Box(modifier = Modifier.padding(it)) {
            Content(state, onEvent)
        }
    }
}

@Composable
private fun Content(state: HomeViewEntity, onEvent: (HomeScreenViewEvent) -> Unit) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        DeviceSection(state.device, onEvent)

        Spacer(modifier = Modifier.size(16.dp))

        state.version?.let {
            SectionTitle(text = stringResource(id = R.string.section_device))

            Spacer(modifier = Modifier.size(16.dp))

            VersionSection(it)
        }

        Spacer(modifier = Modifier.size(16.dp))

        state.status?.let { StatusSection(it) }

        Spacer(modifier = Modifier.size(16.dp))

        state.network?.let {
            SectionTitle(text = stringResource(id = R.string.section_provisioning))

            Spacer(modifier = Modifier.size(16.dp))

            WifiSection(it)
        }

        Spacer(modifier = Modifier.size(16.dp))

        state.password?.let { PasswordSection() }

        Spacer(modifier = Modifier.size(16.dp))

        state.provisioningStatus?.let {
            SectionTitle(text = stringResource(id = R.string.section_status))

            Spacer(modifier = Modifier.size(16.dp))

            ProvisioningSection(it)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall
    )
}

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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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

@Composable
fun HomeScreen() {
    val viewModel = hiltViewModel<HomeViewModel>()
    val state = viewModel.state.collectAsState().value
    val onEvent: (HomeScreenViewEvent) -> Unit = { viewModel.onEvent(it) }

    Column {
        CloseIconAppBar(stringResource(id = R.string.app_name)) {
            viewModel.onEvent(OnFinishedEvent)
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            DeviceSection(state.device, onEvent)

            Spacer(modifier = Modifier.size(16.dp))

            state.version?.let { VersionSection(it) }

            Spacer(modifier = Modifier.size(16.dp))

            state.status?.let { StatusSection(it) }

            Spacer(modifier = Modifier.size(16.dp))

            state.network?.let { WifiSection(it) }

            Spacer(modifier = Modifier.size(16.dp))

            state.password?.let { PasswordSection() }

            Spacer(modifier = Modifier.size(16.dp))

            state.provisioningStatus?.let { ProvisioningSection(it) }

            Spacer(modifier = Modifier.size(16.dp))

            ActionButtonSection(state, onEvent)
        }
    }
}

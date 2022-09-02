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

package com.nordicsemi.android.wifi.provisioning.home.view.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Start
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nordicsemi.android.wifi.provisioning.R
import com.nordicsemi.android.wifi.provisioning.home.view.HomeScreenViewEvent
import com.nordicsemi.android.wifi.provisioning.home.view.HomeViewEntity
import com.nordicsemi.android.wifi.provisioning.home.view.OnFinishedEvent
import com.nordicsemi.android.wifi.provisioning.home.view.OnProvisionClickEvent
import com.nordicsemi.android.wifi.provisioning.home.view.OnProvisionNextDeviceEvent
import com.nordicsemi.android.wifi.provisioning.home.view.OnSelectWifiEvent
import com.nordicsemi.android.wifi.provisioning.home.view.OnShowPasswordDialog
import com.nordicsemi.android.wifi.provisioning.home.view.OnUnprovisionEvent

@Composable
fun ActionButtonSection(viewEntity: HomeViewEntity, onEvent: (HomeScreenViewEvent) -> Unit) {
    if (viewEntity.isRunning()) {
        return
    }
    if (viewEntity.device == null) {
        ActionButton(Icons.Default.Start, stringResource(id = R.string.start)) {
            onEvent(OnProvisionNextDeviceEvent)
        }
    } else if (viewEntity.hasFinishedWithSuccess()) {
        ActionButton(Icons.Default.Bluetooth, stringResource(id = R.string.next_device)) {
            onEvent(OnProvisionNextDeviceEvent)
        }
    } else if (viewEntity.hasFinished()) {
        ActionButton(Icons.Default.Clear, stringResource(id = R.string.finish)) {
            onEvent(OnFinishedEvent)
        }
    } else if (!viewEntity.isStatusSuccess()) {
        //I think it's here to prevent entering somewhere else.
    } else if (viewEntity.isUnprovisioning()) {
        ActionButton(Icons.Default.Wifi, stringResource(id = R.string.unprovision)) {
            onEvent(OnUnprovisionEvent)
        }
    } else if (viewEntity.isStatusSuccess() && viewEntity.network == null) {
        ActionButton(Icons.Default.Wifi, stringResource(id = R.string.wifi_select)) {
            onEvent(OnSelectWifiEvent)
        }
    } else if (viewEntity.network!!.isPasswordRequired() && viewEntity.password == null) {
        ActionButton(Icons.Default.Password, stringResource(id = R.string.password_select)) {
            onEvent(OnShowPasswordDialog)
        }
    } else {
        ActionButton(Icons.Default.PlayArrow, stringResource(id = R.string.provision)) {
            onEvent(OnProvisionClickEvent)
        }
    }
}

@Composable
private fun ActionButton(imageVector: ImageVector, text: String, onClick: () -> Unit) {
    BottomAppBar(modifier = Modifier.heightIn(max = 56.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 4.dp)
        ) {
            Button(
                onClick = onClick,
                modifier = Modifier
                    .align(Alignment.Center)
                    .widthIn(min = 100.dp)
            ) {
                Text(text = text)
            }
        }
    }
}

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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nordicsemi.android.wifi.provisioning.R
import com.nordicsemi.android.wifi.provisioning.home.view.*
import com.nordicsemi.android.wifi.provisioning.password.PasswordDialog
import com.nordicsemi.android.wifi.provisioning.password.PasswordSetDialogEvent

@Composable
fun ActionButtonSection(viewEntity: HomeViewEntity, onEvent: (HomeScreenViewEvent) -> Unit) {
    if (viewEntity.device == null) {
        ExtendedFloatingActionButton(onClick = { onEvent(OnSelectDeviceClickEvent) }) {
            FabContent(Icons.Default.Bluetooth, stringResource(id = R.string.select_device))
        }
    } else if (viewEntity.network == null) {
        ExtendedFloatingActionButton(onClick = { onEvent(OnSelectWifiEvent) }) {
            FabContent(Icons.Default.Wifi, stringResource(id = R.string.wifi_select))
        }
    } else if (viewEntity.network.isPasswordRequired() && viewEntity.password == null) {
        ExtendedFloatingActionButton(onClick = { onEvent(OnShowPasswordDialog) }) {
            FabContent(Icons.Default.Password, stringResource(id = R.string.password_select))
        }
    } else if (viewEntity.hasFinished()) {
        ExtendedFloatingActionButton(onClick = { onEvent(OnFinishedEvent) }) {
            FabContent(Icons.Default.Clear, stringResource(id = R.string.finish))
        }
    } else {
        ExtendedFloatingActionButton(onClick = { onEvent(OnProvisionClickEvent) }) {
            FabContent(Icons.Default.PlayArrow, stringResource(id = R.string.provision))
        }
    }
}

@Composable
private fun FabContent(imageVector: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = imageVector, contentDescription = text)

        Spacer(modifier = Modifier.size(16.dp))

        Text(text = text)
    }
}

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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nordicsemi.android.wifi.provisioning.R
import com.nordicsemi.android.wifi.provisioning.home.view.HomeScreenViewEvent
import com.nordicsemi.android.wifi.provisioning.home.view.OnSelectDeviceClickEvent
import no.nordicsemi.ui.scanner.DiscoveredBluetoothDevice

@Composable
internal fun DeviceSection(
    device: DiscoveredBluetoothDevice?,
    onEvent: (HomeScreenViewEvent) -> Unit
) {
    if (device == null) {
        DeviceNotSelectedSection(onEvent)
    } else {
        BluetoothDevice(device, onEvent)
    }
}

@Composable
private fun BluetoothDevice(
    device: DiscoveredBluetoothDevice,
    onEvent: (HomeScreenViewEvent) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onEvent(OnSelectDeviceClickEvent) }
            .padding(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary)
        ) {
            Icon(
                imageVector = Icons.Default.Bluetooth,
                contentDescription = stringResource(id = R.string.cd_device_selected),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            text = device.displayNameOrAddress(),
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = stringResource(id = R.string.change_device),
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun DeviceNotSelectedSection(onEvent: (HomeScreenViewEvent) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.outline)
                .clickable { onEvent(OnSelectDeviceClickEvent) }
        ) {
            Icon(
                imageVector = Icons.Default.BluetoothDisabled,
                contentDescription = stringResource(id = R.string.add_device),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        Text(text = stringResource(id = R.string.no_device_selected))
    }

    Spacer(modifier = Modifier.size(16.dp))

    Text(
        text = stringResource(id = R.string.app_info),
        modifier = Modifier.padding(horizontal = 16.dp),
        style = MaterialTheme.typography.bodyMedium
    )
}

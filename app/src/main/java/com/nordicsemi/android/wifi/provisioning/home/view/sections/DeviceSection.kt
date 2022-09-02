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

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nordicsemi.android.wifi.provisioning.BuildConfig
import com.nordicsemi.android.wifi.provisioning.R
import com.nordicsemi.android.wifi.provisioning.home.view.HomeScreenViewEvent
import com.nordicsemi.android.wifi.provisioning.home.view.OnShowPasswordDialog
import com.nordicsemi.android.wifi.provisioning.home.view.components.ClickableDataItem
import no.nordicsemi.android.common.theme.view.NordicText
import no.nordicsemi.android.common.ui.scanner.model.DiscoveredBluetoothDevice

@Composable
internal fun DeviceSection(
    device: DiscoveredBluetoothDevice?,
    isEditable: Boolean = false,
    onEvent: (HomeScreenViewEvent) -> Unit
) {
    if (device == null) {
        DeviceNotSelectedSection()
    } else {
        BluetoothDevice(device, isEditable, onEvent)
    }
}

@Composable
private fun BluetoothDevice(
    device: DiscoveredBluetoothDevice,
    isEditable: Boolean = false,
    onEvent: (HomeScreenViewEvent) -> Unit
) {
    ClickableDataItem(
        iconRes = R.drawable.ic_phone_ok,
        title = device.displayNameOrAddress,
        isEditable = isEditable,
        description = device.address
    ) {
        onEvent(OnShowPasswordDialog)
    }
}

@Composable
private fun DeviceNotSelectedSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = R.drawable.ic_nrf70),
            contentDescription = stringResource(id = R.string.add_device),
            modifier = Modifier
                .widthIn(max = 200.dp)
                .padding(8.dp)
                .background(Color.White)
        )

        Spacer(modifier = Modifier.size(16.dp))

        NordicText(
            text = stringResource(id = R.string.app_info),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.size(32.dp))

        Text(
            text = stringResource(
                id = R.string.app_version,
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE
            ),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

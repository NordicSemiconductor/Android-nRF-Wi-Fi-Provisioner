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

package no.nordicsemi.android.wifi.provisioner.feature.nfc.uicomponent

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material.icons.filled.NetworkWifi1Bar
import androidx.compose.material.icons.filled.NetworkWifi2Bar
import androidx.compose.material.icons.filled.NetworkWifi3Bar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.common.ui.R

// Constants used for different signal strengths
private const val FAIR_RSSI = -70
private const val GOOD_RSSI = -60
private const val EXCELLENT_RSSI = -50

/**
 * A function to get the WiFi icon based on the RSSI value.
 * The icon is selected based on the following criteria:
 * - Weak signal: RSSI < -70 dBm
 * - Fair signal: -70 dBm <= RSSI < -60 dBm
 * - Good signal: -60 dBm <= RSSI < -50 dBm
 * - Excellent signal: RSSI >= -50 dBm
 *
 * Selection criteria was inspired by [this](https://www.netspotapp.com/wifi-signal-strength/what-is-rssi-level.html) article.
 *
 * @param rssi The RSSI value.
 */
internal fun getWiFiIcon(rssi: Int): ImageVector {
    return when (rssi) {
        in Int.MIN_VALUE..FAIR_RSSI -> Icons.Default.NetworkWifi3Bar // Weak signal
        in FAIR_RSSI..GOOD_RSSI -> Icons.Default.NetworkWifi2Bar // Fair signal
        in GOOD_RSSI..EXCELLENT_RSSI -> Icons.Default.NetworkWifi1Bar // Good signal
        else -> Icons.Default.NetworkWifi// Excellent signal
    }
}

/**
 * A composable function to display the RSSI icon and value.
 *
 * @param rssi The RSSI value.
 */
@Composable
internal fun RssiIconView(rssi: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            imageVector = getWiFiIcon(rssi),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
        Text(
            text = stringResource(id = R.string.dbm, rssi),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RssiIconViewPreview() {
    RssiIconView(-50)
}

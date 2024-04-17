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

package no.nordicsemi.android.wifi.provisioner.ble.wifi.view

import no.nordicsemi.android.wifi.provisioner.ble.domain.AuthModeDomain
import no.nordicsemi.android.wifi.provisioner.ble.domain.ScanRecordDomain
import no.nordicsemi.android.wifi.provisioner.ui.view.WifiSortOption

data class WifiScannerViewEntity(
    val isLoading: Boolean = true,
    val error: Throwable? = null,
    val sortOption: WifiSortOption = WifiSortOption.RSSI,
    private val items: List<ScanRecordsForSsid> = emptyList()
) {
    val sortedItems: List<ScanRecordsForSsid> = when (sortOption) {
        WifiSortOption.NAME -> items.sortedBy { it.wifiData.ssid }
        WifiSortOption.RSSI -> items.sortedByDescending { it.biggestRssi }
    }
}

data class ScanRecordsForSsid(
    val wifiData: WifiData,
    val items: List<ScanRecordDomain> = emptyList(),
) {
    val biggestRssi: Int = items.maxOf { it.rssi ?: 0 }
}

data class WifiData(
    val ssid: String,
    val authMode: AuthModeDomain,
    val channelFallback: ScanRecordDomain, //Needed for proto v1
    val selectedChannel: ScanRecordDomain? = null
) {
    fun isPasswordRequired(): Boolean {
        return authMode != AuthModeDomain.OPEN
    }
}

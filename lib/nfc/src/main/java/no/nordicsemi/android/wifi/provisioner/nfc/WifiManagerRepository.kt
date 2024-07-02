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

package no.nordicsemi.android.wifi.provisioner.nfc

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import no.nordicsemi.android.wifi.provisioner.nfc.domain.Error
import no.nordicsemi.android.wifi.provisioner.nfc.domain.Loading
import no.nordicsemi.android.wifi.provisioner.nfc.domain.NetworkState
import no.nordicsemi.android.wifi.provisioner.nfc.domain.Success

/**
 * A repository class to manage the Wi-Fi scan.
 */
class WifiManagerRepository(
    context: Context,
) {
    private val wifiManager: WifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val _networkState = MutableStateFlow<NetworkState>(Success(emptyList()))
    val networkState = _networkState.asStateFlow()

    /**
     * Broadcast receiver to receive the scan results.
     */
    private val wifiScanReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.M)
        @RequiresPermission(
            allOf = [
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
            ]
        )
        override fun onReceive(context: Context, intent: Intent) {
            try {
                intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                _networkState.value = Success(wifiManager.scanResults)
            } catch (e: Exception) {
                e.printStackTrace()
                _networkState.value = Error(e)
            }
        }
    }

    init {
        // Register the broadcast receiver
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        context.registerReceiver(wifiScanReceiver, intentFilter)
    }

    /**
     * This method is used to start the wifi scan.
     */
    fun onScan() {
        _networkState.value = Loading
        wifiManager.startScan()
    }
}
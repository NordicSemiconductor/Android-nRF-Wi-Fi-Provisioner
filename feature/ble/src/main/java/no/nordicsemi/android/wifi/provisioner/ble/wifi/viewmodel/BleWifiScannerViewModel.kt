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

package no.nordicsemi.android.wifi.provisioner.ble.wifi.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import no.nordicsemi.android.common.navigation.Navigator
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Error
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Loading
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Success
import no.nordicsemi.android.wifi.provisioner.ble.repository.ProvisionerResourceRepository
import no.nordicsemi.android.wifi.provisioner.ble.view.BleWifiScannerDestination
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.WifiAggregator
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.WifiData
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.viewmodel.GenericWifiScannerViewModel
import javax.inject.Inject

@HiltViewModel
internal class BleWifiScannerViewModel @Inject constructor(
    navigationManager: Navigator,
    wifiAggregator: WifiAggregator,
    private val repository: ProvisionerResourceRepository
) : GenericWifiScannerViewModel(
    navigationManager = navigationManager,
    wifiAggregator = wifiAggregator
) {

    init {
        startScan()
    }

    private fun startScan() {
        repository.startScan().onEach {
            val state = _state.value

            _state.value = when (it) {
                is Error -> state.copy(isLoading = false, error = it.error)
                is Loading -> state.copy(isLoading = true)
                is Success -> state.copy(
                    isLoading = false,
                    error = null,
                    items = wifiAggregator.addWifi(it.data)
                )
            }
        }.launchIn(viewModelScope)
    }

    private suspend fun stopScanning() {
        try {
            repository.stopScanBlocking()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun navigateUp() {
        viewModelScope.launch {
            stopScanning()
            navigationManager.navigateUp()
        }
    }

    override fun navigateUp(wifiData: WifiData) {
        viewModelScope.launch {
            stopScanning()
            navigationManager.navigateUpWithResult(BleWifiScannerDestination, wifiData)
        }
    }
}

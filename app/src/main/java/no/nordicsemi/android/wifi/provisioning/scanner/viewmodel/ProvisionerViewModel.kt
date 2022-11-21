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

package no.nordicsemi.android.wifi.provisioning.scanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import no.nordicsemi.android.wifi.provisioning.scanner.provisioningData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import no.nordicsemi.android.common.ui.scanner.repository.ScannerRepository
import no.nordicsemi.android.common.ui.scanner.repository.ScanningState
import javax.inject.Inject

@HiltViewModel
internal class ProvisionerViewModel @Inject constructor(
    private val scannerRepository: ScannerRepository,
) : ViewModel() {

    //By default only not connected
    private val _allDevices = MutableStateFlow(true)
    val allDevices = _allDevices.asStateFlow()

    val devices = allDevices.combine(scannerRepository.getScannerState()) { onlyUnprovisioned, result ->
            when (result) {
                is ScanningState.DevicesDiscovered -> result.applyFilters(onlyUnprovisioned)
                else -> result
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, ScanningState.Loading)

    private fun ScanningState.DevicesDiscovered.applyFilters(allDevices: Boolean) =
        ScanningState.DevicesDiscovered(
            devices
                .filter { it.provisioningData() != null }
                .filter {
                    val provisioningData = it.provisioningData()
                    (provisioningData?.isConnected == false).takeIf { !allDevices } ?: true
                }
        )

    fun switchFilter() {
        _allDevices.value = !allDevices.value
    }

    override fun onCleared() {
        super.onCleared()
        scannerRepository.clear()
    }
}

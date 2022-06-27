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

package com.nordicsemi.android.wifi.provisioning.home.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nordicsemi.android.wifi.provisioning.WifiScannerId
import com.nordicsemi.android.wifi.provisioning.home.view.*
import com.nordicsemi.wifi.provisioner.library.*
import com.nordicsemi.wifi.provisioner.library.internal.PROVISIONING_SERVICE_UUID
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import no.nordicsemi.android.navigation.*
import no.nordicsemi.ui.scanner.DiscoveredBluetoothDevice
import no.nordicsemi.ui.scanner.ScannerDestinationId
import no.nordicsemi.ui.scanner.ui.exhaustive
import no.nordicsemi.ui.scanner.ui.getDevice
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val navigationManager: NavigationManager
) : ViewModel() {

    private val repository = ProvisionerRepository.newInstance(context)

    private val _status = MutableStateFlow<HomeViewEntity>(IdleHomeViewEntity)
    val status = _status.asStateFlow()

    fun onEvent(event: HomeScreenViewEvent) {
        when (event) {
            HomeScreenViewEvent.ON_SELECT_BUTTON_CLICK -> requestBluetoothDevice()
            HomeScreenViewEvent.FINISH -> navigationManager.navigateUp()
            HomeScreenViewEvent.SELECT_WIFI -> navigationManager.navigateTo(WifiScannerId)
        }.exhaustive
    }

    private fun requestBluetoothDevice() {
        navigationManager.navigateTo(ScannerDestinationId, UUIDArgument(PROVISIONING_SERVICE_UUID))

        navigationManager.recentResult.onEach {
            if (it.destinationId == ScannerDestinationId) {
                handleArgs(it)
            }
        }.launchIn(viewModelScope)
    }

    private fun handleArgs(args: DestinationResult) {
        when (args) {
            is CancelDestinationResult -> navigationManager.navigateUp()
            is SuccessDestinationResult -> installBluetoothDevice(args.getDevice())
        }.exhaustive
    }

    private fun installBluetoothDevice(device: DiscoveredBluetoothDevice) {
        _status.value = DeviceSelectedEntity(device)

        viewModelScope.launchWithCatch {
            repository.start(device.device)
            loadVersion()
        }
    }

    private fun loadVersion() {
        repository.readVersion().onEach {
            val status = _status.value as DeviceSelectedEntity
            _status.value = status.copy(version = it)

            _status.value = when (it) {
                is Error,
                is Loading -> status.copy(version = it)
                is Success -> VersionDownloadedEntity(status.device, it.data).also {
                    loadStatus()
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun loadStatus() {
        repository.getStatus().onEach {
            val status = _status.value as VersionDownloadedEntity

            _status.value = when (it) {
                is Error,
                is Loading -> status.copy(status = it)
                is Success -> StatusDownloadedEntity(status.device, status.version, it.data)
            }
        }.launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            repository.release()
        }
    }
}

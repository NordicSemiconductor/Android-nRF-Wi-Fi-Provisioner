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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nordicsemi.android.wifi.provisioning.WifiScannerId
import com.nordicsemi.android.wifi.provisioning.home.view.*
import com.nordicsemi.wifi.provisioner.library.*
import com.nordicsemi.wifi.provisioner.library.domain.ScanRecordDomain
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

    private val _state = MutableStateFlow<HomeViewEntity>(IdleHomeViewEntity)
    val state = _state.asStateFlow()

    fun onEvent(event: HomeScreenViewEvent) {
        when (event) {
            OnFinishedEvent -> navigationManager.navigateUp()
            is OnPasswordSelectedEvent -> onPasswordSelected(event.password)
            OnSelectButtonClickEvent -> requestBluetoothDevice()
            OnSelectWifiEvent -> navigationManager.navigateTo(WifiScannerId)
            OnProvisionClickEvent -> provision()
        }.exhaustive
    }

    private fun requestBluetoothDevice() {
        navigationManager.navigateTo(ScannerDestinationId, UUIDArgument(PROVISIONING_SERVICE_UUID))

        navigationManager.recentResult.onEach {
            if (it.destinationId == ScannerDestinationId) {
                handleArgs(it)
            } else if (it.destinationId == WifiScannerId) {
                handleWifiArgs(it)
            }
        }.launchIn(viewModelScope)
    }

    private fun handleArgs(args: DestinationResult) {
        when (args) {
            is CancelDestinationResult -> navigationManager.navigateUp()
            is SuccessDestinationResult -> installBluetoothDevice(args.getDevice())
        }.exhaustive
    }

    private fun handleWifiArgs(args: DestinationResult) {
        when (args) {
            is CancelDestinationResult -> navigationManager.navigateUp()
            is SuccessDestinationResult -> installWifi(args.getScanRecord())
        }.exhaustive
    }

    private fun SuccessDestinationResult.getScanRecord(): ScanRecordDomain {
        return (argument as AnyArgument).value as ScanRecordDomain
    }

    private fun installWifi(scanRecord: ScanRecordDomain) {
        val state = _state.value as StatusDownloadedEntity

        _state.value = NetworkSelectedEntity(state.device, state.version, state.status, scanRecord)
    }

    private fun installBluetoothDevice(device: DiscoveredBluetoothDevice) {
        _state.value = DeviceSelectedEntity(device)

        viewModelScope.launchWithCatch {
            repository.start(device.device)
            loadVersion()
        }
    }

    private fun loadVersion() {
        repository.readVersion().onEach {
            val state = _state.value as DeviceSelectedEntity
            _state.value = state.copy(version = it)

            _state.value = when (it) {
                is Error,
                is Loading -> state.copy(version = it)
                is Success -> VersionDownloadedEntity(state.device, it.data)
            }

            (_state.value as? VersionDownloadedEntity)?.let {
                loadStatus()
            }
        }.launchIn(viewModelScope)
    }

    private fun loadStatus() {
        repository.getStatus().onEach {
            val state = _state.value as VersionDownloadedEntity

            _state.value = when (it) {
                is Error,
                is Loading -> state.copy(status = it)
                is Success -> StatusDownloadedEntity(state.device, state.version, it.data)
            }
        }.launchIn(viewModelScope)
    }

    private fun onPasswordSelected(password: String) {
        val state = _state.value as NetworkSelectedEntity

        _state.value = state.copy(password = password)
    }

    private fun provision() {
        val state = _state.value as NetworkSelectedEntity

        _state.value = ProvisioningEntity(state.device, state.version, state.status, state.selectedWifi, state.password)
        repository.setConfig().onEach {
            val state = _state.value as ProvisioningEntity

            _state.value = state.copy(provisioningStatus = it)
        }.launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            repository.release()
        }
    }
}

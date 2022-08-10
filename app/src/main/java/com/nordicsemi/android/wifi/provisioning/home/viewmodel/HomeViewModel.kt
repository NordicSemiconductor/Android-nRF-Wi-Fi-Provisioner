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
import com.nordicsemi.android.wifi.provisioning.scanner.ProvisionerScannerArgument
import com.nordicsemi.android.wifi.provisioning.scanner.ProvisionerScannerDestinationId
import com.nordicsemi.android.wifi.provisioning.scanner.ProvisionerScannerResult
import com.nordicsemi.android.wifi.provisioning.wifi.viewmodel.ScanRecordResult
import com.nordicsemi.wifi.provisioner.library.ProvisionerRepository
import com.nordicsemi.wifi.provisioner.library.Success
import com.nordicsemi.wifi.provisioner.library.domain.ScanRecordDomain
import com.nordicsemi.wifi.provisioner.library.domain.WifiConfigDomain
import com.nordicsemi.wifi.provisioner.library.internal.ConnectionStatus
import com.nordicsemi.wifi.provisioner.library.internal.PROVISIONING_SERVICE_UUID
import com.nordicsemi.wifi.provisioner.library.launchWithCatch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import no.nordicsemi.android.common.navigation.NavigationManager
import no.nordicsemi.android.common.navigation.NavigationResult
import no.nordicsemi.android.common.ui.scanner.model.DiscoveredBluetoothDevice
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val navigationManager: NavigationManager
) : ViewModel() {

    private val repository = ProvisionerRepository.newInstance(context)

    private val _state = MutableStateFlow(HomeViewEntity())
    val state = _state.asStateFlow()

    private val pendingJobs = mutableListOf<Job>()

    fun handleNavigationResult(result: NavigationResult) {
        if (result is ProvisionerScannerResult) {
            installBluetoothDevice(result.device)
        } else if (result is ScanRecordResult) {
            installWifi(result.scanRecord)
        }
    }

    fun onEvent(event: HomeScreenViewEvent) {
        if (event != OpenLoggerEvent) {
            cancelPendingJobs()
        }
        when (event) {
            OnFinishedEvent -> finish()
            is OnPasswordSelectedEvent -> onPasswordSelected(event.password)
            OnSelectDeviceClickEvent -> requestBluetoothDevice()
            OnSelectWifiEvent -> navigationManager.navigateTo(WifiScannerId)
            OnProvisionClickEvent -> provision()
            OnHidePasswordDialog -> hidePasswordDialog()
            OnShowPasswordDialog -> showPasswordDialog()
            OpenLoggerEvent -> repository.openLogger()
            OnUnprovisionEvent -> cancelConfig()
        }
    }

    private fun cancelConfig() {
        repository.forgetConfig().onEach {
            _state.value = _state.value.copy(unprovisioningStatus = it)
        }.launchIn(viewModelScope)
    }

    private fun cancelPendingJobs() {
        pendingJobs.forEach { it.cancel() }
        pendingJobs.clear()
    }

    private fun showPasswordDialog() {
        _state.value = _state.value.copy(showPasswordDialog = true)
    }

    private fun hidePasswordDialog() {
        _state.value = _state.value.copy(showPasswordDialog = false)
    }

    private fun finish() {
        viewModelScope.launch {
            release()
            _state.value = HomeViewEntity()
        }
    }

    private suspend fun release() {
        try {
            repository.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun requestBluetoothDevice() {
        navigationManager.navigateTo(ProvisionerScannerDestinationId, ProvisionerScannerArgument(ProvisionerScannerDestinationId, PROVISIONING_SERVICE_UUID))
    }

    private fun installWifi(scanRecord: ScanRecordDomain) {
        _state.value =
            _state.value.copy(network = scanRecord, password = null, provisioningStatus = null)
    }

    private fun installBluetoothDevice(device: DiscoveredBluetoothDevice) {
        _state.value = HomeViewEntity(device = device)
        viewModelScope.launchWithCatch {
            release()
            repository.start(device.device)
                .onEach { updateConnectionStatus(it) }
                .launchIn(viewModelScope)
            loadVersion()
        }
    }

    private fun updateConnectionStatus(connectionStatus: ConnectionStatus) {
        _state.value = _state.value.copy(isConnected = !connectionStatus.isDisconnecting())
    }

    private fun loadVersion() {
        repository.readVersion()
            .cancellable()
            .onEach {
                _state.value = _state.value.copy(version = it)

                (_state.value.version as? Success)?.let {
                    loadStatus()
                }
            }.launchIn(viewModelScope)
            .let { pendingJobs.add(it) }
    }

    private fun loadStatus() {
        repository.getStatus()
            .cancellable()
            .onEach {
                _state.value = _state.value.copy(status = it)
            }.launchIn(viewModelScope)
            .let { pendingJobs.add(it) }
    }

    private fun onPasswordSelected(password: String) {
        _state.value = _state.value.copy(password = password, provisioningStatus = null)
    }

    private fun provision() {
        val state = _state.value
        val config = WifiConfigDomain(state.network!!.wifiInfo, state.password)
        repository.setConfig(config)
            .cancellable()
            .onEach {
                _state.value = _state.value.copy(provisioningStatus = it)
            }.launchIn(viewModelScope)
            .let { pendingJobs.add(it) }
    }
}

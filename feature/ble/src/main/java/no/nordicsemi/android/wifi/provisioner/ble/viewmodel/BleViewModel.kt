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

package no.nordicsemi.android.wifi.provisioner.ble.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import no.nordicsemi.android.common.navigation.NavigationResult
import no.nordicsemi.android.common.navigation.Navigator
import no.nordicsemi.android.common.navigation.viewmodel.SimpleNavigationViewModel
import no.nordicsemi.android.kotlin.ble.core.RealServerDevice
import no.nordicsemi.android.wifi.provisioner.ble.Loading
import no.nordicsemi.android.wifi.provisioner.ble.Success
import no.nordicsemi.android.wifi.provisioner.ble.internal.ConnectionStatus
import no.nordicsemi.android.wifi.provisioner.ble.launchWithCatch
import no.nordicsemi.android.wifi.provisioner.ble.repository.ProvisionerResourceRepository
import no.nordicsemi.android.wifi.provisioner.ble.scanner.BleScannerDestinationId
import no.nordicsemi.android.wifi.provisioner.ble.view.BleProvisioningViewEvent
import no.nordicsemi.android.wifi.provisioner.ble.view.BleViewEntity
import no.nordicsemi.android.wifi.provisioner.ble.view.OnFinishedEvent
import no.nordicsemi.android.wifi.provisioner.ble.view.OnHidePasswordDialog
import no.nordicsemi.android.wifi.provisioner.ble.view.OnPasswordSelectedEvent
import no.nordicsemi.android.wifi.provisioner.ble.view.OnProvisionClickEvent
import no.nordicsemi.android.wifi.provisioner.ble.view.OnProvisionNextDeviceEvent
import no.nordicsemi.android.wifi.provisioner.ble.view.OnSelectDeviceClickEvent
import no.nordicsemi.android.wifi.provisioner.ble.view.OnSelectWifiEvent
import no.nordicsemi.android.wifi.provisioner.ble.view.OnShowPasswordDialog
import no.nordicsemi.android.wifi.provisioner.ble.view.OnUnprovisionEvent
import no.nordicsemi.android.wifi.provisioner.ble.view.OnVolatileMemoryChangedEvent
import no.nordicsemi.android.wifi.provisioner.ble.view.OpenLoggerEvent
import no.nordicsemi.android.wifi.provisioner.ble.view.WiFiAccessPointsListId
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.WifiData
import no.nordicsemi.android.wifi.provisioner.ble.domain.WifiConfigDomain
import javax.inject.Inject

@HiltViewModel
class BleViewModel @Inject constructor(
    private val navigationManager: Navigator,
    private val repository: ProvisionerResourceRepository,
    savedStateHandle: SavedStateHandle
) : SimpleNavigationViewModel(navigator = navigationManager, savedStateHandle) {

    private var connectionObserverJob: Job? = null

    private val _state = MutableStateFlow(BleViewEntity())
    val state = _state.asStateFlow()

    private val pendingJobs = mutableListOf<Job>()

    init {
        navigationManager.resultFrom(BleScannerDestinationId)
            .mapNotNull { it as? NavigationResult.Success }
            .onEach { installBluetoothDevice(it.value as RealServerDevice) }
            .launchIn(viewModelScope)

        navigationManager.resultFrom(WiFiAccessPointsListId)
            .mapNotNull { it as? NavigationResult.Success }
            .onEach { installWifi(it.value as WifiData) }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: BleProvisioningViewEvent) {
        if (event != OpenLoggerEvent) {
            cancelPendingJobs()
        }
        when (event) {
            OnFinishedEvent -> finish()
            is OnPasswordSelectedEvent -> onPasswordSelected(event.password)
            OnSelectDeviceClickEvent -> requestBluetoothDevice()
            OnSelectWifiEvent -> navigationManager.navigateTo(WiFiAccessPointsListId)
            OnProvisionClickEvent -> provision()
            OnHidePasswordDialog -> hidePasswordDialog()
            OnShowPasswordDialog -> showPasswordDialog()
            OpenLoggerEvent -> {}//repository.openLogger()
            OnUnprovisionEvent -> cancelConfig()
            OnProvisionNextDeviceEvent -> provisionNextDevice()
            OnVolatileMemoryChangedEvent -> onVolatileMemoryChangeEvent()
        }
    }

    private fun onVolatileMemoryChangeEvent() {
        _state.value = _state.value.copy(
            persistentMemory = _state.value.persistentMemory.not(),
            provisioningStatus = null
        )
    }

    private fun provisionNextDevice() {
        viewModelScope.launch {
            cancelPendingJobs()
            connectionObserverJob?.cancel()
            launch { release() }
            requestBluetoothDevice()
            delay(500) //nasty delay to prevent screen change before navigation
            _state.value = BleViewEntity()
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
            _state.value = BleViewEntity()
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
        navigationManager.navigateTo(BleScannerDestinationId)
    }

    private fun installWifi(wifiData: WifiData) {
        _state.value = _state.value.copy(
            network = wifiData,
            password = null,
            provisioningStatus = null,
            showPasswordDialog = wifiData.isPasswordRequired()
        )
    }

    private fun installBluetoothDevice(device: RealServerDevice) {
        _state.value = BleViewEntity(device = device, version = Loading())
        viewModelScope.launchWithCatch {
            release()
            repository.start(device.device)
                .onEach { updateConnectionStatus(it) }
                .launchIn(viewModelScope)
                .let { connectionObserverJob = it }
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
        repository.setConfig(state.network!!.toConfig())
            .cancellable()
            .onEach {
                _state.value = _state.value.copy(provisioningStatus = it)
            }.launchIn(viewModelScope)
            .let { pendingJobs.add(it) }
    }

    private fun WifiData.toConfig(): WifiConfigDomain {
        val state = _state.value
        val wifiInfo = selectedChannel?.wifiInfo ?: channelFallback.wifiInfo
        val anyChannel = selectedChannel?.wifiInfo?.let { false } ?: true
        return WifiConfigDomain(
            wifiInfo,
            state.password,
            !state.persistentMemory,
            anyChannel
        )
    }
}

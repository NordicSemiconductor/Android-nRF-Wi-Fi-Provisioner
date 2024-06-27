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

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import no.nordicsemi.android.common.logger.LoggerLauncher
import no.nordicsemi.android.common.navigation.NavigationResult
import no.nordicsemi.android.common.navigation.Navigator
import no.nordicsemi.android.common.navigation.viewmodel.SimpleNavigationViewModel
import no.nordicsemi.android.kotlin.ble.core.RealServerDevice
import no.nordicsemi.android.log.LogSession
import no.nordicsemi.android.log.timber.nRFLoggerTree
import no.nordicsemi.android.wifi.provisioner.ble.domain.WifiConfigDomain
import no.nordicsemi.android.wifi.provisioner.ble.internal.ConnectionStatus
import no.nordicsemi.android.wifi.provisioner.ble.launchWithCatch
import no.nordicsemi.android.wifi.provisioner.ble.repository.ProvisionerResourceRepository
import no.nordicsemi.android.wifi.provisioner.ble.scanner.BleScannerDestinationId
import no.nordicsemi.android.wifi.provisioner.ble.view.BleViewEntity
import no.nordicsemi.android.wifi.provisioner.ble.view.BleWifiScannerDestination
import no.nordicsemi.android.wifi.provisioner.ble.view.OnUnprovisionEvent
import no.nordicsemi.android.wifi.provisioner.ble.view.OnVolatileMemoryChangedEvent
import no.nordicsemi.android.wifi.provisioner.ble.view.OpenLoggerEvent
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Loading
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Success
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.WifiData
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnHidePasswordDialog
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnPasswordSelectedEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnProvisionClickEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnProvisionNextDeviceEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnReconnectClickEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnSelectDeviceClickEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnSelectWifiEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnShowPasswordDialog
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.ProvisioningViewEvent
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BleViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val navigationManager: Navigator,
    private val repository: ProvisionerResourceRepository,
    savedStateHandle: SavedStateHandle
) : SimpleNavigationViewModel(navigator = navigationManager, savedStateHandle) {

    private var logger: nRFLoggerTree? = null
    private var connectionObserverJob: Job? = null

    private val _state = MutableStateFlow(BleViewEntity())
    val state = _state.asStateFlow()

    private val pendingJobs = mutableListOf<Job>()

    init {
        initLogger()
        navigationManager.resultFrom(BleScannerDestinationId)
            .mapNotNull { it as? NavigationResult.Success }
            .onEach { installBluetoothDevice(it.value as RealServerDevice) }
            .launchIn(viewModelScope)

        navigationManager.resultFrom(BleWifiScannerDestination)
            .mapNotNull { it as? NavigationResult.Success }
            .onEach { installWifi(it.value) }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        release()
        super.onCleared()
    }

    private fun initLogger(){
        if (logger != null) {
            Timber.uproot(logger!!)
            logger = null
        }
        Timber.plant(nRFLoggerTree(context, "Provisioning over Bluetooth LE", "Unknown").also {
            logger = it
        })
    }

    fun onEvent(event: ProvisioningViewEvent) {
        if (event != OpenLoggerEvent) {
            cancelPendingJobs()
        }
        when (event) {
            is OnPasswordSelectedEvent -> onPasswordSelected(event.password)
            OnSelectDeviceClickEvent -> requestBluetoothDevice()
            OnReconnectClickEvent -> (_state.value.device as? RealServerDevice)?.let { installBluetoothDevice(it) }
            OnSelectWifiEvent -> navigationManager.navigateTo(BleWifiScannerDestination)
            OnProvisionClickEvent -> provision()
            OnHidePasswordDialog -> hidePasswordDialog()
            OnShowPasswordDialog -> showPasswordDialog()
            OpenLoggerEvent -> {
                context.packageManager
                    .getLaunchIntentForPackage("no.nordicsemi.android.log")
                    ?.let { launchIntent ->
                        context.startActivity(launchIntent)
                    } ?: run {
                    LoggerLauncher.launch(context, logger?.session as LogSession)
                }
            }
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
        repository.forgetConfig()
            .onEach {
                _state.value = _state.value.copy(unprovisioningStatus = it)
            }
            .launchIn(viewModelScope)
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

    private fun release() {
        repository.release()
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
            }
            .filterIsInstance(Success::class)
            .onEach { loadStatus() }
            .launchIn(viewModelScope)
            .let { pendingJobs.add(it) }
    }

    private fun loadStatus() {
        repository.getStatus()
            .cancellable()
            .onEach {
                _state.value = _state.value.copy(status = it)
            }
            .launchIn(viewModelScope)
            .let { pendingJobs.add(it) }
    }

    private fun onPasswordSelected(password: String) {
        _state.value = _state.value.copy(password = password, provisioningStatus = null)
        hidePasswordDialog()
    }

    private fun provision() {
        val state = _state.value
        repository.setConfig(state.network!!.toConfig())
            .cancellable()
            .onEach {
                _state.value = _state.value.copy(provisioningStatus = it)
            }
            .launchIn(viewModelScope)
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

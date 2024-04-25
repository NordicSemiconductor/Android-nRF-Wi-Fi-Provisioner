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

package no.nordicsemi.android.wifi.provisioner.softap.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import no.nordicsemi.android.common.navigation.NavigationResult
import no.nordicsemi.android.common.navigation.Navigator
import no.nordicsemi.android.common.navigation.viewmodel.SimpleNavigationViewModel
import no.nordicsemi.android.wifi.provisioner.softap.SoftAp
import no.nordicsemi.android.wifi.provisioner.softap.SoftApManager
import no.nordicsemi.android.wifi.provisioner.softap.domain.WifiConfigDomain
import no.nordicsemi.android.wifi.provisioner.softap.view.SoftApConnectorDestination
import no.nordicsemi.android.wifi.provisioner.softap.view.SoftApWifiScannerDestination
import no.nordicsemi.android.wifi.provisioner.softap.view.entity.SoftApViewEntity
import no.nordicsemi.kotlin.wifi.provisioner.domain.WifiConnectionStateDomain
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Resource
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.WifiData
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnFinishedEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnHidePasswordDialog
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnPasswordSelectedEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnProvisionClickEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnProvisionNextDeviceEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnSelectDeviceClickEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnSelectWifiEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnShowPasswordDialog
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.ProvisioningViewEvent
import javax.inject.Inject

@HiltViewModel
class SoftApProvisioningViewModel @Inject constructor(
    private val softApManager: SoftApManager,
    private val navigationManager: Navigator,
    savedStateHandle: SavedStateHandle,
) : SimpleNavigationViewModel(navigator = navigationManager, savedStateHandle) {

    private val _state = MutableStateFlow(SoftApViewEntity())
    val state = _state.asStateFlow()

    init {
        navigationManager.resultFrom(SoftApConnectorDestination)
            .mapNotNull { it as? NavigationResult.Success }
            .onEach { result ->
                result.value?.let {
                    installSoftApDevice(it)
                }
            }
            .launchIn(viewModelScope)
        navigationManager.resultFrom(SoftApWifiScannerDestination)
            .mapNotNull { it as? NavigationResult.Success }
            .onEach { installWifi(it.value) }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: ProvisioningViewEvent) {
        when (event) {
            OnFinishedEvent -> finish()
            is OnPasswordSelectedEvent -> onPasswordSelected(event.password)
            OnSelectDeviceClickEvent -> requestWifiDevice()
            OnSelectWifiEvent -> navigationManager.navigateTo(SoftApWifiScannerDestination)
            OnProvisionClickEvent -> provision()
            OnHidePasswordDialog -> hidePasswordDialog()
            OnShowPasswordDialog -> showPasswordDialog()
            OnProvisionNextDeviceEvent -> provisionNextDevice()
        }
    }

    private fun provisionNextDevice() {
        viewModelScope.launch {
            launch { release() }
            requestSoftApDevice()
            delay(500) //nasty delay to prevent screen change before navigation
            _state.value = SoftApViewEntity()
        }
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
            _state.value = SoftApViewEntity()
        }
    }

    private fun release() {
        try {
            _state.value.device?.let {
                softApManager.disconnect()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun requestSoftApDevice() {
        navigationManager.navigateTo(SoftApConnectorDestination)
    }

    private fun requestWifiDevice() {
        release()
        navigationManager.navigateTo(SoftApConnectorDestination)
    }

    private fun installWifi(wifiData: WifiData) {
        _state.value = _state.value.copy(
            network = wifiData,
            password = null,
            showPasswordDialog = wifiData.isPasswordRequired()
        )
    }

    private fun installSoftApDevice(device: SoftAp) {
        _state.value = SoftApViewEntity(device = device)
    }

    private fun onPasswordSelected(password: String) {
        _state.value = _state.value.copy(password = password)
    }

    private fun provision() {
        viewModelScope.launch {
            val state = _state.value
            val network = state.network
            network?.let {
                softApManager.provision(it.toConfig()).also { response ->
                    if (response.isSuccessful) {
                        val nsdServiceInfo = softApManager.discoverServices()
                        _state.value = state.copy(
                            device = state.device?.apply {
                                connectionInfoDomain = connectionInfoDomain?.copy(
                                    ipv4Address = nsdServiceInfo.host.toString()
                                )
                            },
                            provisioningStatus = Resource.createSuccess(
                                data = WifiConnectionStateDomain.CONNECTED
                            )
                        )
                    } else {
                        _state.value = state.copy(
                            provisioningStatus = Resource.createError(
                                Throwable("Provisioning failed")
                            )
                        )
                    }
                }
            }
        }
    }

    private fun WifiData.toConfig(): WifiConfigDomain {
        val state = _state.value
        val wifiInfo = selectedChannel?.wifiInfo ?: channelFallback.wifiInfo
        return WifiConfigDomain(
            wifiInfo,
            state.password
        )
    }
}

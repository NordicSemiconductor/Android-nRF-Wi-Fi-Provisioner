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

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import no.nordicsemi.android.common.logger.LoggerLauncher
import no.nordicsemi.android.common.navigation.NavigationResult
import no.nordicsemi.android.common.navigation.Navigator
import no.nordicsemi.android.common.navigation.viewmodel.SimpleNavigationViewModel
import no.nordicsemi.android.common.theme.view.WizardStepState
import no.nordicsemi.android.log.timber.nRFLoggerTree
import no.nordicsemi.android.wifi.provisioner.softap.Open
import no.nordicsemi.android.wifi.provisioner.softap.PassphraseConfiguration
import no.nordicsemi.android.wifi.provisioner.softap.SoftApManager
import no.nordicsemi.android.wifi.provisioner.softap.domain.WifiConfigDomain
import no.nordicsemi.android.wifi.provisioner.softap.view.SoftApWifiScannerDestination
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.WifiData
import timber.log.Timber
import java.net.SocketTimeoutException
import javax.inject.Inject


@HiltViewModel
class SoftApViewModel @Inject constructor(
    private val softApManager: SoftApManager,
    navigationManager: Navigator,
    savedStateHandle: SavedStateHandle,
) : SimpleNavigationViewModel(navigator = navigationManager, savedStateHandle) {

    private val _state = MutableStateFlow(SoftApScreenState())
    val state = _state.asStateFlow()

    private var wifiConfigDomain: WifiConfigDomain? = null
    private var logger: Timber.Tree? = null


    init {
        Timber.plant(Timber.DebugTree())
        navigationManager.resultFrom(SoftApWifiScannerDestination)
            .mapNotNull { it as? NavigationResult.Success }
            .onEach {
                onWifiSelected(it.value)
            }
            .launchIn(viewModelScope)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCleared() {
        super.onCleared()
        softApManager.disconnect()
    }

    fun onLoggerAppBarIconPressed(context: Context) {
        context.packageManager
            .getLaunchIntentForPackage("no.nordicsemi.android.log")
            ?.let { launchIntent ->
                context.startActivity(launchIntent)
            } ?: run {
            LoggerLauncher.launch(context)
        }
    }

    fun start(
        context: Context,
        ssid: String = "nrf-wifiprov",
        passphraseConfiguration: PassphraseConfiguration = Open
    ) {
        if (logger != null) {
            Timber.uproot(logger!!)
            logger = null
        }
        Timber.plant(nRFLoggerTree(context, "SoftAP Manager", ssid).also {
            logger = it
        })

        viewModelScope.launch {
            connect(ssid, passphraseConfiguration)
            discoverServices()
            // listSsids()
            // provision()
        }
    }

    private suspend fun connect(
        ssid: String = "nrf-wifiprov",
        passphraseConfiguration: PassphraseConfiguration = Open
    ) {
        _state.value = _state.value.copy(connectionState = WizardStepState.CURRENT)
        softApManager.connect(ssid = ssid, passphraseConfiguration = passphraseConfiguration)
        _state.value = _state.value.copy(configureState = WizardStepState.COMPLETED)
        _state.value = _state.value.copy(connectionState = WizardStepState.COMPLETED)
    }

    private suspend fun discoverServices() {
        _state.value = _state.value.copy(discoveringServicesState = WizardStepState.CURRENT)
        softApManager.discoverServices()
        _state.value = _state.value.copy(discoveringServicesState = WizardStepState.COMPLETED)
        _state.value = _state.value.copy(selectWifiState = WizardStepState.CURRENT)
    }

    fun onSelectWifiPressed() {
        navigateTo(SoftApWifiScannerDestination)
    }

    private fun onWifiSelected(record: WifiData) {
        _state.value = _state.value.copy(
            selectedWifi = record,
            selectWifiState = WizardStepState.COMPLETED,
            providePasswordState = WizardStepState.CURRENT
        )
    }

    fun onPasswordEntered(password: String) {
        val selectedWifi = _state.value.selectedWifi
        selectedWifi?.let { wifiData ->
            wifiConfigDomain = wifiData.toConfig(password)
            _state.value = _state.value.copy(
                providePasswordState = WizardStepState.COMPLETED,
                password = password
            )
        }
    }

    fun onProvisionPressed() {
        viewModelScope.launch {
            wifiConfigDomain?.let {
                try {
                    _state.value = _state.value.copy(provisionState = WizardStepState.CURRENT)
                    softApManager.provision(it)
                } catch (e: SocketTimeoutException) {
                    // There is always chance that a socket timeout is thrown from the DK during
                    // provisioning due to timing constraints. In such cases, we can ignore the response
                    // and assume that the provisioning was successful.
                    Timber.log(Log.WARN, e, "Provisioning succeeded due to timeout")
                } catch (e: Exception) {

                } finally {
                    _state.value = _state.value.copy(provisionState = WizardStepState.COMPLETED)
                }
            }
        }
    }

    fun verify() {
        viewModelScope.launch {
            _state.value = _state.value.copy(verifyState = WizardStepState.CURRENT)
            val verified = softApManager.verify()
            if (verified) {
                _state.value = _state.value.copy(verifyState = WizardStepState.COMPLETED)
            }
        }
    }

    private fun WifiData.toConfig(password: String): WifiConfigDomain {
        val wifiInfo = selectedChannel?.wifiInfo ?: channelFallback.wifiInfo
        return WifiConfigDomain(info = wifiInfo, passphrase = password)
    }
}

data class SoftApScreenState(
    val configureState: WizardStepState = WizardStepState.COMPLETED,
    val connectionState: WizardStepState = WizardStepState.INACTIVE,
    val discoveringServicesState: WizardStepState = WizardStepState.INACTIVE,
    val selectWifiState: WizardStepState = WizardStepState.INACTIVE,
    val selectedWifi: WifiData? = null,
    val password: String? = null,
    val providePasswordState: WizardStepState = WizardStepState.INACTIVE,
    val provisionState: WizardStepState = WizardStepState.INACTIVE,
    val verifyState: WizardStepState = WizardStepState.INACTIVE,
)
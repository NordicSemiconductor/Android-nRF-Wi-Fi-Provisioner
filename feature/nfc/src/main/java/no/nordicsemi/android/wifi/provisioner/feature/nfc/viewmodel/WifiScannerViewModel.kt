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

package no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel

import android.net.wifi.ScanResult
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import no.nordicsemi.android.common.navigation.Navigator
import no.nordicsemi.android.wifi.provisioner.feature.nfc.NfcPublishDestination
import no.nordicsemi.android.wifi.provisioner.feature.nfc.WifiScannerDestination
import no.nordicsemi.android.wifi.provisioner.nfc.WifiManagerRepository
import no.nordicsemi.android.wifi.provisioner.nfc.domain.AuthenticationMode
import no.nordicsemi.android.wifi.provisioner.nfc.domain.EncryptionMode
import no.nordicsemi.android.wifi.provisioner.nfc.domain.Loading
import no.nordicsemi.android.wifi.provisioner.nfc.domain.NetworkState
import no.nordicsemi.android.wifi.provisioner.nfc.domain.Success
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiData
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.WifiSortOption
import javax.inject.Inject

/**
 * A sealed class to represent the events that can be triggered from the UI.
 */
internal sealed interface WifiScannerViewEvent

/**
 * Event triggered when the wifi network is selected.
 *
 * @param network The selected network.
 */
data class OnNetworkSelectEvent(
    val network: ScanResult,
) : WifiScannerViewEvent

/**
 * Event triggered when the password is confirmed.
 *
 * @param wifiData The Wi-Fi data.
 */
data class OnPasswordSetEvent(
    val wifiData: WifiData,
) : WifiScannerViewEvent

data object OnPasswordCancelEvent : WifiScannerViewEvent
data object RefreshWiFiNetworksEvent : WifiScannerViewEvent

/**
 * Event triggered when the back button is clicked.
 */
internal data object OnNavigateUpClickEvent : WifiScannerViewEvent

internal data class OnSortOptionSelected(val sortOption: WifiSortOption) : WifiScannerViewEvent

/**
 * A wrapper class to represent the view state of the Wi-Fi scanner screen.
 *
 * @param networks The state of the available Wi-Fi networks.
 * @param selectedNetwork The selected Wi-Fi network.
 * @param sortOption The selected sort option.
 */
data class WifiScannerViewState(
    val networks: NetworkState = Loading,
    val selectedNetwork: ScanResult? = null,
    val sortOption: WifiSortOption = WifiSortOption.RSSI,
) {
    private val items = (networks as? Success)?.data ?: emptyList()
    val sortedItems: List<ScanResult> = when (sortOption) {
        WifiSortOption.NAME -> items.sortedBy { it.SSID.lowercase() }
        WifiSortOption.RSSI -> items.sortedByDescending { it.level }
    }
}

@RequiresApi(Build.VERSION_CODES.M)
@HiltViewModel
internal class WifiScannerViewModel @Inject constructor(
    private val navigator: Navigator,
    private val wifiManager: WifiManagerRepository,
) : ViewModel() {
    private val _viewState = MutableStateFlow(WifiScannerViewState())
    val viewState = _viewState.asStateFlow()

    init {
        wifiManager.networkState
            .onEach {
                _viewState.value = _viewState.value.copy(networks = it)
            }
            .launchIn(viewModelScope)
    }

    /**
     * Scans for available Wi-Fi networks.
     */
    fun scanAvailableWifiNetworks() {
        wifiManager.onScan()
    }

    private fun onBackClick() {
        navigator.navigateUp()
    }

    fun onEvent(event: WifiScannerViewEvent) {
        when (event) {
            is OnNetworkSelectEvent -> {
                val isOpen = AuthenticationMode.get(event.network) == AuthenticationMode.OPEN
                // If the network is open, navigate to the NFC screen
                if (isOpen) {
                    navigateToNfcScan(
                        WifiData(
                            ssid = event.network.SSID,
                            macAddress = event.network.BSSID,
                            password = null, // Empty password for open network.
                            authType = AuthenticationMode.OPEN,
                            encryptionMode = EncryptionMode.NONE // No encryption for open network.
                        )
                    )
                } else {
                    // Show the dialog to enter the password for the selected network.
                    _viewState.value = _viewState.value.copy(selectedNetwork = event.network)
                }
            }

            is OnNavigateUpClickEvent -> onBackClick()
            is OnPasswordSetEvent -> {
                // Close the dialog and navigate to the NFC screen.
                // Needed to clear the selected network, otherwise the dialog will be shown on back press.
                _viewState.value = _viewState.value.copy(selectedNetwork = null)
                navigateToNfcScan(event.wifiData)
            }
            is RefreshWiFiNetworksEvent -> scanAvailableWifiNetworks()

            OnPasswordCancelEvent -> _viewState.value =
                _viewState.value.copy(selectedNetwork = null)

            is OnSortOptionSelected -> _viewState.value =
                _viewState.value.copy(sortOption = event.sortOption)
        }
    }

    /**
     * Navigates to the NFC scan screen.
     *
     * @param wifiData The Wi-Fi data.
     */
    private fun navigateToNfcScan(wifiData: WifiData) {
        navigator.navigateTo(
            to = NfcPublishDestination,
            args = wifiData
        ) {
            popUpTo(WifiScannerDestination.toString()) {
                inclusive = true
            }
        }
    }
}

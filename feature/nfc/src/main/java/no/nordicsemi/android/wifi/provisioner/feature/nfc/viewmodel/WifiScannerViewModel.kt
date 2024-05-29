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
import no.nordicsemi.android.wifi.provisioner.feature.nfc.NfcDestinationId
import no.nordicsemi.android.wifi.provisioner.nfc.WifiManagerRepository
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
    val networks: NetworkState<List<ScanResult>> = Loading(),
    val selectedNetwork: ScanResult? = null,
    val sortOption: WifiSortOption = WifiSortOption.RSSI,
) {
    private val items = (networks as? Success)?.data ?: emptyList()
    val sortedItems: List<ScanResult> = when (sortOption) {
        WifiSortOption.NAME -> items.sortedBy { it.SSID }
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
        scanAvailableWifiNetworks()
    }

    /**
     * Scans for available Wi-Fi networks.
     */
    private fun scanAvailableWifiNetworks() {
        try {
            wifiManager.onScan()
            wifiManager.networkState.onEach { scanResults ->
                _viewState.value = _viewState.value.copy(networks = scanResults)
            }.launchIn(viewModelScope)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onBackClick() {
        navigator.navigateUp()
    }

    fun onEvent(event: WifiScannerViewEvent) {
        when (event) {
            is OnNetworkSelectEvent -> {
                _viewState.value = _viewState.value.copy(selectedNetwork = event.network)
            }

            OnNavigateUpClickEvent -> onBackClick()
            is OnPasswordSetEvent -> {
                // Close the dialog and navigate to the NFC screen.
                // Needed to clear the selected network, otherwise the dialog will be shown on back press.
                _viewState.value = _viewState.value.copy(selectedNetwork = null)
                navigateToNfcScan(event.wifiData)
            }

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
            NfcDestinationId,
            wifiData
        )
    }
}

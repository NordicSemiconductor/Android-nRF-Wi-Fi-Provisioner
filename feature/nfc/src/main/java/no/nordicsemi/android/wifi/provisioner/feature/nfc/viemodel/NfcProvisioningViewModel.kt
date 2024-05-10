package no.nordicsemi.android.wifi.provisioner.feature.nfc.viemodel

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
import no.nordicsemi.android.wifi.provisioner.nfc.WifiManagerRepository
import no.nordicsemi.android.wifi.provisioner.nfc.domain.Loading
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.M)
@HiltViewModel
internal class NfcProvisioningViewModel @Inject constructor(
    private val navigator: Navigator,
    private val wifiManager: WifiManagerRepository,
) : ViewModel() {
    private val _viewState = MutableStateFlow(NfcProvisioningViewState())
    val viewState = _viewState.asStateFlow()

    /**
     * Handles the events from the UI.
     */
    fun onEvent(event: NfcProvisioningViewEvent) {
        when (event) {
            is OnScanClickEvent -> {
                // Navigate to the Scanning screen.
                _viewState.value = _viewState.value.copy(
                    view = Scan(
                        networkState = Loading(),
                    )
                )
                scanAvailableWifiNetworks()
            }

            OnBackClickEvent -> navigator.navigateUp()
        }
    }

    /**
     * Scans for available Wi-Fi networks.
     */
    private fun scanAvailableWifiNetworks() {
        try {
            wifiManager.onScan()
            wifiManager.networkState.onEach { scanResults ->
                _viewState.value = _viewState.value.copy(
                    view = Scan(
                        networkState = scanResults,
                    )
                )
            }.launchIn(viewModelScope)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

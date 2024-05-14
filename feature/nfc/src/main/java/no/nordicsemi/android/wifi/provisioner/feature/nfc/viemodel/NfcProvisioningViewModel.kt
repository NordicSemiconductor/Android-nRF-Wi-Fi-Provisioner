package no.nordicsemi.android.wifi.provisioner.feature.nfc.viemodel

import android.nfc.NdefMessage
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
import no.nordicsemi.android.wifi.provisioner.nfc.WifiConfigNdefMessageBuilder
import no.nordicsemi.android.wifi.provisioner.nfc.WifiManagerRepository
import no.nordicsemi.android.wifi.provisioner.nfc.domain.Loading
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiData
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.M)
@HiltViewModel
internal class NfcProvisioningViewModel @Inject constructor(
    private val navigator: Navigator,
    private val wifiManager: WifiManagerRepository,
    private val wifiConfigNdefMessageBuilder: WifiConfigNdefMessageBuilder,
) : ViewModel() {
    private val _viewState = MutableStateFlow(NfcProvisioningViewState())
    val viewState = _viewState.asStateFlow()
    var ndefMessage: NdefMessage? = null

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
            is OnNetworkSelectedEvent -> {
                // Navigate to connect to the selected network.
                // Ask the user to enter the password.

                // TODO: Check if the network is open or not.
                // If the network is open, then connect to it directly.
                // Otherwise, ask the user to enter the password.
                _viewState.value = _viewState.value.copy(
                    view = AskForPassword(
                        network = event.network
                    )
                )
            }

            is OnPasswordConfirmedEvent -> {
                // Create a NdefMessage with the network details.
                ndefMessage = wifiConfigNdefMessageBuilder.createNdefMessage(
                    wifiNetwork = WifiData(
                        ssid = event.network.SSID,
                        password = event.password,
                        authType = "WPA2-PSK", // TODO: For now, we are only supporting WPA2_PSK.
                    )
                )
                /* wifiConfigNdefMessageBuilder.onNfcTap(activity = event.activity
                         ,ndefMessage)*/

                _viewState.value = _viewState.value.copy(
                    view = Provisioning
                )
            }
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

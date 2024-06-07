package no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import no.nordicsemi.android.common.navigation.Navigator
import no.nordicsemi.android.wifi.provisioner.feature.nfc.NfcPublishDestination
import no.nordicsemi.android.wifi.provisioner.feature.nfc.WifiScannerDestination
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.M)
@HiltViewModel
internal class NfcProvisioningViewModel @Inject constructor(
    private val navigator: Navigator,
) : ViewModel() {
    /**
     * Handles the events from the UI.
     */
    fun onEvent(event: NfcProvisioningViewEvent) {
        when (event) {
            is OnScanClickEvent -> {
                navigator.navigateTo(WifiScannerDestination)
            }

            OnBackClickEvent -> navigator.navigateUp()
            is OnAddWifiNetworkClickEvent -> {
                // Navigate to the NFC screen with the Wi-Fi data.
                navigator.navigateTo(NfcPublishDestination, event.wifiData)
            }
        }
    }
}

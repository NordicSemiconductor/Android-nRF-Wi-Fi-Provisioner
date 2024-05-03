package no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import no.nordicsemi.android.common.navigation.Navigator
import no.nordicsemi.android.wifi.provisioner.feature.nfc.view.WiFiAccessPointsDestinationIdForNfc
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.WifiAggregator
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.WifiData
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.viewmodel.GenericWifiScannerViewModel
import javax.inject.Inject

@HiltViewModel
internal class WifiScannerViewModel @Inject constructor(
    navigationManager: Navigator,
    wifiAggregator: WifiAggregator,
) : GenericWifiScannerViewModel(
    navigationManager = navigationManager,
    wifiAggregator = wifiAggregator
) {
    override fun navigateUp() {
        navigationManager.navigateUp()
    }

    override fun navigateUp(wifiData: WifiData) {
        navigationManager.navigateUpWithResult(
            from = WiFiAccessPointsDestinationIdForNfc,
            result = wifiData
        )
    }
}
package no.nordicsemi.android.wifi.provisioning.scanner

import androidx.hilt.navigation.compose.hiltViewModel
import no.nordicsemi.android.common.navigation.DestinationId
import no.nordicsemi.android.common.navigation.defineDestination
import no.nordicsemi.android.common.navigation.viewmodel.SimpleNavigationViewModel
import no.nordicsemi.android.common.ui.scanner.DeviceSelected
import no.nordicsemi.android.common.ui.scanner.ScanningCancelled
import no.nordicsemi.android.common.ui.scanner.model.DiscoveredBluetoothDevice
import no.nordicsemi.android.wifi.provisioning.scanner.view.ProvisionerScannerScreen

val ProvisionerScannerDestinationId = DestinationId<Unit, DiscoveredBluetoothDevice>("uiscanner-destination")

val ProvisionerScannerDestination = defineDestination(ProvisionerScannerDestinationId) {
    val navigationManager = hiltViewModel<SimpleNavigationViewModel>()

    ProvisionerScannerScreen(
        onResult = {
            when (it) {
                is DeviceSelected -> navigationManager.navigateUpWithResult(ProvisionerScannerDestinationId, it.device)
                ScanningCancelled -> navigationManager.navigateUp()
            }
        }
    )
}

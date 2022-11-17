package no.nordicsemi.android.wifi.provisioning.scanner

import androidx.hilt.navigation.compose.hiltViewModel
import no.nordicsemi.android.common.navigation.createDestination
import no.nordicsemi.android.common.navigation.defineDestination
import no.nordicsemi.android.common.navigation.viewmodel.SimpleNavigationViewModel
import no.nordicsemi.android.common.ui.scanner.DeviceSelected
import no.nordicsemi.android.common.ui.scanner.ScanningCancelled
import no.nordicsemi.android.common.ui.scanner.model.DiscoveredBluetoothDevice
import no.nordicsemi.android.wifi.provisioning.scanner.view.ProvisionerScannerScreen

val ProvisionerScannerDestinationId =
    createDestination<Unit, DiscoveredBluetoothDevice>("uiscanner-destination")

val ProvisionerScannerDestination = defineDestination(ProvisionerScannerDestinationId) {
    val viewModel = hiltViewModel<SimpleNavigationViewModel>()

    ProvisionerScannerScreen(
        onResult = {
            when (it) {
                is DeviceSelected -> viewModel.navigateUpWithResult(
                    ProvisionerScannerDestinationId,
                    it.device
                )
                ScanningCancelled -> viewModel.navigateUp()
            }
        }
    )
}

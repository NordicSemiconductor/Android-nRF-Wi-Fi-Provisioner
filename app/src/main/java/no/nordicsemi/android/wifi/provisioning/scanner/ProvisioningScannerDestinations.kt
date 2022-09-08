package no.nordicsemi.android.wifi.provisioning.scanner

import no.nordicsemi.android.wifi.provisioning.scanner.view.ProvisionerScannerScreen
import no.nordicsemi.android.common.navigation.ComposeDestination
import no.nordicsemi.android.common.navigation.ComposeDestinations
import no.nordicsemi.android.common.navigation.DestinationId
import no.nordicsemi.android.common.navigation.NavigationResult
import no.nordicsemi.android.common.ui.scanner.DeviceSelected
import no.nordicsemi.android.common.ui.scanner.ScanningCancelled
import no.nordicsemi.android.common.ui.scanner.model.DiscoveredBluetoothDevice

val ProvisionerScannerDestinationId = DestinationId("uiscanner-destination")

private val ProvisionerScannerDestination =
    ComposeDestination(ProvisionerScannerDestinationId) { navigationManager ->
        ProvisionerScannerScreen(
            onResult = {
                when (it) {
                    is DeviceSelected -> navigationManager.navigateUp(
                        ProvisionerScannerResult(
                            ProvisionerScannerDestinationId,
                            it.device
                        )
                    )
                    ScanningCancelled -> navigationManager.navigateUp()
                }
            }
        )
    }

val ProvisionerScannerDestinations = ComposeDestinations(listOf(ProvisionerScannerDestination))

data class ProvisionerScannerResult(
    override val destinationId: DestinationId,
    val device: DiscoveredBluetoothDevice
) : NavigationResult

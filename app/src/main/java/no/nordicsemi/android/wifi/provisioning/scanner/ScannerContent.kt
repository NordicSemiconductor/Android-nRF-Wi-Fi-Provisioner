package no.nordicsemi.android.wifi.provisioning.scanner

import androidx.compose.runtime.Composable
import no.nordicsemi.android.wifi.provisioning.scanner.view.ProvisionerScannerScreen
import no.nordicsemi.android.common.navigation.NavigationManager
import no.nordicsemi.android.common.ui.scanner.DeviceSelected
import no.nordicsemi.android.common.ui.scanner.ScanningCancelled

@Composable
fun ScannerContent(navigationManager: NavigationManager) {
    ProvisionerScannerScreen(
        onResult = {
            when (it) {
                is DeviceSelected -> navigationManager.navigateUp(ProvisionerScannerResult(ProvisionerScannerDestinationId, it.device))
                ScanningCancelled -> navigationManager.navigateUp()
            }
        }
    )
}

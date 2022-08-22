package com.nordicsemi.android.wifi.provisioning.scanner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.nordicsemi.android.wifi.provisioning.scanner.view.ProvisionerScannerScreen
import no.nordicsemi.android.common.navigation.NavigationManager
import no.nordicsemi.android.common.permission.view.PermissionViewModel
import no.nordicsemi.android.common.ui.scanner.ScannerResultCancel
import no.nordicsemi.android.common.ui.scanner.ScannerResultSuccess

@Composable
fun ScannerContent(navigationManager: NavigationManager) {
    val viewModel = hiltViewModel<PermissionViewModel>()
    val isLocationPermissionRequired = viewModel.isLocationPermissionRequired.collectAsState().value

    ProvisionerScannerScreen(
        isLocationPermissionRequired = isLocationPermissionRequired,
        onResult = {
            when (it) {
                ScannerResultCancel -> navigationManager.navigateUp()
                is ScannerResultSuccess -> navigationManager.navigateUp(ProvisionerScannerResult(ProvisionerScannerDestinationId, it.device))
            }
        },
        onDevicesDiscovered = { viewModel.onDevicesDiscovered() }
    )
}

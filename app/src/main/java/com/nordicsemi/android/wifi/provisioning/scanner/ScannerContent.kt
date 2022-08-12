package com.nordicsemi.android.wifi.provisioning.scanner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import no.nordicsemi.android.common.navigation.NavigationManager
import no.nordicsemi.android.common.permission.view.*
import no.nordicsemi.android.common.ui.scanner.ScannerResultCancel
import no.nordicsemi.android.common.ui.scanner.ScannerResultSuccess
import no.nordicsemi.android.common.ui.scanner.ScannerScreen
import no.nordicsemi.android.common.ui.scanner.main.DeviceListItem

@Composable
fun ScannerContent(navigationManager: NavigationManager) {
    val argument = navigationManager.getArgument(ProvisionerScannerDestinationId) as ProvisionerScannerArgument
    val viewModel = hiltViewModel<PermissionViewModel>()
    val isLocationPermissionRequired = viewModel.isLocationPermissionRequired.collectAsState().value

    ScannerScreen(
        uuid = argument.uuid,
        isLocationPermissionRequired = isLocationPermissionRequired,
        onResult = {
            when (it) {
                ScannerResultCancel -> navigationManager.navigateUp()
                is ScannerResultSuccess -> navigationManager.navigateUp(ProvisionerScannerResult(ProvisionerScannerDestinationId, it.device))
            }
        },
        onDevicesDiscovered = { viewModel.onDevicesDiscovered() }
    ) {
        DeviceListItem(it) {
            it.provisioningData()?.let { ProvisioningSection(data = it) }
        }
    }
}

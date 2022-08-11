package com.nordicsemi.android.wifi.provisioning.scanner

import android.os.ParcelUuid
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import no.nordicsemi.android.common.navigation.*
import no.nordicsemi.android.common.permission.view.PermissionViewModel
import no.nordicsemi.android.common.ui.scanner.ScannerResultCancel
import no.nordicsemi.android.common.ui.scanner.ScannerResultSuccess
import no.nordicsemi.android.common.ui.scanner.ScannerScreen
import no.nordicsemi.android.common.ui.scanner.main.DeviceListItem
import no.nordicsemi.android.common.ui.scanner.model.DiscoveredBluetoothDevice
import java.util.*

val ProvisionerScannerDestinationId = DestinationId("uiscanner-destination")

private val ProvisionerScannerDestination =
    ComposeDestination(ProvisionerScannerDestinationId) { navigationManager ->
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

val ProvisionerScannerDestinations = ComposeDestinations(listOf(ProvisionerScannerDestination))

data class ProvisionerScannerArgument(
    override val destinationId: DestinationId,
    val uuid: ParcelUuid
) : NavigationArgument {
    constructor(destinationId: DestinationId, uuid: UUID) : this(destinationId, ParcelUuid(uuid))
}

data class ProvisionerScannerResult(
    override val destinationId: DestinationId,
    val device: DiscoveredBluetoothDevice
) : NavigationResult

package com.nordicsemi.android.wifi.provisioning.scanner

import android.os.ParcelUuid
import no.nordicsemi.android.common.navigation.*
import no.nordicsemi.android.common.permission.view.BluetoothPermissionScreen
import no.nordicsemi.android.common.ui.scanner.model.DiscoveredBluetoothDevice
import java.util.*

val ProvisionerScannerDestinationId = DestinationId("uiscanner-destination")

private val ProvisionerScannerDestination =
    ComposeDestination(ProvisionerScannerDestinationId) { navigationManager ->
        BluetoothPermissionScreen(onNavigateBack = { navigationManager.navigateUp() }) {
            ScannerContent(navigationManager = navigationManager)
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

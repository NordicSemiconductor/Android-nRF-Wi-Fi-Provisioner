package com.nordicsemi.android.wifi.provisioning.scanner

import no.nordicsemi.android.common.navigation.ComposeDestination
import no.nordicsemi.android.common.navigation.ComposeDestinations
import no.nordicsemi.android.common.navigation.DestinationId
import no.nordicsemi.android.common.navigation.NavigationResult
import no.nordicsemi.android.common.permission.view.BluetoothPermissionScreen
import no.nordicsemi.android.common.ui.scanner.model.DiscoveredBluetoothDevice

val ProvisionerScannerDestinationId = DestinationId("uiscanner-destination")

private val ProvisionerScannerDestination =
    ComposeDestination(ProvisionerScannerDestinationId) { navigationManager ->
        BluetoothPermissionScreen(onNavigateBack = { navigationManager.navigateUp() }) {
            ScannerContent(navigationManager = navigationManager)
        }
    }

val ProvisionerScannerDestinations = ComposeDestinations(listOf(ProvisionerScannerDestination))

data class ProvisionerScannerResult(
    override val destinationId: DestinationId,
    val device: DiscoveredBluetoothDevice
) : NavigationResult

package com.nordicsemi.android.wifi.provisioning.scanner

import no.nordicsemi.android.common.navigation.ComposeDestination
import no.nordicsemi.android.common.navigation.ComposeDestinations
import no.nordicsemi.android.common.navigation.DestinationId
import no.nordicsemi.android.common.ui.scanner.FindDeviceScreen
import no.nordicsemi.android.common.ui.scanner.main.DeviceListItem

val ProvisionerScannerDestinationId = DestinationId("uiscanner-destination")

private val ProvisionerScannerDestination = ComposeDestination(ProvisionerScannerDestinationId) {
    FindDeviceScreen {
        DeviceListItem(it) {
            it.provisioningData()?.let { ProvisioningSection(data = it) }
        }
    }
}

val ProvisionerScannerDestinations = ComposeDestinations(listOf(ProvisionerScannerDestination))

package no.nordicsemi.android.wifi.provisioner.softap.view

import no.nordicsemi.android.common.navigation.createSimpleDestination
import no.nordicsemi.android.common.navigation.defineDestination

/**
 * Created by Roshan Rajaratnam on 14/02/2024.
 */

val SoftApProvisionerDestinationId = createSimpleDestination("softap-provider-destination")

val SoftApProvisionerDestinations = listOf(
    defineDestination(SoftApProvisionerDestinationId) { SoftApProvisioningScreen() }
)

package no.nordicsemi.android.wifi.provisioner.softap.view

import android.os.Build
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.nordicsemi.android.common.navigation.createDestination
import no.nordicsemi.android.common.navigation.createSimpleDestination
import no.nordicsemi.android.common.navigation.defineDestination
import no.nordicsemi.android.wifi.provisioner.softap.viewmodel.WifiScannerViewModel
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.WifiData

/**
 * Created by Roshan Rajaratnam on 14/02/2024.
 */

val SoftApProvisionerDestinationId = createSimpleDestination("softap-provider-destination")
val WiFiAccessPointsDestinationId = createDestination<Unit, WifiData>(
    name = "wifi-access-points-destination1"
)

val SoftApProvisionerDestinations = listOf(
    defineDestination(SoftApProvisionerDestinationId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            SoftApProvisioningScreen()
        }
    },
    defineDestination(WiFiAccessPointsDestinationId) {
        val viewModel = hiltViewModel<WifiScannerViewModel>()
        val viewEntity by viewModel.state.collectAsStateWithLifecycle()
        WiFiAccessPointsScreen(viewEntity, viewModel::onEvent)
    }
)

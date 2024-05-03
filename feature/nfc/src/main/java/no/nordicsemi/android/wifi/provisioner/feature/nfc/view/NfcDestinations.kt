package no.nordicsemi.android.wifi.provisioner.feature.nfc.view

import android.os.Build
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.nordicsemi.android.common.navigation.createDestination
import no.nordicsemi.android.common.navigation.createSimpleDestination
import no.nordicsemi.android.common.navigation.defineDestination
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.WifiScannerViewModel
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.WifiData


val NfcProvisionerDestinationId = createSimpleDestination("nfc-provider-destination")
val WiFiAccessPointsDestinationIdForNfc = createDestination<Unit, WifiData>(
    name = "wifi-access-points-destination2"
)

val NfcProvisionerDestinations = listOf(
    defineDestination(NfcProvisionerDestinationId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            NfcProvisioningScreen()
        }
    },
    defineDestination(WiFiAccessPointsDestinationIdForNfc) {
        val viewModel = hiltViewModel<WifiScannerViewModel>()
        val viewEntity by viewModel.state.collectAsStateWithLifecycle()
        WiFiAccessPointsForNfcScreen(viewEntity, viewModel::onEvent)
    }
)
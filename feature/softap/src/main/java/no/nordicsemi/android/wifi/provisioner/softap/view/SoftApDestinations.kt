package no.nordicsemi.android.wifi.provisioner.softap.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.nordicsemi.android.common.navigation.createDestination
import no.nordicsemi.android.common.navigation.createSimpleDestination
import no.nordicsemi.android.common.navigation.defineDestination
import no.nordicsemi.android.wifi.provisioner.softap.viewmodel.SoftApWifiScannerViewModel
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.WifiData
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.WifiScannerViewEvent

/**
 * Created by Roshan Rajaratnam on 14/02/2024.
 */
val SoftApProvisionerDestination = createSimpleDestination("softap-provisioner-destination")
val SoftApWifiScannerDestination = createDestination<Unit, WifiData>(
    name = "wifi-access-points-softap-destination"
)

@RequiresApi(Build.VERSION_CODES.Q)
val SoftApProvisionerDestinations = listOf(
    defineDestination(SoftApProvisionerDestination) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            SoftApProvisioningScreen()
        }
    },
    defineDestination(SoftApWifiScannerDestination) {
        val viewModel = hiltViewModel<SoftApWifiScannerViewModel>()
        val viewEntity by viewModel.state.collectAsStateWithLifecycle()
        val onEvent: (WifiScannerViewEvent) -> Unit = { viewModel.onEvent(it) }
        SoftApWifiScannerScreen(viewEntity, onEvent)
    }
)

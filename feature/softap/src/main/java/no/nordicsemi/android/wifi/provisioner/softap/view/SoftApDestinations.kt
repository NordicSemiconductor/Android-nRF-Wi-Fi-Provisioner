package no.nordicsemi.android.wifi.provisioner.softap.view

import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.nordicsemi.android.common.navigation.createDestination
import no.nordicsemi.android.common.navigation.createSimpleDestination
import no.nordicsemi.android.common.navigation.defineDestination
import no.nordicsemi.android.wifi.provisioner.softap.viewmodel.SoftApViewModel
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

val SoftApProvisionerDestinations = listOf(
    defineDestination(SoftApProvisionerDestination) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val viewModel = hiltViewModel<SoftApViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()
            val context = LocalContext.current
            // SoftApProvisioningScreen()
            SoftApScreen(
                state = state,
                onLoggerAppBarIconPressed = {
                    viewModel.onLoggerAppBarIconPressed(context)
                },
                start = { ssid, configuration ->
                    viewModel.start(
                        context = context,
                        ssid = ssid,
                        passphraseConfiguration = configuration
                    )
                },
                onSelectWifiPressed = viewModel::onSelectWifiPressed,
                onPasswordEntered = {
                    viewModel.onPasswordEntered(it)
                },
                onProvisionPressed = viewModel::onProvisionPressed,
                verify = viewModel::verify,
                navigateUp = viewModel::navigateUp
            )
        }
    },
    defineDestination(SoftApWifiScannerDestination) {
        val viewModel = hiltViewModel<SoftApWifiScannerViewModel>()
        val viewEntity by viewModel.state.collectAsStateWithLifecycle()
        val onEvent: (WifiScannerViewEvent) -> Unit = { viewModel.onEvent(it) }
        SoftApWifiScannerScreen(viewEntity, onEvent)
    }
)

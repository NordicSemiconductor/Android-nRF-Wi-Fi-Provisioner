package no.nordicsemi.android.wifi.provisioner.feature.nfc.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.utils.WifiPermissionNotAvailableReason
import no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.utils.WifiPermissionState
import no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.view.LocationPermissionRequiredView
import no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.viewmodel.PermissionViewModel

@Composable
fun RequireLocationForWifi(
    onChanged: (Boolean) -> Unit = {},
    contentWithoutLocation: @Composable () -> Unit = { LocationPermissionRequiredView() },
    content: @Composable (isLocationRequiredAndDisabled: Boolean) -> Unit,
) {
    val viewModel = hiltViewModel<PermissionViewModel>()
    val state by viewModel.locationPermission.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        onChanged(
            state is WifiPermissionState.Available ||
                    (state as WifiPermissionState.NotAvailable).reason == WifiPermissionNotAvailableReason.DISABLED
        )
    }

    when (val s = state) {
        WifiPermissionState.Available -> content(false)
        is WifiPermissionState.NotAvailable -> when (s.reason) {
            WifiPermissionNotAvailableReason.DISABLED -> content(true)
            else -> contentWithoutLocation()
        }
    }
}
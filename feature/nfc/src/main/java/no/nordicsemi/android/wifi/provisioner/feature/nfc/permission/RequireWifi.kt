package no.nordicsemi.android.wifi.provisioner.feature.nfc.permission

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.utils.WifiPermissionNotAvailableReason
import no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.utils.WifiPermissionState
import no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.view.WifiDisabledView
import no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.view.WifiNotAvailableView
import no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.view.WifiPermissionRequiredView
import no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.viewmodel.PermissionViewModel

@Composable
fun RequireWifi(
    onChanged: (Boolean) -> Unit = {},
    contentWithoutWifi: @Composable (WifiPermissionNotAvailableReason) -> Unit = {
        NoWifiView(reason = it)
    },
    content: @Composable () -> Unit,
) {
    val viewModel = hiltViewModel<PermissionViewModel>()
    val state by viewModel.wifiState.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        onChanged(state is WifiPermissionState.Available)
    }

    when (val s = state) {
        WifiPermissionState.Available -> content()
        is WifiPermissionState.NotAvailable -> contentWithoutWifi(s.reason)
    }
}

@Composable
private fun NoWifiView(
    reason: WifiPermissionNotAvailableReason,
) {
    when (reason) {
        WifiPermissionNotAvailableReason.NOT_AVAILABLE -> WifiNotAvailableView()
        WifiPermissionNotAvailableReason.PERMISSION_REQUIRED ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                WifiPermissionRequiredView()
            }

        WifiPermissionNotAvailableReason.DISABLED -> WifiDisabledView()
    }
}
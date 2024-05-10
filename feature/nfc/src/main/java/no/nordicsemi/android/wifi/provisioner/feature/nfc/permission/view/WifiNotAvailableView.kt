package no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import no.nordicsemi.android.common.theme.NordicTheme
import no.nordicsemi.android.common.theme.view.WarningView
import no.nordicsemi.android.wifi.provisioner.feature.nfc.R

@Composable
internal fun WifiNotAvailableView() {
    WarningView(
        imageVector = Icons.Default.WifiOff,
        title = stringResource(id = R.string.wifi_not_available),
        hint = stringResource(id = R.string.wifi_not_available_des),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    )
}

@Preview
@Composable
private fun BluetoothNotAvailableView_Preview() {
    NordicTheme {
        WifiNotAvailableView()
    }
}

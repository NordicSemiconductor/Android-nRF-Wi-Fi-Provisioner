package no.nordicsemi.android.wifi.provisioning.home.view.sections

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import no.nordicsemi.android.wifi.provisioning.R
import no.nordicsemi.android.wifi.provisioning.home.view.components.DataItem

@Composable
fun DisconnectedDeviceStatus() {
    DataItem(
        iconRes = R.drawable.ic_disconnected,
        title = stringResource(id = R.string.device_info),
        description = stringResource(id = R.string.disconnected)
    )
}

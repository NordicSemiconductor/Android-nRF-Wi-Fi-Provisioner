package no.nordicsemi.android.wifi.provisioner.softap.sections

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SignalWifiConnectedNoInternet4
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import no.nordicsemi.android.wifi.provisioner.feature.softap.R
import no.nordicsemi.android.wifi.provisioner.ui.ClickableDataItem

/**
 * Created by Roshan Rajaratnam on 16/02/2024.
 */
@Composable
private fun WifiNotSelectedSection(
    onClick: () -> Unit
) {
    ClickableDataItem(
        imageVector = Icons.Outlined.SignalWifiConnectedNoInternet4,
        title = "Not selected",
        isEditable = false,
        description = "Please select a device to provision",
        onClick = onClick,
        buttonText = stringResource(id = R.string.setup_wifi)
    )
}
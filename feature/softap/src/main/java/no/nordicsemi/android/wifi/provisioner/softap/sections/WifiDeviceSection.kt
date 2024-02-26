package no.nordicsemi.android.wifi.provisioner.softap.sections

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import no.nordicsemi.android.wifi.provisioner.feature.softap.R
import no.nordicsemi.android.wifi.provisioner.ui.ClickableDataItem

/**
 * Created by Roshan Rajaratnam on 19/02/2024.
 */

@Composable
fun WifiDeviceNotSelected(onClick: () -> Unit) {
    ClickableDataItem(
        imageVector = Icons.Outlined.Wifi,
        title = "Not selected",
        isEditable = false,
        description = "Please setup the Wi-Fi network",
        onClick = onClick,
        buttonText = stringResource(id = R.string.change_wifi)
    )
}
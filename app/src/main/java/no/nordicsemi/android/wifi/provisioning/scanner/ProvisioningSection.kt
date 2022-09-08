package no.nordicsemi.android.wifi.provisioning.scanner

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.wifi.provisioning.R
import no.nordicsemi.android.common.theme.view.RssiIcon

@Composable
internal fun ProvisioningSection(data: ProvisioningData) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = stringResource(id = R.string.version, data.version),
            style = MaterialTheme.typography.labelMedium
        )

        Spacer(modifier = Modifier.size(8.dp))

        if (data.isConnected) {
            RssiIcon(rssi = data.rssi)
        } else if (data.isProvisioned) {
            Icon(
                painter = painterResource(id = R.drawable.ic_wifi_error),
                contentDescription = null,
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.ic_no_wifi),
                contentDescription = null,
            )
        }
    }
}

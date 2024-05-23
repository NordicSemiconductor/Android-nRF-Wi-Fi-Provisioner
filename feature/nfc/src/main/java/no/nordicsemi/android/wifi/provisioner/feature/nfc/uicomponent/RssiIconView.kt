package no.nordicsemi.android.wifi.provisioner.feature.nfc.uicomponent

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material.icons.filled.NetworkWifi1Bar
import androidx.compose.material.icons.filled.NetworkWifi2Bar
import androidx.compose.material.icons.filled.NetworkWifi3Bar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.common.theme.NordicTheme
import no.nordicsemi.android.common.theme.R
import no.nordicsemi.android.common.theme.nordicBlue

// Constants used for different signal strengths
private const val FAIR_RSSI = -70
private const val GOOD_RSSI = -60
private const val EXCELLENT_RSSI = -50

/**
 * A function to get the WiFi icon based on the RSSI value.
 * The icon is selected based on the following criteria:
 * - Weak signal: RSSI < -70 dBm
 * - Fair signal: -70 dBm <= RSSI < -60 dBm
 * - Good signal: -60 dBm <= RSSI < -50 dBm
 * - Excellent signal: RSSI >= -50 dBm
 *
 * Selection criteria was inspired by [this](https://www.netspotapp.com/wifi-signal-strength/what-is-rssi-level.html) article.
 *
 * @param rssi The RSSI value.
 */
internal fun getWiFiIcon(rssi: Int): ImageVector {
    return when (rssi) {
        in Int.MIN_VALUE..FAIR_RSSI -> Icons.Default.NetworkWifi3Bar // Weak signal
        in FAIR_RSSI..GOOD_RSSI -> Icons.Default.NetworkWifi2Bar // Fair signal
        in GOOD_RSSI..EXCELLENT_RSSI -> Icons.Default.NetworkWifi1Bar // Good signal
        else -> Icons.Default.NetworkWifi// Excellent signal
    }
}

/**
 * A composable function to display the RSSI icon and value.
 *
 * @param rssi The RSSI value.
 */
@Composable
internal fun RssiIconView(rssi: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            imageVector = getWiFiIcon(rssi),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.nordicBlue)
        )
        Text(
            text = stringResource(id = R.string.dbm, rssi),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Preview
@Composable
private fun RssiIconViewPreview() {
    NordicTheme {
        RssiIconView(-50)
    }
}

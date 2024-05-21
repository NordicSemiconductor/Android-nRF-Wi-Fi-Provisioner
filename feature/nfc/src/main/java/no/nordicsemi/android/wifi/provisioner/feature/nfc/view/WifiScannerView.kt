package no.nordicsemi.android.wifi.provisioner.feature.nfc.view

import android.net.wifi.ScanResult
import android.net.wifi.WifiSsid
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.wifi.provisioner.feature.nfc.R
import no.nordicsemi.android.wifi.provisioner.feature.nfc.data.getScanResultSecurity
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.NfcProvisioningViewEvent
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.OnNetworkSelectedEvent
import no.nordicsemi.android.wifi.provisioner.nfc.domain.Error
import no.nordicsemi.android.wifi.provisioner.nfc.domain.Loading
import no.nordicsemi.android.wifi.provisioner.nfc.domain.NetworkState
import no.nordicsemi.android.wifi.provisioner.nfc.domain.Success

/**
 * A composable function to display the list of available networks.
 *
 * @param scanningState The state of the network scanning.
 * @param onEvent The event callback.
 */
@Composable
internal fun WifiScannerView(
    scanningState: NetworkState<List<ScanResult>>,
    onEvent: (NfcProvisioningViewEvent) -> Unit
) {
    // Show the scanning screen.
    when (scanningState) {
        is Error -> {
            // Show the error message.
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(id = R.string.error_while_scanning))
                    Text(text = scanningState.t.message ?: "Unknown error occurred.")
                }
            }
        }

        is Loading -> {
            // Show the loading indicator.
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        is Success -> {
            // Show the list of available networks.
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                WifiList(scanningState.data, onEvent)
            }
        }
    }
}

/**
 * A composable function to display the list of available networks.
 *
 * @param networks The list of available networks.
 * @param onEvent The event callback.
 */
@Composable
internal fun WifiList(
    networks: List<ScanResult>,
    onEvent: (NfcProvisioningViewEvent) -> Unit
) {
    networks.forEach { network ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { onEvent(OnNetworkSelectedEvent(network)) },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RssiIconView(network.level)
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Display the SSID of the access point
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Text(
                        text = getSSid(network.wifiSsid),
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    Text(
                        text = network.SSID,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                // Display the address of the access point.
                Text(
                    text = network.BSSID.uppercase(),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.alpha(0.7f)
                )
                // Display the security type of the access point.
                Text(
                    text = getScanResultSecurity(network),
                    modifier = Modifier.alpha(0.7f)
                )
            }
        }
        HorizontalDivider()
    }
}

/**
 * Returns the SSID of the network from given [WifiSsid].
 */
fun getSSid(wifiSsid: WifiSsid?): String {
    return wifiSsid.toString().replace("\"", "")
}

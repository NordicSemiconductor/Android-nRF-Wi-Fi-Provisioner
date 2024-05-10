package no.nordicsemi.android.wifi.provisioner.feature.nfc.view

import android.net.wifi.ScanResult
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viemodel.NfcProvisioningViewEvent
import no.nordicsemi.android.wifi.provisioner.nfc.domain.Error
import no.nordicsemi.android.wifi.provisioner.nfc.domain.Loading
import no.nordicsemi.android.wifi.provisioner.nfc.domain.NetworkState
import no.nordicsemi.android.wifi.provisioner.nfc.domain.Success

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
                    Text(text = "Error occurred while scanning for networks.")
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                scanningState.data.forEach { network ->
                    // Display the network.
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { /*TODO*/ }) {
                        // Display the network details.
                        Column(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(
                                text = network.SSID,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Row {
                                // Display the network name.
                                // Display the network signal strength.
                                Text(
                                    text = "Signal Strength: ${network.level} dBm",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = network.BSSID,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            // Display the network capabilities.
                            Text(text = "Capabilities")
                            Text(
                                text = network.capabilities,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}
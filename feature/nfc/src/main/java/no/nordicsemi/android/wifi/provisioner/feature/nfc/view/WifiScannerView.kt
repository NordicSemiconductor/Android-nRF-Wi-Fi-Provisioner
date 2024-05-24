package no.nordicsemi.android.wifi.provisioner.feature.nfc.view

import android.net.wifi.ScanResult
import android.net.wifi.WifiSsid
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.nordicsemi.android.common.theme.view.NordicAppBar
import no.nordicsemi.android.wifi.provisioner.feature.nfc.R
import no.nordicsemi.android.wifi.provisioner.feature.nfc.data.OPEN
import no.nordicsemi.android.wifi.provisioner.feature.nfc.data.getScanResultSecurity
import no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.RequireLocationForWifi
import no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.RequireWifi
import no.nordicsemi.android.wifi.provisioner.feature.nfc.uicomponent.PasswordDialog
import no.nordicsemi.android.wifi.provisioner.feature.nfc.uicomponent.RssiIconView
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.OnNavigateUpClickEvent
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.OnNetworkSelectEvent
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.OnPasswordCancelEvent
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.OnPasswordSetEvent
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.WifiScannerViewEvent
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.WifiScannerViewModel
import no.nordicsemi.android.wifi.provisioner.nfc.domain.Error
import no.nordicsemi.android.wifi.provisioner.nfc.domain.Loading
import no.nordicsemi.android.wifi.provisioner.nfc.domain.Success
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiData

/**
 * A composable function to display the list of available networks.
 */
@RequiresApi(Build.VERSION_CODES.M)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WifiScannerView() {
    val wifiScannerViewModel = hiltViewModel<WifiScannerViewModel>()
    val onEvent: (WifiScannerViewEvent) -> Unit = { wifiScannerViewModel.onEvent(it) }
    val wifiScannerViewState by wifiScannerViewModel.viewState.collectAsStateWithLifecycle()

    // Handle the back press.
    BackHandler {
        onEvent(OnNavigateUpClickEvent)
    }
    // Show the scanning screen.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 56.dp)
    ) {
        NordicAppBar(
            text = stringResource(id = R.string.wifi_provision_over_nfc_appbar),
            showBackButton = true,
            onNavigationButtonClick = { onEvent(OnNavigateUpClickEvent) }
        )

        RequireWifi {
            RequireLocationForWifi {
                when (val scanningState = wifiScannerViewState.networks) {
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
                when (val selectedNetwork = wifiScannerViewState.selectedNetwork) {
                    null -> {
                        // Do nothing
                    }

                    else -> {
                        // Show the password dialog
                        PasswordDialog(
                            scanResult = selectedNetwork,
                            onCancelClick = {
                                // Dismiss the dialog
                                // Set the selected network to null
                                onEvent(OnPasswordCancelEvent)
                            }) {
                            onEvent(OnPasswordSetEvent(it))
                        }
                    }
                }
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
    onEvent: (WifiScannerViewEvent) -> Unit
) {
    networks.forEach { network ->
        val securityType = getScanResultSecurity(network)
        val isProtected = securityType != OPEN

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {
                    if (isProtected) {
                        // Show the password dialog
                        onEvent(OnNetworkSelectEvent(network))
                    } else {
                        // Password dialog is not required for open networks.
                        val wifiData = WifiData(
                            ssid = network.SSID,
                            password = "", // Todo: Verify if its empty password or null.
                            authType = OPEN,
                        )
                        onEvent(OnPasswordSetEvent(wifiData))
                    }
                },
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
                    text = securityType,
                    modifier = Modifier.alpha(0.7f)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            // TODO: Utilize the common code from no.nordicsemi.android.wifi.provisioner.ui.mapping
            Icon(
                imageVector = if (isProtected) Icons.Outlined.Lock else Icons.Outlined.Wifi,
                contentDescription = null,
            )
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

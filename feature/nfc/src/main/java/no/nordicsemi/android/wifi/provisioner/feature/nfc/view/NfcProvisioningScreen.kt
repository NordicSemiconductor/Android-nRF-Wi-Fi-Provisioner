package no.nordicsemi.android.wifi.provisioner.feature.nfc.view

import android.app.Activity
import android.net.wifi.ScanResult
import android.nfc.NdefMessage
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiFind
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.nordicsemi.android.common.permissions.nfc.RequireNfc
import no.nordicsemi.android.common.theme.view.NordicAppBar
import no.nordicsemi.android.wifi.provisioner.feature.nfc.R
import no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.RequireLocationForWifi
import no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.RequireWifi
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.AskForPassword
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.Home
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.NfcManagerViewModel
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.NfcProvisioningViewEvent
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.NfcProvisioningViewModel
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.OnBackClickEvent
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.OnPasswordConfirmedEvent
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.OnScanClickEvent
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.Provisioning
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.Scan

@RequiresApi(Build.VERSION_CODES.M)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NfcProvisioningScreen() {
    val viewModel: NfcProvisioningViewModel = hiltViewModel()
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val onEvent: (NfcProvisioningViewEvent) -> Unit = { viewModel.onEvent(it) }
    var openPasswordDialog by remember { mutableStateOf(false) }
    var scanResult: ScanResult? = null

    RequireWifi {
        RequireLocationForWifi {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 56.dp)
            ) {
                NordicAppBar(
                    text = stringResource(id = R.string.wifi_provision_over_nfc_appbar),
                    showBackButton = true,
                    onNavigationButtonClick = { onEvent(OnBackClickEvent) }
                )
                // Show Content.
                when (val s = viewState.view) {
                    Home -> {
                        // Show the home screen.
                        NfcProvisioningHomeView(onEvent)
                    }

                    is Scan -> {
                        // Show the scanning screen.
                        WifiScannerView(s.networkState, onEvent)
                    }

                    is AskForPassword -> {
                        // Show the screen to ask for the password.
                        openPasswordDialog = true
                        scanResult = s.network
                    }

                    Provisioning -> {
                        // TODO: Show the provisioning screen.
                        // Publish the NDEF message to the tag.
                        ProvisioningView(
                            ndefMessage = viewModel.ndefMessage!!
                        )
                    }
                }
                if (openPasswordDialog) {
                    // Open a dialog to enter the WiFi credentials manually.
                    scanResult?.let { result ->
                        PasswordDialog(
                            scanResult = result,
                            onCancelClick = { openPasswordDialog = false },
                            onConfirmClick = { wifiData ->
                                openPasswordDialog = false
                                // Go to the next screen.
                                onEvent(OnPasswordConfirmedEvent(wifiData))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun ProvisioningView(
    ndefMessage: NdefMessage
) {
    val nfcManagerVm: NfcManagerViewModel = hiltViewModel()
    val context = LocalContext.current

    RequireNfc {
        DisposableEffect(key1 = nfcManagerVm) {
            nfcManagerVm.onScan(context as Activity, ndefMessage)
            onDispose { nfcManagerVm.onPause(context) }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(
                text = "Hold the NFC tag near the device to provision the WiFi network.",
                textAlign = TextAlign.Center,

                )
        }
    }
}

@Composable
private fun NfcProvisioningHomeView(
    onEvent: (NfcProvisioningViewEvent) -> Unit
) {
    var isAddWifiManuallyDialogOpen by remember { mutableStateOf(false) }
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.Companion
            .padding(8.dp)
    ) {
        // Show an option to enter the WiFi credentials manually.
        OutlinedCard(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                    isAddWifiManuallyDialogOpen = true
                }

        ) {
            NfcRecordOutlinedCardItem(
                headline = stringResource(id = R.string.enter_wifi_credentials),
                description = {
                    Text(
                        text = stringResource(id = R.string.enter_wifi_credentials_des),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                icon = Icons.Default.Wifi,
            ) {
                Spacer(Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            isAddWifiManuallyDialogOpen = true
                        }
                        .padding(8.dp)
                )
            }
        }

        // Show an option to search for a WiFi network.
        OutlinedCard(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                    onEvent(OnScanClickEvent)
                }
        ) {
            NfcRecordOutlinedCardItem(
                headline = stringResource(id = R.string.search_for_wifi_networks),
                description = {
                    Text(
                        text = stringResource(id = R.string.search_for_wifi_networks_des),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                icon = Icons.Default.WifiFind,
            ) {
                Spacer(Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onEvent(OnScanClickEvent) }
                        .padding(8.dp)
                )
            }
        }

        if (isAddWifiManuallyDialogOpen) {
            // Open a dialog to enter the WiFi credentials manually.
            AddWifiManuallyDialog(
                onCancelClick = {
                    isAddWifiManuallyDialogOpen = false
                },
                onConfirmClick = {
                    isAddWifiManuallyDialogOpen = false
                    onEvent(OnPasswordConfirmedEvent(it))
                },
            )
        }
    }
}

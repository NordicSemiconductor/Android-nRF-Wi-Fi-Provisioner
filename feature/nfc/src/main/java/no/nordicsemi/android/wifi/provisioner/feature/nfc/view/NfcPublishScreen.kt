package no.nordicsemi.android.wifi.provisioner.feature.nfc.view

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.nordicsemi.android.common.permissions.nfc.RequireNfc
import no.nordicsemi.android.common.theme.view.NordicAppBar
import no.nordicsemi.android.common.theme.view.ProgressItem
import no.nordicsemi.android.common.theme.view.ProgressItemStatus
import no.nordicsemi.android.common.theme.view.WizardStepComponent
import no.nordicsemi.android.common.theme.view.WizardStepState
import no.nordicsemi.android.wifi.provisioner.feature.nfc.R
import no.nordicsemi.android.wifi.provisioner.feature.nfc.uicomponent.NfcPasswordRow
import no.nordicsemi.android.wifi.provisioner.feature.nfc.uicomponent.NfcTextRow
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.NfcManagerViewModel
import no.nordicsemi.android.wifi.provisioner.nfc.Error
import no.nordicsemi.android.wifi.provisioner.nfc.Loading
import no.nordicsemi.android.wifi.provisioner.nfc.Success

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NfcPublishScreen() {
    val nfcManagerVm: NfcManagerViewModel = hiltViewModel()
    val context = LocalContext.current
    val nfcScanEvent by nfcManagerVm.nfcScanEvent.collectAsStateWithLifecycle()
    val ndefMessage = nfcManagerVm.ndefMessage
    val wifiData = nfcManagerVm.wifiData
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle back navigation.
    BackHandler {
        nfcManagerVm.onBackNavigation()
    }
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            NordicAppBar(
                text = stringResource(id = R.string.ndef_publish_appbar),
                showBackButton = true,
                onNavigationButtonClick = { nfcManagerVm.onBackNavigation() }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            RequireNfc {
                DisposableEffect(key1 = nfcManagerVm) {
                    nfcManagerVm.onScan(context as Activity)
                    onDispose { nfcManagerVm.onPause(context) }
                }
                OutlinedCard(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 16.dp, horizontal = 16.dp)
                    // Leave more space for the navigation bar.
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Show Ndef Record information.
                        WizardStepComponent(
                            icon = Icons.Default.Wifi,
                            title = stringResource(id = R.string.wifi_record),
                            state = WizardStepState.COMPLETED
                        ) {
                            NfcTextRow(
                                title = stringResource(id = R.string.ssid_title),
                                text = wifiData.ssid
                            )
                            if (wifiData.password.isNotEmpty()) {
                                NfcPasswordRow(title = stringResource(id = R.string.password_title))
                            }
                            if (wifiData.authType.isNotEmpty()) {
                                NfcTextRow(
                                    title = stringResource(id = R.string.authentication_title),
                                    text = wifiData.authType
                                )
                            }
                            if (wifiData.encryptionMode.isNotEmpty()) {
                                NfcTextRow(
                                    title = stringResource(id = R.string.encryption_title),
                                    text = wifiData.encryptionMode
                                )
                            }
                            NfcTextRow(
                                title = stringResource(id = R.string.message_size),
                                text = stringResource(
                                    id = R.string.message_size_in_bytes,
                                    ndefMessage.byteArrayLength
                                )
                            )
                        }

                        WizardStepComponent(
                            icon = Icons.Default.Edit,
                            title = stringResource(id = R.string.discover_tag_title),
                            state = WizardStepState.CURRENT,
                            showVerticalDivider = false,
                        ) {
                            Column {
                                when (val e = nfcScanEvent) {
                                    is Error -> {
                                        // Show the error message.
                                        ProgressItem(
                                            text = stringResource(id = R.string.write_failed),
                                            status = ProgressItemStatus.ERROR,
                                            iconRightPadding = 24.dp,
                                        )
                                        Text(
                                            text = if (e.message.length > 35) e.message.slice(0..35) else e.message,
                                            modifier = Modifier
                                                .alpha(0.7f)
                                                .padding(start = 48.dp),
                                            style = MaterialTheme.typography.bodySmall,
                                        )
                                    }

                                    Loading -> {
                                        // Show the loading indicator.
                                        ProgressItem(
                                            text = stringResource(id = R.string.discovering_tag),
                                            status = ProgressItemStatus.WORKING,
                                            iconRightPadding = 24.dp,
                                        )
                                    }

                                    Success -> {
                                        ProgressItem(
                                            text = stringResource(id = R.string.write_success),
                                            status = ProgressItemStatus.SUCCESS,
                                            iconRightPadding = 24.dp,
                                        )
                                        Text(
                                            text = stringResource(id = R.string.success_des),
                                            modifier = Modifier
                                                .alpha(0.7f)
                                                .padding(start = 48.dp),
                                            style = MaterialTheme.typography.bodySmall,
                                        )
                                    }

                                    null -> {
                                        ProgressItem(
                                            text = stringResource(id = R.string.tap_nfc_tag),
                                            status = ProgressItemStatus.WORKING,
                                            iconRightPadding = 24.dp,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

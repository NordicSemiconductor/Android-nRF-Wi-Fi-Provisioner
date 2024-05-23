package no.nordicsemi.android.wifi.provisioner.feature.nfc.view

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.nordicsemi.android.common.permissions.nfc.RequireNfc
import no.nordicsemi.android.common.theme.view.NordicAppBar
import no.nordicsemi.android.wifi.provisioner.feature.nfc.R
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.NfcManagerViewModel
import no.nordicsemi.android.wifi.provisioner.nfc.Error
import no.nordicsemi.android.wifi.provisioner.nfc.Loading
import no.nordicsemi.android.wifi.provisioner.nfc.Success

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NfcScreen() {
    val nfcManagerVm: NfcManagerViewModel = hiltViewModel()
    val context = LocalContext.current
    val nfcScanEvent by nfcManagerVm.nfcScanEvent.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 56.dp)
    ) {
        NordicAppBar(
            text = stringResource(id = R.string.wifi_provision_over_nfc_appbar),
            showBackButton = true,
            onNavigationButtonClick = { nfcManagerVm.onBackNavigation() }
        )
        RequireNfc {
            DisposableEffect(key1 = nfcManagerVm) {
                nfcManagerVm.onScan(context as Activity)
                onDispose { nfcManagerVm.onPause(context) }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                modifier = Modifier.fillMaxSize()
            ) {
                when (val e = nfcScanEvent) {
                    is Error -> {
                        Text(text = "Nfc scan error")
                        Text(text = "Please try again")
                        Text(text = e.message)
                    }

                    Loading -> {
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

                    Success -> {
                        Text(text = "Nfc scan success")
                        Text(
                            text = "It might take a few seconds to connect to wifi once the NFC tag is detected.",
                            textAlign = TextAlign.Center,
                        )
                    }

                    null -> {
                        Image(
                            painter = painterResource(id = R.drawable.nfc_scanning),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            colorFilter = ColorFilter.tint(
                                MaterialTheme.colorScheme.onBackground.copy(
                                    alpha = 0.6f
                                )
                            ),
                        )
                        Text(
                            text = "Hold the device near to the NFC tag to provision the WiFi network.",
                            textAlign = TextAlign.Center,
                        )
                    }
                }

            }
        }
    }
}

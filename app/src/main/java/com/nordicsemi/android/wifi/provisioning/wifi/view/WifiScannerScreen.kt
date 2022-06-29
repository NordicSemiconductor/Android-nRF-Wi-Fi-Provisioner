package com.nordicsemi.android.wifi.provisioning.wifi.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nordicsemi.android.wifi.provisioning.R
import com.nordicsemi.android.wifi.provisioning.home.view.components.BackIconAppBar
import com.nordicsemi.android.wifi.provisioning.home.view.components.CloseIconAppBar
import com.nordicsemi.android.wifi.provisioning.home.view.toIcon
import com.nordicsemi.android.wifi.provisioning.wifi.viewmodel.WifiScannerViewModel
import com.nordicsemi.wifi.provisioner.library.domain.AuthModeDomain
import com.nordicsemi.wifi.provisioner.library.domain.BandDomain
import com.nordicsemi.wifi.provisioner.library.domain.ScanRecordDomain
import com.nordicsemi.wifi.provisioner.library.domain.WifiInfoDomain

@Composable
internal fun WifiScannerScreen() {
    val viewModel = hiltViewModel<WifiScannerViewModel>()
    val viewEntity = viewModel.state.collectAsState().value
    val onEvent: (WifiScannerViewEvent) -> Unit = { viewModel.onEvent(it) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        BackIconAppBar(stringResource(id = R.string.wifi_title)) {
            viewModel.onEvent(NavigateUpEvent)
        }

        if (viewEntity.isLoading) {
            LoadingItem()
        } else if (viewEntity.isError) {
            ErrorItem()
        } else {
            WifiList(viewEntity, onEvent)
        }
    }
}

@Composable
private fun LoadingItem() {
    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
}

@Composable
private fun ErrorItem() {
    Text(
        text = stringResource(id = R.string.error_scanning), modifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun WifiList(viewEntity: WifiScannerViewEntity, onEvent: (WifiScannerViewEvent) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        viewEntity.items.forEach {
            item { WifiItem(scanRecord = it, onEvent = onEvent) }
        }
    }
}

@Composable
private fun WifiItem(scanRecord: ScanRecordDomain, onEvent: (WifiScannerViewEvent) -> Unit) {
    val wifi = scanRecord.wifiInfo

    Row(modifier = Modifier.fillMaxWidth().clickable { onEvent(WifiSelectedEvent(scanRecord)) }) {
        Icon(
            painter = painterResource(id = wifi.authModeDomain.toIcon()),
            contentDescription = stringResource(id = R.string.cd_wifi_icon),
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.secondary, shape = CircleShape
                )
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.size(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = wifi.ssid, style = MaterialTheme.typography.labelLarge
            )

            Text(
                text = stringResource(id = R.string.bssid, wifi.bssid),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview
@Composable
private fun WifiItemPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        WifiItem(
            ScanRecordDomain(
                23,
                WifiInfoDomain("Dobre wifi", "00:00:5e:00:53:af", BandDomain.BAND_2_4_GH, 2, AuthModeDomain.OPEN),
            )
        ) { }
    }
}

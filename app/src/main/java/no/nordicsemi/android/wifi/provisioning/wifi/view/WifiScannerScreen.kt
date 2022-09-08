package no.nordicsemi.android.wifi.provisioning.wifi.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import no.nordicsemi.android.wifi.provisioning.R
import no.nordicsemi.android.wifi.provisioning.home.view.components.BackIconAppBar
import no.nordicsemi.android.wifi.provisioning.home.view.components.ErrorDataItem
import no.nordicsemi.android.wifi.provisioning.home.view.toDisplayString
import no.nordicsemi.android.wifi.provisioning.home.view.toIcon
import no.nordicsemi.android.wifi.provisioning.wifi.viewmodel.WifiScannerViewModel
import no.nordicsemi.wifi.provisioner.library.domain.ScanRecordDomain
import no.nordicsemi.android.common.theme.view.getWiFiRes

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
        } else if (viewEntity.error != null) {
            ErrorItem(viewEntity.error)
        } else {
            WifiList(viewEntity, onEvent)
        }
    }
}

@Composable
private fun LoadingItem() {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        repeat(6) {
            item { WifiLoadingItem() }
        }
    }
}

@Composable
private fun ErrorItem(error: Throwable) {
    Box(
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        ErrorDataItem(
            iconRes = R.drawable.ic_error,
            title = stringResource(id = R.string.wifi_scanning),
            error = error
        )
    }
}

@Composable
private fun WifiList(viewEntity: WifiScannerViewEntity, onEvent: (WifiScannerViewEvent) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        viewEntity.items.forEach {
            item { WifiItem(records = it, onEvent = onEvent) }
        }
    }
}

@Composable
private fun WifiItem(records: ScanRecordsSameSsid, onEvent: (WifiScannerViewEvent) -> Unit) {
    val selectedScanRecord = remember { mutableStateOf<ScanRecordDomain?>(null) }
    val scanRecord = selectedScanRecord.value ?: records.items.first()
    val wifi = scanRecord.wifiInfo

    val showSelectChannelDialog = rememberSaveable { mutableStateOf(false) }

    if (showSelectChannelDialog.value) {
        SelectChannelDialog(
            records = records,
            onDismiss = { showSelectChannelDialog.value = false }
        ) {
            selectedScanRecord.value = it
            showSelectChannelDialog.value = false
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onEvent(WifiSelectedEvent(scanRecord)) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = wifi.authModeDomain.toIcon()),
            contentDescription = stringResource(id = R.string.cd_wifi_icon),
            tint = MaterialTheme.colorScheme.onPrimary,
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

            if (wifi.bssid.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.bssid, wifi.bssid),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (wifi.band != null) {
                Text(
                    text = stringResource(
                        id = R.string.band_and_channel,
                        wifi.band!!.toDisplayString(),
                        wifi.channel.toString()
                    ),
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                Text(
                    text = stringResource(id = R.string.channel, wifi.channel.toString()),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        scanRecord.rssi?.let {
            if (records.items.size <= 1) {
                Icon(
                    getWiFiRes(it),
                    contentDescription = "",
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            } else {
                Row(modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { showSelectChannelDialog.value = true }
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.onSurface,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(getWiFiRes(it), contentDescription = "")

                    Icon(Icons.Default.ArrowDropDown, contentDescription = "")
                }
            }
        }
    }
}

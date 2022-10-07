package no.nordicsemi.android.wifi.provisioning.wifi.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.common.theme.view.RssiIcon
import no.nordicsemi.android.wifi.provisioning.R
import no.nordicsemi.android.wifi.provisioning.home.view.toDisplayString
import no.nordicsemi.wifi.provisioner.library.domain.ScanRecordDomain

@Composable
internal fun SelectChannelDialog(
    records: ScanRecordsForSsid,
    onDismiss: () -> Unit,
    onRecordSelected: (ScanRecordDomain?) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = stringResource(id = R.string.select_wifi))
        },
        text = {
            LazyColumn {
                records.items.forEach {
                    item { ChannelListItem(record = it) { onRecordSelected(it) } }
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onRecordSelected(null) }
            ) {
                Text(stringResource(id = R.string.clear))
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onDismiss() }
            ) {
                Text(stringResource(id = R.string.dismiss))
            }
        }
    )
}

@Composable
private fun ChannelListItem(
    record: ScanRecordDomain,
    onRecordSelected: (ScanRecordDomain) -> Unit
) {
    val wifi = record.wifiInfo

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onRecordSelected(record) }
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(id = R.string.channel, wifi.channel.toString()),
                style = MaterialTheme.typography.labelLarge
            )

            if (wifi.macAddress.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.bssid, wifi.macAddress),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            wifi.band?.toDisplayString()?.let {
                Text(
                    text = stringResource(id = R.string.band, it),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        record.rssi?.let { RssiIcon(rssi = it) }
    }
}

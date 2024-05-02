package no.nordicsemi.android.wifi.provisioner.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.wifi.provisioner.ui.view.section.SectionTitle


@Composable
fun ProvisionOverBle(onClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .padding(all = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(all = 16.dp)) {
            SectionTitle(text = stringResource(R.string.provision_over_ble))
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = stringResource(R.string.provision_over_ble_rationale),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ProvisionOverWifi(onClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .padding(all = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(all = 16.dp)) {
            SectionTitle(text = stringResource(R.string.provision_over_wi_fi))
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = stringResource(R.string.provision_over_wifi_rationale),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
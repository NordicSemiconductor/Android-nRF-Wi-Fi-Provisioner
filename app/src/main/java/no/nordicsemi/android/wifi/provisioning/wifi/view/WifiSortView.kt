package no.nordicsemi.android.wifi.provisioning.wifi.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material.icons.filled.NetworkWifi3Bar
import androidx.compose.material.icons.filled.SignalWifi4Bar
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.wifi.provisioning.R
import no.nordicsemi.android.wifi.provisioning.wifi.viewmodel.WifiSortOption
import no.nordicsemi.android.common.theme.R as themeR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WifiSortView(
    sortOption: WifiSortOption,
    onChanged: (WifiSortOption) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = themeR.color.appBarColor))
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.sorting_hint),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimary,
        )

        Spacer(modifier = Modifier.size(16.dp))

        val isRssiSortSelected = sortOption == WifiSortOption.RSSI
        ElevatedFilterChip(
            selected = isRssiSortSelected,
            onClick = { onChanged(WifiSortOption.RSSI) },
            label = { Text(text = stringResource(id = R.string.sorting_rssi),) },
            leadingIcon = {
                if (isRssiSortSelected) {
                    Icon(Icons.Default.Done, contentDescription = "")
                } else {
                    Icon(Icons.Default.NetworkWifi, contentDescription = "")
                }
            },
        )

        Spacer(modifier = Modifier.size(16.dp))

        val isNameSortSelected = sortOption == WifiSortOption.NAME
        ElevatedFilterChip(
            selected = isNameSortSelected,
            onClick = { onChanged(WifiSortOption.NAME) },
            label = { Text(text = stringResource(id = R.string.sorting_name),) },
            leadingIcon = {
                if (isNameSortSelected) {
                    Icon(Icons.Default.Done, contentDescription = "")
                } else {
                    Icon(Icons.Default.Label, contentDescription = "")
                }
            },
        )
    }
}

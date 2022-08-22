package com.nordicsemi.android.wifi.provisioning.scanner.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.common.theme.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FilterView(allDevices: Boolean, onChange: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.appBarColor))
            .padding(start = 56.dp)
    ) {
        ElevatedFilterChip(
            selected = !allDevices,
            onClick = { onChange() },
            label = { Text(text = stringResource(id = com.nordicsemi.android.wifi.provisioning.R.string.unprovisioned),) },
            modifier = Modifier.padding(end = 8.dp),
            leadingIcon = {
                if (!allDevices) {
                    Icon(Icons.Default.Done, contentDescription = "")
                } else {
                    Icon(Icons.Default.Filter, contentDescription = "")
                }
            },
        )
    }
}

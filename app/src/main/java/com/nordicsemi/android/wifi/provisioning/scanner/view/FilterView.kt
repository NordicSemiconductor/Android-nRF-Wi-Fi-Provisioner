package com.nordicsemi.android.wifi.provisioning.scanner.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FilterView(allDevices: Boolean, onChange: () -> Unit) {
    ElevatedFilterChip(
        selected = !allDevices,
        onClick = { onChange() },
        label = { Text(text = stringResource(id = com.nordicsemi.android.wifi.provisioning.R.string.unprovisioned)) },
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

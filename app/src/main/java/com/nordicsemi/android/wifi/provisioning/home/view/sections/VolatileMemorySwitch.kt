package com.nordicsemi.android.wifi.provisioning.home.view.sections

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nordicsemi.android.wifi.provisioning.R
import com.nordicsemi.android.wifi.provisioning.home.view.HomeScreenViewEvent
import com.nordicsemi.android.wifi.provisioning.home.view.OnVolatileMemoryChangedEvent

@Composable
fun VolatileMemorySwitch(
    volatileMemory: Boolean,
    enabled: Boolean,
    onEvent: (HomeScreenViewEvent) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_storage),
            contentDescription = stringResource(id = R.string.cd_data_item_icon),
        )

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            text = stringResource(id = R.string.persistent_storage),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Checkbox(
            checked = volatileMemory,
            enabled = enabled,
            onCheckedChange = { onEvent(OnVolatileMemoryChangedEvent) },
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

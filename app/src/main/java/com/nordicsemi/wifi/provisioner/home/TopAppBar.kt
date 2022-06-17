package com.nordicsemi.wifi.provisioner.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import no.nordicsemi.android.theme.R as mainR
import com.nordicsemi.wifi.provisioner.R

@Composable
fun CloseIconAppBar(text: String, onClick: () -> Unit) {
    SmallTopAppBar(
        title = { Text(text, maxLines = 2) },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            scrolledContainerColor = MaterialTheme.colorScheme.primary,
            containerColor = colorResource(id = mainR.color.appBarColor),
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        navigationIcon = {
            IconButton(onClick = { onClick() }) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(id = R.string.close_app),
                )
            }
        }
    )
}

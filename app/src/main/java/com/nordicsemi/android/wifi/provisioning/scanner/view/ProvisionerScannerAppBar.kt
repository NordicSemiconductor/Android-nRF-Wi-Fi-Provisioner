package com.nordicsemi.android.wifi.provisioning.scanner.view

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.common.ui.scanner.R
import no.nordicsemi.android.common.theme.R as themeR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProvisionerScannerAppBar(
    text: String,
    showProgress: Boolean = false,
    allDevicesFilter: Boolean = false,
    onFilterChange: () -> Unit,
    onNavigationButtonClick: () -> Unit
) {
    SmallTopAppBar(
        title = { Text(text, overflow = TextOverflow.Ellipsis, maxLines = 1) },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            scrolledContainerColor = MaterialTheme.colorScheme.primary,
            containerColor = colorResource(id = themeR.color.appBarColor),
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        navigationIcon = {
            IconButton(onClick = { onNavigationButtonClick() }) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.navigation_item_accessibility),
                    tint = MaterialTheme.colorScheme.onSecondary,
                )
            }
        },
        actions = {
            if (showProgress) {
                FilterView(allDevices = allDevicesFilter, onChange = onFilterChange)
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .size(30.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
    )
}

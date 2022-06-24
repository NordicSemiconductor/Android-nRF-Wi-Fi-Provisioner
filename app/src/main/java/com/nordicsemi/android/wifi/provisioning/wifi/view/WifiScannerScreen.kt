package com.nordicsemi.android.wifi.provisioning.wifi.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nordicsemi.android.wifi.provisioning.R
import com.nordicsemi.android.wifi.provisioning.home.CloseIconAppBar
import com.nordicsemi.android.wifi.provisioning.wifi.viewmodel.WifiScannerViewModel
import com.nordicsemi.wifi.provisioner.library.domain.WifiInfoDomain

@Composable
internal fun WifiScannerScreen() {
    val viewModel = hiltViewModel<WifiScannerViewModel>()
    val viewEntity = viewModel.state.collectAsState().value

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CloseIconAppBar(stringResource(id = R.string.app_name)) {
            viewModel.onEvent(NavigateUpEvent)
        }

        if (viewEntity.isLoading) {
            LoadingItem()
        } else if (viewEntity.isError) {
            ErrorItem()
        } else {
            WifiList(viewEntity)
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
        text = stringResource(id = R.string.error_scanning),
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun WifiList(viewEntity: WifiScannerViewEntity) {
    LazyColumn {
        viewEntity.items.forEach {
            item { WifiItem(wifi = it.wifiInfo) }
        }
    }
}

@Composable
private fun WifiItem(wifi: WifiInfoDomain) {
    Row {
        Icon(
            Icons.Default.Wifi, contentDescription = "",
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = CircleShape
                )
        )

        Spacer(modifier = Modifier.size(16.dp))
        
        Text(text = wifi.ssid)
    }
}

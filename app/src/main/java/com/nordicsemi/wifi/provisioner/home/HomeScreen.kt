package com.nordicsemi.wifi.provisioner.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nordicsemi.wifi.provisioner.R

enum class HomeScreenViewEvent {
    ON_SELECT_BUTTON_CLICK, FINISH
}

@Composable
fun HomeScreen() {
    val viewModel = hiltViewModel<HomeViewModel>()
    val state = viewModel.status.collectAsState()

    Column {
        CloseIconAppBar(stringResource(id = R.string.app_name)) {
            viewModel.onEvent(HomeScreenViewEvent.FINISH)
        }

        Column(modifier = Modifier.padding(16.dp)) {
            SelectDeviceSection { viewModel.onEvent(it) }

            Spacer(modifier = Modifier.size(16.dp))

            Text(text = stringResource(id = R.string.app_info))
        }
    }
}

@Composable
private fun SelectDeviceSection(onEvent: (HomeScreenViewEvent) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        FloatingActionButton(onClick = { onEvent(HomeScreenViewEvent.ON_SELECT_BUTTON_CLICK) }) {
            Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.add_device))
        }

        Spacer(modifier = Modifier.size(16.dp))

        Text(text = stringResource(id = R.string.add_device))
    }
}

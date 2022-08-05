package com.nordicsemi.android.wifi.provisioning.wifi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nordicsemi.android.wifi.provisioning.WifiScannerId
import com.nordicsemi.android.wifi.provisioning.wifi.view.NavigateUpEvent
import com.nordicsemi.android.wifi.provisioning.wifi.view.WifiScannerViewEntity
import com.nordicsemi.android.wifi.provisioning.wifi.view.WifiScannerViewEvent
import com.nordicsemi.android.wifi.provisioning.wifi.view.WifiSelectedEvent
import com.nordicsemi.wifi.provisioner.library.Error
import com.nordicsemi.wifi.provisioner.library.Loading
import com.nordicsemi.wifi.provisioner.library.ProvisionerRepository
import com.nordicsemi.wifi.provisioner.library.Success
import com.nordicsemi.wifi.provisioner.library.domain.ScanRecordDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import no.nordicsemi.android.common.navigation.AnyArgument
import no.nordicsemi.android.common.navigation.NavigationManager
import no.nordicsemi.android.common.navigation.SuccessDestinationResult
import javax.inject.Inject

@HiltViewModel
internal class WifiScannerViewModel @Inject constructor(
    private val navigationManager: NavigationManager
) : ViewModel() {

    private val repository = ProvisionerRepository.instance()

    private val _state = MutableStateFlow(WifiScannerViewEntity())
    val state = _state.asStateFlow()

    init {
        repository.startScan().onEach {
            val state = _state.value

            _state.value = when (it) {
                is Error -> state.copy(isLoading = false, error = it.error)
                is Loading -> state.copy(isLoading = true)
                is Success -> state.copy(isLoading = false, error = null, items = state.items + it.data)
            }
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: WifiScannerViewEvent) {
        when (event) {
            NavigateUpEvent -> navigateUp()
            is WifiSelectedEvent -> navigateUp(event.scanRecord)
        }
    }

    private suspend fun stopScanning() {
        try {
            repository.stopScanBlocking()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun navigateUp() {
        viewModelScope.launch {
            stopScanning()
            navigationManager.navigateUp()
        }
    }

    private fun navigateUp(scanRecord: ScanRecordDomain) {
        viewModelScope.launch {
            stopScanning()
            navigationManager.navigateUp(
                WifiScannerId,
                SuccessDestinationResult(WifiScannerId, AnyArgument(scanRecord))
            )
        }
    }
}

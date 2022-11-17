package no.nordicsemi.android.wifi.provisioning.wifi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import no.nordicsemi.android.common.navigation.Navigator
import no.nordicsemi.android.wifi.provisioning.WifiScannerId
import no.nordicsemi.android.wifi.provisioning.repository.ProvisionerResourceRepository
import no.nordicsemi.android.wifi.provisioning.wifi.view.NavigateUpEvent
import no.nordicsemi.android.wifi.provisioning.wifi.view.OnSortOptionSelected
import no.nordicsemi.android.wifi.provisioning.wifi.view.WifiData
import no.nordicsemi.android.wifi.provisioning.wifi.view.WifiScannerViewEntity
import no.nordicsemi.android.wifi.provisioning.wifi.view.WifiScannerViewEvent
import no.nordicsemi.android.wifi.provisioning.wifi.view.WifiSelectedEvent
import no.nordicsemi.wifi.provisioner.library.Error
import no.nordicsemi.wifi.provisioner.library.Loading
import no.nordicsemi.wifi.provisioner.library.Success
import javax.inject.Inject

@HiltViewModel
internal class WifiScannerViewModel @Inject constructor(
    private val navigationManager: Navigator,
    private val wifiAggregator: WifiAggregator,
    private val repository: ProvisionerResourceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WifiScannerViewEntity())
    val state = _state.asStateFlow()

    init {
        repository.startScan().onEach {
            val state = _state.value

            _state.value = when (it) {
                is Error -> state.copy(isLoading = false, error = it.error)
                is Loading -> state.copy(isLoading = true)
                is Success -> state.copy(isLoading = false, error = null, items = wifiAggregator.addWifi(it.data))
            }
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: WifiScannerViewEvent) {
        when (event) {
            NavigateUpEvent -> navigateUp()
            is WifiSelectedEvent -> navigateUp(event.wifiData)
            is OnSortOptionSelected -> onSortOptionSelected(event.sortOption)
        }
    }

    private fun onSortOptionSelected(sortOption: WifiSortOption) {
        _state.value = _state.value.copy(sortOption = sortOption)
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

    private fun navigateUp(wifiData: WifiData) {
        viewModelScope.launch {
            stopScanning()
            navigationManager.navigateUpWithResult(WifiScannerId, wifiData)
        }
    }
}

package com.nordicsemi.android.wifi.provisioning.scanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nordicsemi.android.wifi.provisioning.scanner.provisioningData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import no.nordicsemi.android.common.ui.scanner.repository.ScannerRepository
import no.nordicsemi.android.common.ui.scanner.repository.ScanningState
import javax.inject.Inject

@HiltViewModel
internal class ProvisionerViewModel @Inject constructor(
    private val scannerRepository: ScannerRepository,
) : ViewModel() {

    //By default only not connected
    private val _allDevices = MutableStateFlow(true)
    val allDevices = _allDevices.asStateFlow()

    val devices = allDevices.combine(scannerRepository.getScannerState()) { onlyUnprovisioned, result ->
            when (result) {
                is ScanningState.DevicesDiscovered -> result.applyFilters(onlyUnprovisioned)
                else -> result
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, ScanningState.Loading)

    private fun ScanningState.DevicesDiscovered.applyFilters(allDevices: Boolean) =
        ScanningState.DevicesDiscovered(
            devices
                .filter { it.provisioningData() != null }
                .filter {
                    val provisioningData = it.provisioningData()
                    (provisioningData?.isConnected == false).takeIf { !allDevices } ?: true
                }
        )

    fun switchFilter() {
        _allDevices.value = !allDevices.value
    }

    override fun onCleared() {
        super.onCleared()
        scannerRepository.clear()
    }
}

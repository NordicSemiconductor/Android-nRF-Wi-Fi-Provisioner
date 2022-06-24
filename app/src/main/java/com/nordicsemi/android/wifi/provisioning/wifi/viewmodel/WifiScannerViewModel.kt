package com.nordicsemi.android.wifi.provisioning.wifi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nordicsemi.android.wifi.provisioning.wifi.view.NavigateUpEvent
import com.nordicsemi.android.wifi.provisioning.wifi.view.WifiScannerViewEntity
import com.nordicsemi.android.wifi.provisioning.wifi.view.WifiScannerViewEvent
import com.nordicsemi.android.wifi.provisioning.wifi.view.WifiSelectedEvent
import com.nordicsemi.wifi.provisioner.library.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import no.nordicsemi.android.navigation.NavigationManager
import no.nordicsemi.ui.scanner.ui.exhaustive
import javax.inject.Inject

@HiltViewModel
internal class WifiScannerViewModel @Inject constructor(
    private val navigationManager: NavigationManager
) : ViewModel() {

    private val repository = ProvisionerRepository.instance()

    private val _state = MutableStateFlow(WifiScannerViewEntity())
    val state = _state.asStateFlow()

    init {
//        viewModelScope.launch {
//            repository.scan()
//        }
        repository.startScan().onEach {
            val state = _state.value

            _state.value = when (it) {
                is Error -> state.copy(isLoading = false, isError = true)
                is Loading -> state.copy(isLoading = true)
                is Success -> state.copy(isLoading = false, isError = false, items = state.items + it.data)
            }
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: WifiScannerViewEvent) {
        when (event) {
            NavigateUpEvent -> navigationManager.navigateUp()
            is WifiSelectedEvent -> TODO()
        }.exhaustive
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            repository.stopScan()
        }
    }
}

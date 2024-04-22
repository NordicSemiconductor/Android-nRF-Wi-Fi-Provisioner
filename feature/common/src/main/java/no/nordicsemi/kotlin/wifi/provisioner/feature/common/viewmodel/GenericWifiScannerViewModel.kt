package no.nordicsemi.kotlin.wifi.provisioner.feature.common.viewmodel

import androidx.lifecycle.ViewModel
import no.nordicsemi.android.common.navigation.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.WifiAggregator
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.WifiDataConfiguration
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.WifiScannerViewEntity
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.NavigateUpEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnSortOptionSelected
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.WifiScannerViewEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.WifiSelectedEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.WifiSortOption

abstract class GenericWifiScannerViewModel(
    protected val navigationManager: Navigator,
    protected val wifiAggregator: WifiAggregator
) : ViewModel() {

    protected val _state = MutableStateFlow(WifiScannerViewEntity())
    val state = _state.asStateFlow()

    open fun onEvent(event: WifiScannerViewEvent) {
        when (event) {
            NavigateUpEvent -> navigateUp()
            is WifiSelectedEvent -> navigateUp(event.wifiData)
            is OnSortOptionSelected -> onSortOptionSelected(event.sortOption)
        }
    }

    protected fun onSortOptionSelected(sortOption: WifiSortOption) {
        _state.value = _state.value.copy(sortOption = sortOption)
    }

    protected abstract fun navigateUp()

    protected abstract fun navigateUp(wifiData: WifiDataConfiguration)
}
package no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.location.LocationStateManager
import no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.utils.WifiPermissionNotAvailableReason
import no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.utils.WifiPermissionState
import no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.wifi.WifiStateManager
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(
    private val wifiStateManager: WifiStateManager,
    private val locationManager: LocationStateManager,
) : ViewModel() {
    val wifiState = wifiStateManager.wifiState()
        .stateIn(
            viewModelScope, SharingStarted.Lazily,
            WifiPermissionState.NotAvailable(WifiPermissionNotAvailableReason.NOT_AVAILABLE)
        )

    val locationPermission = locationManager.locationState()
        .stateIn(
            viewModelScope, SharingStarted.Lazily,
            WifiPermissionState.NotAvailable(WifiPermissionNotAvailableReason.NOT_AVAILABLE)
        )

    fun refreshWifiPermission() {
        wifiStateManager.refreshPermission()
    }

    fun refreshLocationPermission() {
        locationManager.refreshPermission()
    }

    fun markLocationPermissionRequested() {
        locationManager.markLocationPermissionRequested()
    }

    fun markWifiPermissionRequested() {
        wifiStateManager.markWifiPermissionRequested()
    }

    fun isWifiPermissionDeniedForever(context: Context): Boolean {
        return wifiStateManager.isWifiPermissionDeniedForever(context)
    }

    fun isLocationPermissionDeniedForever(context: Context): Boolean {
        return locationManager.isLocationPermissionDeniedForever(context)
    }
}

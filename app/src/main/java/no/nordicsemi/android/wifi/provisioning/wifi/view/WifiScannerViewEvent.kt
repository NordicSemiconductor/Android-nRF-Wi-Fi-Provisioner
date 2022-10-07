package no.nordicsemi.android.wifi.provisioning.wifi.view

import no.nordicsemi.android.wifi.provisioning.wifi.viewmodel.WifiSortOption

internal sealed class WifiScannerViewEvent

internal object NavigateUpEvent : WifiScannerViewEvent()

internal data class WifiSelectedEvent(val wifiData: WifiData) : WifiScannerViewEvent()

internal data class OnSortOptionSelected(val sortOption: WifiSortOption) : WifiScannerViewEvent()

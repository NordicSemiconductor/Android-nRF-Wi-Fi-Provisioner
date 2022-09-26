package no.nordicsemi.android.wifi.provisioning.wifi.view

import no.nordicsemi.android.wifi.provisioning.wifi.viewmodel.WifiSortOption
import no.nordicsemi.wifi.provisioner.library.domain.ScanRecordDomain

internal sealed class WifiScannerViewEvent

internal object NavigateUpEvent : WifiScannerViewEvent()

internal data class WifiSelectedEvent(val scanRecord: ScanRecordDomain) : WifiScannerViewEvent()

internal data class OnSortOptionSelected(val sortOption: WifiSortOption) : WifiScannerViewEvent()

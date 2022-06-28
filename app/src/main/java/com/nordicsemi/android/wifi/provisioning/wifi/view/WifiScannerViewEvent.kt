package com.nordicsemi.android.wifi.provisioning.wifi.view

import com.nordicsemi.wifi.provisioner.library.domain.ScanRecordDomain

internal sealed class WifiScannerViewEvent

internal object NavigateUpEvent : WifiScannerViewEvent()

internal data class WifiSelectedEvent(val scanRecord: ScanRecordDomain) : WifiScannerViewEvent()

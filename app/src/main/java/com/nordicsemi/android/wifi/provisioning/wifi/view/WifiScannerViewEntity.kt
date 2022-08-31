package com.nordicsemi.android.wifi.provisioning.wifi.view

import com.nordicsemi.wifi.provisioner.library.domain.AuthModeDomain
import com.nordicsemi.wifi.provisioner.library.domain.ScanRecordDomain

data class WifiScannerViewEntity(
    val isLoading: Boolean = true,
    val error: Throwable? = null,
    val items: List<ScanRecordsSameSsid> = emptyList()
)

data class ScanRecordsSameSsid(
    val ssid: String,
    val authMode: AuthModeDomain,
    val items: List<ScanRecordDomain> = emptyList()
)

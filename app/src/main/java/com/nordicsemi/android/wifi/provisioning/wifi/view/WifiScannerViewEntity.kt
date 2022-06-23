package com.nordicsemi.android.wifi.provisioning.wifi.view

import com.nordicsemi.wifi.provisioner.library.Resource
import com.nordicsemi.wifi.provisioner.library.domain.ScanRecordDomain

data class WifiScannerViewEntity(
    val items: List<ScanRecordDomain> = emptyList(),
    val recentItem: Resource<ScanRecordDomain> = Resource.createLoading()
)

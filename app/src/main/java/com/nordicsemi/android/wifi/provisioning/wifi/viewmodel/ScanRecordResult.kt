package com.nordicsemi.android.wifi.provisioning.wifi.viewmodel

import com.nordicsemi.wifi.provisioner.library.domain.ScanRecordDomain
import no.nordicsemi.android.common.navigation.DestinationId
import no.nordicsemi.android.common.navigation.NavigationResult

data class ScanRecordResult(
    override val destinationId: DestinationId,
    val scanRecord: ScanRecordDomain
) : NavigationResult

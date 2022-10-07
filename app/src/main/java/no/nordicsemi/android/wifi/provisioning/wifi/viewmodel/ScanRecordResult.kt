package no.nordicsemi.android.wifi.provisioning.wifi.viewmodel

import no.nordicsemi.android.common.navigation.DestinationId
import no.nordicsemi.android.common.navigation.NavigationResult
import no.nordicsemi.android.wifi.provisioning.wifi.view.WifiData

data class ScanRecordResult(
    override val destinationId: DestinationId,
    val wifiData: WifiData
) : NavigationResult

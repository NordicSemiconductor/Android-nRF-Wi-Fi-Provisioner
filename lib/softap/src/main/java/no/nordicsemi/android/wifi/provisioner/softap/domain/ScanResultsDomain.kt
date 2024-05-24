package no.nordicsemi.android.wifi.provisioner.softap.domain

import no.nordicsemi.kotlin.wifi.provisioner.domain.ScanRecordDomain

data class ScanResultsDomain(
    val results : List<ScanRecordDomain>
)

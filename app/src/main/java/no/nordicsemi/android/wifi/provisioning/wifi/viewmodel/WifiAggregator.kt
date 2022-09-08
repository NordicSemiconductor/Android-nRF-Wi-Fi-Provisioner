package no.nordicsemi.android.wifi.provisioning.wifi.viewmodel

import no.nordicsemi.android.wifi.provisioning.wifi.view.ScanRecordsSameSsid
import no.nordicsemi.wifi.provisioner.library.domain.ScanRecordDomain
import javax.inject.Inject

class WifiAggregator @Inject constructor() {

    private val records = mutableMapOf<String, List<ScanRecordDomain>>()

    fun addWifi(record: ScanRecordDomain): List<ScanRecordsSameSsid> {
        if (record.wifiInfo.authModeDomain == null) {
            return createResult(records)
        }

        val ssid = record.wifiInfo.ssid
        val ssidRecords = records[ssid]?.let {
            (it + record).distinctBy { it.wifiInfo.channel }.sortedByDescending { it.rssi }
        } ?: listOf(record)
        this.records[ssid] = ssidRecords
        return createResult(records)
    }

    private fun createResult(records: Map<String, List<ScanRecordDomain>>): List<ScanRecordsSameSsid> {
        return this.records.map {
            ScanRecordsSameSsid(
                it.key,
                it.value.first().wifiInfo.authModeDomain!!,
                it.value
            )
        }
    }
}

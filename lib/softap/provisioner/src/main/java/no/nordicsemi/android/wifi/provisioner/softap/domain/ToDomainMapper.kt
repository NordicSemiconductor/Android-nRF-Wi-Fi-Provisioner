package no.nordicsemi.android.wifi.provisioner.softap.domain

import no.nordicsemi.android.wifi.provisioner.softap.proto.AuthMode
import no.nordicsemi.android.wifi.provisioner.softap.proto.Band
import no.nordicsemi.android.wifi.provisioner.softap.proto.ScanRecord
import no.nordicsemi.android.wifi.provisioner.softap.proto.ScanResults
import no.nordicsemi.android.wifi.provisioner.softap.proto.WifiConfig
import no.nordicsemi.android.wifi.provisioner.softap.proto.WifiInfo
import no.nordicsemi.kotlin.wifi.provisioner.domain.AuthModeDomain
import no.nordicsemi.kotlin.wifi.provisioner.domain.BandDomain
import no.nordicsemi.kotlin.wifi.provisioner.domain.ScanRecordDomain
import no.nordicsemi.kotlin.wifi.provisioner.domain.WifiConfigDomain
import no.nordicsemi.kotlin.wifi.provisioner.domain.WifiInfoDomain

internal fun AuthMode.toDomain() = when(this) {
    AuthMode.OPEN -> AuthModeDomain.OPEN
    AuthMode.WEP -> AuthModeDomain.WEP
    AuthMode.WPA_PSK -> AuthModeDomain.WPA_PSK
    AuthMode.WPA2_PSK -> AuthModeDomain.WPA2_PSK
    AuthMode.WPA_WPA2_PSK -> AuthModeDomain.WPA_WPA2_PSK
    AuthMode.WPA2_ENTERPRISE -> AuthModeDomain.WPA2_ENTERPRISE
    AuthMode.WPA3_PSK -> AuthModeDomain.WPA3_PSK
}

internal fun Band.toDomain() = when(this) {
    Band.BAND_UNSPECIFIED -> BandDomain.BAND_ANY
    Band.BAND_2_4_GHZ -> BandDomain.BAND_2_4_GH
    Band.BAND_5_GHZ -> BandDomain.BAND_5_GH
    Band.BAND_6_GHZ -> BandDomain.BAND_6_GH
}

internal fun WifiInfo.toDomain() = WifiInfoDomain(
    ssid = ssid,
    band = band.toDomain(),
    channel = channel,
    authModeDomain = authMode.toDomain(),
    bssid = ""
)

@Suppress("unused")
internal fun WifiConfig.toDomain() = WifiConfigDomain(
    info = info?.toDomain()!!,
    passphrase = passphrase,
    anyChannel = false,
    volatileMemory = false
)

internal fun ScanRecord.toDomain() = ScanRecordDomain(
    wifiInfo = wifi?.toDomain(),
    rssi = rssi
)

internal fun ScanResults.toDomain() = ScanResultsDomain(
    results = results.map { it.toDomain() }
)
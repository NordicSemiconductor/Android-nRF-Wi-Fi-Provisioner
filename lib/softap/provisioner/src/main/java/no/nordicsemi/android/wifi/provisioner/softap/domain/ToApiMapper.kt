package no.nordicsemi.android.wifi.provisioner.softap.domain

import no.nordicsemi.android.wifi.provisioner.softap.proto.AuthMode
import no.nordicsemi.android.wifi.provisioner.softap.proto.Band
import no.nordicsemi.android.wifi.provisioner.softap.proto.ScanRecord
import no.nordicsemi.android.wifi.provisioner.softap.proto.ScanResults
import no.nordicsemi.android.wifi.provisioner.softap.proto.WifiConfig
import no.nordicsemi.android.wifi.provisioner.softap.proto.WifiInfo

internal fun AuthModeDomain.toApi() = when(this) {
    AuthModeDomain.OPEN -> AuthMode.OPEN
    AuthModeDomain.WEP -> AuthMode.WEP
    AuthModeDomain.WPA_PSK -> AuthMode.WPA_PSK
    AuthModeDomain.WPA2_PSK -> AuthMode.WPA2_PSK
    AuthModeDomain.WPA_WPA2_PSK -> AuthMode.WPA_WPA2_PSK
    AuthModeDomain.WPA2_ENTERPRISE -> AuthMode.WPA2_ENTERPRISE
    AuthModeDomain.WPA3_PSK -> AuthMode.WPA3_PSK
}

internal fun BandDomain.toApi() = when(this) {
    BandDomain.BAND_UNSPECIFIED -> Band.BAND_UNSPECIFIED
    BandDomain.BAND_2_4_GH -> Band.BAND_2_4_GHZ
    BandDomain.BAND_5_GH -> Band.BAND_5_GHZ
    BandDomain.BAND_6_GH -> Band.BAND_6_GHZ
}

internal fun WifiConfigDomain.toApi() = WifiConfig(
    info = wifiInfoDomain?.toApi(),
    passphrase = passphrase
)

internal fun WifiInfoDomain.toApi() = WifiInfo(
    ssid = ssid,
    band = bandDomain.toApi(),
    channel = channel,
    authMode = authModeDomain.toApi()
)

internal fun ScanRecordDomain.toApi() = ScanRecord(
    wifi = infoDomain?.toApi(),
    rssi = rssi
)

internal fun ScanResultsDomain.toApi() = ScanResults(
    results = results.map { it.toApi() }
)

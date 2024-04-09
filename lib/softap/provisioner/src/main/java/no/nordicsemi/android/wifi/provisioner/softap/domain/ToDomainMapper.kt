package no.nordicsemi.android.wifi.provisioner.softap.domain

import no.nordicsemi.android.wifi.provisioner.softap.proto.AuthMode
import no.nordicsemi.android.wifi.provisioner.softap.proto.Band
import no.nordicsemi.android.wifi.provisioner.softap.proto.ScanResults
import no.nordicsemi.android.wifi.provisioner.softap.proto.WifiConfig
import no.nordicsemi.android.wifi.provisioner.softap.proto.WifiScanResult


fun AuthMode.toDomain() : AuthModeDomain = when(this) {
    AuthMode.AUTH_MODE_UNSPECIFIED -> AuthModeDomain.AUTH_MODE_UNSPECIFIED
    AuthMode.OPEN -> AuthModeDomain.OPEN
    AuthMode.WEP -> AuthModeDomain.WEP
    AuthMode.WPA_PSK -> AuthModeDomain.WPA_PSK
    AuthMode.WPA2_PSK -> AuthModeDomain.WPA2_PSK
    AuthMode.WPA_WPA2_PSK -> AuthModeDomain.WPA_WPA2_PSK
    AuthMode.WPA2_ENTERPRISE -> AuthModeDomain.WPA2_ENTERPRISE
    AuthMode.WPA3_PSK -> AuthModeDomain.WPA3_PSK
}

internal fun Band.toDomain() : BandDomain = when(this) {
    Band.BAND_UNSPECIFIED -> BandDomain.BAND_UNSPECIFIED
    Band.BAND_2_4_GHZ -> BandDomain.BAND_2_4_GH
    Band.BAND_5_GHZ -> BandDomain.BAND_5_GH
    Band.BAND_6_GHZ -> BandDomain.BAND_6_GH
}

fun WifiConfig.toDomain(): WifiConfigDomain {
    return WifiConfigDomain(
        ssid = ssid,
        passphrase = passphrase,
        bandDomain = band.toDomain(),
        channel = channel,
        authModeDomain = authMode.toDomain()
    )
}

fun WifiScanResult.toDomain(): WifiScanResultDomain = WifiScanResultDomain(
    ssid = ssid,
    bssid = bssid,
    bandDomain = band.toDomain(),
    channel = channel,
    authModeDomain = authMode.toDomain(),
    rssi = rssi
)

fun ScanResults.toDomain(): ScanResultsDomain = ScanResultsDomain(
    wifiScanResults = results.map { it.toDomain() }
)

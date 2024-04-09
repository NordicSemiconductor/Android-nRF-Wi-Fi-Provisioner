package no.nordicsemi.android.wifi.provisioner.softap.domain

import no.nordicsemi.android.wifi.provisioner.softap.proto.AuthMode
import no.nordicsemi.android.wifi.provisioner.softap.proto.Band
import no.nordicsemi.android.wifi.provisioner.softap.proto.ScanResults
import no.nordicsemi.android.wifi.provisioner.softap.proto.WifiConfig
import no.nordicsemi.android.wifi.provisioner.softap.proto.WifiScanResult


fun AuthModeDomain.toApi() : AuthMode = when(this) {
    AuthModeDomain.AUTH_MODE_UNSPECIFIED -> AuthMode.AUTH_MODE_UNSPECIFIED
    AuthModeDomain.OPEN -> AuthMode.OPEN
    AuthModeDomain.WEP -> AuthMode.WEP
    AuthModeDomain.WPA_PSK -> AuthMode.WPA_PSK
    AuthModeDomain.WPA2_PSK -> AuthMode.WPA2_PSK
    AuthModeDomain.WPA_WPA2_PSK -> AuthMode.WPA_WPA2_PSK
    AuthModeDomain.WPA2_ENTERPRISE -> AuthMode.WPA2_ENTERPRISE
    AuthModeDomain.WPA3_PSK -> AuthMode.WPA3_PSK
}

internal fun BandDomain.toApi() : Band = when(this) {
    BandDomain.BAND_UNSPECIFIED -> Band.BAND_UNSPECIFIED
    BandDomain.BAND_2_4_GH -> Band.BAND_2_4_GHZ
    BandDomain.BAND_5_GH -> Band.BAND_5_GHZ
    BandDomain.BAND_6_GH -> Band.BAND_6_GHZ
}

fun WifiConfigDomain.toApi(): WifiConfig {
    return WifiConfig(
        ssid = ssid,
        passphrase = passphrase,
        band = bandDomain.toApi(),
        channel = channel,
        authMode = authModeDomain.toApi()
    )
}

fun ScanResultsDomain.toApi(): ScanResults = ScanResults(
    results = wifiScanResults.map { it.toApi() }
)

fun WifiScanResultDomain.toApi(): WifiScanResult = WifiScanResult(
    ssid = ssid,
    bssid = bssid,
    band = bandDomain.toApi(),
    channel = channel,
    authMode = authModeDomain.toApi(),
    rssi = rssi
)
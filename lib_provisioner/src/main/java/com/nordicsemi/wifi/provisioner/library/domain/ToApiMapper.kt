package com.nordicsemi.wifi.provisioner.library.domain

import no.nordicsemi.android.wifi.provisioning.AuthMode
import no.nordicsemi.android.wifi.provisioning.Band
import no.nordicsemi.android.wifi.provisioning.WifiConfig
import no.nordicsemi.android.wifi.provisioning.WifiInfo
import okio.ByteString.Companion.toByteString

internal fun WifiConfigDomain.toApi(): WifiConfig {
    return WifiConfig(
        wifi = info.toApi(),
        passphrase = password?.toByteArray()?.toByteString(),
        volatileMemory = volatileMemory
    )
}

internal fun WifiInfoDomain.toApi(): WifiInfo {
    return WifiInfo(
        ssid = ssid.toByteArray().toByteString(),
        bssid = bssid.toByteArray().toByteString(),
        band = band?.toApi(),
        channel = channel,
        auth = authModeDomain?.toApi()
    )
}

internal fun BandDomain.toApi(): Band {
    return when (this) {
        BandDomain.BAND_ANY -> Band.BAND_ANY
        BandDomain.BAND_2_4_GH -> Band.BAND_2_4_GH
        BandDomain.BAND_5_GH -> Band.BAND_5_GH
    }
}

internal fun AuthModeDomain.toApi(): AuthMode {
    return when (this) {
        AuthModeDomain.OPEN -> AuthMode.OPEN
        AuthModeDomain.WEP -> AuthMode.WEP
        AuthModeDomain.WPA_PSK -> AuthMode.WPA_PSK
        AuthModeDomain.WPA2_PSK -> AuthMode.WPA2_PSK
        AuthModeDomain.WPA_WPA2_PSK -> AuthMode.WPA_WPA2_PSK
        AuthModeDomain.WPA2_ENTERPRISE -> AuthMode.WPA2_ENTERPRISE
        AuthModeDomain.WPA3_PSK -> AuthMode.WPA3_PSK
    }
}

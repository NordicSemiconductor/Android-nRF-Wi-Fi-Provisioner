package no.nordicsemi.android.wifi.provisioner.ui.mapping

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SignalWifi4BarLock
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import no.nordicsemi.android.wifi.provisioner.ui.R
import no.nordicsemi.kotlin.wifi.provisioner.domain.AuthModeDomain
import no.nordicsemi.kotlin.wifi.provisioner.domain.BandDomain

fun AuthModeDomain?.toImageVector() = when (this) {
    AuthModeDomain.OPEN -> Icons.Outlined.Wifi
    AuthModeDomain.WEP,
    AuthModeDomain.WPA_PSK,
    AuthModeDomain.WPA2_PSK,
    AuthModeDomain.WPA_WPA2_PSK,
    AuthModeDomain.WPA2_ENTERPRISE,
    AuthModeDomain.WPA3_PSK,
    null -> Icons.Outlined.SignalWifi4BarLock
}

fun AuthModeDomain.toDisplayString(): String = when(this){
    AuthModeDomain.OPEN -> "Open"
    AuthModeDomain.WEP -> "WEP"
    AuthModeDomain.WPA_PSK -> "WPA PSK"
    AuthModeDomain.WPA2_PSK -> "WPA2 PSK"
    AuthModeDomain.WPA_WPA2_PSK -> "WPA/WPA2 PSK"
    AuthModeDomain.WPA2_ENTERPRISE -> "WPA2 Enterprise"
    AuthModeDomain.WPA3_PSK -> "WPA3 PSK"
}

@Composable
fun BandDomain.toDisplayString() = when (this) {
    BandDomain.BAND_ANY -> R.string.any
    BandDomain.BAND_2_4_GH -> R.string.band_2_4
    BandDomain.BAND_5_GH -> R.string.band_5
}.let { stringResource(id = it) }
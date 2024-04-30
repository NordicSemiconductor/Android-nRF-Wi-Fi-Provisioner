package no.nordicsemi.android.wifi.provisioner.softap.view

import no.nordicsemi.android.wifi.provisioner.softap.Open
import no.nordicsemi.android.wifi.provisioner.softap.PassphraseConfiguration
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.ProvisioningViewEvent

data class OnSoftApConnectEvent(
    val ssid: String,
    val passphraseConfiguration: PassphraseConfiguration = Open
) : ProvisioningViewEvent
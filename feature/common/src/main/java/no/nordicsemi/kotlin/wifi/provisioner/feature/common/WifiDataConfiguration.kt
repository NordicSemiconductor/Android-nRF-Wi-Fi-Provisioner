package no.nordicsemi.kotlin.wifi.provisioner.feature.common

import no.nordicsemi.kotlin.wifi.provisioner.domain.AuthModeDomain


/**
 * Common interface for the Wifi data configuration.
 *
 * @property ssid        SSID of the network.
 * @property authMode    Authentication mode used for provisioning.
 */
interface WifiDataConfiguration {
    val ssid: String
    val authMode: AuthModeDomain
}
package no.nordicsemi.android.wifi.provisioner.ble.view

import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.ProvisioningViewEvent

data object OpenLoggerEvent : ProvisioningViewEvent

data object OnUnprovisionEvent : ProvisioningViewEvent

data object OnVolatileMemoryChangedEvent : ProvisioningViewEvent
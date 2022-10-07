package no.nordicsemi.wifi.provisioner.library.domain

data class WifiConfigDomain(
    val info: WifiInfoDomain,
    val password: String?,
    val volatileMemory: Boolean,
    val anyChannel: Boolean
)

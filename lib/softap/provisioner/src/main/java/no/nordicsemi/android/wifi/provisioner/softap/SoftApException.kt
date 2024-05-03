package no.nordicsemi.android.wifi.provisioner.softap

/**
 * An exception that is thrown when the SoftAP provisioning fails.
 */
sealed class SoftApException : Exception()

/**
 * An exception that is thrown when the WiFi is not enabled.
 */
data object WifiNotEnabledException : SoftApException() {
    private fun readResolve(): Any = WifiNotEnabledException
}

/**
 * An exception that is thrown when the SoftAp manager fails to bind to the connected SoftAP
 * network.
 */
data object FailedToBindToNetwork : SoftApException() {
    private fun readResolve(): Any = WifiNotEnabledException
}
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
 * An exception that is thrown when the SoftAp manager fails to connect to the SoftAP network.
 */
data object UnableToConnectToNetwork : SoftApException() {
    private fun readResolve(): Any = UnableToConnectToNetwork
}

/**
 * An exception that is thrown when the SoftAp manager fails to bind to the connected SoftAP
 * network.
 */
data object FailedToBindToNetwork : SoftApException() {
    private fun readResolve(): Any = FailedToBindToNetwork
}

/**
 * An exception that is thrown when the SoftAp manager loses connectivity from the SoftAP.
 */
data object OnConnectionLost : SoftApException() {
    private fun readResolve(): Any = OnConnectionLost
}

package no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.utils

/**
 * Represents the reason for Wi-Fi permission is not available.
 */
enum class WifiPermissionNotAvailableReason {
    PERMISSION_REQUIRED,
    NOT_AVAILABLE,
    DISABLED,
}

/**
 * Represents the state of Wi-Fi permission.
 */
sealed class WifiPermissionState {

    /**
     * Represents the Wi-Fi permission is available.
     */
    data object Available : WifiPermissionState()

    /**
     * Represents the Wi-Fi permission is not available.
     * @param reason The reason for Wi-Fi permission is not available.
     */
    data class NotAvailable(
        val reason: WifiPermissionNotAvailableReason,
    ) : WifiPermissionState()
}

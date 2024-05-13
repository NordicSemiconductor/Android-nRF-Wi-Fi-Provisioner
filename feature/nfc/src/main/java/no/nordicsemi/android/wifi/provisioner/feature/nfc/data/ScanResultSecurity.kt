package no.nordicsemi.android.wifi.provisioner.feature.nfc.data

import android.net.wifi.ScanResult

// Constants used for different security types
const val WPA2 = "WPA2"
const val WPA = "WPA"
const val WEP = "WEP"
const val OPEN = "Open"
const val IEEE8021X = "IEEE8021X"
const val WPA_EAP = "WPA-EAP"

/**
 * @return The security of a given [ScanResult].
 */
fun getScanResultSecurity(scanResult: ScanResult): String {
    val cap = scanResult.capabilities
    val securityModes = arrayOf(WEP, WPA, WPA2, WPA_EAP, IEEE8021X)
    for (i in securityModes.indices.reversed()) {
        if (cap.contains(securityModes[i])) {
            return securityModes[i]
        }
    }

    return OPEN
}
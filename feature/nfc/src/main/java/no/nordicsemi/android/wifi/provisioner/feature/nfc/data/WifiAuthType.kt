package no.nordicsemi.android.wifi.provisioner.feature.nfc.data

import android.net.wifi.ScanResult
import android.os.Build
import no.nordicsemi.android.wifi.provisioner.nfc.domain.AuthMode

/**
 * @return The security of a given [ScanResult].
 */
fun getScanResultSecurity(scanResult: ScanResult): String {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        return AuthMode.getMatchedAuthMode(scanResult.securityTypes)
    } else {
        val cap = scanResult.capabilities
        val securityModes = AuthMode.getListOfAuthModes()
        for (i in securityModes.indices.reversed()) {
            if (cap.contains(securityModes[i])) {
                return securityModes[i]
            }
        }
        return AuthMode.OPEN.name
    }
}
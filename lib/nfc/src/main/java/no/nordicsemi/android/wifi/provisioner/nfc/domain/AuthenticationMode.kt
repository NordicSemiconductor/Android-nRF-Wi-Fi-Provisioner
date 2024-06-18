package no.nordicsemi.android.wifi.provisioner.nfc.domain

import android.net.wifi.ScanResult
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Interface that represents the authentication mode of a Wi-Fi network.
 */
@Parcelize
enum class AuthenticationMode(private val stringRep: String) : Parcelable {
    OPEN("Open"),
    WEP("WEP"),
    WPA_PSK("WPA-PSK"),
    WPA2_PSK("WPA2-PSK"),
    WPA_EAP("WPA-EAP"),
    WPA_WPA2_PSK("WPA/WPA2-PSK"),
    WPA2_EAP("WPA2-EAP"),
    WPA3_PSK("WPA3-PSK");

    companion object {
        /**
         * @return The security of a given [ScanResult].
         */
        private fun getSecurityTypes(scanResult: ScanResult): AuthenticationMode {
            val cap = scanResult.capabilities
            val securityModes = AuthenticationMode.entries.map { it.stringRep }
            for (i in securityModes.indices.reversed()) {
                if (cap.contains(securityModes[i])) {
                    AuthenticationMode.entries.find { it.stringRep == securityModes[i] }
                        ?.let {
                            return it
                        }
                }
            }
            return OPEN
        }

        /**
         * @return The authentication mode(s) of a given [ScanResult].
         */
        fun get(scanResult: ScanResult): List<AuthenticationMode> {
            return listOf(AuthenticationMode.getSecurityTypes(scanResult))
        }
    }
}

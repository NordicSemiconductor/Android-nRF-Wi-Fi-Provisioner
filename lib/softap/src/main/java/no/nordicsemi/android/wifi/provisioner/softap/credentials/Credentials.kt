package no.nordicsemi.android.wifi.provisioner.softap.credentials

/**
 * Created by Roshan Rajaratnam on 22/02/2024.
 */
data class Credentials(private val ssid: String, private val password: String) {
    val value = "$ssid $password"
}
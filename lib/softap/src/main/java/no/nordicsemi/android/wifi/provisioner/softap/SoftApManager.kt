@file:Suppress("unused")

package no.nordicsemi.android.wifi.provisioner.softap

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.nsd.NsdManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import no.nordicsemi.android.wifi.provisioner.softap.credentials.Credentials
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

/**
 * Created by Roshan Rajaratnam on 23/02/2024.
 *
 * Entry point to the SoftApManager
 *
 * @param context             Application context.
 * @param wifiService         WifiServer api.
 * @param coroutineDispatcher Coroutine dispatcher.
 *
 */
class SoftApManager @Inject constructor(
    context: Context,
    private val wifiService: WifiService,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager

    private var _provisioningState =
        MutableStateFlow<ProvisioningState>(ProvisioningState.Disconnected)
    val provisioningState = _provisioningState.asStateFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            // do success processing here..
            try {
                if (connectivityManager.bindProcessToNetwork(network)) {
                    Log.d(
                        "AAAA", "Link properties ${
                            connectivityManager.getLinkProperties(network)?.toString()
                        }"
                    )
                    _provisioningState.value = ProvisioningState.Connected
                } else {
                    disconnect()
                }
            } catch (e: Exception) {
                Log.e("AAAA", "Error: $e")
            }
        }

        override fun onUnavailable() {
            // do failure processing here..
            Log.d("AAAA", "Something went wrong!")
            disconnect()
        }
    }

    /**
     * Connects to an unprovisioned wifi device by establishing a temporary wifi network connection
     * with the given ssid and the password.
     *
     * Note: The SSID and the password are the ones used to connect to the unprovisioned device, not
     * the SSID or the passphrase of the network that the device must be provisioned into.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun connect(ssid: String, password: String = "") {
        _provisioningState.value = ProvisioningState.Connecting
        val specifier = WifiNetworkSpecifier.Builder()
            .setSsid(ssid)
            .setWpa2Passphrase(password)
            .build()

        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .setNetworkSpecifier(specifier)
            .build()
        connectivityManager.requestNetwork(request, networkCallback)
    }

    /**
     * Disconnects from an unprovisioned or a newly provisioned device.
     */
    fun disconnect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.bindProcessToNetwork(null)
        }
        connectivityManager.unregisterNetworkCallback(networkCallback)
        _provisioningState.value = ProvisioningState.Disconnected
    }

    /**
     * Lists the SSIDs scanned by the nRF7002 device
     */
    suspend fun listSsids(): Response<ResponseBody> = withContext(coroutineDispatcher) {
        wifiService.listSsids()
    }

    /**
     * Provisions the nRF7002 device a wifi network with the given credentials.
     *
     * @param credentials Credentials of the wifi network.
     */
    suspend fun provision(credentials: Credentials) = withContext(coroutineDispatcher) {
        wifiService.provision(credentials.value).also {
            nsdManager.discoverServices(
                "_https._tcp",
                NsdManager.PROTOCOL_DNS_SD,
                NetworkServiceDiscoveryListener(nsdManager = nsdManager)
            )
        }

    }

    /**
     * Toggles the given LED on the nRF7000 device.
     *
     * @param led    Led number to blink.
     * @param toggle Boolean representing on or off.
     */
    suspend fun blink(led: Int, toggle: Boolean) = withContext(coroutineDispatcher) {
        wifiService.blink(led = led, toggle = if (toggle) "1" else "0")
    }
}
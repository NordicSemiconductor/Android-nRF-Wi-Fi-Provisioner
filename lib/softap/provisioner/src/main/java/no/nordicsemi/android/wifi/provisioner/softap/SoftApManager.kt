@file:Suppress("unused")

package no.nordicsemi.android.wifi.provisioner.softap

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.nsd.NsdServiceInfo
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import no.nordicsemi.android.wifi.provisioner.softap.domain.WifiConfigDomain
import no.nordicsemi.android.wifi.provisioner.softap.domain.toApi
import no.nordicsemi.android.wifi.provisioner.softap.domain.toDomain
import javax.inject.Inject

/**
 * Created by Roshan Rajaratnam on 23/02/2024.
 *
 * Entry point to the SoftApManager
 * @property connectivityManager Android's connectivity manager.
 * @property nsdListener         Network service discovery listener.
 * @property wifiService         WifiServer api.
 * @property coroutineDispatcher Coroutine dispatcher.
 * @property provisioningState   Provisioning state.
 * @constructor Create empty SoftApManager.
 */
class SoftApManager @Inject constructor(
    private val connectivityManager: ConnectivityManager,
    private val nsdListener: NetworkServiceDiscoveryListener,
    private val wifiService: WifiService,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    private var _provisioningState =
        MutableStateFlow<ProvisioningState>(ProvisioningState.Disconnected)
    val provisioningState = _provisioningState.asStateFlow()

    private val _discoveredServices = mutableListOf<NsdServiceInfo>()
    private var discoveredService: NsdServiceInfo? = null

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
     * Discover services on the network.
     */
    suspend fun discoverServices(): NsdServiceInfo {
        val serviceInfo = nsdListener.discoverServices()
        nsdListener.stopDiscovery()
        return serviceInfo!!
    }

    /**
     * Stops the network service discovery.
     */
    private fun stopDiscovery() {
        nsdListener.stopDiscovery()
    }

    /**
     * Lists the SSIDs scanned by the nRF7002 device
     */
    suspend fun listSsids() = withContext(coroutineDispatcher) {
        wifiService.listSsids().toDomain()
    }

    /**
     * Provisions the nRF7002 device a wifi network with the given credentials.
     *
     * @param config Credentials of the wifi network.
     */
    suspend fun provision(config: WifiConfigDomain) = withContext(coroutineDispatcher) {
        wifiService.provision(config.toApi())
    }
}
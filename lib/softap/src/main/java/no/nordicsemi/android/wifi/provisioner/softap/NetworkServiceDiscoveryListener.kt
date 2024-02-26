package no.nordicsemi.android.wifi.provisioner.softap

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log

/**
 * Created by Roshan Rajaratnam on 23/02/2024.
 */
internal class NetworkServiceDiscoveryListener(private val nsdManager: NsdManager) :
    NsdManager.DiscoveryListener {

    // Called as soon as service discovery begins.
    override fun onDiscoveryStarted(regType: String) {
        Log.d("AAAA", "Service discovery started $regType")
    }

    override fun onServiceFound(service: NsdServiceInfo) {
        // A service was found! Do something with it.
        Log.d("AAAA", "Service discovery success $service")
    }

    override fun onServiceLost(service: NsdServiceInfo) {
        // When the network service is no longer available.
        // Internal bookkeeping code goes here.
        Log.e("AAAA", "service lost: $service")
    }

    override fun onDiscoveryStopped(serviceType: String) {
        Log.i("AAAA", "Discovery stopped: $serviceType")
    }

    override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
        try {
            Log.e("AAAA", "Discovery failed: Error code:$errorCode")
            // nsdManager.stopServiceDiscovery(this)
        } catch (e: Exception) {
            Log.e("AAAA", "error on start discovery failed: $e")
        }
    }

    override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
        Log.e("AAAA", "Discovery failed: Error code:$errorCode")
        nsdManager.stopServiceDiscovery(this)
    }
}
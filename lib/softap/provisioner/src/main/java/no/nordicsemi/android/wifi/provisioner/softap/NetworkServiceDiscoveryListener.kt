package no.nordicsemi.android.wifi.provisioner.softap

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import kotlinx.coroutines.sync.Mutex
import java.net.InetAddress

/**
 * NetworkServiceDiscoveryListener that would listen to the network service discovery events.
 *
 * @param nsdManager       NsdManager instance.
 * @property discoveredIps List of discovered IP addresses of the network services. Note that this
 *                         list could be empty if no services are discovered.
 */
class NetworkServiceDiscoveryListener internal constructor(private val nsdManager: NsdManager) {
    private val mutex = Mutex(true)
    private val _discoveredIps = mutableListOf<InetAddress>()
    val discoveredIps: List<InetAddress>
        get() = _discoveredIps

    private var _serviceInfo: NsdServiceInfo? = null

    /**
     * Callback used to listen for service discovery.
     */
    private val nsdListener = object : NsdManager.DiscoveryListener {

        // Called as soon as service discovery begins.
        override fun onDiscoveryStarted(regType: String) {
            Log.d("AAAA", "Service discovery started $regType")
        }

        override fun onServiceFound(service: NsdServiceInfo) {
            // A service was found! Do something with it.
            Log.d("AAAA", "Service discovery success $service")
            if (service.serviceName == SERVICE_NAME) {
                nsdManager.resolveService(service, resolveListener)
            }
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
            } finally {
                nsdManager.stopServiceDiscovery(this)
            }
        }

        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e("AAAA", "Discovery failed: Error code:$errorCode")
            nsdManager.stopServiceDiscovery(this)
        }
    }

    /**
     * Callback used to listen for service resolution.
     */
    private val resolveListener = object : NsdManager.ResolveListener {
        override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
            // Called when the resolve fails. Use the error code to debug.
            Log.e("AAAA", "Resolve failed: $errorCode")
        }

        override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
            Log.d("AAAA", "Resolve success: $serviceInfo")
            serviceInfo?.let {
                _serviceInfo = it
                _discoveredIps.add(it.host)
                mutex.unlock()
            }
        }
    }

    /**
     * Discovers the network services.
     *
     * @return NsdServiceInfo instance.
     */
    internal suspend fun discoverServices(): NsdServiceInfo? {
        nsdManager.discoverServices(
            SERVICE_TYPE,
            NsdManager.PROTOCOL_DNS_SD,
            nsdListener
        )
        mutex.lock()
        return _serviceInfo!!
    }

    /**
     * Stops the network service discovery.
     */
    internal fun stopDiscovery() {
        nsdManager.stopServiceDiscovery(nsdListener)
    }

    internal companion object {
        const val SERVICE_NAME = "wifiprov"
        private const val SERVICE_TYPE = "_http._tcp."
    }
}
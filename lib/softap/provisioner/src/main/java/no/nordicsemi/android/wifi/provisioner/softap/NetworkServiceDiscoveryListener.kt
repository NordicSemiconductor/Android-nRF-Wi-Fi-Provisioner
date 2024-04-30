package no.nordicsemi.android.wifi.provisioner.softap

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import kotlinx.coroutines.suspendCancellableCoroutine
import java.net.InetAddress
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * NetworkServiceDiscoveryListener that would listen to the network service discovery events.
 *
 * @param nsdManager       NsdManager instance.
 * @property discoveredIps List of discovered IP addresses of the network services. Note that this
 *                         list could be empty if no services are discovered.
 */
class NetworkServiceDiscoveryListener internal constructor(private val nsdManager: NsdManager, private val serviceName: String) {
    private val _discoveredIps = mutableListOf<InetAddress>()
    val discoveredIps: List<InetAddress>
        get() = _discoveredIps

    /**
     * Discovers the network services.
     *
     * @return NsdServiceInfo instance.
     */
    internal suspend fun discoverServices(
        nsdServiceInfo: NsdServiceInfo
    ): NsdServiceInfo = suspendCancellableCoroutine { continuation ->

        lateinit var nsdListener: NsdManager.DiscoveryListener

        val resolveListener = object : NsdManager.ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                // Called when the resolve fails. Use the error code to debug.
                Log.e("AAAA", "Resolve failed: $errorCode")
            }

            override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
                Log.d("AAAA", "Resolve success: $serviceInfo")
                serviceInfo?.let {
                    Log.d("AAAA", "Service attributes: ${serviceInfo.attributes}")
                    _discoveredIps.add(it.host)
                    continuation.resume(it)
                    nsdManager.stopServiceDiscovery(nsdListener)
                }
            }
        }
        nsdListener = object : NsdManager.DiscoveryListener {

            // Called as soon as service discovery begins.
            override fun onDiscoveryStarted(regType: String) {
                Log.d("AAAA", "Service discovery started $regType")
            }

            override fun onServiceFound(service: NsdServiceInfo) {
                // A service was found! Do something with it.
                Log.d("AAAA", "Service discovered $service")
                if (service.serviceName == serviceName) {
                    Log.d("AAAA", "Resolving service $service")
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
                Log.e("AAAA", "error on starting service discovery for " +
                        "$serviceType failed: $errorCode")
                continuation.resumeWithException(
                    Throwable("Discovery failed: Error code:$errorCode")
                )
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e("AAAA", "Discovery failed: Error code:$errorCode")
                continuation.resumeWithException(
                    Throwable("Discovery failed: Error code:$errorCode")
                )
            }
        }

        nsdManager.discoverServices(
            nsdServiceInfo.serviceType,
            NsdManager.PROTOCOL_DNS_SD,
            nsdListener
        )

        continuation.invokeOnCancellation {
            nsdManager.stopServiceDiscovery(nsdListener)
        }
    }
}
package no.nordicsemi.android.wifi.provisioner.softap

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import kotlinx.coroutines.suspendCancellableCoroutine
import no.nordicsemi.android.wifi.provisioner.softap.SoftApManager.Companion.KEY_LINK_ADDR
import okio.ByteString.Companion.toByteString
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
class NetworkServiceDiscoveryListener internal constructor(private val nsdManager: NsdManager) {
    private val _discoveredIps = mutableListOf<InetAddress>()
    val discoveredIps: List<InetAddress>
        get() = _discoveredIps

    /**
     * Discovers the network services.
     *
     * @return NsdServiceInfo instance.
     */
    internal suspend fun discoverServices(
        macAddress: String? = null,
        nsdServiceInfo: NsdServiceInfo
    ): NsdServiceInfo = suspendCancellableCoroutine { continuation ->
        _discoveredIps.clear()
        lateinit var nsdListener: NsdManager.DiscoveryListener

        val resolveListener = object : NsdManager.ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                // Called when the resolve fails. Use the error code to debug.
                Log.e("AAA", "Resolve failed: $errorCode")
            }

            override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
                Log.d("AAA", "Resolve success: $serviceInfo")
                serviceInfo?.let {
                    if (macAddress == null) {
                        stopDiscovery(it)
                    } else {
                        val mac = serviceInfo.attributes?.get(KEY_LINK_ADDR)?.toByteString()?.utf8()
                        if (mac == macAddress) {
                            stopDiscovery(it)
                        }
                    }
                }
            }

            private fun stopDiscovery(serviceInfo: NsdServiceInfo) {
                Log.d("AAA", "Service attributes: ${serviceInfo.attributes}")
                _discoveredIps.add(serviceInfo.host)
                continuation.resume(serviceInfo)
                nsdManager.stopServiceDiscovery(nsdListener)
            }
        }

        nsdListener = object : NsdManager.DiscoveryListener {

            // Called as soon as service discovery begins.
            override fun onDiscoveryStarted(regType: String) {
                Log.d("AAA", "Service discovery started $regType")
            }

            override fun onServiceFound(service: NsdServiceInfo) {
                Log.d("AAA", "Service discovered $service")
                // Check if the service name found matches the service name we are looking for.
                if (service.serviceName == nsdServiceInfo.serviceName) {
                    Log.d("AAA", "Resolving service $service")
                    nsdManager.resolveService(service, resolveListener)
                }
            }

            override fun onServiceLost(service: NsdServiceInfo) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e("AAA", "service lost: $service")
            }

            override fun onDiscoveryStopped(serviceType: String) {
                Log.i("AAA", "Discovery stopped: $serviceType")
            }

            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e(
                    "AAA", "error on starting service discovery for " +
                            "$serviceType failed: $errorCode"
                )
                continuation.resumeWithException(
                    Throwable("Discovery failed: Error code:$errorCode")
                )
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e("AAA", "Discovery failed: Error code:$errorCode")
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
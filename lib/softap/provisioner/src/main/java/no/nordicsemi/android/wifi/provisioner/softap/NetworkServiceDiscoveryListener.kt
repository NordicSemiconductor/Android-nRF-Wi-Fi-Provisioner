package no.nordicsemi.android.wifi.provisioner.softap

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import kotlinx.coroutines.suspendCancellableCoroutine
import no.nordicsemi.android.wifi.provisioner.softap.SoftApManager.Companion.KEY_LINK_ADDR
import okio.ByteString.Companion.toByteString
import org.slf4j.LoggerFactory
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
    private val logger = LoggerFactory.getLogger(NetworkServiceDiscoveryListener::class.java)
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
                logger.error("Resolve failed: {}", errorCode)
            }

            override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
                logger.info("Resolve success: {}", serviceInfo)
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
                _discoveredIps.add(serviceInfo.host)
                continuation.resume(serviceInfo)
                nsdManager.stopServiceDiscovery(nsdListener)
            }
        }

        nsdListener = object : NsdManager.DiscoveryListener {

            // Called as soon as service discovery begins.
            override fun onDiscoveryStarted(regType: String) {
                logger.trace("Service discovery started for {}", regType)
            }

            override fun onServiceFound(service: NsdServiceInfo) {
                logger.info("Service discovered: {}", service)
                // Check if the service name found matches the service name we are looking for.
                if (service.serviceName == nsdServiceInfo.serviceName) {
                    logger.info("Resolving service...")
                    nsdManager.resolveService(service, resolveListener)
                }
            }

            override fun onServiceLost(service: NsdServiceInfo) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
            }

            override fun onDiscoveryStopped(serviceType: String) {
                logger.trace("Discovery stopped: {}", serviceType)
            }

            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                logger.error(
                    "Error while starting service discovery for {} failed: {}",
                    serviceType,
                    errorCode
                )
                continuation.resumeWithException(
                    Throwable("Discovery failed: Error code:$errorCode")
                )
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                logger.error(
                    "Error while stopping service discovery for {} failed: {}",
                    serviceType,
                    errorCode
                )
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
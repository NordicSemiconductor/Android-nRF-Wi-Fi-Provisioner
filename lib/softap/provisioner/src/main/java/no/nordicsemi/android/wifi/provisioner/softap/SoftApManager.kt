@file:Suppress("unused")

package no.nordicsemi.android.wifi.provisioner.softap

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.suspendCancellableCoroutine
import no.nordicsemi.android.wifi.provisioner.softap.Open.passphrase
import no.nordicsemi.android.wifi.provisioner.softap.domain.WifiConfigDomain
import no.nordicsemi.android.wifi.provisioner.softap.domain.toApi
import no.nordicsemi.android.wifi.provisioner.softap.domain.toDomain
import no.nordicsemi.kotlin.wifi.provisioner.domain.ConnectionInfoDomain
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.wire.WireConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Created by Roshan Rajaratnam on 23/02/2024.
 *
 * Entry point to the SoftApManager
 *
 * @constructor Create empty SoftApManager.
 */
class SoftApManager(
    context: Context,
    hostNameConfiguration: HostNameConfiguration = HostNameConfiguration(),
) {
    var softAp: SoftAp? = null
        private set

    private val _discoveredServices = mutableListOf<NsdServiceInfo>()
    private var discoveredService: NsdServiceInfo? = null
    private var isConnected = false

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    private val nsdListener = NetworkServiceDiscoveryListener(nsdManager, serviceName = hostNameConfiguration.serviceName)

    private val interceptor = HttpLoggingInterceptor().apply {
        level = when {
            BuildConfig.DEBUG -> HttpLoggingInterceptor.Level.BODY
            else -> HttpLoggingInterceptor.Level.NONE
        }
    }

    private val hostNameVerifier = HostnameVerifier { hostname, _ ->
        hostname.contains(hostNameConfiguration.serviceName)
    }

    private val client = OkHttpClient.Builder()
        .sslSocketFactory(
            hostNameConfiguration.handshakeCertificates.sslSocketFactory(),
            hostNameConfiguration.handshakeCertificates.trustManager
        )
        .addInterceptor(interceptor)
        .hostnameVerifier { hostname, _ ->
            hostname.contains(hostNameConfiguration.serviceName)
        }
        .followRedirects(false)
        .dns {
            nsdListener.discoveredIps
        }
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    private val softApProvisioningService = Retrofit.Builder()
        .baseUrl(hostNameConfiguration.hostName)
        .client(client)
        .addConverterFactory(WireConverterFactory.create())
        .build()
        .create(SoftApProvisioningService::class.java)

    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    /**
     * Connects to an unprovisioned wifi device by establishing a temporary wifi network connection
     * with the given ssid and the password.
     *
     * Note: The SSID and the password are the ones used to connect to the unprovisioned device, not
     * the SSID or the passphrase of the network that the device must be provisioned into.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun connect(
        ssid: String,
        passphraseConfiguration: PassphraseConfiguration,
    ): Unit = suspendCancellableCoroutine { continuation ->
        networkCallback = object : ConnectivityManager.NetworkCallback() {

            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onAvailable(network: Network) {
                // do success processing here..\
                if (connectivityManager.bindProcessToNetwork(network)) {
                    isConnected = true
                    softAp = SoftAp(
                        ssid = ssid,
                        passphraseConfiguration = passphraseConfiguration
                    )
                    continuation.resume(Unit)
                } else {
                    disconnect()
                    continuation.resumeWithException(
                        exception = IllegalStateException("Failed to bind to the network.")
                    )
                }
            }

            override fun onUnavailable() {
                // do failure processing here..
                disconnect()
                continuation.resumeWithException(
                    exception = IllegalStateException("Failed to bind to the network.")
                )
            }
        }

        val wifNetworkBuilder = WifiNetworkSpecifier.Builder()
            .setSsid(ssid)

        val networkSpecifier = when (passphraseConfiguration) {
            is Open -> wifNetworkBuilder.setWpa2Passphrase(passphrase)
            is Wpa2Passphrase -> wifNetworkBuilder.setWpa2Passphrase(passphrase)
            is Wpa3Passphrase -> wifNetworkBuilder.setWpa3Passphrase(passphrase)
        }.build()

        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .setNetworkSpecifier(networkSpecifier)
            .build()

        connectivityManager.requestNetwork(request, networkCallback)

        // Invoked if the coroutine calling this suspend function is cancelled.
        continuation.invokeOnCancellation {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

    /**
     * Discover services on the network.
     *
     * @param nsdServiceInfo NsdServiceInfo instance.
     */
    suspend fun discoverServices(
        nsdServiceInfo: NsdServiceInfo = NsdServiceInfo()
            .apply {
                serviceName = "wifiprov"
                serviceType = "_http._tcp."
            }
    ) {
        val serviceInfo = nsdListener
            .discoverServices(nsdServiceInfo = nsdServiceInfo)
        softAp?.apply {
            connectionInfoDomain = ConnectionInfoDomain(ipv4Address = serviceInfo.host.toString())
        }
    }

    /**
     * Lists the SSIDs scanned by the nRF7002 device
     */
    suspend fun listSsids() = softApProvisioningService.listSsids().toDomain()

    /**
     * Provisions the nRF7002 device a wifi network with the given credentials.
     *
     * @param config Credentials of the wifi network.
     */
    suspend fun provision(config: WifiConfigDomain) =
        softApProvisioningService.provision(config.toApi())

    /**
     * Disconnects from an unprovisioned or a newly provisioned device.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun disconnect() {
        isConnected = false
        connectivityManager.bindProcessToNetwork(null)
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    /**
     * Did provisioning complete.
     *
     * @param nsdServiceInfo NsdServiceInfo instance.
     */
    suspend fun verify(nsdServiceInfo: NsdServiceInfo) {
        discoverServices(nsdServiceInfo = nsdServiceInfo)
    }
}
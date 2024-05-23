@file:Suppress("unused")

package no.nordicsemi.android.wifi.provisioner.softap

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import no.nordicsemi.android.wifi.provisioner.softap.Open.passphrase
import no.nordicsemi.android.wifi.provisioner.softap.domain.ScanResultsDomain
import no.nordicsemi.android.wifi.provisioner.softap.domain.WifiConfigDomain
import no.nordicsemi.android.wifi.provisioner.softap.domain.toApi
import no.nordicsemi.android.wifi.provisioner.softap.domain.toDomain
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.ByteString.Companion.toByteString
import retrofit2.Response
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
 * @param context               Context of the application.
 * @param hostNameConfiguration HostNameConfiguration to be used for the SoftApManager.
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
    private var isBoundToNetwork = false
    private var isConnected = false

    private val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    val isWifiEnabled: Boolean
        get() = wifiManager.isWifiEnabled

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    private val nsdListener = NetworkServiceDiscoveryListener(nsdManager)

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

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    /**
     * Connects to an unprovisioned wifi device by establishing a temporary wifi network connection
     * with the given ssid and the password.
     *
     * Note: The SSID and the password are the ones used to connect to the unprovisioned device, not
     * the SSID or the passphrase of the network that the device must be provisioned into.
     *
     * @throws WifiNotEnabledException if the wifi is not enabled.
     * @throws FailedToBindToNetwork if the device failed to bind to the connected wifi network.
     *                               Call [connect] to connect to the softap and bind to the network.
     */
    @OptIn(InternalCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.Q)
    @Throws(WifiNotEnabledException::class, FailedToBindToNetwork::class)
    suspend fun connect(
        ssid: String,
        passphraseConfiguration: PassphraseConfiguration,
    ): Unit = suspendCancellableCoroutine { continuation ->
        if (!isWifiEnabled) {
            continuation.resumeWithException(exception = WifiNotEnabledException)
            return@suspendCancellableCoroutine
        }

        networkCallback = object : ConnectivityManager.NetworkCallback() {

            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onAvailable(network: Network) {
                // do success processing here..\
                if (connectivityManager.bindProcessToNetwork(network)) {
                    isConnected = true
                    isBoundToNetwork = true
                    softAp = SoftAp(
                        ssid = ssid,
                        passphraseConfiguration = passphraseConfiguration
                    )
                    continuation.resume(Unit)
                } else {
                    disconnect()
                    continuation.resumeWithException(exception = FailedToBindToNetwork)
                }
            }

            override fun onUnavailable() {
                // do failure processing here..
                disconnect()
                continuation.resumeWithException(exception = UnableToConnectToNetwork)
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

        connectivityManager.requestNetwork(request, networkCallback!!)

        // Invoked if the coroutine calling this suspend function is cancelled.
        continuation.invokeOnCancellation {
            connectivityManager.unregisterNetworkCallback(networkCallback!!)
        }
    }

    /**
     * Discovers and resolves the mDNS services on the network.
     *
     * Note: If the mDNS service on the Soft AP is to be discovered make sure to call [connect]
     * before calling this method.
     *
     * @param nsdServiceInfo NsdServiceInfo to be discovered.
     * @throws WifiNotEnabledException if the wifi is not enabled.
     */
    suspend fun discoverServices(
        nsdServiceInfo: NsdServiceInfo = NsdServiceInfo()
            .apply {
                serviceName = "wifiprov"
                serviceType = "_http._tcp."
            }
    ) {
        require(isWifiEnabled) { throw WifiNotEnabledException }
        val serviceInfo = discoverNetworkServices(nsdServiceInfo = nsdServiceInfo)
        softAp?.apply {
            this.serviceInfo = serviceInfo
        }
    }

    private suspend fun discoverNetworkServices(
        macAddress: String? = null,
        nsdServiceInfo: NsdServiceInfo
    ): NsdServiceInfo {
        val serviceInfo = nsdListener
            .discoverServices(
                macAddress = macAddress,
                nsdServiceInfo = nsdServiceInfo
            )
        return serviceInfo
    }

    /**
     * Lists the SSIDs scanned by the nRF700x device
     *
     * @return ScanResultsDomain containing the list of SSIDs.
     * @throws WifiNotEnabledException if the wifi is not enabled.
     * @throws FailedToBindToNetwork if the device failed to bind to the connected wifi network.
     *                               Call [connect] to connect to the softap and bind to the network.
     */
    suspend fun listSsids(): ScanResultsDomain {
        require(isWifiEnabled) { throw WifiNotEnabledException }
        require(isBoundToNetwork) { throw FailedToBindToNetwork }
        return softApProvisioningService.listSsids().toDomain()
    }

    /**
     * Provisions the nRF7002 device a wifi network with the given credentials.
     *
     * @param config Credentials of the wifi network.
     * @return Response<ResponseBody> received from the device.
     * @throws WifiNotEnabledException if the wifi is not enabled.
     * @throws FailedToBindToNetwork if the device failed to bind to the connected wifi network.
     *                               Call [connect] to connect to the softap and bind to the network.
     */
    suspend fun provision(config: WifiConfigDomain): Response<ResponseBody> {
        require(isWifiEnabled) { throw WifiNotEnabledException }
        require(isBoundToNetwork) { throw FailedToBindToNetwork }
        return softApProvisioningService.provision(config.toApi()).also {
            if (it.isSuccessful) {
                softAp?.wifiConfigDomain = config
            }
        }
    }

    /**
     * Disconnects from an unprovisioned or a newly provisioned device.
     * @throws WifiNotEnabledException if the wifi is not enabled.
     * @throws FailedToBindToNetwork if the device failed to bind to the connected wifi network.
     *                               Call [connect] to connect to the softap and bind to the network.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun disconnect() {
        if (isConnected) {
            isConnected = false
            isBoundToNetwork = false
            connectivityManager.bindProcessToNetwork(null)
        }

        networkCallback?.let {
            connectivityManager.unregisterNetworkCallback(it)
        }
    }

    /**
     * Verifies if the provisioning completed successfully.
     *
     * Note: Before calling this ensure that both the phone and the provisioned soft ap device are
     * on the same wifi network.
     */
    suspend fun verify() = softAp?.takeIf {
        it.serviceInfo != null
    }?.let {
        val serviceInfo = discoverNetworkServices(
            macAddress = it.macAddress,
            nsdServiceInfo = NsdServiceInfo().apply {
                serviceName = "wifiprov"
                serviceType = "_http._tcp."
            }
        )
        val mac = serviceInfo.attributes[KEY_LINK_ADDR]?.toByteString()?.utf8()
        mac == it.macAddress
    } ?: false


    companion object {
        const val KEY_LINK_ADDR = "linkaddr"
    }
}
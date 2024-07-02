/*
 * Copyright (c) 2024, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific prior
 * written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

@file:Suppress("unused")

package no.nordicsemi.android.wifi.provisioner.softap

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import androidx.annotation.RequiresApi
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
import org.slf4j.LoggerFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.wire.WireConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Entry point to the SoftApManager.
 *
 * @param context               Context of the application.
 * @param hostNameConfiguration HostNameConfiguration to be used for the SoftApManager.
 * @constructor Create empty SoftApManager.
 */
class SoftApManager(
    context: Context,
    hostNameConfiguration: HostNameConfiguration = HostNameConfiguration(),
) {
    private val logger = LoggerFactory.getLogger(SoftApManager::class.java)
    var softAp: SoftAp? = null
        private set

    private val _discoveredServices = mutableListOf<NsdServiceInfo>()
    private var discoveredService: NsdServiceInfo? = null
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

        if (isConnected) {
            return@suspendCancellableCoroutine
        }

        networkCallback?.let {
            connectivityManager.unregisterNetworkCallback(it)
        }
        networkCallback = object : ConnectivityManager.NetworkCallback() {

            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onAvailable(network: Network) {
                logger.trace("Binding to network {}...", network)
                if (connectivityManager.bindProcessToNetwork(network)) {
                    isConnected = true
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

            override fun onLosing(network: Network, maxMsToLive: Int) {
                logger.warn("Loosing network {}", network)
            }

            override fun onLost(network: Network) {
                logger.error("Network {} lost", network)
                disconnect()
            }

            override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                logger.debug("Link properties changed for network {} to {}", network, linkProperties)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                logger.debug("Capabilities changed for network {} to {}", network, networkCapabilities)
            }

            override fun onUnavailable() {
                logger.error("Network unavailable")
                // do failure processing here..
                connectivityManager.unregisterNetworkCallback(this)
                networkCallback = null
                if (isConnected) {
                    disconnect()
                    continuation.resumeWithException(exception = UnableToConnectToNetwork)
                }
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

        logger.trace("Connecting to {}...", ssid)
        connectivityManager.requestNetwork(request, networkCallback!!)

        // Invoked if the coroutine calling this suspend function is cancelled.
        continuation.invokeOnCancellation {
            disconnect()
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
        require(isConnected) { throw FailedToBindToNetwork }
        logger.trace("Obtaining list of SSIDs...")
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
        require(isConnected) { throw FailedToBindToNetwork }
        logger.trace("Provisioning to {}...", config.info!!.ssid)
        return softApProvisioningService.provision(config.toApi()).also {
            if (it.isSuccessful) {
                logger.info("Provisioning to {} succeeded", config.info.ssid)
                softAp?.wifiConfigDomain = config
            } else {
                logger.error("Provisioning failed with error {}", it.code())
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
            // don't clear softAP, as it's needed for verification
            connectivityManager.bindProcessToNetwork(null)
            logger.info("Disconnected from network")
        }

        networkCallback?.let {
            connectivityManager.unregisterNetworkCallback(it)
            networkCallback = null
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


    internal companion object {
        const val KEY_LINK_ADDR = "linkaddr"
    }
}
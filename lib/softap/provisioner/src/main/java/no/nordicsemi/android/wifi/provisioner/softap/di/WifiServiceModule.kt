/*
 *
 *  * Copyright (c) 2022, Nordic Semiconductor
 *  *
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package no.nordicsemi.android.wifi.provisioner.softap.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.nsd.NsdManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import no.nordicsemi.android.wifi.provisioner.softap.BuildConfig
import no.nordicsemi.android.wifi.provisioner.softap.NetworkServiceDiscoveryListener
import no.nordicsemi.android.wifi.provisioner.softap.WifiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.decodeCertificatePem
import retrofit2.Retrofit
import retrofit2.converter.wire.WireConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.HostnameVerifier


/**
 * Created by Roshan Rajaratnam on 19/02/2024.
 */
@Module
@InstallIn(SingletonComponent::class)
object WifiServiceModule {

    @Provides
    @Singleton
    fun provideNetworkServiceDiscoveryListener(@ApplicationContext context : Context): NetworkServiceDiscoveryListener {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
        return NetworkServiceDiscoveryListener(nsdManager)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(nsdListener: NetworkServiceDiscoveryListener): OkHttpClient {
        val interceptor = HttpLoggingInterceptor().apply {
            level = when {
                BuildConfig.DEBUG -> Level.BODY
                else -> Level.NONE
            }
        }
        val certificate = HandshakeCertificates.Builder()
            .addTrustedCertificate(CERTIFICATE.decodeCertificatePem())
            .build()

        // This is a work around to avoid unknown host exception
        val hostNameVerifier = HostnameVerifier { hostname, _ ->
            hostname.contains(NetworkServiceDiscoveryListener.SERVICE_NAME)
        }

        return OkHttpClient.Builder()
            .sslSocketFactory(certificate.sslSocketFactory(), certificate.trustManager)
            .addInterceptor(interceptor)
            .hostnameVerifier(hostNameVerifier)
            .followRedirects(false)
            .dns {
                nsdListener.discoveredIps
            }
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideService(client: OkHttpClient): WifiService = Retrofit.Builder()
        .baseUrl("https://${NetworkServiceDiscoveryListener.SERVICE_NAME}.local/")
        .client(client)
        .addConverterFactory(WireConverterFactory.create())
        .build()
        .create()

    private const val CERTIFICATE = "" +
            "-----BEGIN CERTIFICATE-----\n" +
            "MIIB1jCCAXugAwIBAgIUbOY1v8ubBZy6qIsZXelxvLy5l+QwCgYIKoZIzj0EAwIw\n" +
            "YDELMAkGA1UEBhMCVVMxDTALBgNVBAgMBFRlc3QxDTALBgNVBAcMBFRlc3QxGjAY\n" +
            "BgNVBAoMEVRlc3QgT3JnYW5pemF0aW9uMRcwFQYDVQQDDA53aWZpcHJvdi5sb2Nh\n" +
            "bDAeFw0yNDAzMjYxMzI4MzNaFw0yNTAzMjYxMzI4MzNaMGAxCzAJBgNVBAYTAlVT\n" +
            "MQ0wCwYDVQQIDARUZXN0MQ0wCwYDVQQHDARUZXN0MRowGAYDVQQKDBFUZXN0IE9y\n" +
            "Z2FuaXphdGlvbjEXMBUGA1UEAwwOd2lmaXByb3YubG9jYWwwWTATBgcqhkjOPQIB\n" +
            "BggqhkjOPQMBBwNCAAR6COfDiVYhNJkqCe3COkrN/Y9U8LPSDElE+mDk0ri7Ivb8\n" +
            "LefdeYP3HgoTEEgem5eDNy10UZlf6+q6VUWyCH8toxMwETAPBgNVHRMBAf8EBTAD\n" +
            "AQH/MAoGCCqGSM49BAMCA0kAMEYCIQDmEcPlg4GuPIAE9xvpW8t8LGit/+eDWCqE\n" +
            "3ADi/H6f0QIhALUgBnN1+7awE7M1FvSnizX3b5ff7BfzltskPYnpjxqS\n" +
            "-----END CERTIFICATE-----\n"
}
/*
 *
 *  * Copyright (c) 2022, Nordic Semiconductor
 *  *
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package no.nordicsemi.android.wifi.provisioner.softap.di

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import no.nordicsemi.android.wifi.provisioner.softap.BuildConfig
import no.nordicsemi.android.wifi.provisioner.softap.WifiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.decodeCertificatePem
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
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
    fun provideOkHttpClient(): OkHttpClient {
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
        val hostNameVerifier = HostnameVerifier { hostname, session ->
            true
        }

        return OkHttpClient.Builder()
            .sslSocketFactory(certificate.sslSocketFactory(), certificate.trustManager)
            .addInterceptor(interceptor)
            .hostnameVerifier(hostNameVerifier)
            .followRedirects(false)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideService(client: OkHttpClient): WifiService = Retrofit.Builder()
        .baseUrl("https://192.0.2.1/")
        .client(client)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
        .create()

    private const val CERTIFICATE = "" +
            "-----BEGIN CERTIFICATE-----\n" +
            "MIIDmTCCAoGgAwIBAgIUNPeHg7DpZ5bXuQIi6Xb5+3QsLU8wDQYJKoZIhvcNAQEL\n" +
            "BQAwXDELMAkGA1UEBhMCTk8xDzANBgNVBAgMBk5vcndheTEhMB8GA1UECgwYSW50\n" +
            "ZXJuZXQgV2lkZ2l0cyBQdHkgTHRkMRkwFwYDVQQDDBBodHRwc2VydmVyLmxvY2Fs\n" +
            "MB4XDTIzMDgyOTA5MDUxMVoXDTMzMDgyNjA5MDUxMVowXDELMAkGA1UEBhMCTk8x\n" +
            "DzANBgNVBAgMBk5vcndheTEhMB8GA1UECgwYSW50ZXJuZXQgV2lkZ2l0cyBQdHkg\n" +
            "THRkMRkwFwYDVQQDDBBodHRwc2VydmVyLmxvY2FsMIIBIjANBgkqhkiG9w0BAQEF\n" +
            "AAOCAQ8AMIIBCgKCAQEAtu0duZvD2kDOg1Qqh9VxhC/kVpquO3C31w8J3CgI/tZA\n" +
            "hlOGxxjXySrQdHUUpQHjWk9LaQhD8TMag8hA+4Rxx+MJpvx6G29se9dbG7TRTswx\n" +
            "MdRweXjlygjl+2DJr5Fy7541ceRfBc2O2+AZWsCxxryVhEyIUSAIhEL7cEF2NV7m\n" +
            "3O5POSPSOe86s8REaRbnrEcVAXGgViNf2oQGLAS2ul8gzkIcU/7/foHNI8RL/9qD\n" +
            "arQrxyUrUzAnGxdaaVgajbHVGpGyKOuhEYwZlHZbJI+juHtw3iDaOb8oz1fXsoDo\n" +
            "mSO0Zzt4Q/pGvxgitbBKKHaM0juo2q81tto/pa82bwIDAQABo1MwUTAdBgNVHQ4E\n" +
            "FgQUfLgHfBTGAvqmcg/c7AhbLwaA5WUwHwYDVR0jBBgwFoAUfLgHfBTGAvqmcg/c\n" +
            "7AhbLwaA5WUwDwYDVR0TAQH/BAUwAwEB/zANBgkqhkiG9w0BAQsFAAOCAQEAYieg\n" +
            "3NtCYNWAq0ASjMUIlN4xTWlWPvQMLcvaBWs27GB8dYR9uq450G1ADZqMWV7CWT5G\n" +
            "6QVVUj5XOCLhlmIfuBBdh5Yw9DSq7f6ALa+eUwsn/yC2pVrMYiWuSwAMB0XO06ip\n" +
            "p95K05lDFhjAzHTNJSXs0bHFNnxDwk687hfWQCsjPh2Gocg5OGJVq6b9KA9kv3FT\n" +
            "ndfNnVdyfBsKn/USf96kJ97qUkTWKwNNvPn9eBhP9T5FdTfv9bCjv3kqHTCSvF5/\n" +
            "Hv7J6UHlxNcWxBDq0FGj3jxKoxUyXuDi7curcCPPC/gy57NMyg0nyq/gE4alJKVH\n" +
            "8ticzSyJlz9rS7AbAg==\n" +
            "-----END CERTIFICATE-----\n"
}
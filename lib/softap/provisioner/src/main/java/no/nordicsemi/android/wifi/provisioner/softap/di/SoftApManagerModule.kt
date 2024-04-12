package no.nordicsemi.android.wifi.provisioner.softap.di

import android.net.ConnectivityManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import no.nordicsemi.android.wifi.provisioner.softap.NetworkServiceDiscoveryListener
import no.nordicsemi.android.wifi.provisioner.softap.SoftApManager
import no.nordicsemi.android.wifi.provisioner.softap.WifiService
import javax.inject.Singleton

/**
 * Created by Roshan Rajaratnam on 22/02/2024.
 */
@Module
@InstallIn(SingletonComponent::class)
object SoftApManagerModule {

    @Provides
    @Singleton
    fun provideSoftApManager(
        connectivityManager: ConnectivityManager,
        nsdListener: NetworkServiceDiscoveryListener,
        wifiService: WifiService,
        @IODispatcher coroutineDispatcher: CoroutineDispatcher
    ) = SoftApManager(
        connectivityManager = connectivityManager,
        nsdListener = nsdListener,
        wifiService = wifiService,
        coroutineDispatcher = coroutineDispatcher
    )
}
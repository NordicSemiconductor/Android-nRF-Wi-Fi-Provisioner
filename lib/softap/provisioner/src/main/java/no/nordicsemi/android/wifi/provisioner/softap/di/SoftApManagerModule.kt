package no.nordicsemi.android.wifi.provisioner.softap.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
        @ApplicationContext context: Context,
        nsdListener: NetworkServiceDiscoveryListener,
        wifiService: WifiService,
        @IODispatcher coroutineDispatcher: CoroutineDispatcher
    ) = SoftApManager(
        context = context,
        nsdListener = nsdListener,
        wifiService = wifiService,
        coroutineDispatcher = coroutineDispatcher
    )
}
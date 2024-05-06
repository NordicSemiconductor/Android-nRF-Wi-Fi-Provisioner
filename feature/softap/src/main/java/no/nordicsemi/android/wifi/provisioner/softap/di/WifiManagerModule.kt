package no.nordicsemi.android.wifi.provisioner.softap.di

import android.content.Context
import android.net.wifi.WifiManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import no.nordicsemi.android.wifi.provisioner.softap.SoftApManager
import javax.inject.Singleton

/**
 * Created by Roshan Rajaratnam on 22/02/2024.
 */
@Module
@InstallIn(SingletonComponent::class)
object WifiManagerModule {

    @Provides
    @Singleton
    fun provideWifiManager(@ApplicationContext context: Context) =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
}
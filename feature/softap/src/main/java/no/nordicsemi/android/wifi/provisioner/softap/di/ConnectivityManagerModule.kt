package no.nordicsemi.android.wifi.provisioner.softap.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by Roshan Rajaratnam on 22/02/2024.
 */
@Module
@InstallIn(SingletonComponent::class)
object ConnectivityManagerModule {

    @Provides
    @Singleton
    fun provideWifiManager(@ApplicationContext context: Context) =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    @Provides
    @Singleton
    fun provideConnectivityManager(@ApplicationContext context: Context) =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
}
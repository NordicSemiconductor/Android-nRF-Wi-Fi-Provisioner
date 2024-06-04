package no.nordicsemi.android.wifi.provisioner.feature.nfc.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import no.nordicsemi.android.wifi.provisioner.nfc.NdefMessageBuilder
import no.nordicsemi.android.wifi.provisioner.nfc.WifiManagerRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WifiManagerRepositoryModule {

    @Provides
    @Singleton
    fun provideNdefMessageBuilder() = NdefMessageBuilder()

    @Provides
    @Singleton
    fun provideWifiManagerRepository(
        @ApplicationContext context: Context
    ) = WifiManagerRepository(context)
}
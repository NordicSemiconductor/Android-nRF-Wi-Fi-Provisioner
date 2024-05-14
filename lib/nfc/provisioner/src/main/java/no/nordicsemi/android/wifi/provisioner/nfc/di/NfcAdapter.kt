package no.nordicsemi.android.wifi.provisioner.nfc.di

import android.content.Context
import android.nfc.NfcAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import no.nordicsemi.android.wifi.provisioner.nfc.NfcManagerForWifi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NfcAdapterModule {

    @Provides
    @Singleton
    fun provideNfcAdapter(@ApplicationContext context: Context): NfcAdapter? {
        return NfcAdapter.getDefaultAdapter(context)
    }

    @Provides
    @Singleton
    fun provideWifiConnectionManager(@ApplicationContext context: Context) =
        NfcManagerForWifi(
            nfcAdapter = provideNfcAdapter(context)!!
        )
}
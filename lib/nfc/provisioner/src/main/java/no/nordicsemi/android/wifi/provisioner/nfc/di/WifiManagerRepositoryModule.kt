package no.nordicsemi.android.wifi.provisioner.nfc.di

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import no.nordicsemi.android.wifi.provisioner.nfc.WifiManagerRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WifiManagerRepositoryModule {

    @RequiresApi(Build.VERSION_CODES.M)
    @Provides
    @Singleton
    fun provideWifiManagerRepositoryModule(@ApplicationContext context: Context) =
        WifiManagerRepository(context = context)
}
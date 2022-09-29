package no.nordicsemi.android.wifi.provisioning.repository

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import no.nordicsemi.wifi.provisioner.library.ProvisionerRepository

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun providesRepository(
        @ApplicationContext
        context: Context
    ): ProvisionerRepository {
        return ProvisionerRepository.newInstance(context)
    }

    @Provides
    fun providesResourceRepository(
        repository: ProvisionerRepository
    ): ProvisionerResourceRepository {
        return ProvisionerResourceRepository(repository)
    }
}

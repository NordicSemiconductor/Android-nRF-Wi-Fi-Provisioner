package no.nordicsemi.android.wifi.provisioning.repository

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import no.nordicsemi.wifi.provisioner.library.ProvisionerRepository
import no.nordicsemi.android.wifi.provisioning.util.Resource
import no.nordicsemi.wifi.provisioner.library.domain.DeviceStatusDomain
import no.nordicsemi.wifi.provisioner.library.domain.ScanRecordDomain
import no.nordicsemi.wifi.provisioner.library.domain.VersionDomain
import no.nordicsemi.wifi.provisioner.library.domain.WifiConfigDomain
import no.nordicsemi.wifi.provisioner.library.domain.WifiConnectionStateDomain
import no.nordicsemi.wifi.provisioner.library.ConnectionStatus

class ProvisionerResourceRepository(
    val repository: ProvisionerRepository
) {

    suspend fun start(device: BluetoothDevice): Flow<ConnectionStatus> {
        return repository.start(device)
    }

    fun readVersion(): Flow<Resource<VersionDomain>> {
        return runTask { repository.readVersion() }
    }

    fun getStatus(): Flow<Resource<DeviceStatusDomain>> {
        return runTask { repository.getStatus() }
    }

    fun startScan(): Flow<Resource<ScanRecordDomain>> {
        return repository.startScan()
            .map { Resource.createSuccess(it) }
            .onStart { emit(Resource.createLoading()) }
            .catch {
                it.printStackTrace()
                emit(Resource.createError(it))
            }
    }

    suspend fun stopScanBlocking() {
        repository.stopScan()
    }

    fun setConfig(config: WifiConfigDomain): Flow<Resource<WifiConnectionStateDomain>> {
        return repository.setConfig(config)
            .map { Resource.createSuccess(it) }
            .onStart { emit(Resource.createLoading()) }
            .catch {
                it.printStackTrace()
                emit(Resource.createError(it))
            }
    }

    fun forgetConfig(): Flow<Resource<Unit>> {
        return runTask { repository.forgetConfig() }
    }

    suspend fun release() {
        repository.release()
    }

    fun openLogger() {
        repository.openLogger()
    }

    private fun <T> runTask(block: suspend () -> T): Flow<Resource<T>> {
        return flow { emit(Resource.createSuccess(block())) }
            .onStart { emit(Resource.createLoading()) }
            .catch {
                it.printStackTrace()
                emit(Resource.createError(it))
            }
    }
}

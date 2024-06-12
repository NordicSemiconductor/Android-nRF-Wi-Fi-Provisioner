package no.nordicsemi.android.wifi.provisioner.nfc.domain

import android.net.wifi.ScanResult

/**
 * NetworkState is a sealed interface that holds the different network states.
 */
sealed interface NetworkState

/**
 * Success is a data class that represents the success state of the network.
 *
 * @param data The data that is returned from the network.
 */
data class Success(val data: List<ScanResult>) : NetworkState

/**
 * Error is a data class that represents the error state of the network.
 *
 * @param t The throwable that is returned from the network.
 */
data class Error(val t: Throwable) : NetworkState

/**
 * Loading is a class that represents the loading state of the network.
 */
data object Loading : NetworkState

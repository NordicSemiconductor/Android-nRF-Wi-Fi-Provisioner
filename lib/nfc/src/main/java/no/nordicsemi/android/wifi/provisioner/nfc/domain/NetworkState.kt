package no.nordicsemi.android.wifi.provisioner.nfc.domain

/**
 * NetworkState is a sealed interface that holds the different network states.
 */
sealed interface NetworkState<T>

/**
 * Success is a data class that represents the success state of the network.
 *
 * @param data The data that is returned from the network.
 */
data class Success<T>(val data: T) : NetworkState<T>

/**
 * Error is a data class that represents the error state of the network.
 *
 * @param t The throwable that is returned from the network.
 */
data class Error<T>(val t: Throwable) : NetworkState<T>

/**
 * Loading is a class that represents the loading state of the network.
 */
class Loading<T> : NetworkState<T>

package no.nordicsemi.android.wifi.provisioner.softap.viewmodel

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import no.nordicsemi.android.common.navigation.Navigator
import no.nordicsemi.android.common.navigation.viewmodel.SimpleNavigationViewModel
import javax.inject.Inject

/**
 * Created by Roshan Rajaratnam on 15/02/2024.
 */
@HiltViewModel
class SoftApViewModel @Inject constructor(
    navigator: Navigator,
    savedStateHandle: SavedStateHandle
) : SimpleNavigationViewModel(navigator = navigator, savedStateHandle = savedStateHandle) {

    val wifiConfigFlow = MutableStateFlow<WifiConfig?>(null)
    var network: Network? = null
        private set

    @RequiresApi(Build.VERSION_CODES.Q)
    fun connect(connectivityManager: ConnectivityManager, ssid: String = "mobileappsrules") {
        val specifier = WifiNetworkSpecifier.Builder()
            .setSsid(ssid)
            .setWpa2Passphrase("")
            .build()

        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .setNetworkSpecifier(specifier)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                // do success processing here..
                this@SoftApViewModel.network = network
                Log.d("AAAA", "Wifi connected")
            }

            override fun onUnavailable() {
                // do failure processing here..
                Log.d("AAAA", "Something went wrong!")
            }
        }
        connectivityManager.requestNetwork(request, networkCallback)
    }


}

data class WifiConfig(val ssid: String, val password:String)

package no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.location

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.utils.LocalDataProvider
import no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.utils.PermissionUtils
import no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.utils.WifiPermissionNotAvailableReason
import no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.utils.WifiPermissionState
import javax.inject.Inject
import javax.inject.Singleton

private const val REFRESH_PERMISSIONS =
    "no.nordicsemi.android.common.permission.REFRESH_LOCATION_PERMISSIONS"

@Singleton
class LocationStateManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val dataProvider = LocalDataProvider(context)
    private val utils = PermissionUtils(context, dataProvider)

    @SuppressLint("WrongConstant")
    fun locationState() = callbackFlow {
        trySend(getLocationState())

        val locationStateChangeHandler = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                trySend(getLocationState())
            }
        }
        val filter = IntentFilter().apply {
            addAction(LocationManager.MODE_CHANGED_ACTION)
            addAction(REFRESH_PERMISSIONS)
        }
        ContextCompat.registerReceiver(
            context,
            locationStateChangeHandler,
            filter,
            ContextCompat.RECEIVER_EXPORTED
        )
        awaitClose {
            context.unregisterReceiver(locationStateChangeHandler)
        }
    }

    fun refreshPermission() {
        val intent = Intent(REFRESH_PERMISSIONS)
        context.sendBroadcast(intent)
    }

    fun markLocationPermissionRequested() {
        dataProvider.locationPermissionRequested = true
    }

    fun isLocationPermissionDeniedForever(context: Context): Boolean {
        return utils.isLocationPermissionDeniedForever(context)
    }

    private fun getLocationState(): WifiPermissionState {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return when {
            !utils.isLocationPermissionGranted ->
                WifiPermissionState.NotAvailable(WifiPermissionNotAvailableReason.PERMISSION_REQUIRED)

            dataProvider.isLocationPermissionRequired && !LocationManagerCompat.isLocationEnabled(lm) ->
                WifiPermissionState.NotAvailable(WifiPermissionNotAvailableReason.DISABLED)

            else -> WifiPermissionState.Available
        }
    }
}
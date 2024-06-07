package no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.wifi

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.RECEIVER_EXPORTED
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
    "no.nordicsemi.android.common.permission.REFRESH_WIFI_PERMISSIONS"

@Singleton
class WifiStateManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val dataProvider = LocalDataProvider(context)
    private val utils = PermissionUtils(context, dataProvider)

    @SuppressLint("WrongConstant")
    fun wifiState() = callbackFlow {
        trySend(getWifiPermissionState())

        val wifiStateChangeHandler = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                trySend(getWifiPermissionState())
            }
        }
        val filter = IntentFilter().apply {
            addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
            addAction(REFRESH_PERMISSIONS)
        }

        ContextCompat.registerReceiver(
            context,
            wifiStateChangeHandler,
            filter,
            RECEIVER_EXPORTED
        )

        awaitClose {
            context.unregisterReceiver(wifiStateChangeHandler)
        }
    }

    fun refreshPermission() {
        val intent = Intent(REFRESH_PERMISSIONS)
        context.sendBroadcast(intent)
    }

    fun markWifiPermissionRequested() {
        dataProvider.wifiPermissionRequested = true
    }

    fun isWifiPermissionDeniedForever(context: Context): Boolean {
        return utils.isWifiPermissionDeniedForever(context)
    }

    private fun getWifiPermissionState() = when {
        !utils.isWifiAvailable -> WifiPermissionState.NotAvailable(
            WifiPermissionNotAvailableReason.NOT_AVAILABLE
        )

        !utils.areNecessaryWifiPermissionsGranted -> WifiPermissionState.NotAvailable(
            WifiPermissionNotAvailableReason.PERMISSION_REQUIRED
        )

        !utils.isWifiEnabled -> WifiPermissionState.NotAvailable(
            WifiPermissionNotAvailableReason.DISABLED
        )

        else -> WifiPermissionState.Available
    }
}

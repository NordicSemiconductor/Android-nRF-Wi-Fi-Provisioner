package no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import androidx.core.content.ContextCompat

internal class PermissionUtils(
    private val context: Context,
    private val dataProvider: LocalDataProvider,
) {
    val isWifiEnabled: Boolean
        get() = (context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager)
            .isWifiEnabled

    val isWifiAvailable: Boolean
        get() = context.packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI)

    private val isLocationPermissionRequired: Boolean
        get() = dataProvider.isMarshmallowOrAbove

    private val isWifiPermissionGranted: Boolean
        get() = !dataProvider.isTiramisuOrAbove ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.NEARBY_WIFI_DEVICES
                ) == PackageManager.PERMISSION_GRANTED

    val isLocationPermissionGranted: Boolean
        get() = !isLocationPermissionRequired ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

    val areNecessaryWifiPermissionsGranted: Boolean
        get() = isWifiPermissionGranted

    fun isWifiPermissionDeniedForever(context: Context): Boolean {
        return dataProvider.isTiramisuOrAbove &&
                !isWifiPermissionGranted && // Wifi permission must be denied
                dataProvider.wifiPermissionRequested && // Permission must have been requested before
                !context.findActivity()
                    .shouldShowRequestPermissionRationale(Manifest.permission.NEARBY_WIFI_DEVICES)
    }

    fun isLocationPermissionDeniedForever(context: Context): Boolean {
        return dataProvider.isMarshmallowOrAbove &&
                !isLocationPermissionGranted // Location permission must be denied
                && dataProvider.locationPermissionRequested // Permission must have been requested before
                && !context.findActivity()
            .shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    /**
     * Finds the activity from the given context.
     *
     * https://github.com/google/accompanist/blob/6611ebda55eb2948eca9e1c89c2519e80300855a/permissions/src/main/java/com/google/accompanist/permissions/PermissionsUtil.kt#L99
     *
     * @throws IllegalStateException if no activity was found.
     * @return the activity.
     */
    private fun Context.findActivity(): Activity {
        var context = this
        while (context is ContextWrapper) {
            if (context is Activity) return context
            context = context.baseContext
        }
        throw IllegalStateException("no activity")
    }
}

package no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.core.app.ActivityCompat

private const val SHARED_PREFS_NAME = "SHARED_PREFS_NAME"

private const val PREFS_PERMISSION_REQUESTED = "permission_requested"
private const val PREFS_WIFI_PERMISSION_REQUESTED = "wifi_permission_requested"

internal class LocalDataProvider(
    private val context: Context
) {
    private val sharedPrefs: SharedPreferences
        get() = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * The first time an app requests a permission there is no 'Don't ask again' checkbox and
     * [ActivityCompat.shouldShowRequestPermissionRationale] returns false.
     * This situation is similar to a permission being denied forever, so to distinguish both cases
     * a flag needs to be saved.
     */
    var locationPermissionRequested: Boolean
        get() = sharedPrefs.getBoolean(PREFS_PERMISSION_REQUESTED, false)
        set(value) {
            sharedPrefs.edit().putBoolean(PREFS_PERMISSION_REQUESTED, value).apply()
        }

    /**
     * The first time an app requests a permission there is no 'Don't ask again' checkbox and
     * [ActivityCompat.shouldShowRequestPermissionRationale] returns false.
     * This situation is similar to a permission being denied forever, so to distinguish both cases
     * a flag needs to be saved.
     */
    var wifiPermissionRequested: Boolean
        get() = sharedPrefs.getBoolean(PREFS_WIFI_PERMISSION_REQUESTED, false)
        set(value) {
            sharedPrefs.edit().putBoolean(PREFS_WIFI_PERMISSION_REQUESTED, value).apply()
        }

    val isLocationPermissionRequired: Boolean
        /**
         * Location enabled is required on phones running Android 6 - 11
         * (for example on Nexus and Pixel devices). Initially, Samsung phones didn't require it,
         * but that has been fixed for those phones in Android 9.
         * Several Wi-Fi APIs require the ACCESS_FINE_LOCATION permission,
         * even when your app targets Android 13 or higher.
         *
         * @return False if it is known that location is not required, true otherwise.
         */
        get() = isMarshmallowOrAbove

    val isMarshmallowOrAbove: Boolean
        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.M)
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    val isTiramisuOrAbove: Boolean
        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
}

package com.nordicsemi.android.wifi.provisioning.wifi.view

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nordicsemi.android.wifi.provisioning.R

private const val MEDIUM_RSSI = -85
private const val MAX_RSSI = -65

@Composable
internal fun RssiIcon(rssi: Int) {
    Image(
        painter = painterResource(id = getImageRes(rssi)),
        contentDescription = stringResource(id = R.string.cd_rssi)
    )
}

@DrawableRes
private fun getImageRes(rssi: Int): Int {
    return when  {
        rssi < MEDIUM_RSSI -> R.drawable.ic_signal_min
        rssi < MAX_RSSI -> R.drawable.ic_signal_medium
        else -> R.drawable.ic_signal_max
    }
}

@Preview
@Composable
private fun RssiIconPreview() {
    RssiIcon(-90)
}
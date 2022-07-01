package com.nordicsemi.android.wifi.provisioning.home.view.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder

@Composable
fun Modifier.applyPlaceholder(): Modifier {
    return this.placeholder(
        visible = true,
        color = MaterialTheme.colorScheme.outline,
        highlight = PlaceholderHighlight.shimmer()
    )
}

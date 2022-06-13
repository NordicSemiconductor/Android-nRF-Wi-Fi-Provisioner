package com.nordicsemi.wifi.provisioner

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import no.nordicsemi.android.navigation.NavigationView
import no.nordicsemi.android.theme.NordicActivity
import no.nordicsemi.android.theme.NordicTheme
import no.nordicsemi.ui.scanner.ScannerDestinations

@AndroidEntryPoint
class MainActivity : NordicActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NordicTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NavigationView(HomeDestinations + ScannerDestinations)
                }
            }
        }
    }
}
